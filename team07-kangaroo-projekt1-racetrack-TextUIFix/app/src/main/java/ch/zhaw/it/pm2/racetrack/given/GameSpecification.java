package ch.zhaw.it.pm2.racetrack.given;

import ch.zhaw.it.pm2.racetrack.Direction;
import ch.zhaw.it.pm2.racetrack.PositionVector;
import ch.zhaw.it.pm2.racetrack.strategy.MoveStrategy;

import java.util.List;

/**
 * Interface representing the mandatory functions of the Game controller class.<br/>
 * IMPORTANT: This interface shall not be altered!<br/>
 * It specifies elements we use to test Racetrack for grading.<br/>
 * You may change or extend the default implementation<br/>
 * Full Javadoc can be found in the implementation file.
 */
public interface GameSpecification {

    /** Value representing, that the game is still running, and we have no winner. */
    int NO_WINNER = -1;

    /**
     * Return the number of cars on the track.
     * @return the number of cars
     */
    int getCarCount();

    /**
     * Return the index of the current active car.
     * Car indexes are zero-based, so the first car is 0, and the last car is getCarCount() - 1.
     * @return the zero-based number of the current car
     */
    int getCurrentCarIndex();

    /**
     * Get the id of the specified car.
     * @param carIndex The zero-based carIndex number
     * @return a char containing the id of the car
     */
    char getCarId(int carIndex);

    /**
     * Get the position of the specified car.
     * @param carIndex The zero-based carIndex number
     * @return a PositionVector containing the car's current position
     */
    PositionVector getCarPosition(int carIndex);

    /**
     * Get the velocity of the specified car.
     * @param carIndex The zero-based carIndex number
     * @return a PositionVector containing the car's current velocity
     */
    PositionVector getCarVelocity(int carIndex);

    /**
     * Set the {@link MoveStrategy} for the specified car.
     * @param carIndex The zero-based carIndex number
     * @param moveStrategy the {@link MoveStrategy} to be associated with the specified car
     */
    void setCarMoveStrategy(int carIndex, MoveStrategy moveStrategy);

    /**
     * Get the next move for the specified car, depending on its {@link MoveStrategy}.
     * @param carIndex The zero-based carIndex number
     * @return the {@link Direction} containing the next move for the specified car
     */
    Direction nextCarMove(int carIndex);

    /**
     * Return the carIndex of the winner.<br/>
     * If the game is still in progress, returns {@link #NO_WINNER}.
     * @return the winning car's index (zero-based, see {@link #getCurrentCarIndex()}),
     * or {@link #NO_WINNER} if the game is still in progress
     */
    int getWinner();

    /**
     * Execute the next turn for the current active car.
     * <p>This method changes the current car's velocity and checks on the path to the next position,
     * if it crashes (car state to crashed) or passes the finish line in the right direction (set winner state).</p>
     * <p>The calling method must check the winner state and decide how to go on. If the winner is different
     * than {@link #NO_WINNER}, or the current car is already marked as crashed the method returns immediately.</p>
     *
     * @param acceleration a Direction containing the current cars acceleration vector (-1,0,1) in x and y direction
     *                     for this turn
     */
    void doCarTurn(Direction acceleration);

    /**
     * Switches to the next car who is still in the game. Skips crashed cars.
     */
    void switchToNextActiveCar();

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
    List<PositionVector> calculatePath(PositionVector startPosition, PositionVector endPosition);
}
