package ch.zhaw.it.pm2.racetrack.strategy;

import ch.zhaw.it.pm2.racetrack.Car;
import ch.zhaw.it.pm2.racetrack.Direction;
import ch.zhaw.it.pm2.racetrack.PositionVector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the {@code PathFollowerStrategy}.
 */
public class PathFollowerStrategyTest {

    /**
     * Dummy implementation of {@code Car} for testing purposes.
     * This class extends {@code Car} and overrides the position and velocity methods.
     */
    private static class DummyCar extends Car {
        private PositionVector position;
        private PositionVector velocity;

        /**
         * Constructs a new DummyCar with the given position and velocity.
         *
         * @param position the starting position of the car
         * @param velocity the starting velocity of the car
         */
        public DummyCar(PositionVector position, PositionVector velocity) {
            super('D', position);
            this.position = position;
            this.velocity = velocity;
        }

        /**
         * Returns the current position of the dummy car.
         *
         * @return the current position
         */
        @Override
        public PositionVector getPosition() {
            return position;
        }

        /**
         * Returns the current velocity of the dummy car.
         *
         * @return the current velocity vector
         */
        @Override
        public PositionVector getVelocity() {
            return velocity;
        }

        /**
         * Sets the current position of the dummy car.
         *
         * @param position the new position
         */
        public void setPosition(PositionVector position) {
            this.position = position;
        }

        /**
         * Sets the current velocity of the dummy car.
         *
         * @param velocity the new velocity vector
         */
        public void setVelocity(PositionVector velocity) {
            this.velocity = velocity;
        }
    }

    /**
     * Tests the constructor with a valid waypoint file and a valid car.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws IOException if an I/O error occurs
     */
    @Test
    void testConstructorValid(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("waypoints.txt");
        Files.write(filePath, List.of("(X:5, Y:3)"));
        DummyCar car = new DummyCar(PositionVector.ofString("(X:0, Y:0)"), PositionVector.ofString("(X:0, Y:0)"));
        PathFollowerStrategy strategy = new PathFollowerStrategy(filePath.toFile(), car);
        assertNotNull(strategy, "The instance should be created correctly.");
    }

    /**
     * Tests the constructor with a null waypoint file.
     */
    @Test
    void testConstructorNullFile() {
        DummyCar car = new DummyCar(PositionVector.ofString("(X:0, Y:0)"), PositionVector.ofString("(X:0, Y:0)"));
        assertThrows(IllegalArgumentException.class, () ->
            new PathFollowerStrategy(null, car), "Null file should cause IllegalArgumentException.");
    }

    /**
     * Tests the constructor with a null car.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws IOException if an I/O error occurs
     */
    @Test
    void testConstructorNullCar(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("waypoints.txt");
        Files.write(filePath, List.of("(X:5, Y:3)"));
        assertThrows(IllegalArgumentException.class, () ->
            new PathFollowerStrategy(filePath.toFile(), null), "Null car should cause IllegalArgumentException.");
    }

