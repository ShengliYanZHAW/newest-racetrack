package ch.zhaw.it.pm2.racetrack.given;

import ch.zhaw.it.pm2.racetrack.PositionVector;
import ch.zhaw.it.pm2.racetrack.SpaceType;

/**
 * Interface representing the mandatory functions of the racetrack board.<br/>
 * IMPORTANT: This interface shall not be altered!<br/>
 * It specifies elements we use to test Racetrack for grading.<br/>
 * You may change or extend the default implementation<br/>
 * Full Javadoc can be found in the implementation file.
 */
public interface TrackSpecification {
    /** Maximum number of cars allowed in a track. */
    int MAX_CARS = 9;

    /** Character used to indicate a crashed car. */
    char CRASH_INDICATOR = 'X';

    /**
     * Return the height (number of rows) of the track grid.
     *
     * @return the height of the track grid
     */
    int getHeight();
    /**
     * Return the width (number of columns) of the track grid.
     *
     * @return the width of the track grid
     */
    int getWidth();

    /**
     * Return the number of cars.
     *
     * @return the number of cars
     */
    int getCarCount();

    /**
     * Get instance of specified car.
     *
     * @param carIndex the zero-based carIndex number
     * @return the car instance at the given index
     */
    CarSpecification getCar(int carIndex);

    /**
     * Return the type of space at the given position.
     * If the location is outside the track bounds, it is considered a WALL.
     *
     * @param position the coordinates of the position to examine
     * @return the type of track position at the given location
     */
    SpaceType getSpaceTypeAtPosition(PositionVector position);

    /**
     * Gets the character representation for the given position of the racetrack, including cars.<br/>
     * This can be used for generating the {@link #toString()} representation of the racetrack.<br/>
     * If there is an active car (not crashed) at the given position, then the car id is returned.<br/>
     * If there is a crashed car at the position, {@link #CRASH_INDICATOR} is returned.<br/>
     * Otherwise, the space character for the given position is returned
     *
     * @param row row (y-value) of the racetrack position
     * @param col column (x-value) of the racetrack position
     * @return character representing the position (col,row) on the track
     *    or {@link CarSpecification#getId()} resp. {@link #CRASH_INDICATOR}, if a car is at the given position
     */
    char getCharRepresentationAtPosition(int row, int col);

    /**
     * Return a String representation of the track, including the car locations and status.
     *
     * @return a String representation of the track
     */
    String toString();
}
