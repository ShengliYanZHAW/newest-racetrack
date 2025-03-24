package ch.zhaw.it.pm2.racetrack.strategy;

import ch.zhaw.it.pm2.racetrack.Direction;

/**
 * Do not accelerate in any direction.
 * This is the simplest strategy that just maintains the car's current velocity.
 */
public class DoNotMoveStrategy implements MoveStrategy {
    /**
     * {@inheritDoc}
     *
     * @return always {@link Direction#NONE}
     */
    @Override
    public Direction nextMove() {
        return Direction.NONE;
    }
}