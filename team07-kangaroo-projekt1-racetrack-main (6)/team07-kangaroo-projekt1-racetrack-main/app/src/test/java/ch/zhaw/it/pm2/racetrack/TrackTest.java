package ch.zhaw.it.pm2.racetrack;

import ch.zhaw.it.pm2.racetrack.given.TrackSpecification;
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
 * Test class for the Track class.
 *
 * This class tests all public methods of the Track class according to the following tables:
 *
 * 1. Constructor Track(File trackFile)
 *    - Valid: A file with a valid rectangular grid and proper car placements.
 *    - Invalid:
 *       - Null file (should throw IllegalArgumentException).
 *       - File that is empty (should throw InvalidFileFormatException).
 *       - File with inconsistent line lengths (should throw InvalidFileFormatException).
 *       - File with a valid grid but no car characters (should throw InvalidFileFormatException).
 *       - File with more than MAX_CARS (9) cars (should throw InvalidFileFormatException).
 *
 * 2. getHeight() and getWidth()
 *    - Valid: Return the number of non-empty lines and the length of the first line.
 *
 * 3. getCarCount()
 *    - Valid: For a track with 1 car, 2 cars, and maximum 9 cars.
 *    - Invalid: Files with no cars or more than 9 cars are rejected by the constructor.
 *
 * 4. getCar(int carIndex)
 *    - Valid: Returns the car instance when index is in range.
 *    - Invalid: Negative index or index out-of-range should throw IndexOutOfBoundsException.
 *
 * 5. getSpaceTypeAtPosition(PositionVector position)
 *    - Valid: For positions within the grid.
 *    - Invalid: Negative or out-of-bound coordinates return SpaceType.WALL.
 *    - Passing a null position should throw NullPointerException.
 *
 * 6. getCharRepresentationAtPosition(int row, int col)
 *    - Valid: Returns car id if an active car is located; returns the space character otherwise.
 *    - Invalid: Out-of-bound row or col values.
 *
 * 7. toString()
 *    - Valid: Returns a String representing the track state (grid with car positions).
 *
 * Note: Many "invalid" cases (e.g., empty file, inconsistent lengths) cause the constructor to throw an exception,
 * so these methods are indirectly tested.
 */
public class TrackTest {

    private File tempTrackFile;  // temporary file for track tests
    private Track track;         // valid track instance created from the temporary file

