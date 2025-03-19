package ch.zhaw.it.pm2.racetrack.given;

import ch.zhaw.it.pm2.racetrack.Direction;
import ch.zhaw.it.pm2.racetrack.PositionVector;

/**
 * Interface representing the mandatory functions of a car on the racetrack.<br/>
 * IMPORTANT: This interface shall not be altered!<br/>
 * It specifies elements we use to test Racetrack for grading.<br/>
 * You may change or extend the default implementation<br/>
 * Full Javadoc can be found in the implementation file.
 */
public interface CarSpecification {
    /**
     * Returns Identifier of the car, which represents the car on the track.
     *
     * @return the identifier character
     */
    char getId();

    /**
     * Returns the current immutable position of the car on the track as a {@link PositionVector}.
     * @return the car's current position
     */
    PositionVector getPosition();

    /**
     * Returns the current immutable velocity vector of the car as a {@link PositionVector}.
     * @return the car's current velocity vector
     */
    PositionVector getVelocity();

    /**
     * Return the position that will apply after the next move at the current velocity.
     * Does not complete the move, so the current position remains unchanged.
     *
     * @return expected position after the next move
     */
    PositionVector nextPosition();

    /**
     * Add the specified amounts to this car's velocity.<br/>
     * The only acceleration values allowed are -1, 0 or 1 in both axis<br/>
     * There are 9 possible acceleration vectors, which are defined in {@link Direction}.<br/>
     * Changes only velocity, not position.<br/>
     *
     * @param acceleration a Direction vector containing the amounts to add to the velocity in x and y dimension
     */
    void accelerate(Direction acceleration);

    /**
     * Update this Car's position based on its current velocity.
     */
    void move();

    /**
     * Mark this Car as being crashed at the given position.
     *
     * @param crashPosition position the car crashed.
     */
    void crash(PositionVector crashPosition);

    /**
     * Returns whether this Car has been marked as crashed.
     *
     * @return true if crash() has been called on this Car, false otherwise.
     */
    boolean isCrashed();
}
