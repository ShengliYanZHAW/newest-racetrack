package ch.zhaw.it.pm2.racetrack.strategy;

import ch.zhaw.it.pm2.racetrack.Direction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Determines the next move based on a file containing a list of directions.
 * Each line in the file should contain a Direction enum value (UP, DOWN, LEFT, etc.).
 * When the file ends, it returns NONE.
 */
public class MoveListStrategy implements MoveStrategy {
    private final List<Direction> moves;
    private int currentMoveIndex = 0;

    /**
     * Constructor that reads directions from the specified file.
     *
     * @param moveFile the file containing the list of moves
     */
    public MoveListStrategy(File moveFile) {
        this.moves = readMovesFromFile(moveFile);
    }

    /**
     * Reads moves from a file, one per line.
     *
     * @param file the file to read from
     * @return a list of Direction objects
     */
    private List<Direction> readMovesFromFile(File file) {
        List<Direction> movesList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        Direction direction = Direction.valueOf(line);
                        movesList.add(direction);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid direction in file: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading moves file: " + e.getMessage());
        }

        return movesList;
    }

    /**
     * {@inheritDoc}
     *
     * @return next direction from move file or NONE, if no more moves are available.
     */
    @Override
    public Direction nextMove() {
        if (currentMoveIndex < moves.size()) {
            return moves.get(currentMoveIndex++);
        }
        return Direction.NONE;
    }
}