    /**
     * Tests the constructor with an empty file (or a file containing only blank lines).
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws IOException if an I/O error occurs
     */
    @Test
    void testConstructorEmptyFile(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("empty.txt");
        Files.write(filePath, List.of("   ", ""));
        DummyCar car = new DummyCar(PositionVector.ofString("(X:0, Y:0)"), PositionVector.ofString("(X:0, Y:0)"));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new PathFollowerStrategy(filePath.toFile(), car),
            "An empty file should cause IllegalArgumentException.");
        assertTrue(ex.getMessage().contains(filePath.toFile().getAbsolutePath()));
    }

    /**
     * Tests the constructor with a file containing an improperly formatted waypoint.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws IOException if an I/O error occurs
     */
    @Test
    void testConstructorInvalidWaypoint(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("invalid.txt");
        Files.write(filePath, List.of("X:5, Y:3"));
        DummyCar car = new DummyCar(PositionVector.ofString("(X:0, Y:0)"), PositionVector.ofString("(X:0, Y:0)"));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new PathFollowerStrategy(filePath.toFile(), car),
            "An improperly formatted waypoint should cause IllegalArgumentException.");
        assertTrue(ex.getMessage().contains("Invalid waypoint format at line"));
    }

    /**
     * Tests the constructor with a non-existent waypoint file.
     */
    @Test
    void testConstructorFileNotFound() {
        File nonExistent = new File("nonexistent.txt");
        DummyCar car = new DummyCar(PositionVector.ofString("(X:0, Y:0)"), PositionVector.ofString("(X:0, Y:0)"));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new PathFollowerStrategy(nonExistent, car),
            "A non-existent file should cause IllegalArgumentException.");
        assertTrue(ex.getMessage().contains("Waypoint file not found"));
    }

    /**
     * Tests the private method loadWaypoints with a valid file containing multiple waypoints.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws Exception if reflection fails
     */
    @Test
    void testLoadWaypointsValid(@TempDir Path tempDir) throws Exception {
        Path filePath = tempDir.resolve("multi.txt");
        Files.write(filePath, List.of("(X:0, Y:0)", "(X:5, Y:5)"));
        DummyCar dummyCar = new DummyCar(PositionVector.ofString("(X:0, Y:0)"), PositionVector.ofString("(X:0, Y:0)"));
        PathFollowerStrategy strategy = new PathFollowerStrategy(filePath.toFile(), dummyCar);
        Method loadMethod = PathFollowerStrategy.class.getDeclaredMethod("loadWaypoints", File.class);
        loadMethod.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<PositionVector> waypoints = (List<PositionVector>) loadMethod.invoke(strategy, filePath.toFile());
        assertEquals(2, waypoints.size(), "The file should contain 2 valid waypoints.");
        assertEquals(PositionVector.ofString("(X:0, Y:0)"), waypoints.get(0));
        assertEquals(PositionVector.ofString("(X:5, Y:5)"), waypoints.get(1));
    }

    /**
     * Tests the private method loadWaypoints with a file containing blank lines between valid waypoints.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws Exception if reflection fails
     */
    @Test
    void testLoadWaypointsWithBlankLines(@TempDir Path tempDir) throws Exception {
        Path filePath = tempDir.resolve("blank.txt");
        Files.write(filePath, List.of("", "(X:1, Y:1)", "   ", "(X:2, Y:2)"));
        DummyCar dummyCar = new DummyCar(PositionVector.ofString("(X:0, Y:0)"), PositionVector.ofString("(X:0, Y:0)"));
        PathFollowerStrategy strategy = new PathFollowerStrategy(filePath.toFile(), dummyCar);
        Method loadMethod = PathFollowerStrategy.class.getDeclaredMethod("loadWaypoints", File.class);
        loadMethod.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<PositionVector> waypoints = (List<PositionVector>) loadMethod.invoke(strategy, filePath.toFile());
        assertEquals(2, waypoints.size(), "Blank lines should be ignored.");
    }

    /**
     * Tests the private method loadWaypoints with a file containing an invalid waypoint line.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws Exception if reflection fails
     */
    @Test
    void testLoadWaypointsInvalidLine(@TempDir Path tempDir) throws Exception {
        Path filePath = tempDir.resolve("invalidLine.txt");
        Files.write(filePath, List.of("(X:0, Y:0)", "X:5, Y:5"));
        DummyCar dummyCar = new DummyCar(PositionVector.ofString("(X:0, Y:0)"), PositionVector.ofString("(X:0, Y:0)"));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new PathFollowerStrategy(filePath.toFile(), dummyCar),
            "An invalid formatted line should cause IllegalArgumentException.");
        assertTrue(ex.getMessage().contains("Invalid waypoint format"));
    }


    /**
     * Tests the private method loadWaypoints with a non-existent file.
     *
     * @throws Exception if reflection fails
     */
    @Test
    void testLoadWaypointsFileNotFound() throws Exception {
        Path tempFile = Files.createTempFile("dummy", ".txt");
        Files.write(tempFile, List.of("(X:0, Y:0)"));
        DummyCar dummyCar = new DummyCar(PositionVector.ofString("(X:0, Y:0)"), PositionVector.ofString("(X:0, Y:0)"));
        PathFollowerStrategy strategy = new PathFollowerStrategy(tempFile.toFile(), dummyCar);
        Method loadMethod = PathFollowerStrategy.class.getDeclaredMethod("loadWaypoints", File.class);
        loadMethod.setAccessible(true);
        File nonExistent = new File("nonexistent_file.txt");
        InvocationTargetException ex = assertThrows(InvocationTargetException.class, () ->
                loadMethod.invoke(strategy, nonExistent),
            "A non-existent file should cause IllegalArgumentException.");
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }

    /**
     * Tests the private method parseWaypoint with a valid formatted string.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws Exception if reflection fails
     */
    @Test
    void testParseWaypointValid(@TempDir Path tempDir) throws Exception {
        Path filePath = tempDir.resolve("dummy.txt");
        Files.write(filePath, List.of("(X:5, Y:3)"));
        DummyCar dummyCar = new DummyCar(PositionVector.ofString("(X:0, Y:0)"), PositionVector.ofString("(X:0, Y:0)"));
        PathFollowerStrategy strategy = new PathFollowerStrategy(filePath.toFile(), dummyCar);
        Method parseMethod = PathFollowerStrategy.class.getDeclaredMethod("parseWaypoint", String.class);
        parseMethod.setAccessible(true);
        Object result = parseMethod.invoke(strategy, "(X:3, Y:-2)");
        assertEquals(PositionVector.ofString("(X:3, Y:-2)"), result, "The parsing should return the correct PositionVector.");
    }

    /**
     * Tests the private method parseWaypoint with a missing parentheses.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws Exception if reflection fails
     */
    @Test
    void testParseWaypointMissingParentheses(@TempDir Path tempDir) throws Exception {
        Path filePath = tempDir.resolve("dummy.txt");
        Files.write(filePath, List.of("(X:5, Y:3)"));
        DummyCar dummyCar = new DummyCar(PositionVector.ofString("(X:0, Y:0)"), PositionVector.ofString("(X:0, Y:0)"));
        PathFollowerStrategy strategy = new PathFollowerStrategy(filePath.toFile(), dummyCar);
        Method parseMethod = PathFollowerStrategy.class.getDeclaredMethod("parseWaypoint", String.class);
        parseMethod.setAccessible(true);
        InvocationTargetException ex = assertThrows(InvocationTargetException.class, () ->
                parseMethod.invoke(strategy, "X:3, Y:-2"),
            "Missing parentheses should cause IllegalArgumentException.");
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }

    /**
     * Tests the private method parseWaypoint with a missing value.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws Exception if reflection fails
     */
    @Test
    void testParseWaypointMissingValue(@TempDir Path tempDir) throws Exception {
        Path filePath = tempDir.resolve("dummy.txt");
        Files.write(filePath, List.of("(X:5, Y:3)"));
        DummyCar dummyCar = new DummyCar(PositionVector.ofString("(X:0, Y:0)"), PositionVector.ofString("(X:0, Y:0)"));
        PathFollowerStrategy strategy = new PathFollowerStrategy(filePath.toFile(), dummyCar);
        Method parseMethod = PathFollowerStrategy.class.getDeclaredMethod("parseWaypoint", String.class);
        parseMethod.setAccessible(true);
        InvocationTargetException ex = assertThrows(InvocationTargetException.class, () ->
                parseMethod.invoke(strategy, "(X:, Y:5)"),
            "A missing value should cause IllegalArgumentException.");
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }

    /**
     * Tests the private method parseWaypoint with an empty string.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws Exception if reflection fails
     */
    @Test
    void testParseWaypointEmptyString(@TempDir Path tempDir) throws Exception {
        Path filePath = tempDir.resolve("dummy.txt");
        Files.write(filePath, List.of("(X:5, Y:3)"));
        DummyCar dummyCar = new DummyCar(PositionVector.ofString("(X:0, Y:0)"), PositionVector.ofString("(X:0, Y:0)"));
        PathFollowerStrategy strategy = new PathFollowerStrategy(filePath.toFile(), dummyCar);
        Method parseMethod = PathFollowerStrategy.class.getDeclaredMethod("parseWaypoint", String.class);
        parseMethod.setAccessible(true);
        InvocationTargetException ex = assertThrows(InvocationTargetException.class, () ->
                parseMethod.invoke(strategy, ""),
            "An empty string should cause IllegalArgumentException.");
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }

    /**
     * Tests the {@code nextMove} method when the car has not yet reached the current waypoint.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws IOException if an I/O error occurs
     */
    @Test
    void testNextMoveNotReached(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("waypoints.txt");
        Files.write(filePath, List.of("(X:5, Y:3)"));
        DummyCar car = new DummyCar(PositionVector.ofString("(X:2, Y:1)"), PositionVector.ofString("(X:1, Y:0)"));
        PathFollowerStrategy strategy = new PathFollowerStrategy(filePath.toFile(), car);
        Direction move = strategy.nextMove();
        Direction expected = Direction.ofVector(new PositionVector(1, 1));
        assertEquals(expected, move, "The computed move should be correct.");
    }

    /**
     * Tests the {@code nextMove} method when the car has reached the current waypoint and there are more waypoints.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws IOException if an I/O error occurs
     */
    @Test
    void testNextMoveWaypointReached(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("waypoints.txt");
        Files.write(filePath, List.of("(X:2, Y:1)", "(X:5, Y:3)"));
        DummyCar car = new DummyCar(PositionVector.ofString("(X:2, Y:1)"), PositionVector.ofString("(X:0, Y:0)"));
        PathFollowerStrategy strategy = new PathFollowerStrategy(filePath.toFile(), car);
        Direction move = strategy.nextMove();
        Direction expected = Direction.ofVector(new PositionVector(1, 1));
        assertEquals(expected, move, "The move towards the next waypoint should be correct.");
    }

    /**
     * Tests the {@code nextMove} method when there are no remaining waypoints.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws IOException if an I/O error occurs
     */
    @Test
    void testNextMoveNoRemainingWaypoints(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("waypoints.txt");
        Files.write(filePath, List.of("(X:2, Y:1)"));
        DummyCar car = new DummyCar(PositionVector.ofString("(X:2, Y:1)"), PositionVector.ofString("(X:0, Y:0)"));
        PathFollowerStrategy strategy = new PathFollowerStrategy(filePath.toFile(), car);
        Direction move = strategy.nextMove();
        assertEquals(Direction.NONE, move, "When there are no remaining waypoints, Direction.NONE should be returned.");
    }

    /**
     * Tests the private method {@code clamp} with a value greater than 1.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws Exception if reflection fails
     */
    @Test
    void testClampGreaterThanOne(@TempDir Path tempDir) throws Exception {
        Path filePath = tempDir.resolve("dummy.txt");
        Files.write(filePath, List.of("(X:0, Y:0)"));
        DummyCar car = new DummyCar(PositionVector.ofString("(X:0, Y:0)"), PositionVector.ofString("(X:0, Y:0)"));
        PathFollowerStrategy strategy = new PathFollowerStrategy(filePath.toFile(), car);
        Method clampMethod = PathFollowerStrategy.class.getDeclaredMethod("clamp", int.class);
        clampMethod.setAccessible(true);
        Object result = clampMethod.invoke(strategy, 2);
        assertEquals(1, result, "Value 2 should be clamped to 1.");
    }

    /**
     * Tests the private method {@code clamp} with a value equal to 1.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws Exception if reflection fails
     */
    @Test
    void testClampEqualToOne(@TempDir Path tempDir) throws Exception {
        Path filePath = tempDir.resolve("dummy.txt");
        Files.write(filePath, List.of("(X:0, Y:0)"));
        DummyCar car = new DummyCar(PositionVector.ofString("(X:0, Y:0)"), PositionVector.ofString("(X:0, Y:0)"));
        PathFollowerStrategy strategy = new PathFollowerStrategy(filePath.toFile(), car);
        Method clampMethod = PathFollowerStrategy.class.getDeclaredMethod("clamp", int.class);
        clampMethod.setAccessible(true);
        Object result = clampMethod.invoke(strategy, 1);
        assertEquals(1, result, "Value 1 should remain unchanged.");
    }

    /**
     * Tests the private method {@code clamp} with a value between -1 and 1.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws Exception if reflection fails
     */
    @Test
    void testClampBetweenMinusOneAndOne(@TempDir Path tempDir) throws Exception {
        Path filePath = tempDir.resolve("dummy.txt");
        Files.write(filePath, List.of("(X:0, Y:0)"));
        DummyCar car = new DummyCar(PositionVector.ofString("(X:0, Y:0)"), PositionVector.ofString("(X:0, Y:0)"));
        PathFollowerStrategy strategy = new PathFollowerStrategy(filePath.toFile(), car);
        Method clampMethod = PathFollowerStrategy.class.getDeclaredMethod("clamp", int.class);
        clampMethod.setAccessible(true);
        Object result = clampMethod.invoke(strategy, 0);
        assertEquals(0, result, "Value 0 should remain unchanged.");
    }

    /**
     * Tests the private method {@code clamp} with a value equal to -1.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws Exception if reflection fails
     */
    @Test
    void testClampEqualToMinusOne(@TempDir Path tempDir) throws Exception {
        Path filePath = tempDir.resolve("dummy.txt");
        Files.write(filePath, List.of("(X:0, Y:0)"));
        DummyCar car = new DummyCar(PositionVector.ofString("(X:0, Y:0)"), PositionVector.ofString("(X:0, Y:0)"));
        PathFollowerStrategy strategy = new PathFollowerStrategy(filePath.toFile(), car);
        Method clampMethod = PathFollowerStrategy.class.getDeclaredMethod("clamp", int.class);
        clampMethod.setAccessible(true);
        Object result = clampMethod.invoke(strategy, -1);
        assertEquals(-1, result, "Value -1 should remain unchanged.");
    }

    /**
     * Tests the private method {@code clamp} with a value less than -1.
     *
     * @param tempDir temporary directory provided by JUnit
     * @throws Exception if reflection fails
     */
    @Test
    void testClampLessThanMinusOne(@TempDir Path tempDir) throws Exception {
        Path filePath = tempDir.resolve("dummy.txt");
        Files.write(filePath, List.of("(X:0, Y:0)"));
        DummyCar car = new DummyCar(PositionVector.ofString("(X:0, Y:0)"), PositionVector.ofString("(X:0, Y:0)"));
        PathFollowerStrategy strategy = new PathFollowerStrategy(filePath.toFile(), car);
        Method clampMethod = PathFollowerStrategy.class.getDeclaredMethod("clamp", int.class);
        clampMethod.setAccessible(true);
        Object result = clampMethod.invoke(strategy, -2);
        assertEquals(-1, result, "Value -2 should be clamped to -1.");
    }
}
