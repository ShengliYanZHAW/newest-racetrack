package ch.zhaw.it.pm2.racetrack;

import ch.zhaw.it.pm2.racetrack.given.GameSpecification;
import ch.zhaw.it.pm2.racetrack.strategy.MoveStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Game controller class, performing all actions to modify the game state.
 * It contains the logic to switch and move the cars, detect if they are crashed
 * and if we have a winner.
 * It also acts as a facade to track and car information, to get game state information.
 */
public class Game implements GameSpecification {

    private final Track track;
    private final List<MoveStrategy> moveStrategies;
    private int currentCarIndex;
    private int winner;

    // Track if a car has crossed a finish line incorrectly
    private boolean[] hasIncorrectCrossing;
    // Track how many consecutive correct crossings a car has made
    private int[] consecutiveCorrectCrossings;

    /**
     * Constructor for the Game class.
     * @param track the track to be used for this game
     */
    public Game(final Track track) {
        this.track = track;
        int carCount = track.getCarCount();
        this.moveStrategies = new ArrayList<>(carCount);
        for (int i = 0; i < carCount; i++) {
            this.moveStrategies.add(null);
        }
        this.currentCarIndex = 0;
        this.winner = NO_WINNER;

        // Initialize tracking arrays for finish line crossing logic
        this.hasIncorrectCrossing = new boolean[carCount];
        this.consecutiveCorrectCrossings = new int[carCount];
    }

    /**
     * Return the number of cars on the track.
     * @return the number of cars
     */
    @Override
    public int getCarCount() {
        return track.getCarCount();
    }

    /**
     * Return the index of the current active car.
     * Car indexes are zero-based, so the first car is 0, and the last car is getCarCount() - 1.
     * @return the zero-based number of the current car
     */
    @Override
    public int getCurrentCarIndex() {
        return currentCarIndex;
    }

    /**
     * Get the id of the specified car.
     * @param carIndex The zero-based carIndex number
     * @return a char containing the id of the car
     */
    @Override
    public char getCarId(int carIndex) {
        return track.getCar(carIndex).getId();
    }

    /**
     * Get the position of the specified car.
     * @param carIndex The zero-based carIndex number
     * @return a PositionVector containing the car's current position
     */
    @Override
    public PositionVector getCarPosition(int carIndex) {
        return track.getCar(carIndex).getPosition();
    }

    /**
     * Get the velocity of the specified car.
     * @param carIndex The zero-based carIndex number
     * @return a PositionVector containing the car's current velocity
     */
    @Override
    public PositionVector getCarVelocity(int carIndex) {
        return track.getCar(carIndex).getVelocity();
    }

    /**
     * Set the {@link MoveStrategy} for the specified car.
     * @param carIndex The zero-based carIndex number
     * @param moveStrategy the {@link MoveStrategy} to be associated with the specified car
     */
    @Override
    public void setCarMoveStrategy(int carIndex, MoveStrategy moveStrategy) {
        moveStrategies.set(carIndex, moveStrategy);
    }

    /**
     * Get the next move for the specified car, depending on its {@link MoveStrategy}.
     * @param carIndex The zero-based carIndex number
     * @return the {@link Direction} containing the next move for the specified car
     */
    @Override
    public Direction nextCarMove(int carIndex) {
        MoveStrategy strategy = moveStrategies.get(carIndex);
        if (strategy == null) {
            throw new IllegalStateException("MoveStrategy for car " + carIndex + " is not set.");
        }
        return strategy.nextMove();
    }

    /**
     * Return the carIndex of the winner.<br/>
     * If the game is still in progress, returns {@link #NO_WINNER}.
     * @return the winning car's index (zero-based, see {@link #getCurrentCarIndex()}),
     * or {@link #NO_WINNER} if the game is still in progress
     */
    @Override
    public int getWinner() {
        return winner;
    }

