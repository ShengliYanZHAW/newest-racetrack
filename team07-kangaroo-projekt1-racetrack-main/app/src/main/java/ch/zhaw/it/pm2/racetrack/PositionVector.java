package ch.zhaw.it.pm2.racetrack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Holds a position (vector to x,y-position of the car on the track grid)
 * or a velocity vector (velocity x,y-components of a car).<br/>
 * PositionVectors are immutable, which means they cannot be modified.<br/>
 * Vector operations like {@link #add(PositionVector)} and {@link #subtract(PositionVector)}
 * return a new PositionVector containing the result.
 */
public final class PositionVector {
    /** Format to print the position vector. */
    private static final String POSITION_VECTOR_FORMAT = "(X:%d, Y:%d)";

    /** Pattern to parse a position vector from string format. */
    private static final Pattern POSITION_VECTOR_PATTERN =
        Pattern.compile("\\([Xx]:(?<x>\\d+)\\s*,\\s*[Yy]:(?<y>\\d+)\\)");

    /** horizontal value (position / velocity). */
    private final int x;

    /** vertical value (position / velocity). */
    private final int y;

    /**
     * Base constructor, initializing the position using coordinates or a velocity vector.
     * @param x horizontal value (position or velocity)
     * @param y vertical value (position or velocity)
     */
    public PositionVector(final int x, final int y) {
        this.y = y;
        this.x = x;
    }

    /**
     * Copy constructor, copying the values from another PositionVector.
     * @param other position vector to copy from
     */
    public PositionVector(final PositionVector other) {
        this.x = other.getX();
        this.y = other.getY();
    }

    /**
     * Get the horizontal value (position or velocity).
     * @return the horizontal value (position or velocity)
     */
    public int getX() {
        return this.x;
    }

    /**
     * Get the vertical value (position or velocity).
     * @return vertical value (position or velocity)
     */
    public int getY() {
        return this.y;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof final PositionVector otherVector)) return false;
        return this.y == otherVector.getY() && this.x == otherVector.getX();
    }

    @Override
    public int hashCode() {
        return this.x ^ this.y;
    }

    @Override
    public String toString() {
        return  POSITION_VECTOR_FORMAT.formatted(this.x, this.y);
    }

    /**
     * Calculates the vector addition of the current vector with the given vector, e.g.
     * <ul>
     *   <li>if a velocity vector is added to a position, the next position is returned</li>
     *   <li>if a direction vector is added to a velocity, the new velocity is returned</li>
     * </ul>
     * The vector values are not modified, but a new Vector containing the result is returned.
     *
     * @param vector a position or velocity vector to add
     * @return A new PositionVector holding the result of the addition.
     */
    public PositionVector add(final PositionVector vector) {
        return new PositionVector(this.getX() + vector.getX(), this.getY() + vector.getY());
    }

    /**
     * Calculates the vector difference of the current vector to the given vector,
     * i.e., subtracts the given from the current vectors coordinates,
     * e.g., car position and/or velocity vector <br>
     * The vector values are not modified, but a new Vector containing the result is returned.
     * @param vector A position or velocity vector to subtract
     * @return A new PositionVector holding the result of the subtraction.
     */
    public PositionVector subtract(final PositionVector vector) {
        return new PositionVector(this.getX() - vector.getX(), this.getY() - vector.getY());
    }

    /**
     * Calculates the absolute coordinates (positive values) of a PositionVector.
     *
     * @return a new PositionVector containing the absolute values of the current vectors coordinates.
     */
    public PositionVector abs() {
        return new PositionVector(Math.abs(this.getX()), Math.abs(this.getY()));
    }

    /**
     * Calculates the coordinates signum of a PositionVector.<br>
     * Returned values for each coordinate are -1, 0 or 1, which represent the direction the vector is pointing to:<br>
     * x = -1 -> LEFT, x = 0 -> NONE, x = 1 -> RIGHT<br>
     * y = -1 -> UP, y = 0 -> NONE, y = 1 -> DOWN
     *
     * @return a new PositionVector containing the signum of the current vector.
     */
    public PositionVector signum() {
        return new PositionVector(Integer.signum(this.getX()), Integer.signum(this.getY()));
    }

    /**
     * Calculates the scalar product (dot product) of the current vector with the given vector.
     *
     * @param vector to calculate the scalar product with
     * @return the scalar product of the two vectors
     */
    public int scalarProduct(final PositionVector vector) {
        return this.getX() * vector.getX() + this.getY() * vector.getY();
    }

    /**
     * Parses a position vector from a string in the format (X:1, Y:2).
     * This is the format produced by {@link #toString()}.
     *
     * @param positionString string to parse
     * @return parsed position vector
     */
    public static PositionVector ofString(String positionString) {
        Matcher matcher = POSITION_VECTOR_PATTERN.matcher(positionString);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("String does not match position vector pattern: " + positionString);
        }
        return new PositionVector(Integer.parseInt(matcher.group("x")), Integer.parseInt(matcher.group("y")));
    }
}
