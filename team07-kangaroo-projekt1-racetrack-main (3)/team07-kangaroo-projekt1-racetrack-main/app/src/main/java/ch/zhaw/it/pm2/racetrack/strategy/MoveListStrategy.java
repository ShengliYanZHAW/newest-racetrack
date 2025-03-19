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
 * Implements a strategy that determines the next move based on a file containing
 * a list of directions. Each line in the file should contain a Direction enum value
 * (UP, DOWN, LEFT, RIGHT, etc.). When the list of moves in the file is exhausted,
 * the strategy returns Direction.NONE to maintain a constant velocity.
 */
public class MoveListStrategy implements MoveStrategy {
    /** List of moves read from the file. */
    private final List<Direction> moves;
    
    /** Index of the current move. */
    private int currentMoveIndex = 0;

    /**
     * Constructor that reads directions from the specified file.
     *
     * @param moveFile the file containing the list of moves
     * @throws IllegalArgumentException if the moveFile is null
     * @throws InvalidFileFormatException if there are formatting errors in the moves file
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
                    try {
                        Direction direction = Direction.valueOf(line);
                        movesList.add(direction);
                    } catch (IllegalArgumentException e) {
                        throw new InvalidFileFormatException(
                            InvalidFileFormatException.FormatErrorType.INVALID_MOVE_FORMAT,
                            "Invalid direction at line " + lineNumber + ": '" + line + "'. Must be one of " + 
                            String.join(", ", getDirectionNames())
                        );
                    }
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
     * Helper method to get an array of all valid direction names.
     * 
     * @return array of direction enum constant names
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
     *
     * @return next direction from move file or NONE if no more moves are available
     */
    @Override
    public Direction nextMove() {
        if (currentMoveIndex < moves.size()) {
            return moves.get(currentMoveIndex++);
        }
        return Direction.NONE;
    }
}