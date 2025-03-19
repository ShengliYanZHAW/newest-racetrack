package ch.zhaw.it.pm2.racetrack;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Game class.
 *
 * This class tests the main functionalities of Game including:
 * - Correct initialization of the game using a temporary track file.
 * - Execution of a car's turn when no collision occurs.
 * - Switching to the next active car when one is crashed.
 * - Calculation of the path between two positions.
 *
 * It also tests invalid equivalence classes, such as:
 * - Calling nextCarMove when no MoveStrategy is set.
 * - Passing a null acceleration to doCarTurn.
 * - Calling calculatePath with null start or end parameters.
 * - Calling getCarId with an invalid car index.
 */
public class GameTest {

    private Game game;
    private Track track;
    private File tempTrackFile;

    /**
     * Sets up a temporary track file and initializes the game.
     * The track is defined as a 5x5 grid with walls '#' on the borders,
     * car 'A' at position (1,1), and car 'B' at position (3,3).
     */
    @BeforeEach
    public void setUp() throws IOException, InvalidFileFormatException {
        // Define track content
        List<String> lines = List.of(
            "#####",
            "#A  #",
            "#   #",
            "#  B#",
            "#####"
        );
        // Create a temporary file and write the lines into it
        tempTrackFile = File.createTempFile("testTrack", ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempTrackFile))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
        // Construct the track using the temporary file
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

    /**
     * Tests that the game is correctly initialized.
     * Equivalence class: A valid track description leads to the correct car count and initial positions.
     */
    @Test
    public void testGameInitialization() {
        // The temporary track defines 2 cars: 'A' and 'B'
        assertEquals(2, game.getCarCount(), "Game should have 2 cars");
        PositionVector posA = game.getCarPosition(0);
        PositionVector posB = game.getCarPosition(1);
        assertEquals(new PositionVector(1, 1), posA, "Car A initial position should be (1,1)");
        assertEquals(new PositionVector(3, 3), posB, "Car B initial position should be (3,3)");
    }

    /**
     * Tests that nextCarMove throws an exception when no MoveStrategy is set.
     * Equivalence class: Calling nextCarMove without a set strategy should throw IllegalStateException.
     */
    @Test
    public void testNextCarMoveWithoutStrategy() {
        Exception exception = assertThrows(IllegalStateException.class,
            () -> game.nextCarMove(0),
            "Calling nextCarMove without a strategy should throw IllegalStateException");
        assertTrue(exception.getMessage().contains("MoveStrategy for car 0 is not set"),
            "Error message should indicate that MoveStrategy for car 0 is not set");
    }

    /**
     * Tests that doCarTurn throws a NullPointerException when a null acceleration is passed.
     * Equivalence class: Passing a null acceleration value is not accepted.
     */
    @Test
    public void testDoCarTurnWithNullAcceleration() {
        // Set a dummy MoveStrategy for car 0 that returns a valid direction (e.g., UP)
        game.setCarMoveStrategy(0, () -> Direction.UP);
        assertThrows(NullPointerException.class, () -> game.doCarTurn(null),
            "doCarTurn with null acceleration should throw NullPointerException");
    }

    /**
     * Tests that calculatePath throws a NullPointerException when a null start position is passed.
     * Equivalence class: Passing a null start position is not accepted.
     */
    @Test
    public void testCalculatePathWithNullStart() {
        PositionVector end = new PositionVector(3, 3);
        assertThrows(NullPointerException.class, () -> game.calculatePath(null, end),
            "calculatePath with a null start position should throw NullPointerException");
    }

    /**
     * Tests that calculatePath throws a NullPointerException when a null end position is passed.
     * Equivalence class: Passing a null end position is not accepted.
     */
    @Test
    public void testCalculatePathWithNullEnd() {
        PositionVector start = new PositionVector(1, 1);
        assertThrows(NullPointerException.class, () -> game.calculatePath(start, null),
            "calculatePath with a null end position should throw NullPointerException");
    }

    /**
     * Tests that getCarId throws an IndexOutOfBoundsException when an invalid car index is used.
     * Equivalence class: Passing an invalid index should not be accepted.
     */
    @Test
    public void testGetCarIdWithInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> game.getCarId(-1),
            "getCarId with an index less than zero should throw IndexOutOfBoundsException");
        assertThrows(IndexOutOfBoundsException.class, () -> game.getCarId(10),
            "getCarId with an index greater than available should throw IndexOutOfBoundsException");
    }

    /**
     * Tests that doCarTurn executes correctly in a valid scenario.
     * Equivalence class: A valid acceleration updates the car's position appropriately.
     */
    @Test
    public void testDoCarTurnNoCollision() {
        // For car 'A' at (1,1) with initial velocity (0,0),
        // an acceleration of (1,0) should update the position to (2,1).
        // Use the enum constant Direction.RIGHT which has vector (1,0).
        game.doCarTurn(Direction.RIGHT);
        PositionVector newPos = game.getCarPosition(0);
        assertEquals(new PositionVector(2, 1), newPos, "Car A should move to (2,1) with acceleration (1,0)");
    }

    /**
     * Tests that switchToNextActiveCar correctly skips crashed cars.
     * Equivalence class: When one car is crashed, the game switches to the next active car.
     */
    @Test
    public void testSwitchToNextActiveCar() {
        // Crash car 'A' (index 0)
        track.getCar(0).crash(new PositionVector(1, 1));
        int previousIndex = game.getCurrentCarIndex();
        game.switchToNextActiveCar();
        int newIndex = game.getCurrentCarIndex();
        assertNotEquals(previousIndex, newIndex, "Switching active car should change the current car index");
        assertEquals(1, newIndex, "The next active car should be at index 1");
    }

    /**
     * Tests the calculatePath method.
     * Equivalence class: Given valid start and end positions, calculatePath returns a valid list of positions.
     */
    @Test
    public void testCalculatePath() {
        PositionVector start = new PositionVector(0, 0);
        PositionVector end = new PositionVector(3, 4);
        List<PositionVector> path = game.calculatePath(start, end);
        assertFalse(path.isEmpty(), "Path should not be empty");
        assertEquals(start, path.get(0), "Path should start at the starting position");
        assertEquals(end, path.get(path.size() - 1), "Path should end at the ending position");
        // Optionally, check that the path length is within an expected range.
        assertTrue(path.size() >= 5 && path.size() <= 7, "Path length should be within the expected range");
    }
}
