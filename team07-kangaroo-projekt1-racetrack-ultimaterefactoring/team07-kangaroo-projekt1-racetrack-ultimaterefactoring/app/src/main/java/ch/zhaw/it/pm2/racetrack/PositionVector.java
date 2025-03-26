package ch.zhaw.it.pm2.racetrack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an immutable position or velocity vector on a track grid.
 * All vector operations (e.g. add, subtract) return a new PositionVector instance.
 */
public final class PositionVector {
    private static final String POSITION_VECTOR_FORMAT = "(X:%d, Y:%d)";
    // Updated regex to support optional negative sign for both x and y values.
    private static final Pattern POSITION_VECTOR_PATTERN =
        Pattern.compile("\\([Xx]:(?<x>-?\\d+)\\s*,\\s*[Yy]:(?<y>-?\\d+)\\)");

    private final int x;
    private final int y;

    /**
     * Constructs a new PositionVector with the specified coordinates.
     *
     * @param x the horizontal value (position or velocity).
     * @param y the vertical value (position or velocity).
     */
    public PositionVector(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Copy constructor to create a new PositionVector from an existing one.
     *
     * @param other the PositionVector to copy.
     */
    public PositionVector(final PositionVector other) {
        this.x = other.getX();
        this.y = other.getY();
    }

    /**
     * Returns the horizontal value (position or velocity).
     *
     * @return the horizontal component.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Returns the vertical value (position or velocity).
     *
     * @return the vertical component.
     */
    public int getY() {
        return this.y;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PositionVector)) {
            return false;
        }
        PositionVector otherVector = (PositionVector) other;
        return this.x == otherVector.x && this.y == otherVector.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public String toString() {
        return POSITION_VECTOR_FORMAT.formatted(this.x, this.y);
    }

    /**
     * Returns a new PositionVector representing the sum of this vector and the specified vector.
     *
     * @param vector the vector to add.
     * @return a new PositionVector containing the result of the addition.
     */
    public PositionVector add(final PositionVector vector) {
        return new PositionVector(this.x + vector.getX(), this.y + vector.getY());
    }

    /**
     * Returns a new PositionVector representing the difference between this vector and the specified vector.
     *
     * @param vector the vector to subtract.
     * @return a new PositionVector containing the result of the subtraction.
     */
    public PositionVector subtract(final PositionVector vector) {
        return new PositionVector(this.x - vector.getX(), this.y - vector.getY());
    }

    /**
     * Returns a new PositionVector with the absolute values of this vector's components.
     *
     * @return a new PositionVector with absolute coordinate values.
     */
    public PositionVector abs() {
        return new PositionVector(Math.abs(this.x), Math.abs(this.y));
    }

    /**
     * Returns a new PositionVector representing the signum of this vector.
     * Each coordinate is mapped to -1, 0, or 1 indicating its sign.
     *
     * @return a new PositionVector containing the signum values of the coordinates.
     */
    public PositionVector signum() {
        return new PositionVector(Integer.signum(this.x), Integer.signum(this.y));
    }

    /**
     * Calculates the scalar (dot) product of this vector with the specified vector.
     *
     * @param vector the vector to calculate the scalar product with.
     * @return the scalar product of the two vectors.
     */
    public int scalarProduct(final PositionVector vector) {
        return this.x * vector.getX() + this.y * vector.getY();
    }

    /**
     * Parses a PositionVector from a string in the format (X:1, Y:2).
     * The string must match the expected pattern; otherwise, an IllegalArgumentException is thrown.
     *
     * @param positionString the string to parse.
     * @return a PositionVector parsed from the string.
     * @throws IllegalArgumentException if the string does not match the expected pattern.
     */
    public static PositionVector ofString(String positionString) {
        Matcher matcher = POSITION_VECTOR_PATTERN.matcher(positionString);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("String does not match position vector pattern: " + positionString);
        }
        int x = Integer.parseInt(matcher.group("x"));
        int y = Integer.parseInt(matcher.group("y"));
        return new PositionVector(x, y);
    }
}
