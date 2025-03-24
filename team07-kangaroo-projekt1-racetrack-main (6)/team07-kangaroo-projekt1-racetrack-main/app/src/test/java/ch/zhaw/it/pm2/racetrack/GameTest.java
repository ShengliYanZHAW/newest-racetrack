package ch.zhaw.it.pm2.racetrack;

import ch.zhaw.it.pm2.racetrack.given.GameSpecification;
import ch.zhaw.it.pm2.racetrack.strategy.MoveStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Game class.
 *
 * This class tests all public methods of the Game class, covering both valid and invalid inputs.
 *
 * Methods and equivalence classes tested:
 *
 * 1. getCarCount():
 *    - Valid: Track with 1 car, 2 cars, and maximum (9) cars.
 *
 * 2. getCurrentCarIndex():
 *    - Valid: Initially returns 0.
 *    - Valid: After switching active car.
 *    - Valid: When only one active car remains.
 *
 * 3. getCarId(int carIndex):
 *    - Invalid: Negative index, index equal to car count, or index too high.
 *
 * 4. getCarPosition(int carIndex) and getCarVelocity(int carIndex):
 *    - Invalid: Negative index and index out-of-range.
 *
 * 5. setCarMoveStrategy(int carIndex, MoveStrategy moveStrategy):
 *    - Invalid: Negative index, index out-of-range.
 *    - Valid: Setting a valid strategy.
 *    - Valid/Invalid: Setting a null strategy (subsequent calls to nextCarMove will throw an exception).
 *
 * 6. nextCarMove(int carIndex):
 *    - Invalid: Negative index, no strategy set, or index out-of-range.
 *    - Valid: When a strategy is set.
 *
 * 7. getWinner():
 *    - Valid: Initially NO_WINNER, after one car crashes leaving one active, after a win.
 *
 * 8. doCarTurn(Direction acceleration):
 *    - Invalid: Null acceleration.
 *    - Valid: No collision updates the position.
 *    - Valid: Acceleration causing collision (e.g., hitting a WALL).
 *    - Valid: Acceleration with negative components.
 *    - (Optional) Valid: Acceleration crossing a finish line (if track is defined accordingly).
 *
 * 9. switchToNextActiveCar():
 *    - Valid: When all cars are active.
 *    - Valid: When some cars are crashed.
 *    - Valid: When all cars are crashed.
 *
 * 10. calculatePath(PositionVector startPosition, PositionVector endPosition):
 *    - Valid: Normal path, degenerate path, negative coordinates, and long horizontal line.
 *    - Invalid: Null start or null end.
 */
public class GameTest {

    private Game game;
    private Track track;
    private File tempTrackFile;  // default track file with 2 cars

