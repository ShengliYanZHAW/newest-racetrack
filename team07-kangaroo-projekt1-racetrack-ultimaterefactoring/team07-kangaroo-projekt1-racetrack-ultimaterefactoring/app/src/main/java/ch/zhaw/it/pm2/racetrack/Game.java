package ch.zhaw.it.pm2.racetrack;

import ch.zhaw.it.pm2.racetrack.given.GameSpecification;
import ch.zhaw.it.pm2.racetrack.strategy.MoveStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the game controller which manages game state changes.
 * It handles car movements, collision detection, finish line crossing logic,
 * and provides a facade to access track and car information.
 */
public class Game implements GameSpecification {

    private final Track track;
    private final List<MoveStrategy> moveStrategies;
    private int currentCarIndex;
    private int winner;
    private final boolean[] hasIncorrectCrossing;
    private final int[] consecutiveCorrectCrossings;

    /**
     * Constructs a new Game instance using the provided track.
     *
     * @param track the track to be used for this game
     * @throws NullPointerException if track is null
     */
    public Game(final Track track) {
        this.track = Objects.requireNonNull(track, "Track must not be null");
        int carCount = track.getCarCount();
        this.moveStrategies = new ArrayList<>(carCount);
        for (int i = 0; i < carCount; i++) {
            this.moveStrategies.add(null);
        }
        this.currentCarIndex = 0;
        this.winner = NO_WINNER;
        this.hasIncorrectCrossing = new boolean[carCount];
        this.consecutiveCorrectCrossings = new int[carCount];
    }

    /**
     * Returns the number of cars on the track.
     *
     * @return the car count
     */
    @Override
    public int getCarCount() {
        return track.getCarCount();
    }

    /**
     * Returns the index of the current active car.
     *
     * @return the zero-based index of the current car
     */
    @Override
    public int getCurrentCarIndex() {
        return currentCarIndex;
    }

    /**
     * Returns the ID of the specified car.
     *
     * @param carIndex the zero-based index of the car
     * @return the car ID as a character
     * @throws IndexOutOfBoundsException if carIndex is invalid
     */
    @Override
    public char getCarId(int carIndex) {
        return track.getCar(carIndex).getId();
    }

    /**
     * Returns the current position of the specified car.
     *
     * @param carIndex the zero-based index of the car
     * @return the PositionVector representing the car's position
     * @throws IndexOutOfBoundsException if carIndex is invalid
     */
    @Override
    public PositionVector getCarPosition(int carIndex) {
        return track.getCar(carIndex).getPosition();
    }

    /**
     * Returns the current velocity of the specified car.
     *
     * @param carIndex the zero-based index of the car
     * @return the PositionVector representing the car's velocity
     * @throws IndexOutOfBoundsException if carIndex is invalid
     */
    @Override
    public PositionVector getCarVelocity(int carIndex) {
        return track.getCar(carIndex).getVelocity();
    }

    /**
     * Sets the MoveStrategy for the specified car.
     *
     * @param carIndex     the zero-based index of the car
     * @param moveStrategy the MoveStrategy to be associated with the car
     * @throws IndexOutOfBoundsException if carIndex is invalid
     */
    @Override
    public void setCarMoveStrategy(int carIndex, MoveStrategy moveStrategy) {
        if (carIndex < 0 || carIndex >= getCarCount()) {
            throw new IndexOutOfBoundsException("Invalid car index: " + carIndex);
        }
        moveStrategies.set(carIndex, moveStrategy);
    }

    /**
     * Returns the next move for the specified car based on its MoveStrategy.
     *
     * @param carIndex the zero-based index of the car
     * @return the Direction representing the next move
     * @throws IllegalStateException if the MoveStrategy for the car is not set
     * @throws IndexOutOfBoundsException if carIndex is invalid
     */
    @Override
    public Direction nextCarMove(int carIndex) {
        if (carIndex < 0 || carIndex >= getCarCount()) {
            throw new IndexOutOfBoundsException("Invalid car index: " + carIndex);
        }
        
        MoveStrategy strategy = moveStrategies.get(carIndex);
        if (strategy == null) {
            throw new IllegalStateException("MoveStrategy for car " + carIndex + " is not set.");
        }
        return strategy.nextMove();
    }

    /**
     * Returns the index of the winning car.
     *
     * @return the zero-based index of the winner, or NO_WINNER if the game is still in progress
     */
    @Override
    public int getWinner() {
        return winner;
    }

    /**
     * Executes the turn for the current active car using the provided acceleration.
     * The method updates the car's velocity, computes the movement path using Bresenham's algorithm,
     * and checks each position for collisions or finish line crossings.
     * If a collision or winning condition is detected, the turn ends immediately.
     *
     * @param acceleration the Direction representing the acceleration for this turn;
     *                     its components must be -1, 0, or 1
     * @throws IllegalArgumentException if acceleration is null
     */
    @Override
    public void doCarTurn(Direction acceleration) {
        Objects.requireNonNull(acceleration, "Acceleration must not be null");
        
        Car currentCar = track.getCar(currentCarIndex);
        if (isTurnInvalid(currentCar)) {
            return;
        }
        currentCar.accelerate(acceleration);
        List<PositionVector> path = calculatePath(currentCar.getPosition(), currentCar.nextPosition());
        if (processPath(currentCar, path)) {
            return;
        }
        currentCar.move();
        checkForWinner();
    }

