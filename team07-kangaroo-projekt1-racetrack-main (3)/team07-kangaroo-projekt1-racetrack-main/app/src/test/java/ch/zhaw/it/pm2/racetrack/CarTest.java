package ch.zhaw.it.pm2.racetrack;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Car class.
 *
 * This class tests all public methods of the Car class, covering both valid and invalid inputs.
 *
 * Methods and their equivalence classes:
 *
 * 1. getId():
 *    - Precondition: Car is constructed with a valid id.
 *      Input: (none)
 *      Expected: Returns the id (e.g., 'A')
 *      Comments: Normal case.
 *
 * 2. getPosition():
 *    - Precondition: Car is constructed with a valid starting position.
 *      Input: (none)
 *      Expected: Returns the starting position.
 *      Comments: Also tests the behavior if constructed with a null position.
 *
 * 3. getVelocity():
 *    - Precondition: Car is newly constructed.
 *      Input: (none)
 *      Expected: Returns (0,0)
 *      Comments: Normal initial condition; after acceleration, returns updated velocity.
 *
 * 4. nextPosition():
 *    - Precondition: Car is constructed with valid position and velocity.
 *      Input: (none)
 *      Expected: Returns position + velocity.
 *      Comments: If velocity is zero, returns current position; if position is null, throws exception.
 *
 * 5. accelerate(Direction acceleration):
 *    - Precondition: Car is constructed normally.
 *      Input: Valid Direction (e.g., UP, RIGHT, etc.)
 *      Expected: Velocity is updated by adding the acceleration vector.
 *      Comments: Passing null should throw NullPointerException; consecutive calls add up.
 *
 * 6. move():
 *    - Precondition: Car has a valid position and a nonzero velocity.
 *      Input: (none)
 *      Expected: Position becomes position + velocity.
 *      Comments: Normal movement.
 *
 * 7. crash(PositionVector crashPosition):
 *    - Precondition: Car is constructed normally.
 *      Input: A valid crash position (e.g., (4,4))
 *      Expected: Car's position is updated to crashPosition and isCrashed() returns true.
 *      Comments: Passing null sets position to null and marks the car as crashed.
 *
 * 8. isCrashed():
 *    - Precondition: Car is constructed normally.
 *      Input: (none)
 *      Expected: Initially returns false; after crash() returns true.
 */
public class CarTest {

    // ----------------- Tests for getId() -----------------

    /**
     * Tests that getId() returns the correct car identifier.
     */
    @Test
    public void testGetId() {
        PositionVector start = new PositionVector(5, 10);
        Car car = new Car('A', start);
        assertEquals('A', car.getId(), "getId() should return the identifier 'A'");
    }

    // ----------------- Tests for getPosition() -----------------

    /**
     * Tests that getPosition() returns the initial position.
     */
    @Test
    public void testGetPositionValid() {
        PositionVector start = new PositionVector(3, 4);
        Car car = new Car('B', start);
        assertEquals(start, car.getPosition(), "getPosition() should return the starting position (3,4)");
    }

    /**
     * Tests that getPosition() returns null if the car was constructed with a null position.
     */
    @Test
    public void testGetPositionNull() {
        Car car = new Car('C', null);
        assertNull(car.getPosition(), "getPosition() should return null if constructed with null");
    }

    // ----------------- Tests for getVelocity() -----------------

    /**
     * Tests that getVelocity() initially returns (0,0).
     */
    @Test
    public void testGetVelocityInitial() {
        PositionVector start = new PositionVector(2, 2);
        Car car = new Car('D', start);
        assertEquals(new PositionVector(0, 0), car.getVelocity(), "Initial velocity should be (0,0)");
    }

    /**
     * Tests that getVelocity() returns updated velocity after acceleration.
     */
    @Test
    public void testGetVelocityAfterAcceleration() {
        PositionVector start = new PositionVector(0, 0);
        Car car = new Car('E', start);
        // Assume Direction.UP has vector (0, -1)
        car.accelerate(Direction.UP);
        assertEquals(new PositionVector(0, -1), car.getVelocity(), "Velocity should be updated to (0,-1) after acceleration");
    }

    // ----------------- Tests for nextPosition() -----------------

    /**
     * Tests that nextPosition() returns the current position when velocity is zero.
     */
    @Test
    public void testNextPositionWithZeroVelocity() {
        PositionVector start = new PositionVector(4, 4);
        Car car = new Car('F', start);
        assertEquals(start, car.nextPosition(), "nextPosition() should equal current position when velocity is zero");
    }