    /**
     * Helper method to create a temporary track file with the given lines.
     *
     * @param lines List of strings representing each line of the track file.
     * @return A temporary File containing the track data.
     * @throws IOException if file writing fails.
     */
    private File createTempFileWithLines(List<String> lines) throws IOException {
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
     * Setup for tests using a default valid track file.
     * The default track is a 5x5 grid with walls on the borders,
     * car 'A' at (1,1), and car 'B' at (3,3).
     */
    @BeforeEach
    public void setUp() {
        try {
            List<String> lines = List.of(
                "#####",
                "#A  #",
                "#   #",
                "#  B#",
                "#####"
            );
            tempTrackFile = createTempFileWithLines(lines);
            track = new Track(tempTrackFile);
            // Ensure that track is not null.
            assertNotNull(track, "Track should be initialized in setUp()");
        } catch (IOException | InvalidFileFormatException e) {
            fail("Setup failed: " + e.getMessage());
        }
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

    // ==================== Tests for the Constructor ====================

    /**
     * Test that the constructor throws IllegalArgumentException when the file is null.
     */
    @Test
    public void testConstructorNullFile() {
        assertThrows(IllegalArgumentException.class, () -> new Track(null),
            "Passing a null file should throw IllegalArgumentException");
    }

    /**
     * Test that the constructor throws InvalidFileFormatException when the file is empty.
     */
    @Test
    public void testConstructorEmptyFile() throws IOException {
        File emptyFile = createTempFileWithLines(new ArrayList<>());
        assertThrows(InvalidFileFormatException.class, () -> new Track(emptyFile),
            "An empty file should throw InvalidFileFormatException");
        emptyFile.delete();
    }

    /**
     * Test that the constructor throws InvalidFileFormatException when the track lines have inconsistent lengths.
     */
    @Test
    public void testConstructorInconsistentLineLengths() throws IOException {
        List<String> lines = List.of(
            "#####",
            "##  ",    // length 4 instead of 5
            "#####"
        );
        File file = createTempFileWithLines(lines);
        assertThrows(InvalidFileFormatException.class, () -> new Track(file),
            "Inconsistent line lengths should throw InvalidFileFormatException");
        file.delete();
    }

    /**
     * Test that the constructor throws InvalidFileFormatException when no car characters are present.
     */
    @Test
    public void testConstructorNoCars() throws IOException {
        List<String> lines = List.of(
            "#####",
            "#   #",
            "#   #",
            "#   #",
            "#####"
        );
        File file = createTempFileWithLines(lines);
        assertThrows(InvalidFileFormatException.class, () -> new Track(file),
            "A track with no car characters should throw InvalidFileFormatException");
        file.delete();
    }

    /**
     * Test that the constructor throws InvalidFileFormatException when there are more than MAX_CARS (9) cars.
     */
    @Test
    public void testConstructorTooManyCars() throws IOException {
        // Create a grid with a border and an inner area with more than 9 car IDs.
        // For example, a 7x4 grid with inner 5 car IDs in each of two rows (10 cars).
        List<String> lines = List.of(
            "#######",
            "#12345#",
            "#67890#",
            "#######"
        );
        File file = createTempFileWithLines(lines);
        assertThrows(InvalidFileFormatException.class, () -> new Track(file),
            "A track with more than 9 cars should throw InvalidFileFormatException");
        file.delete();
    }

    /**
     * Test that the constructor successfully creates a Track with valid input.
     */
    @Test
    public void testConstructorValid() throws IOException, InvalidFileFormatException {
        List<String> lines = List.of(
            "#####",
            "#A  #",
            "#   #",
            "#  B#",
            "#####"
        );
        File file = createTempFileWithLines(lines);
        Track validTrack = new Track(file);
        assertNotNull(validTrack, "Valid track should be created successfully");
        assertEquals(5, validTrack.getWidth(), "Track width should be 5");
        assertEquals(5, validTrack.getHeight(), "Track height should be 5");
        assertEquals(2, validTrack.getCarCount(), "Track should contain 2 cars");
        file.delete();
    }

    // ==================== Tests for getHeight() and getWidth() ====================

    /**
     * Test getHeight() on a valid track.
     */
    @Test
    public void testGetHeight() {
        assertEquals(5, track.getHeight(), "Height should equal the number of non-empty lines");
    }

    /**
     * Test getWidth() on a valid track.
     */
    @Test
    public void testGetWidth() {
        assertEquals(5, track.getWidth(), "Width should equal the length of the first non-empty line");
    }

    // ==================== Tests for getCarCount() ====================

    /**
     * Test getCarCount() for a track with one car.
     */
    @Test
    public void testGetCarCountOneCar() throws IOException, InvalidFileFormatException {
        List<String> lines = List.of(
            "#####",
            "#A  #",
            "#####"
        );
        File file = createTempFileWithLines(lines);
        Track oneCarTrack = new Track(file);
        assertEquals(1, oneCarTrack.getCarCount(), "There should be 1 car in the track");
        file.delete();
    }

    /**
     * Test getCarCount() for the default track with 2 cars.
     */
    @Test
    public void testGetCarCountTwoCars() {
        assertEquals(2, track.getCarCount(), "There should be 2 cars in the track");
    }

    /**
     * Test getCarCount() for a track with maximum (9) cars.
     */
    @Test
    public void testGetCarCountMaxCars() throws IOException, InvalidFileFormatException {
        // Create a track with a 5x5 grid and inner 3x3 area filled with unique car IDs (9 cars)
        List<String> lines = new ArrayList<>();
        lines.add("#####");
        lines.add("#123#"); // 3 cars
        lines.add("#456#"); // 3 cars
        lines.add("#789#"); // 3 cars -> total 9
        lines.add("#####");
        File file = createTempFileWithLines(lines);
        Track maxCarTrack = new Track(file);
        assertEquals(9, maxCarTrack.getCarCount(), "There should be 9 cars in the track");
        file.delete();
    }

    // ==================== Tests for getCar(int) ====================

    /**
     * Test getCar() with a negative index.
     */
    @Test
    public void testGetCarNegativeIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> track.getCar(-1),
            "A negative index should throw IndexOutOfBoundsException");
    }

