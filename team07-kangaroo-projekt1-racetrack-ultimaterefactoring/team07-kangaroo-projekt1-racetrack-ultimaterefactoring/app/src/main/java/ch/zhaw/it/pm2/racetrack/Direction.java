package ch.zhaw.it.pm2.racetrack;

import java.util.Objects;

/**
 * Represents a direction on the track grid along with its corresponding acceleration vector.
 */
public enum Direction {
    DOWN_LEFT(new PositionVector(-1, 1)),
    DOWN(new PositionVector(0, 1)),
    DOWN_RIGHT(new PositionVector(1, 1)),
    LEFT(new PositionVector(-1, 0)),
    NONE(new PositionVector(0, 0)),
    RIGHT(new PositionVector(1, 0)),
    UP_LEFT(new PositionVector(-1, -1)),
    UP(new PositionVector(0, -1)),
    UP_RIGHT(new PositionVector(1, -1));

    private final PositionVector vector;

    Direction(final PositionVector vector) {
        this.vector = vector;
    }

    /**
     * Returns the PositionVector representing the direction.
     *
     * @return the PositionVector associated with this direction.
     */
    public PositionVector getVector() {
        return vector;
    }

    /**
     * Determines the Direction corresponding to the given PositionVector.
     * The provided vector may be, for example, a velocity or displacement vector.
     * The direction is determined based on the quadrant the vector points to or the axis
     * if one of the components is zero. If no matching Direction is found, an IllegalStateException is thrown.
     *
     * @param vector the PositionVector to evaluate.
     * @return the Direction corresponding to the provided vector.
     * @throws NullPointerException if the vector is null.
     * @throws IllegalStateException if no matching direction is found.
     */
    public static Direction ofVector(PositionVector vector) {
        PositionVector directionVector = Objects.requireNonNull(vector, "Input vector must not be null").signum();
        for (Direction direction : Direction.values()) {
            if (direction.getVector().equals(directionVector)) {
                return direction;
            }
        }
        throw new IllegalStateException("No matching direction found for vector: " + directionVector);
    }
}