    /**
     * Tests that nextPosition() returns the correct value when velocity is nonzero.
     */
    @Test
    public void testNextPositionWithNonzeroVelocity() {
        PositionVector start = new PositionVector(1, 1);
        Car car = new Car('G', start);
        // Accelerate with Direction.RIGHT (assume vector (1,0))
        car.accelerate(Direction.RIGHT);
        PositionVector expected = start.add(new PositionVector(1, 0));
        assertEquals(expected, car.nextPosition(), "nextPosition() should return (1,1) + (1,0) = (2,1)");
    }

    /**
     * Tests that nextPosition() throws NullPointerException when position is null.
     */
    @Test
    public void testNextPositionWithNullPosition() {
        Car car = new Car('H', null);
        assertThrows(NullPointerException.class, () -> car.nextPosition(),
            "nextPosition() should throw NullPointerException when position is null");
    }

    // ----------------- Tests for accelerate(Direction) -----------------

    /**
     * Tests that accelerate() with a valid direction updates the velocity correctly.
     */
    @Test
    public void testAccelerateValid() {
        PositionVector start = new PositionVector(0, 0);
        Car car = new Car('I', start);
        // Assume Direction.DOWN has vector (0,1)
        car.accelerate(Direction.DOWN);
        assertEquals(new PositionVector(0, 1), car.getVelocity(), "Velocity should be updated to (0,1) after accelerating DOWN");
    }

    /**
     * Tests that accelerate() with a null direction throws NullPointerException.
     */
    @Test
    public void testAccelerateNull() {
        PositionVector start = new PositionVector(0, 0);
        Car car = new Car('J', start);
        assertThrows(NullPointerException.class, () -> car.accelerate(null),
            "accelerate() with null direction should throw NullPointerException");
    }

    /**
     * Tests consecutive calls to accelerate() sum the acceleration vectors.
     */
    @Test
    public void testConsecutiveAccelerations() {
        PositionVector start = new PositionVector(0, 0);
        Car car = new Car('K', start);
        // Accelerate UP (0,-1) then RIGHT (1,0)
        car.accelerate(Direction.UP);
        car.accelerate(Direction.RIGHT);
        assertEquals(new PositionVector(1, -1), car.getVelocity(),
            "Consecutive accelerations UP and RIGHT should result in velocity (1,-1)");
    }

    // ----------------- Tests for move() -----------------

    /**
     * Tests that move() correctly updates the position by adding the current velocity.
     */
    @Test
    public void testMoveNormal() {
        PositionVector start = new PositionVector(2, 3);
        Car car = new Car('L', start);
        // Accelerate RIGHT (1,0)
        car.accelerate(Direction.RIGHT);
        car.move();
        assertEquals(new PositionVector(3, 3), car.getPosition(),
            "move() should update position from (2,3) to (3,3) when velocity is (1,0)");
    }

    // ----------------- Tests for crash(PositionVector) -----------------

    /**
     * Tests that crash() correctly marks the car as crashed and updates its position.
     */
    @Test
    public void testCrashValid() {
        PositionVector start = new PositionVector(5, 5);
        Car car = new Car('M', start);
        PositionVector crashPos = new PositionVector(7, 7);
        car.crash(crashPos);
        assertTrue(car.isCrashed(), "After crash(), isCrashed() should return true");
        assertEquals(crashPos, car.getPosition(), "After crash(), position should be updated to the crash position");
    }

    /**
     * Tests that crash() with a null crash position sets the position to null and marks the car as crashed.
     */
    @Test
    public void testCrashWithNull() {
        PositionVector start = new PositionVector(5, 5);
        Car car = new Car('N', start);
        car.crash(null);
        assertTrue(car.isCrashed(), "After crash(null), isCrashed() should return true");
        assertNull(car.getPosition(), "After crash(null), getPosition() should return null");
    }

    // ----------------- Tests for isCrashed() -----------------

    /**
     * Tests that isCrashed() returns false for a newly constructed car.
     */
    @Test
    public void testIsCrashedInitially() {
        Car car = new Car('O', new PositionVector(3, 3));
        assertFalse(car.isCrashed(), "Newly constructed car should not be crashed");
    }

    /**
     * Tests that isCrashed() returns true after the car is crashed.
     */
    @Test
    public void testIsCrashedAfterCrash() {
        Car car = new Car('P', new PositionVector(4, 4));
        car.crash(new PositionVector(6, 6));
        assertTrue(car.isCrashed(), "Car should be crashed after crash() is called");
    }
}
