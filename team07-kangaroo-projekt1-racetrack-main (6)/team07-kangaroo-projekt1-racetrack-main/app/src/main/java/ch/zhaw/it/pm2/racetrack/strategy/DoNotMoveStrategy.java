package ch.zhaw.it.pm2.racetrack.strategy;

import ch.zhaw.it.pm2.racetrack.Direction;

/**
 * A strategy that does not accelerate in any direction.
 * This simple strategy always returns  Direction NONE,
 * thus maintaining the car's current velocity.
 */
public class DoNotMoveStrategy implements MoveStrategy {

    /**
     * Returns the next move, which is always Direction NONE.
     *
     * @return Direction NONE
     */
    @Override
    public Direction nextMove() {
        return Direction.NONE;
    }
}
