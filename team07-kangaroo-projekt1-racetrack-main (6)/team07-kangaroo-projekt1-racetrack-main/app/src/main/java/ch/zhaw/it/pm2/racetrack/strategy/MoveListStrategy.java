package ch.zhaw.it.pm2.racetrack.strategy;

import ch.zhaw.it.pm2.racetrack.Direction;
import ch.zhaw.it.pm2.racetrack.InvalidFileFormatException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements a strategy that determines the next move based on a file containing a list of directions.
 * Each line in the file should contain a valid Direction enum value (e.g. UP, DOWN, LEFT, RIGHT, etc.).
 * When the list of moves is exhausted, the strategy returns Direction NONE.
 */
public class MoveListStrategy implements MoveStrategy {

    private final List<Direction> moves;
    private int currentMoveIndex = 0;

    /**
     * Constructs a new MoveListStrategy by reading moves from the specified file.
     *
     * @param moveFile the file containing the list of moves; must not be null
     * @throws IllegalArgumentException if the moveFile is null
     * @throws InvalidFileFormatException if the file contains invalid direction values or cannot be read
     */
    public MoveListStrategy(File moveFile) throws InvalidFileFormatException {
        if (moveFile == null) {
            throw new IllegalArgumentException("Move file must not be null");
        }
        this.moves = readMovesFromFile(moveFile);
    }

    /**
     * Reads moves from a file, one per line.
     *
     * @param file the file to read from
     * @return a list of Direction objects
     * @throws InvalidFileFormatException if the file cannot be read or contains invalid direction values
     */
    private List<Direction> readMovesFromFile(File file) throws InvalidFileFormatException {
        List<Direction> movesList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (!line.isEmpty()) {
                    movesList.add(parseMoveLine(line, lineNumber));
                }
            }
        } catch (IOException e) {
            throw new InvalidFileFormatException(
                InvalidFileFormatException.FormatErrorType.UNKNOWN,
                "Error reading moves file: " + e.getMessage(),
                e
            );
        }
        return movesList;
    }

    /**
     * Parses a single line from the move file into a Direction.
     *
     * @param line the line to parse
     * @param lineNumber the current line number (for error reporting)
     * @return the parsed Direction
     * @throws InvalidFileFormatException if the line does not represent a valid direction
     */
    private Direction parseMoveLine(String line, int lineNumber) throws InvalidFileFormatException {
        try {
            return Direction.valueOf(line);
        } catch (IllegalArgumentException e) {
            throw new InvalidFileFormatException(
                InvalidFileFormatException.FormatErrorType.INVALID_MOVE_FORMAT,
                "Invalid direction at line " + lineNumber + ": '" + line + "'. Must be one of " +
                    String.join(", ", getDirectionNames())
            );
        }
    }

    /**
     * Returns an array of all valid direction names.
     *
     * @return an array of String representing all Direction enum constant names
     */
    private String[] getDirectionNames() {
        Direction[] values = Direction.values();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            names[i] = values[i].name();
        }
        return names;
    }

    /**
     * Returns the next direction from the move file.
     * If no more moves are available, returns Direction NONE.
     *
     * @return the next Direction or Direction NONE if moves are exhausted
     */
    @Override
    public Direction nextMove() {
        if (currentMoveIndex < moves.size()) {
            return moves.get(currentMoveIndex++);
        }
        return Direction.NONE;
    }
}
