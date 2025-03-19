package ch.zhaw.it.pm2.racetrack;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Car class.
 *
 * Equivalence Classes:
 * - Constructor: verifies that a new car has the correct ID, the correct initial position,
 *   an initial velocity of (0,0), and is not crashed.
 * - nextPosition: verifies that with zero velocity the nextPosition equals the current position;
 *   and that after accelerating, nextPosition equals current position plus velocity without changing
 *   the current position (until move() is called).
 * - accelerate: verifies that the velocity is correctly updated based on the provided acceleration vector.
 * - move: verifies that move() updates the position by adding the current velocity, while the velocity remains unchanged.
 * - crash: verifies that invoking crash() marks the car as crashed and updates the car's position to the crash location.
 * - isCrashed: verifies that the car is not crashed initially and becomes crashed after calling crash().
 */
public class CarTest {

    /**
     * Equivalence Class: Valid constructor input.
     * Verifies that the created car has:
     * - the ID passed to the constructor,
     * - the correct initial position,
     * - an initial velocity of (0,0),
     * - and is not crashed.
     */
    @Test
    public void testConstructor() {
        Car car = new Car('A', new PositionVector(3, 4));
        assertEquals('A', car.getId(), "Car ID should match the one passed to constructor");
        assertEquals(new PositionVector(3, 4), car.getPosition(), "Initial position should match the one passed to constructor");
        assertEquals(new PositionVector(0, 0), car.getVelocity(), "Initial velocity should be (0,0)");
        assertFalse(car.isCrashed(), "Car should not be crashed upon creation");
    }

    /**
     * Equivalence Class: nextPosition method.
     * Verifies that:
     * - With a velocity of (0,0), nextPosition equals the current position.
     * - After applying accelerations, nextPosition equals the sum of the current position and the resulting velocity,
     *   without changing the current position (until move() is called).
     */
    @Test
    public void testNextPosition() {
        Car car = new Car('B', new PositionVector(0, 0));
        // Initial velocity (0,0)
        assertEquals(new PositionVector(0, 0), car.nextPosition(), "With zero velocity, nextPosition should equal current position");

        // Apply two accelerations to reach velocity (1, 2)
        car.accelerate(Direction.DOWN_RIGHT); // velocity becomes (1,1)
        car.accelerate(Direction.DOWN);       // velocity becomes (1,2)

        PositionVector expectedNext = new PositionVector(0, 0).add(new PositionVector(1, 2));
        assertEquals(expectedNext, car.nextPosition(), "nextPosition should be the sum of current position and velocity");
        // Verify that the current position remains unchanged
        assertEquals(new PositionVector(0, 0), car.getPosition(), "Car position should remain unchanged until move() is called");
    }

    /**
     * Equivalence Class: accelerate method.
     * Verifies that the velocity is updated correctly based on the acceleration vector.
     */
    @Test
    public void testAccelerate() {
        Car car = new Car('C', new PositionVector(5, 5));

        // Check initial velocity
        assertEquals(new PositionVector(0, 0), car.getVelocity(), "Initial velocity should be (0,0)");

        // Accelerate upward: velocity becomes (0,-1)
        car.accelerate(Direction.UP);
        assertEquals(new PositionVector(0, -1), car.getVelocity(), "Velocity should be updated correctly with upward acceleration");

        // Accelerate left: velocity becomes (-1,-1)
        car.accelerate(Direction.LEFT);
        assertEquals(new PositionVector(-1, -1), car.getVelocity(), "Velocity should be updated correctly with leftward acceleration");
    }

    /**
     * Equivalence Class: move method.
     * Verifies that move() updates the position by adding the current velocity,
     * while the velocity remains unchanged.
     */
    @Test
    public void testMove() {
        Car car = new Car('D', new PositionVector(2, 2));

        // Accelerate to achieve velocity (1,1)
        car.accelerate(Direction.DOWN_RIGHT);
        // Execute move
        car.move();
        // Position should now be (3,3) and velocity remains (1,1)
        assertEquals(new PositionVector(3, 3), car.getPosition(), "Position should be updated by adding velocity after move()");
        assertEquals(new PositionVector(1, 1), car.getVelocity(), "Velocity should remain unchanged after move()");
    }

    /**
     * Equivalence Class: crash method.
     * Verifies that:
     * - Upon crashing, the car is marked as crashed.
     * - The car's position is updated to the crash location.
     */
    @Test
    public void testCrash() {
        Car car = new Car('E', new PositionVector(10, 10));
        assertFalse(car.isCrashed(), "Car should not be crashed initially");

        // Crash at (12,12)
        PositionVector crashPos = new PositionVector(12, 12);
        car.crash(crashPos);

        assertTrue(car.isCrashed(), "Car should be marked as crashed after crash() is called");
        assertEquals(crashPos, car.getPosition(), "Car's position should be updated to the crash location");
    }

    /**
     * Equivalence Class: isCrashed method.
     * Verifies that the car is not crashed initially and becomes crashed after crash() is called.
     */
    @Test
    public void testIsCrashed() {
        Car car = new Car('F', new PositionVector(0, 0));
        assertFalse(car.isCrashed(), "Car should not be crashed initially");

        // Invoke crash and verify status
        car.crash(new PositionVector(2, 2));
        assertTrue(car.isCrashed(), "Car should be marked as crashed after crash() is called");
    }
}
