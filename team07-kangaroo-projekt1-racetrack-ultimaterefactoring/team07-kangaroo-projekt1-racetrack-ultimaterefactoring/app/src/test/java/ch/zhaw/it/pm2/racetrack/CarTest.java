package ch.zhaw.it.pm2.racetrack;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Test class for the Car class.
 * This class tests all public methods of the Car class, covering both valid and invalid inputs.
 */
public class CarTest {

    /**
     * Tests that getId() returns the correct car identifier.
     */
    @Test
    public void testGetId() {
        final PositionVector start = new PositionVector(5, 10);
        final Car car = new Car('A', start);
        assertEquals('A', car.getId(), "getId() should return the identifier 'A'");
    }

    /**
     * Tests that getPosition() returns the initial position.
     */
    @Test
    public void testGetPositionValid() {
        final PositionVector start = new PositionVector(3, 4);
        final Car car = new Car('B', start);
        assertEquals(start, car.getPosition(), "getPosition() should return the starting position (3,4)");
    }

    /**
     * Tests that constructing a Car with a null start position throws IllegalArgumentException.
     */
    @Test
    public void testGetPositionWithNullStart() {
        assertThrows(IllegalArgumentException.class, () -> new Car('C', null),
            "Constructing a Car with a null start position should throw IllegalArgumentException");
    }

    /**
     * Tests that getVelocity() initially returns (0,0).
     */
    @Test
    public void testGetVelocityInitial() {
        final PositionVector start = new PositionVector(2, 2);
        final Car car = new Car('D', start);
        assertEquals(new PositionVector(0, 0), car.getVelocity(), "Initial velocity should be (0,0)");
    }

    /**
     * Tests that getVelocity() returns updated velocity after acceleration.
     */
    @Test
    public void testGetVelocityAfterAcceleration() {
        final PositionVector start = new PositionVector(0, 0);
        final Car car = new Car('E', start);
        car.accelerate(Direction.UP);
        assertEquals(new PositionVector(0, -1), car.getVelocity(), "Velocity should be updated to (0,-1) after acceleration");
    }

    /**
     * Tests that nextPosition() returns the current position when velocity is zero.
     */
    @Test
    public void testNextPositionWithZeroVelocity() {
        final PositionVector start = new PositionVector(4, 4);
        final Car car = new Car('F', start);
        assertEquals(start, car.nextPosition(), "nextPosition() should equal current position when velocity is zero");
    }

    /**
     * Tests that nextPosition() returns the correct value when velocity is nonzero.
     */
    @Test
    public void testNextPositionWithNonzeroVelocity() {
        final PositionVector start = new PositionVector(1, 1);
        final Car car = new Car('G', start);
        car.accelerate(Direction.RIGHT);
        final PositionVector expected = start.add(new PositionVector(1, 0));
        assertEquals(expected, car.nextPosition(), "nextPosition() should return (1,1) + (1,0) = (2,1)");
    }

    /**
     * Tests that constructing a Car with a null start position throws IllegalArgumentException.
     */
    @Test
    public void testConstructorWithNullStartPosition() {
        assertThrows(IllegalArgumentException.class, () -> new Car('H', null),
            "Constructing a Car with a null start position should throw IllegalArgumentException");
    }

    /**
     * Tests that accelerate() with a valid direction updates the velocity correctly.
     */
    @Test
    public void testAccelerateValid() {
        final PositionVector start = new PositionVector(0, 0);
        final Car car = new Car('I', start);
        car.accelerate(Direction.DOWN);
        assertEquals(new PositionVector(0, 1), car.getVelocity(), "Velocity should be updated to (0,1) after accelerating DOWN");
    }

    /**
     * Tests that accelerate() with a null direction throws NullPointerException.
     */
    @Test
    public void testAccelerateNull() {
        final PositionVector start = new PositionVector(0, 0);
        final Car car = new Car('J', start);
        assertThrows(IllegalArgumentException.class, () -> car.accelerate(null),
            "accelerate() with null direction should throw IllegalArgumentException");
    }

    /**
     * Tests consecutive calls to accelerate() sum the acceleration vectors.
     */
    @Test
    public void testConsecutiveAccelerations() {
        final PositionVector start = new PositionVector(0, 0);
        final Car car = new Car('K', start);
        car.accelerate(Direction.UP);
        car.accelerate(Direction.RIGHT);
        assertEquals(new PositionVector(1, -1), car.getVelocity(),
            "Consecutive accelerations UP and RIGHT should result in velocity (1,-1)");
    }

    /**
     * Tests that move() correctly updates the position by adding the current velocity.
     */
    @Test
    public void testMoveNormal() {
        final PositionVector start = new PositionVector(2, 3);
        final Car car = new Car('L', start);
        car.accelerate(Direction.RIGHT);
        car.move();
        assertEquals(new PositionVector(3, 3), car.getPosition(),
            "move() should update position from (2,3) to (3,3) when velocity is (1,0)");
    }

    /**
     * Tests that crash() correctly marks the car as crashed and updates its position.
     */
    @Test
    public void testCrashValid() {
        final PositionVector start = new PositionVector(5, 5);
        final Car car = new Car('M', start);
        final PositionVector crashPos = new PositionVector(7, 7);
        car.crash(crashPos);
        assertTrue(car.isCrashed(), "After crash(), isCrashed() should return true");
        assertEquals(crashPos, car.getPosition(), "After crash(), position should be updated to the crash position");
    }

    /**
     * Tests that crash() with a null crash position throws IllegalArgumentException.
     */
    @Test
    public void testCrashWithNull() {
        final PositionVector start = new PositionVector(5, 5);
        final Car car = new Car('N', start);
        assertThrows(IllegalArgumentException.class, () -> car.crash(null),
            "crash() with null crash position should throw IllegalArgumentException");
    }

    /**
     * Tests that isCrashed() returns false for a newly constructed car.
     */
    @Test
    public void testIsCrashedInitially() {
        final Car car = new Car('O', new PositionVector(3, 3));
        assertFalse(car.isCrashed(), "Newly constructed car should not be crashed");
    }

    /**
     * Tests that isCrashed() returns true after the car is crashed.
     */
    @Test
    public void testIsCrashedAfterCrash() {
        final Car car = new Car('P', new PositionVector(4, 4));
        car.crash(new PositionVector(6, 6));
        assertTrue(car.isCrashed(), "Car should be crashed after crash() is called");
    }
}