    /**
     * Executes the next turn for the current active car.
     *
     * This method updates the current car's velocity based on the provided acceleration,
     * calculates the movement path, and verifies each position along the path for collisions or
     * finish line crossings. It also handles the outcome of the turn, whether it is a crash,
     * a valid finish, or simply a movement update.
     *
     * The method performs the following steps:
     * 1. Accelerate the Car: Update the car's velocity using the provided acceleration vector.
     * 2. Calculate the Path: Compute the path from the current position (start) to the new position (end)
     *    using the calculatePath(PositionVector, PositionVector) method.
     * 3. Path Verification: For each position in the calculated path, determine the type of space encountered:
     *    - TRACK: Check for collision with another car. If a collision occurs, mark the car as crashed and stop further processing.
     *    - WALL: The car has hit a wall. Mark the car as crashed and terminate further movement.
     *    - FINISH_*: The car has crossed a finish line segment.
     *      - If crossing in the correct direction, the car wins if it has either never crossed incorrectly before,
     *        or if it has achieved 2 consecutive correct crossings.
     *      - If crossing in the incorrect direction, record the incorrect crossing and reset the consecutive correct crossings counter.
     * 4. If the car crashes or wins, update its position to the corresponding crash or win coordinates.
     * 5. In case of a crash, check if only one car remains; if so, declare the remaining car as the winner.
     * 6. If none of the above conditions are met, move the car to the computed end position.
     *
     * Note: The calling method must inspect the winner state and decide the next course of action.
     * If the winner is not NO_WINNER, or if the current car is already marked as crashed,
     * the method returns immediately without further processing.
     *
     * @param acceleration a Direction object representing the car's acceleration vector
     *                     for this turn. The vector components are restricted to -1, 0, or 1 for both x and y directions.
     */

    @Override
    public void doCarTurn(Direction acceleration) {
        Car currentCar = track.getCar(currentCarIndex);
        if (winner != NO_WINNER || currentCar.isCrashed()) {
            return;
        }

        currentCar.accelerate(acceleration);

        PositionVector startPosition = currentCar.getPosition();
        PositionVector endPosition = currentCar.nextPosition();
        List<PositionVector> path = calculatePath(startPosition, endPosition);

        for (PositionVector pos : path) {
            SpaceType space = track.getSpaceTypeAtPosition(pos);
            if (space == SpaceType.WALL) {
                currentCar.crash(pos);
                checkForWinner();
                return;
            }
            if (space == SpaceType.TRACK && hasCollisionAt(pos, currentCarIndex)) {
                currentCar.crash(pos);
                checkForWinner();
                return;
            }
            if (isFinish(space)) {
                if (isWinningFinish(space, currentCar.getVelocity())) {
                    // Correct direction crossing
                    if (!hasIncorrectCrossing[currentCarIndex]) {
                        // No incorrect crossings before, this is a win
                        currentCar.move();
                        winner = currentCarIndex;
                        return;
                    } else {
                        // Had incorrect crossing before, need 2 consecutive correct crossings
                        consecutiveCorrectCrossings[currentCarIndex]++;
                        if (consecutiveCorrectCrossings[currentCarIndex] >= 2) {
                            // Two consecutive correct crossings, this is a win
                            currentCar.move();
                            winner = currentCarIndex;
                            return;
                        }
                    }
                } else {
                    // Incorrect direction crossing
                    hasIncorrectCrossing[currentCarIndex] = true;
                    consecutiveCorrectCrossings[currentCarIndex] = 0;
                }
            }
        }
        currentCar.move();
        checkForWinner();
    }

/**
 * Switches to the next car who is still in the game. Skips crashed cars.
 * If no active cars are found, the currentCarIndex remains unchanged.
 */
@Override
public void switchToNextActiveCar() {
    int carCount = track.getCarCount();
    if (carCount == 0) return;

    for (int i = 1; i <= carCount; i++) {
        int index = (currentCarIndex + i) % carCount;
        if (!track.getCar(index).isCrashed()) {
            currentCarIndex = index;
            return;
        }
    }

}