    /**
     * Switches to the next active car that has not crashed.
     * If no active car is found, the current car remains unchanged.
     */
    @Override
    public void switchToNextActiveCar() {
        int carCount = track.getCarCount();
        if (carCount == 0) {
            return;
        }
        for (int i = 1; i <= carCount; i++) {
            int index = (currentCarIndex + i) % carCount;
            if (!track.getCar(index).isCrashed()) {
                currentCarIndex = index;
                return;
            }
        }
    }

    /**
     * Calculates the grid positions in the path between two positions using Bresenham's algorithm.
     *
     * @param start the starting PositionVector
     * @param end   the ending PositionVector
     * @return a List of PositionVectors representing the path from start to end (inclusive)
     * @throws NullPointerException if start or end is null
     */
    @Override
    public List<PositionVector> calculatePath(PositionVector start, PositionVector end) {
        return PathCalculator.calculatePath(start, end);
    }

    /**
     * Checks if the current turn should be skipped due to game state.
     *
     * @param currentCar the current Car
     * @return true if the turn is invalid (winner determined or car crashed), false otherwise
     */
    private boolean isTurnInvalid(Car currentCar) {
        return winner != NO_WINNER || currentCar.isCrashed();
    }

    /**
     * Processes the movement path of the current car.
     * Iterates through each PositionVector in the path, checking for collisions and finish line crossings.
     *
     * @param currentCar the current Car
     * @param path       the movement path as a List of PositionVectors
     * @return true if the turn should be terminated early (due to a crash or win), false otherwise
     */
    private boolean processPath(Car currentCar, List<PositionVector> path) {
        for (PositionVector pos : path) {
            SpaceType space = track.getSpaceTypeAtPosition(pos);
            if (isCollision(space, currentCar, pos)) {
                return true;
            }
            if (isFinish(space) && processFinishCrossing(currentCar, space)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks for a collision at the given position. If a collision is detected, the car crashes and the winner is checked.
     *
     * @param space      the SpaceType at the position
     * @param currentCar the current Car
     * @param pos        the PositionVector to check
     * @return true if a collision occurred, false otherwise
     */
    private boolean isCollision(SpaceType space, Car currentCar, PositionVector pos) {
        if (space == SpaceType.WALL) {
            currentCar.crash(pos);
            checkForWinner();
            return true;
        }
        if (space == SpaceType.TRACK && hasCollisionAt(pos, currentCarIndex)) {
            currentCar.crash(pos);
            checkForWinner();
            return true;
        }
        return false;
    }

    /**
     * Processes finish line crossing for the current car.
     * Determines if the crossing is valid for winning, and updates crossing state accordingly.
     *
     * @param currentCar the current Car
     * @param space      the finish SpaceType encountered
     * @return true if the crossing resulted in a win, false otherwise
     */
    private boolean processFinishCrossing(Car currentCar, SpaceType space) {
        if (isWinningFinish(space, currentCar.getVelocity())) {
            if (!hasIncorrectCrossing[currentCarIndex]) {
                currentCar.move();
                winner = currentCarIndex;
                return true;
            } else {
                consecutiveCorrectCrossings[currentCarIndex]++;
                if (consecutiveCorrectCrossings[currentCarIndex] >= 2) {
                    currentCar.move();
                    winner = currentCarIndex;
                    return true;
                }
            }
        } else {
            hasIncorrectCrossing[currentCarIndex] = true;
            consecutiveCorrectCrossings[currentCarIndex] = 0;
        }
        return false;
    }

    /**
     * Checks if only one active car remains and sets it as the winner.
     */
    private void checkForWinner() {
        int activeCars = 0;
        int lastActiveIndex = -1;
        int carCount = track.getCarCount();
        for (int i = 0; i < carCount; i++) {
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

    /**
     * Determines if the given space is a finish space.
     *
     * @param space the SpaceType to check
     * @return true if the space is one of the finish types, false otherwise
     */
    private boolean isFinish(SpaceType space) {
        return space == SpaceType.FINISH_LEFT ||
            space == SpaceType.FINISH_RIGHT ||
            space == SpaceType.FINISH_UP ||
            space == SpaceType.FINISH_DOWN;
    }

    /**
     * Determines if the finish crossing is valid for winning based on the finish space and car velocity.
     *
     * @param space    the finish SpaceType encountered
     * @param velocity the car's velocity as a PositionVector
     * @return true if the finish crossing is valid for winning, false otherwise
     */
    private boolean isWinningFinish(SpaceType space, PositionVector velocity) {
        PositionVector vSignum = velocity.signum();
        switch (space) {
            case FINISH_LEFT:
                return vSignum.getX() == -1;
            case FINISH_RIGHT:
                return vSignum.getX() == 1;
            case FINISH_UP:
                return vSignum.getY() == -1;
            case FINISH_DOWN:
                return vSignum.getY() == 1;
            default:
                return false;
        }
    }

    /**
     * Checks for collision with any active car (excluding the current car) at the specified position.
     *
     * @param pos              the PositionVector to check
     * @param currentCarIndex  the index of the current car
     * @return true if a collision is detected, false otherwise
     */
    private boolean hasCollisionAt(PositionVector pos, int currentCarIndex) {
        int carCount = track.getCarCount();
        for (int i = 0; i < carCount; i++) {
            if (i == currentCarIndex) {
                continue;
            }
            Car otherCar = track.getCar(i);
            if (!otherCar.isCrashed() && otherCar.getPosition().equals(pos)) {
                return true;
            }
        }
        return false;
    }
}