    /**
     * Test getCar() with an index equal to the car count.
     */
    @Test
    public void testGetCarIndexEqualToCount() {
        int count = track.getCarCount();
        assertThrows(IndexOutOfBoundsException.class, () -> track.getCar(count),
            "Index equal to car count should throw IndexOutOfBoundsException");
    }

    /**
     * Test getCar() with an index much larger than available.
     */
    @Test
    public void testGetCarIndexTooLarge() {
        assertThrows(IndexOutOfBoundsException.class, () -> track.getCar(10),
            "An index of 10 should throw IndexOutOfBoundsException when there are fewer cars");
    }

    // ==================== Tests for getSpaceTypeAtPosition(PositionVector) ====================

    /**
     * Test getSpaceTypeAtPosition() with a valid position within bounds.
     */
    @Test
    public void testGetSpaceTypeValidPosition() {
        PositionVector pos = new PositionVector(0, 0);
        assertEquals(SpaceType.WALL, track.getSpaceTypeAtPosition(pos),
            "Position (0,0) should be WALL");
    }

    /**
     * Test getSpaceTypeAtPosition() with a negative coordinate.
     */
    @Test
    public void testGetSpaceTypeNegativeCoordinates() {
        PositionVector pos = new PositionVector(-1, 2);
        assertEquals(SpaceType.WALL, track.getSpaceTypeAtPosition(pos),
            "Negative x-coordinate should result in WALL");
    }

    /**
     * Test getSpaceTypeAtPosition() with x-coordinate out-of-bounds.
     */
    @Test
    public void testGetSpaceTypeXOutOfBounds() {
        PositionVector pos = new PositionVector(track.getWidth(), 2);
        assertEquals(SpaceType.WALL, track.getSpaceTypeAtPosition(pos),
            "x-coordinate equal to width should be out-of-bound (WALL)");
    }

    /**
     * Test getSpaceTypeAtPosition() with y-coordinate out-of-bounds.
     */
    @Test
    public void testGetSpaceTypeYOutOfBounds() {
        PositionVector pos = new PositionVector(2, track.getHeight());
        assertEquals(SpaceType.WALL, track.getSpaceTypeAtPosition(pos),
            "y-coordinate equal to height should be out-of-bound (WALL)");
    }

    /**
     * Test getSpaceTypeAtPosition() with a null position.
     */
    @Test
    public void testGetSpaceTypeNullPosition() {
        assertThrows(NullPointerException.class, () -> track.getSpaceTypeAtPosition(null),
            "Passing a null position should throw NullPointerException");
    }

    // ==================== Tests for getCharRepresentationAtPosition(int, int) ====================

    /**
     * Test getCharRepresentationAtPosition() where an active car is located.
     */
    @Test
    public void testGetCharRepresentationActiveCar() {
        // In the default track, car 'A' is at (1,1)
        char repr = track.getCharRepresentationAtPosition(1, 1);
        assertEquals('A', repr, "Active car 'A' should be represented by its id at (1,1)");
    }

    /**
     * Test getCharRepresentationAtPosition() where a crashed car is located.
     */
    @Test
    public void testGetCharRepresentationCrashedCar() {
        track.getCar(0).crash(new PositionVector(1, 1));
        char repr = track.getCharRepresentationAtPosition(1, 1);
        assertEquals(TrackSpecification.CRASH_INDICATOR, repr,
            "Crashed car should be represented by CRASH_INDICATOR");
    }

    /**
     * Test getCharRepresentationAtPosition() for a position with no car.
     */
    @Test
    public void testGetCharRepresentationEmptySpace() {
        char repr = track.getCharRepresentationAtPosition(2, 2);
        assertEquals(SpaceType.TRACK.getSpaceChar(), repr,
            "Empty track space should be represented by its space character");
    }

    // ==================== Tests for toString() ====================

    /**
     * Test that toString() returns a string representation of the track.
     */
    @Test
    public void testToString() {
        String trackStr = track.toString();
        String[] lines = trackStr.split(System.lineSeparator());
        assertEquals(track.getHeight(), lines.length,
            "toString() should produce as many lines as track height");
        assertEquals("#####", lines[0],
            "The first row of the track should be \"#####\"");
    }
}
