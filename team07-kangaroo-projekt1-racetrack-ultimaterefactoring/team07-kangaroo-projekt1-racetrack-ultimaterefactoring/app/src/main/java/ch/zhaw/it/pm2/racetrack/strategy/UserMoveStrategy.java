package ch.zhaw.it.pm2.racetrack.strategy;

import ch.zhaw.it.pm2.racetrack.Direction;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

import java.util.HashMap;
import java.util.Map;

/**
 * A strategy that lets the user decide the next move.
 * Uses the Text-IO library to interact with the user and obtain their move choice.
 */
public class UserMoveStrategy implements MoveStrategy {

    private final TextIO textIO;
    private final TextTerminal<?> terminal;
    private final Map<Integer, Direction> keypadMapping;

    /**
     * Constructs a new UserMoveStrategy and initializes the Text-IO components and keypad mapping.
     */
    public UserMoveStrategy() {
        this.textIO = TextIoFactory.getTextIO();
        this.terminal = textIO.getTextTerminal();
        this.keypadMapping = createKeypadMapping();
    }

    /**
     * Creates a mapping from numeric keypad values to Direction enum values.
     *
     * @return a map mapping integers 1-9 to corresponding Direction values
     */
    private Map<Integer, Direction> createKeypadMapping() {
        Map<Integer, Direction> mapping = new HashMap<>();
        mapping.put(7, Direction.UP_LEFT);
        mapping.put(8, Direction.UP);
        mapping.put(9, Direction.UP_RIGHT);
        mapping.put(4, Direction.LEFT);
        mapping.put(5, Direction.NONE);
        mapping.put(6, Direction.RIGHT);
        mapping.put(1, Direction.DOWN_LEFT);
        mapping.put(2, Direction.DOWN);
        mapping.put(3, Direction.DOWN_RIGHT);
        return mapping;
    }

    /**
     * Displays the keypad layout with direction mappings to the user.
     */
    private void displayKeypadHelp() {
        terminal.println("\nChoose acceleration direction using the numpad layout:");
        terminal.println("7 8 9     7=up-left,     8=up,              9=up-right");
        terminal.println("4 5 6     4=left,        5=no acceleration, 6=right");
        terminal.println("1 2 3     1=down-left,   2=down,            3=down-right");
    }

    /**
     * Asks the user for the next move based on keypad input.
     *
     * @return the Direction corresponding to the user's choice; if the user terminates the input, null is returned
     */
    @Override
    public Direction nextMove() {
        displayKeypadHelp();
        int keyChoice = textIO.newIntInputReader()
            .withMinVal(1)
            .withMaxVal(9)
            .read("Enter acceleration (1-9)");
        return keypadMapping.get(keyChoice);
    }
}
