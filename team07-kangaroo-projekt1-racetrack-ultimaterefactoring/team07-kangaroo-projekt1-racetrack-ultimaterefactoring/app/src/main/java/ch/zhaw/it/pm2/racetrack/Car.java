package ch.zhaw.it.pm2.racetrack;

import ch.zhaw.it.pm2.racetrack.given.CarSpecification;

/**
 * Represents a car on the racetrack.
 * This class uses PositionVector to store the current position and velocity.
 * Each car has an identifier used to represent it on the track.
 * The crash state is maintained and cannot be reverted.
 * Velocity is updated by applying an acceleration vector.
 * The car can calculate the endpoint of its next move and move to it.
 */
public class Car implements CarSpecification {

    private static final PositionVector INITIAL_VELOCITY = new PositionVector(0, 0);

    private final char id;
    private PositionVector position;
    private PositionVector velocity;
    private boolean crashed;
    private int moveCount = 0;

    /**
     * Constructs a new Car with the given identifier and starting position.
     * @param id unique car identifier
     * @param startPosition initial position of the car; must not be null
     * @throws IllegalArgumentException if startPosition is null
     */
    public Car(char id, PositionVector startPosition) {
        if (startPosition == null) {
            throw new IllegalArgumentException("startPosition must not be null");
        }
        this.id = id;
        this.position = startPosition;
        this.velocity = INITIAL_VELOCITY;
        this.crashed = false;
    }

    /**
     * Returns the car identifier.
     * @return the identifier character
     */
    @Override
    public char getId() {
        return id;
    }

    /**
     * Returns the current position of the car.
     * @return the current position
     */
    @Override
    public PositionVector getPosition() {
        return position;
    }

    /**
     * Returns the current velocity of the car.
     * @return the current velocity vector
     */
    @Override
    public PositionVector getVelocity() {
        return velocity;
    }

    /**
     * Calculates the expected position after the next move without updating the current position.
     * @return the expected position after the next move
     */
    @Override
    public PositionVector nextPosition() {
        return position.add(velocity);
    }

    /**
     * Adds the given acceleration vector to the car's velocity.
     * Only acceleration values of -1, 0, or 1 per axis are allowed.
     * @param acceleration a Direction representing the acceleration vector; must not be null
     * @throws IllegalArgumentException if acceleration is null
     */
    @Override
    public void accelerate(Direction acceleration) {
        if (acceleration == null) {
            throw new IllegalArgumentException("acceleration must not be null");
        }
        this.velocity = velocity.add(acceleration.getVector());
    }

    /**
     * Updates the car's position based on its current velocity and increments the move count.
     */
    @Override
    public void move() {
        position = position.add(velocity);
        moveCount++;
    }

    /**
     * Returns the total number of moves executed by this car.
     * @return the move count
     */
    public int getMoveCount() {
        return moveCount;
    }

    /**
     * Marks the car as crashed at the given position.
     * @param crashPosition the position where the car crashed; must not be null
     * @throws IllegalArgumentException if crashPosition is null
     */
    @Override
    public void crash(PositionVector crashPosition) {
        if (crashPosition == null) {
            throw new IllegalArgumentException("crashPosition must not be null");
        }
        position = crashPosition;
        crashed = true;
    }

    /**
     * Indicates whether the car has crashed.
     * @return true if the car has crashed, false otherwise
     */
    @Override
    public boolean isCrashed() {
        return crashed;
    }
}
