package ch.zhaw.it.pm2.racetrack.strategy;

import ch.zhaw.it.pm2.racetrack.Direction;

/**
 * Move Strategy, the different cars may choose to determine the next direction to accelerate.
 */
public interface MoveStrategy {
    /**
     * Determine direction to accelerate in the next move.
     *
     * @return Direction vector to accelerate in the next move. null will terminate the game.
     */
    Direction nextMove();

    /**
     * Possible Move Strategies which can be selected. This shall not be altered!
     */
    enum StrategyType {
        DO_NOT_MOVE,
        USER,
        MOVE_LIST,
        PATH_FOLLOWER,
        PATH_FINDER
    }
}