    /**
     * Returns all the grid positions in the path between two positions, for use in determining line of sight. <br>
     * Determine the 'pixels/positions' on a raster/grid using Bresenham's line algorithm.
     * (<a href="https://de.wikipedia.org/wiki/Bresenham-Algorithmus">Bresenham-Algorithmus</a>)<br>
     * Basic steps are <ul>
     *   <li>Detect which axis of the distance vector is longer (faster movement)</li>
     *   <li>for each pixel on the 'faster' axis calculate the position on the 'slower' axis.</li>
     * </ul>
     * Direction of the movement has to correctly considered.
     *
     * @param startPosition Starting position as a PositionVector
     * @param endPosition Ending position as a PositionVector
     * @return intervening grid positions as a List of PositionVector's, including the starting and ending positions.
     */
    @Override
    public List<PositionVector> calculatePath(PositionVector startPosition, PositionVector endPosition) {
        List<PositionVector> path = new ArrayList<>();

        int startX = startPosition.getX();
        int startY = startPosition.getY();
        int endX = endPosition.getX();
        int endY = endPosition.getY();

        int diffX = endX - startX;
        int diffY = endY - startY;
        int distX = Math.abs(diffX);
        int distY = Math.abs(diffY);
        int dirX = Integer.signum(diffX);
        int dirY = Integer.signum(diffY);

        // Determine the fast axis and set step increments
        int parallelStepX, parallelStepY, diagonalStepX, diagonalStepY, distanceSlowAxis, distanceFastAxis;

        if (distX > distY) {
            parallelStepX = dirX;
            parallelStepY = 0;
            diagonalStepX = dirX;
            diagonalStepY = dirY;
            distanceSlowAxis = distY;
            distanceFastAxis = distX;
        } else {
            parallelStepX = 0;
            parallelStepY = dirY;
            diagonalStepX = dirX;
            diagonalStepY = dirY;
            distanceSlowAxis = distX;
            distanceFastAxis = distY;
        }

        int x = startX, y = startY;
        path.add(new PositionVector(x, y));

        int error = distanceFastAxis / 2;
        for (int step = 0; step < distanceFastAxis; step++) {
            error -= distanceSlowAxis;
            if (error < 0) {
                error += distanceFastAxis;
                x += diagonalStepX;
                y += diagonalStepY;
            } else {
                x += parallelStepX;
                y += parallelStepY;
            }
            path.add(new PositionVector(x, y));
        }
        return path;
    }

    /**
     * Checks if the given space is a finish space.
     *
     * @param space the space type to check
     * @return true if the space is one of the finish spaces
     */
    private boolean isFinish(SpaceType space) {
        return space == SpaceType.FINISH_LEFT ||
            space == SpaceType.FINISH_RIGHT ||
            space == SpaceType.FINISH_UP ||
            space == SpaceType.FINISH_DOWN;
    }

    /**
     * Determines if the finish crossing is valid based on the finish space type and the car's velocity.
     * For FINISH_LEFT, the car must be moving from right to left (negative X velocity).
     * For FINISH_RIGHT, the car must be moving from left to right (positive X velocity).
     * For FINISH_UP, the car must be moving from bottom to top (negative Y velocity).
     * For FINISH_DOWN, the car must be moving from top to bottom (positive Y velocity).
     *
     * @param space the finish space type
     * @param velocity the car's velocity vector
     * @return true if the finish crossing is valid for winning
     */
    private boolean isWinningFinish(SpaceType space, PositionVector velocity) {
        PositionVector vSignum = velocity.signum();
        switch (space) {
            case FINISH_LEFT:  return vSignum.getX() == -1;
            case FINISH_RIGHT: return vSignum.getX() == 1;
            case FINISH_UP:    return vSignum.getY() == -1;
            case FINISH_DOWN:  return vSignum.getY() == 1;
            default:           return false;
        }
    }

    /**
     * Checks if there is a collision at the specified position with any active car (excluding the current car).
     *
     * @param pos the position to check for collision
     * @param currentCarIndex the index of the current car
     * @return true if there is a collision
     */
    private boolean hasCollisionAt(PositionVector pos, int currentCarIndex) {
        for (int i = 0; i < track.getCarCount(); i++) {
            if (i == currentCarIndex) continue;
            Car otherCar = track.getCar(i);
            if (!otherCar.isCrashed() && otherCar.getPosition().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if only one car remains active and sets it as the winner if applicable.
     */
    private void checkForWinner() {
        int activeCars = 0;
        int lastActiveIndex = -1;
        for (int i = 0; i < track.getCarCount(); i++) {
            Car car = track.getCar(i);
            if (!car.isCrashed()) {
                activeCars++;
                lastActiveIndex = i;
            }
        }
        if (activeCars == 1) {
            winner = lastActiveIndex;
        }
    }
}
