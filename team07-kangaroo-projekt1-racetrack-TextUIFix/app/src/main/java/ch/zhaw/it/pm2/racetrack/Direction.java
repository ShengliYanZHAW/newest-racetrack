package ch.zhaw.it.pm2.racetrack;

import java.util.Objects;

/**
 * Enum representing a direction on the track grid.
 * Also representing the possible acceleration values.
 */
public enum Direction {
    /** Direction Down and left -> PositionVector(-1,1). */
    DOWN_LEFT(new PositionVector(-1, 1)),
    /** Direction down -> PositionVector(0,1). */
    DOWN(new PositionVector(0, 1)),
    /** Direction down and right -> PositionVector(1,1). */
    DOWN_RIGHT(new PositionVector(1, 1)),
    /** Direction left -> PositionVector(-1,0). */
    LEFT(new PositionVector(-1, 0)),
    /** No direction -> PositionVector(0,0) */
    NONE(new PositionVector(0, 0)),
    /** Direction right -> PositionVector(1,0). */
    RIGHT(new PositionVector(1, 0)),
    /** Direction up and left -> PositionVector(-1,-1). */
    UP_LEFT(new PositionVector(-1, -1)),
    /** Direction up -> PositionVector(0,-1). */
    UP(new PositionVector(0, -1)),
    /** Up and right direction (1,-1). */
    UP_RIGHT(new PositionVector(1, -1));

    private final PositionVector vector;

    Direction(final PositionVector vector) {
        this.vector = vector;
    }

    /**
     * Get the {@link PositionVector} representing the direction velocity.
     * @return the PositionVector representing the direction velocity
     */
    public PositionVector getVector() {
        return vector;
    }

    /**
     * Determines the {@link Direction} the given {@link PositionVector} is pointing to. <br>
     * The given vector may be, for example, a velocity vector or a distance vector between two positions.
     * The direction is determined based on the quadrant the vector is pointing to (UP_RIGHT, UP_LEFT, DOWN_LEFT, DOWN_RIGHT),
     * the axis (UP, DOWN, LEFT, RIGHT) if one of the components is 0, or nothing (NONE) if both components are 0.
     *
     * @param vector PositionVector containing a vector of any length
     * @return The Direction the vector is pointing to
     * @throws NullPointerException if the vector is null
     */
    public static Direction ofVector(PositionVector vector) {
        PositionVector directionVector = Objects.requireNonNull(vector).signum();
        for (Direction direction : Direction.values()) {
            if (direction.getVector().equals(directionVector)) {
                return direction;
            }
        }
        return NONE; // should never happen because all directions are covered.
    }
}
