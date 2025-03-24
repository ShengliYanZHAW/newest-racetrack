package ch.zhaw.it.pm2.racetrack.strategy;

import ch.zhaw.it.pm2.racetrack.Direction;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

/**
 * Let the user decide the next move.
 * Uses Text-IO library to interact with the user and get their move choice.
 */
public class UserMoveStrategy implements MoveStrategy {
    private final TextIO textIO;
    private final TextTerminal<?> terminal;

    /**
     * Constructor that initializes the Text-IO components.
     */
    public UserMoveStrategy() {
        this.textIO = TextIoFactory.getTextIO();
        this.terminal = textIO.getTextTerminal();
    }

    /**
     * {@inheritDoc}
     * Asks the user for the direction vector.
     *
     * @return next direction, null if the user terminates the game.
     */
    @Override
    public Direction nextMove() {
        return textIO.newEnumInputReader(Direction.class)
                .read("Choose a direction to accelerate");
    }
}