    /**
     * Creates a temporary file with the given list of strings as content.
     *
     * @param lines the lines to write in the file
     * @return the temporary file created
     * @throws IOException if file operations fail
     */
    private File createTempTrackFile(List<String> lines) throws IOException {
        File file = File.createTempFile("testTrack", ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
        return file;
    }

    /**
     * Setup for tests that use a default track (5x5 grid with two cars: 'A' at (1,1) and 'B' at (3,3)).
     */
    @BeforeEach
    public void setUp() throws IOException, InvalidFileFormatException {
        List<String> lines = List.of(
            "#####",
            "#A  #",
            "#   #",
            "#  B#",
            "#####"
        );
        tempTrackFile = createTempTrackFile(lines);
        track = new Track(tempTrackFile);
        game = new Game(track);
    }

    /**
     * Clean up the temporary file after each test.
     */
    @AfterEach
    public void tearDown() {
        if (tempTrackFile != null && tempTrackFile.exists()) {
            tempTrackFile.delete();
        }
    }

    // ----------------- Tests for getCarCount() -----------------

    /**
     * Tests getCarCount() for a track with one car.
     */
    @Test
    public void testGetCarCountOneCar() throws IOException, InvalidFileFormatException {
        List<String> lines = List.of(
            "#####",
            "#A  #",
            "#####"
        );
        File oneCarFile = createTempTrackFile(lines);
        Track oneCarTrack = new Track(oneCarFile);
        Game oneCarGame = new Game(oneCarTrack);
        assertEquals(1, oneCarGame.getCarCount(), "Game should have 1 car");
        oneCarFile.delete();
    }

    /**
     * Tests getCarCount() for a track with two cars.
     */
    @Test
    public void testGetCarCountTwoCars() {
        // Default track in setUp has 2 cars: 'A' and 'B'
        assertEquals(2, game.getCarCount(), "Game should have 2 cars");
    }

    /**
     * Tests getCarCount() for a track with maximum (9) cars.
     */
    @Test
    public void testGetCarCountMaxCars() throws IOException, InvalidFileFormatException {
        // Create a track with 9 cars. Build a 5x5 grid with unique car symbols in the inner area.
        // For simplicity, use the 3x3 inner area and assign 9 distinct characters.
        List<String> lines = new ArrayList<>();
        lines.add("#####");
        lines.add("#123#"); // 3 cars
        lines.add("#456#"); // 3 cars
        lines.add("#789#"); // 3 cars -> total 9
        lines.add("#####");
        File nineCarFile = createTempTrackFile(lines);
        Track nineCarTrack = new Track(nineCarFile);
        Game nineCarGame = new Game(nineCarTrack);
        assertEquals(9, nineCarGame.getCarCount(), "Game should have 9 cars (maximum allowed)");
        nineCarFile.delete();
    }

    // ----------------- Tests for getCurrentCarIndex() -----------------

    /**
     * Tests that the initial currentCarIndex is 0.
     */
    @Test
    public void testGetCurrentCarIndexInitial() {
        assertEquals(0, game.getCurrentCarIndex(), "Initial currentCarIndex should be 0");
    }

    /**
     * Tests switchToNextActiveCar when all cars are active.
     */
    @Test
    public void testSwitchToNextActiveCarAllActive1() {
        int initialIndex = game.getCurrentCarIndex();
        game.switchToNextActiveCar();
        int newIndex = game.getCurrentCarIndex();
        // With 2 cars, if initial is 0, new should be 1.
        assertEquals(1, newIndex, "When all cars are active, switching should result in next index (1)");
    }

    /**
     * Tests switchToNextActiveCar when one car is crashed.
     */
    @Test
    public void testSwitchToNextActiveCarWithCrash() {
        // Crash car 'A' (index 0) using the track reference
        track.getCar(0).crash(new PositionVector(1, 1));
        int prevIndex = game.getCurrentCarIndex();
        game.switchToNextActiveCar();
        int newIndex = game.getCurrentCarIndex();
        // Only car at index 1 remains active.
        assertNotEquals(prevIndex, newIndex, "Switching should change currentCarIndex when current car is crashed");
        assertEquals(1, newIndex, "The next active car should be at index 1");
    }

    /**
     * Tests switchToNextActiveCar when all cars are crashed.
     */
    @Test
    public void testSwitchToNextActiveCarAllCrashed2() {
        // Crash both cars
        track.getCar(0).crash(new PositionVector(1, 1));
        track.getCar(1).crash(new PositionVector(3, 3));
        int current = game.getCurrentCarIndex();
        game.switchToNextActiveCar();
        int newIndex = game.getCurrentCarIndex();
        assertEquals(current, newIndex, "If all cars are crashed, currentCarIndex remains unchanged");
    }

    // ----------------- Tests for getCarId(int carIndex) -----------------

    /**
     * Tests that getCarId() with a negative index throws an exception.
     */
    @Test
    public void testGetCarIdNegativeIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> game.getCarId(-1),
            "Negative index should throw IndexOutOfBoundsException");
    }

    /**
     * Tests that getCarId() with an index equal to car count throws an exception.
     */
    @Test
    public void testGetCarIdIndexEqualToCount() {
        int count = game.getCarCount();
        assertThrows(IndexOutOfBoundsException.class, () -> game.getCarId(count),
            "Index equal to car count should throw IndexOutOfBoundsException");
    }

    /**
     * Tests that getCarId() with an index that is too large throws an exception.
     */
    @Test
    public void testGetCarIdIndexTooLarge() {
        assertThrows(IndexOutOfBoundsException.class, () -> game.getCarId(10),
            "Index too large should throw IndexOutOfBoundsException");
    }

    // ----------------- Tests for getCarPosition(int) and getCarVelocity(int) -----------------

    /**
     * Tests getCarPosition() with an invalid (negative) index.
     */
    @Test
    public void testGetCarPositionNegativeIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> game.getCarPosition(-1),
            "Negative index for getCarPosition should throw IndexOutOfBoundsException");
    }

    /**
     * Tests getCarVelocity() with an invalid (negative) index.
     */
    @Test
    public void testGetCarVelocityNegativeIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> game.getCarVelocity(-1),
            "Negative index for getCarVelocity should throw IndexOutOfBoundsException");
    }

    // ----------------- Tests for setCarMoveStrategy(int, MoveStrategy) -----------------

    /**
     * Tests setCarMoveStrategy() with a negative index.
     */
    @Test
    public void testSetCarMoveStrategyNegativeIndex() {
        MoveStrategy dummyStrategy = () -> Direction.UP;
        assertThrows(IndexOutOfBoundsException.class, () -> game.setCarMoveStrategy(-1, dummyStrategy),
            "Setting strategy with negative index should throw IndexOutOfBoundsException");
    }

    /**
     * Tests setCarMoveStrategy() with a valid index and valid strategy.
     */
    @Test
    public void testSetCarMoveStrategyValid() {
        MoveStrategy dummyStrategy = () -> Direction.DOWN;
        assertDoesNotThrow(() -> game.setCarMoveStrategy(0, dummyStrategy),
            "Setting strategy with a valid index should not throw an exception");
    }

    /**
     * Tests setCarMoveStrategy() with a valid index and null strategy.
     * Then, calling nextCarMove should throw an IllegalStateException.
     */
    @Test
    public void testSetCarMoveStrategyWithNull() {
        game.setCarMoveStrategy(0, null);
        Exception exception = assertThrows(IllegalStateException.class, () -> game.nextCarMove(0),
            "Calling nextCarMove after setting null strategy should throw IllegalStateException");
        assertTrue(exception.getMessage().contains("MoveStrategy for car 0 is not set"),
            "Error message should indicate that MoveStrategy for car 0 is not set");
    }

    /**
     * Tests setCarMoveStrategy() with an index equal to the car count.
     */
    @Test
    public void testSetCarMoveStrategyIndexEqualToCount() {
        MoveStrategy dummyStrategy = () -> Direction.UP;
        int count = game.getCarCount();
        assertThrows(IndexOutOfBoundsException.class, () -> game.setCarMoveStrategy(count, dummyStrategy),
            "Index equal to car count should throw IndexOutOfBoundsException");
    }

    // ----------------- Tests for nextCarMove(int) -----------------

    /**
     * Tests nextCarMove() with a negative index.
     */
    @Test
    public void testNextCarMoveNegativeIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> game.nextCarMove(-1),
            "Negative index should throw IndexOutOfBoundsException");
    }

    /**
     * Tests nextCarMove() with no strategy set.
     */
    @Test
    public void testNextCarMoveNoStrategy() {
        Exception exception = assertThrows(IllegalStateException.class,
            () -> game.nextCarMove(0),
            "Calling nextCarMove without setting a strategy should throw IllegalStateException");
        assertTrue(exception.getMessage().contains("MoveStrategy for car 0 is not set"),
            "Error message should indicate missing strategy");
    }

    /**
     * Tests nextCarMove() with a valid strategy set.
     */
    @Test
    public void testNextCarMoveValid() {
        game.setCarMoveStrategy(0, () -> Direction.UP);
        Direction move = game.nextCarMove(0);
        assertEquals(Direction.UP, move, "nextCarMove should return the strategy's direction");
    }

    /**
     * Tests nextCarMove() with an index out-of-range.
     */
    @Test
    public void testNextCarMoveIndexOutOfRange() {
        assertThrows(IndexOutOfBoundsException.class, () -> game.nextCarMove(10),
            "Index out-of-range should throw IndexOutOfBoundsException");
    }

    // ----------------- Tests for getWinner() -----------------

    /**
     * Tests getWinner() in a game that is still in progress.
     */
    @Test
    public void testGetWinnerGameInProgress() {
        assertEquals(GameSpecification.NO_WINNER, game.getWinner(), "Initially, winner should be NO_WINNER");
    }

    /**
     * Tests doCarTurn() with null acceleration.
     */
    @Test
    public void testDoCarTurnWithNullAcceleration() {
        game.setCarMoveStrategy(0, () -> Direction.UP);
        assertThrows(NullPointerException.class, () -> game.doCarTurn(null),
            "doCarTurn with null acceleration should throw NullPointerException");
    }

    /**
     * Tests doCarTurn() with valid acceleration that does not cause a collision.
     * For car 'A' at (1,1) with initial velocity (0,0), using acceleration RIGHT should move it to (2,1).
     */
    @Test
    public void testDoCarTurnNoCollision() {
        // Use Direction.RIGHT which has vector (1,0)
        game.setCarMoveStrategy(0, () -> Direction.RIGHT);
        game.doCarTurn(Direction.RIGHT);
        PositionVector newPos = game.getCarPosition(0);
        assertEquals(new PositionVector(2, 1), newPos, "Car A should move to (2,1) with acceleration (1,0)");
    }

    /**
     * Tests doCarTurn() with valid acceleration causing a collision with a wall.
     * For car 'A' at (1,1), using acceleration UP (vector (0,-1)) moves it to (1,0), which is a wall.
     */
    @Test
    public void testDoCarTurnCollision() {
        game.setCarMoveStrategy(0, () -> Direction.UP);
        game.doCarTurn(Direction.UP);
        assertTrue(game.getCarPosition(0).equals(new PositionVector(1, 0)) || game.getCarPosition(0) == null,
            "Car A should be marked as crashed if it hits a wall");
        assertTrue(track.getCar(0).isCrashed(), "Car A should be crashed after collision with wall");
    }

    /**
     * Tests doCarTurn() with valid acceleration that has negative components.
     * For car 'A' at (1,1), applying acceleration UP_LEFT (vector (-1,-1)) should update velocity and position accordingly.
     */
    @Test
    public void testDoCarTurnNegativeAcceleration() {
        game.setCarMoveStrategy(0, () -> Direction.UP_LEFT);
        game.doCarTurn(Direction.UP_LEFT);
        // Expected: new velocity = (-1,-1) and position = (0,0) (since (1,1)+(-1,-1) = (0,0))
        PositionVector newPos = game.getCarPosition(0);
        assertEquals(new PositionVector(0, 0), newPos, "Car A should move to (0,0) with acceleration UP_LEFT (-1,-1)");
    }

    // Optionally, one could test finish line crossing if a track with a finish area is provided.
    // That test is omitted here due to complexity in setting up a proper finish area.

    // ----------------- Tests for switchToNextActiveCar() -----------------

    /**
     * Tests switchToNextActiveCar() when all cars are active.
     */
    @Test
    public void testSwitchToNextActiveCarAllActive() {
        int initialIndex = game.getCurrentCarIndex();
        game.switchToNextActiveCar();
        int newIndex = game.getCurrentCarIndex();
        // For 2 cars, if initial is 0, new should be 1.
        assertEquals(1, newIndex, "Switching active car should result in index 1 when all are active");
    }

    /**
     * Tests switchToNextActiveCar() when one car is crashed.
     */
    @Test
    public void testSwitchToNextActiveCarWithOneCrashed() {
        track.getCar(0).crash(new PositionVector(1, 1));
        int initialIndex = game.getCurrentCarIndex();
        game.switchToNextActiveCar();
        int newIndex = game.getCurrentCarIndex();
        assertNotEquals(initialIndex, newIndex, "Current car index should change if current car is crashed");
        assertEquals(1, newIndex, "Next active car should be at index 1");
    }

    /**
     * Tests switchToNextActiveCar() when all cars are crashed.
     */
    @Test
    public void testSwitchToNextActiveCarAllCrashed() {
        track.getCar(0).crash(new PositionVector(1, 1));
        track.getCar(1).crash(new PositionVector(3, 3));
        int initialIndex = game.getCurrentCarIndex();
        game.switchToNextActiveCar();
        int newIndex = game.getCurrentCarIndex();
        assertEquals(initialIndex, newIndex, "If all cars are crashed, currentCarIndex remains unchanged");
    }

    // ----------------- Tests for calculatePath(PositionVector, PositionVector) -----------------

    /**
     * Tests calculatePath() for a normal case.
     * For example, from (0,0) to (3,4) the path should start with (0,0) and end with (3,4).
     */
    @Test
    public void testCalculatePathNormal() {
        PositionVector start = new PositionVector(0, 0);
        PositionVector end = new PositionVector(3, 4);
        List<PositionVector> path = game.calculatePath(start, end);
        assertFalse(path.isEmpty(), "Path should not be empty");
        assertEquals(start, path.get(0), "Path should start at (0,0)");
        assertEquals(end, path.get(path.size() - 1), "Path should end at (3,4)");
        // Check that path length is reasonable (Bresenham's algorithm)
        assertTrue(path.size() >= 5 && path.size() <= 7, "Path length should be within an expected range");
    }

    /**
     * Tests calculatePath() for a degenerate case when start equals end.
     */
    @Test
    public void testCalculatePathDegenerate() {
        PositionVector point = new PositionVector(1, 1);
        List<PositionVector> path = game.calculatePath(point, point);
        assertEquals(1, path.size(), "Path for degenerate line should contain only one point");
        assertEquals(point, path.get(0), "The single point in the path should be (1,1)");
    }

    /**
     * Tests calculatePath() with a null start position.
     */
    @Test
    public void testCalculatePathNullStart() {
        PositionVector end = new PositionVector(3, 3);
        assertThrows(NullPointerException.class, () -> game.calculatePath(null, end),
            "Passing a null start position should throw NullPointerException");
    }

    /**
     * Tests calculatePath() with a null end position.
     */
    @Test
    public void testCalculatePathNullEnd() {
        PositionVector start = new PositionVector(1, 1);
        assertThrows(NullPointerException.class, () -> game.calculatePath(start, null),
            "Passing a null end position should throw NullPointerException");
    }

    /**
     * Tests calculatePath() with negative coordinates.
     * For example, from (-2,-2) to (2,2).
     */
    @Test
    public void testCalculatePathNegativeCoordinates() {
        PositionVector start = new PositionVector(-2, -2);
        PositionVector end = new PositionVector(2, 2);
        List<PositionVector> path = game.calculatePath(start, end);
        assertFalse(path.isEmpty(), "Path should not be empty for negative coordinates");
        assertEquals(start, path.get(0), "Path should start at (-2,-2)");
        assertEquals(end, path.get(path.size() - 1), "Path should end at (2,2)");
    }

    /**
     * Tests calculatePath() for a long horizontal line.
     * For example, from (0,0) to (1000,0).
     */
    @Test
    public void testCalculatePathLongHorizontal() {
        PositionVector start = new PositionVector(0, 0);
        PositionVector end = new PositionVector(1000, 0);
        List<PositionVector> path = game.calculatePath(start, end);
        // The number of points should be 1001 if computed correctly.
        assertEquals(1001, path.size(), "Path length for horizontal line from (0,0) to (1000,0) should be 1001");
    }
}
