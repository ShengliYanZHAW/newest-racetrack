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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Test class for the Game class.
 * This class tests all public methods of the Game class using the equivalence classes documented in readme.
 */
public class GameTest {

    private Game game;
    private Track track;
    private File tempTrackFile; // Default track file with 2 cars

    /**
     * Creates a temporary track file with the given lines as content.
     *
     * @param lines the lines to write in the file
     * @return the temporary file created
     * @throws IOException if file operations fail
     */
    private File createTempTrackFile(List<String> lines) throws IOException {
        final File file = File.createTempFile("testTrack", ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
        return file;
    }

    /**
     * Setup for tests using a default track (5x5 grid with two cars: 'A' at (1,1) and 'B' at (3,3)).
     *
     * @throws IOException if file operations fail
     * @throws InvalidFileFormatException if track file is invalid
     */
    @BeforeEach
    public void setUp() throws IOException, InvalidFileFormatException {
        final List<String> lines = List.of(
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
     * Cleans up the temporary file after each test.
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
     *
     * @throws IOException if file operations fail
     * @throws InvalidFileFormatException if track file is invalid
     */
    @Test
    public void testGetCarCountOneCar() throws IOException, InvalidFileFormatException {
        final List<String> lines = List.of(
            "#####",
            "#A  #",
            "#####"
        );
        final File oneCarFile = createTempTrackFile(lines);
        final Track oneCarTrack = new Track(oneCarFile);
        assertEquals(1, oneCarTrack.getCarCount(), "Game should have 1 car");
        oneCarFile.delete();
    }

    /**
     * Tests getCarCount() for a track with two cars.
     */
    @Test
    public void testGetCarCountTwoCars() {
        assertEquals(2, game.getCarCount(), "Game should have 2 cars");
    }

    /**
     * Tests getCarCount() for a track with maximum (9) cars.
     *
     * @throws IOException if file operations fail
     * @throws InvalidFileFormatException if track file is invalid
     */
    @Test
    public void testGetCarCountMaxCars() throws IOException, InvalidFileFormatException {
        final List<String> lines = new ArrayList<>();
        lines.add("#####");
        lines.add("#123#"); // 3 cars
        lines.add("#456#"); // 3 cars
        lines.add("#789#"); // 3 cars -> total 9
        lines.add("#####");
        final File nineCarFile = createTempTrackFile(lines);
        final Track nineCarTrack = new Track(nineCarFile);
        final Game nineCarGame = new Game(nineCarTrack);
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
        final int initialIndex = game.getCurrentCarIndex();
        game.switchToNextActiveCar();
        final int newIndex = game.getCurrentCarIndex();
        assertEquals(1, newIndex, "When all cars are active, switching should result in next index (1)");
    }

    /**
     * Tests switchToNextActiveCar when one car is crashed.
     */
    @Test
    public void testSwitchToNextActiveCarWithCrash() {
        track.getCar(0).crash(new PositionVector(1, 1));
        final int prevIndex = game.getCurrentCarIndex();
        game.switchToNextActiveCar();
        final int newIndex = game.getCurrentCarIndex();
        assertNotEquals(prevIndex, newIndex, "Switching should change currentCarIndex when current car is crashed");
        assertEquals(1, newIndex, "The next active car should be at index 1");
    }

    /**
     * Tests switchToNextActiveCar when all cars are crashed.
     */
    @Test
    public void testSwitchToNextActiveCarAllCrashed2() {
        track.getCar(0).crash(new PositionVector(1, 1));
        track.getCar(1).crash(new PositionVector(3, 3));
        final int current = game.getCurrentCarIndex();
        game.switchToNextActiveCar();
        final int newIndex = game.getCurrentCarIndex();
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
        final int count = game.getCarCount();
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
        final MoveStrategy dummyStrategy = () -> Direction.UP;
        assertThrows(IndexOutOfBoundsException.class, () -> game.setCarMoveStrategy(-1, dummyStrategy),
            "Setting strategy with negative index should throw IndexOutOfBoundsException");
    }

    /**
     * Tests setCarMoveStrategy() with a valid index and valid strategy.
     */
    @Test
    public void testSetCarMoveStrategyValid() {
        final MoveStrategy dummyStrategy = () -> Direction.DOWN;
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
        final Exception exception = assertThrows(IllegalStateException.class, () -> game.nextCarMove(0),
            "Calling nextCarMove after setting null strategy should throw IllegalStateException");
        assertTrue(exception.getMessage().contains("MoveStrategy for car 0 is not set"),
            "Error message should indicate that MoveStrategy for car 0 is not set");
    }

    /**
     * Tests setCarMoveStrategy() with an index equal to the car count.
     */
    @Test
    public void testSetCarMoveStrategyIndexEqualToCount() {
        final MoveStrategy dummyStrategy = () -> Direction.UP;
        final int count = game.getCarCount();
        assertThrows(IndexOutOfBoundsException.class, () -> game.setCarMoveStrategy(count, dummyStrategy),
            "Index equal to car count should throw IndexOutOfBoundsException");
    }

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
        final Exception exception = assertThrows(IllegalStateException.class,
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
        final Direction move = game.nextCarMove(0);
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
        assertThrows(IllegalArgumentException.class, () -> game.doCarTurn(null),
            "doCarTurn with null acceleration should throw IllegalArgumentException");
    }

    /**
     * Tests doCarTurn() with valid acceleration that does not cause a collision.
     */
    @Test
    public void testDoCarTurnNoCollision() {
        game.setCarMoveStrategy(0, () -> Direction.RIGHT);
        game.doCarTurn(Direction.RIGHT);
        final PositionVector newPos = game.getCarPosition(0);
        assertEquals(new PositionVector(2, 1), newPos, "Car A should move to (2,1) with acceleration (1,0)");
    }

    /**
     * Tests doCarTurn() with valid acceleration causing a collision with a wall.
     */
    @Test
    public void testDoCarTurnCollision() {
        game.setCarMoveStrategy(0, () -> Direction.UP);
        game.doCarTurn(Direction.UP);
        final PositionVector pos = game.getCarPosition(0);
        assertTrue(pos.equals(new PositionVector(1, 0)) || pos == null,
            "Car A should be marked as crashed if it hits a wall");
        assertTrue(track.getCar(0).isCrashed(), "Car A should be crashed after collision with wall");
    }

    /**
     * Tests doCarTurn() with valid acceleration that has negative components.
     */
    @Test
    public void testDoCarTurnNegativeAcceleration() {
        game.setCarMoveStrategy(0, () -> Direction.UP_LEFT);
        game.doCarTurn(Direction.UP_LEFT);
        final PositionVector newPos = game.getCarPosition(0);
        assertEquals(new PositionVector(0, 0), newPos, "Car A should move to (0,0) with acceleration UP_LEFT (-1,-1)");
    }

    /**
     * Tests switchToNextActiveCar() when all cars are active.
     */
    @Test
    public void testSwitchToNextActiveCarAllActive() {
        final int initialIndex = game.getCurrentCarIndex();
        game.switchToNextActiveCar();
        final int newIndex = game.getCurrentCarIndex();
        assertEquals(1, newIndex, "Switching active car should result in index 1 when all are active");
    }

    /**
     * Tests switchToNextActiveCar() when one car is crashed.
     */
    @Test
    public void testSwitchToNextActiveCarWithOneCrashed() {
        track.getCar(0).crash(new PositionVector(1, 1));
        final int initialIndex = game.getCurrentCarIndex();
        game.switchToNextActiveCar();
        final int newIndex = game.getCurrentCarIndex();
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
        final int initialIndex = game.getCurrentCarIndex();
        game.switchToNextActiveCar();
        final int newIndex = game.getCurrentCarIndex();
        assertEquals(initialIndex, newIndex, "If all cars are crashed, currentCarIndex remains unchanged");
    }

    /**
     * Tests calculatePath() for a normal case.
     */
    @Test
    public void testCalculatePathNormal() {
        final PositionVector start = new PositionVector(0, 0);
        final PositionVector end = new PositionVector(3, 4);
        final List<PositionVector> path = game.calculatePath(start, end);
        assertFalse(path.isEmpty(), "Path should not be empty");
        assertEquals(start, path.get(0), "Path should start at (0,0)");
        assertEquals(end, path.get(path.size() - 1), "Path should end at (3,4)");
        assertTrue(path.size() >= 5 && path.size() <= 7, "Path length should be within an expected range");
    }

    /**
     * Tests calculatePath() for a degenerate case when start equals end.
     */
    @Test
    public void testCalculatePathDegenerate() {
        final PositionVector point = new PositionVector(1, 1);
        final List<PositionVector> path = game.calculatePath(point, point);
        assertEquals(1, path.size(), "Path for degenerate line should contain only one point");
        assertEquals(point, path.get(0), "The single point in the path should be (1,1)");
    }

    /**
     * Tests calculatePath() with a null start position.
     */
    @Test
    public void testCalculatePathNullStart() {
        final PositionVector end = new PositionVector(3, 3);
        assertThrows(NullPointerException.class, () -> game.calculatePath(null, end),
            "Passing a null start position should throw NullPointerException");
    }

    /**
     * Tests calculatePath() with a null end position.
     */
    @Test
    public void testCalculatePathNullEnd() {
        final PositionVector start = new PositionVector(1, 1);
        assertThrows(NullPointerException.class, () -> game.calculatePath(start, null),
            "Passing a null end position should throw NullPointerException");
    }

    /**
     * Tests calculatePath() with negative coordinates.
     */
    @Test
    public void testCalculatePathNegativeCoordinates() {
        final PositionVector start = new PositionVector(-2, -2);
        final PositionVector end = new PositionVector(2, 2);
        final List<PositionVector> path = game.calculatePath(start, end);
        assertFalse(path.isEmpty(), "Path should not be empty for negative coordinates");
        assertEquals(start, path.get(0), "Path should start at (-2,-2)");
        assertEquals(end, path.get(path.size() - 1), "Path should end at (2,2)");
    }

    /**
     * Tests calculatePath() for a long horizontal line.
     */
    @Test
    public void testCalculatePathLongHorizontal() {
        final PositionVector start = new PositionVector(0, 0);
        final PositionVector end = new PositionVector(1000, 0);
        final List<PositionVector> path = game.calculatePath(start, end);
        assertEquals(1001, path.size(), "Path length for horizontal line from (0,0) to (1000,0) should be 1001");
    }
}
