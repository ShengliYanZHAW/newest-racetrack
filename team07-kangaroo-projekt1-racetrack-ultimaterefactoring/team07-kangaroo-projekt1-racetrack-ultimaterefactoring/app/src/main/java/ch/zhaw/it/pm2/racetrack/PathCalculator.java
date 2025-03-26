package ch.zhaw.it.pm2.racetrack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Utility class for calculating paths between positions on a grid.
 * Implements Bresenham's line algorithm for path calculation to determine
 * all grid positions along a line from a start position to an end position.
 */
public class PathCalculator {
    
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private PathCalculator() {
        throw new AssertionError("Utility class should not be instantiated");
    }
    
    /**
     * Calculates a path between two positions using Bresenham's algorithm.
     * The path includes both the starting and ending positions.
     *
     * @param start the starting position
     * @param end the ending position
     * @return a list of positions representing the path from start to end, inclusive
     * @throws NullPointerException if start or end is null
     */
    public static List<PositionVector> calculatePath(PositionVector start, PositionVector end) {
        Objects.requireNonNull(start, "Start position must not be null");
        Objects.requireNonNull(end, "End position must not be null");
        
        List<PositionVector> path = new ArrayList<>();
        int x = start.getX();
        int y = start.getY();
        path.add(new PositionVector(x, y));

        // If start and end are the same position, return a path with just that position
        if (start.equals(end)) {
            return path;
        }

        int diffX = end.getX() - x;
        int diffY = end.getY() - y;
        
        // Calculate path parameters using Bresenham's algorithm
        PathParameters params = calculatePathParameters(diffX, diffY);
        
        int error = params.distanceFastAxis / 2;  // Initialize the error term
        
        // Loop through each position along the path
        for (int step = 0; step < params.distanceFastAxis; step++) {
            error -= params.distanceSlowAxis;  // Update error term
            
            if (error < 0) {
                // If error becomes negative, take a diagonal step
                error += params.distanceFastAxis;  // Correct error value
                x += params.diagonalStepX;
                y += params.diagonalStepY;
            } else {
                // Otherwise, take a parallel step along the fast axis
                x += params.parallelStepX;
                y += params.parallelStepY;
            }
            
            path.add(new PositionVector(x, y));
        }
        
        return path;
    }
    
    /**
     * Calculates the parameters needed for Bresenham's algorithm based on the displacement vector.
     *
     * @param diffX the x-component of the displacement vector
     * @param diffY the y-component of the displacement vector
     * @return a PathParameters object containing the calculated parameters
     */
    private static PathParameters calculatePathParameters(int diffX, int diffY) {
        int distX = Math.abs(diffX);  // Absolute distance in x-direction
        int distY = Math.abs(diffY);  // Absolute distance in y-direction
        int dirX = Integer.signum(diffX);  // Direction of movement in x (-1, 0, or 1)
        int dirY = Integer.signum(diffY);  // Direction of movement in y (-1, 0, or 1)
        
        int parallelStepX, parallelStepY;  // Step values for moving parallel to the fast axis
        int diagonalStepX, diagonalStepY;  // Step values for moving diagonally
        int distanceSlowAxis, distanceFastAxis;  // Distance along the slow and fast axes
        
        if (distX > distY) {
            // x-axis is the 'fast' direction
            parallelStepX = dirX;  // Parallel step only moves in x direction
            parallelStepY = 0;
            diagonalStepX = dirX;  // Diagonal step moves in both directions
            diagonalStepY = dirY;
            distanceSlowAxis = distY;
            distanceFastAxis = distX;
        } else {
            // y-axis is the 'fast' direction
            parallelStepX = 0;  // Parallel step only moves in y direction
            parallelStepY = dirY;
            diagonalStepX = dirX;  // Diagonal step moves in both directions
            diagonalStepY = dirY;
            distanceSlowAxis = distX;
            distanceFastAxis = distY;
        }
        
        return new PathParameters(
            parallelStepX, parallelStepY,
            diagonalStepX, diagonalStepY,
            distanceSlowAxis, distanceFastAxis
        );
    }
    
    /**
     * Inner class to hold the step parameters needed for Bresenham's algorithm.
     */
    private static class PathParameters {
        final int parallelStepX;
        final int parallelStepY;
        final int diagonalStepX;
        final int diagonalStepY;
        final int distanceSlowAxis;
        final int distanceFastAxis;

        /**
         * Constructs a new PathParameters with the specified values.
         *
         * @param parallelStepX x-component of the parallel step
         * @param parallelStepY y-component of the parallel step
         * @param diagonalStepX x-component of the diagonal step
         * @param diagonalStepY y-component of the diagonal step
         * @param distanceSlowAxis distance along the slow axis
         * @param distanceFastAxis distance along the fast axis
         */
        PathParameters(int parallelStepX, int parallelStepY,
                      int diagonalStepX, int diagonalStepY,
                      int distanceSlowAxis, int distanceFastAxis) {
            this.parallelStepX = parallelStepX;
            this.parallelStepY = parallelStepY;
            this.diagonalStepX = diagonalStepX;
            this.diagonalStepY = diagonalStepY;
            this.distanceSlowAxis = distanceSlowAxis;
            this.distanceFastAxis = distanceFastAxis;
        }
    }
}