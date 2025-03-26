package ch.zhaw.it.pm2.racetrack;

import ch.zhaw.it.pm2.racetrack.given.TrackSpecification;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test class for the Track class.
 * This class tests all public methods of the Track class, with equivalence classes documentet in readme.
 */
public class TrackTest {

    private File tempTrackFile;
    private Track track;

    /**
     * Helper method to create a temporary file with the given lines.
     *
     * @param lines List of strings representing each line of the track file.
     * @return A temporary File containing the track data.
     * @throws IOException if file writing fails.
     */
    private File createTempFileWithLines(List<String> lines) throws IOException {
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
     * Setup for tests using a default valid track file.
     * The default track is a 5x5 grid with walls on the borders,
     * car 'A' at (1,1), and car 'B' at (3,3).
     */
    @BeforeEach
    public void setUp() {
        try {
            final List<String> lines = List.of(
                "#####",
                "#A  #",
                "#   #",
                "#  B#",
                "#####"
            );
            tempTrackFile = createTempFileWithLines(lines);
            track = new Track(tempTrackFile);
            assertNotNull(track, "Track should be initialized in setUp()");
        } catch (IOException | InvalidFileFormatException e) {
            fail("Setup failed: " + e.getMessage());
        }
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

    // ==================== Tests for the Constructor ====================

    /**
     * Tests that the constructor throws IllegalArgumentException when the file is null.
     */
    @Test
    public void testConstructorNullFile() {
        assertThrows(IllegalArgumentException.class, () -> new Track(null),
            "Passing a null file should throw IllegalArgumentException");
    }

    /**
     * Tests that the constructor throws InvalidFileFormatException when the file is empty.
     *
     * @throws IOException if file writing fails.
     */
    @Test
    public void testConstructorEmptyFile() throws IOException {
        final File emptyFile = createTempFileWithLines(new ArrayList<>());
        assertThrows(InvalidFileFormatException.class, () -> new Track(emptyFile),
            "An empty file should throw InvalidFileFormatException");
        emptyFile.delete();
    }

    /**
     * Tests that the constructor throws InvalidFileFormatException when the track lines have inconsistent lengths.
     *
     * @throws IOException if file writing fails.
     */
    @Test
    public void testConstructorInconsistentLineLengths() throws IOException {
        final List<String> lines = List.of(
            "#####",
            "##  ", // length 4 instead of 5
            "#####"
        );
        final File file = createTempFileWithLines(lines);
        assertThrows(InvalidFileFormatException.class, () -> new Track(file),
            "Inconsistent line lengths should throw InvalidFileFormatException");
        file.delete();
    }

    /**
     * Tests that the constructor throws InvalidFileFormatException when no car characters are present.
     *
     * @throws IOException if file writing fails.
     */
    @Test
    public void testConstructorNoCars() throws IOException {
        final List<String> lines = List.of(
            "#####",
            "#   #",
            "#   #",
            "#   #",
            "#####"
        );
        final File file = createTempFileWithLines(lines);
        assertThrows(InvalidFileFormatException.class, () -> new Track(file),
            "A track with no car characters should throw InvalidFileFormatException");
        file.delete();
    }

    /**
     * Tests that the constructor throws InvalidFileFormatException when there are more than MAX_CARS (9) cars.
     *
     * @throws IOException if file writing fails.
     */
    @Test
    public void testConstructorTooManyCars() throws IOException {
        final List<String> lines = List.of(
            "#######",
            "#12345#",
            "#67890#",
            "#######"
        );
        final File file = createTempFileWithLines(lines);
        assertThrows(InvalidFileFormatException.class, () -> new Track(file),
            "A track with more than 9 cars should throw InvalidFileFormatException");
        file.delete();
    }

    /**
     * Tests that the constructor successfully creates a Track with valid input.
     *
     * @throws IOException if file writing fails.
     * @throws InvalidFileFormatException if track file is invalid.
     */
    @Test
    public void testConstructorValid() throws IOException, InvalidFileFormatException {
        final List<String> lines = List.of(
            "#####",
            "#A  #",
            "#   #",
            "#  B#",
            "#####"
        );
        final File file = createTempFileWithLines(lines);
        final Track validTrack = new Track(file);
        assertNotNull(validTrack, "Valid track should be created successfully");
        assertEquals(5, validTrack.getWidth(), "Track width should be 5");
        assertEquals(5, validTrack.getHeight(), "Track height should be 5");
        assertEquals(2, validTrack.getCarCount(), "Track should contain 2 cars");
        file.delete();
    }

    // ==================== Tests for getHeight() and getWidth() ====================

    /**
     * Tests getHeight() on a valid track.
     */
    @Test
    public void testGetHeight() {
        assertEquals(5, track.getHeight(), "Height should equal the number of non-empty lines");
    }

    /**
     * Tests getWidth() on a valid track.
     */
    @Test
    public void testGetWidth() {
        assertEquals(5, track.getWidth(), "Width should equal the length of the first non-empty line");
    }

    // ==================== Tests for getCarCount() ====================

    /**
     * Tests getCarCount() for a track with one car.
     *
     * @throws IOException if file writing fails.
     * @throws InvalidFileFormatException if track file is invalid.
     */
    @Test
    public void testGetCarCountOneCar() throws IOException, InvalidFileFormatException {
        final List<String> lines = List.of(
            "#####",
            "#A  #",
            "#####"
        );
        final File file = createTempFileWithLines(lines);
        final Track oneCarTrack = new Track(file);
        assertEquals(1, oneCarTrack.getCarCount(), "There should be 1 car in the track");
        file.delete();
    }

    /**
     * Tests getCarCount() for the default track with 2 cars.
     */
    @Test
    public void testGetCarCountTwoCars() {
        assertEquals(2, track.getCarCount(), "There should be 2 cars in the track");
    }

    /**
     * Tests getCarCount() for a track with maximum (9) cars.
     *
     * @throws IOException if file writing fails.
     * @throws InvalidFileFormatException if track file is invalid.
     */
    @Test
    public void testGetCarCountMaxCars() throws IOException, InvalidFileFormatException {
        final List<String> lines = new ArrayList<>();
        lines.add("#####");
        lines.add("#123#"); // 3 cars
        lines.add("#456#"); // 3 cars
        lines.add("#789#"); // 3 cars -> total 9
        lines.add("#####");
        final File file = createTempFileWithLines(lines);
        final Track maxCarTrack = new Track(file);
        assertEquals(9, maxCarTrack.getCarCount(), "There should be 9 cars in the track");
        file.delete();
    }

    // ==================== Tests for getCar(int) ====================

    /**
     * Tests getCar() with a negative index.
     */
    @Test
    public void testGetCarNegativeIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> track.getCar(-1),
            "A negative index should throw IndexOutOfBoundsException");
    }

    /**
     * Tests getCar() with an index equal to the car count.
     */
    @Test
    public void testGetCarIndexEqualToCount() {
        final int count = track.getCarCount();
        assertThrows(IndexOutOfBoundsException.class, () -> track.getCar(count),
            "Index equal to car count should throw IndexOutOfBoundsException");
    }

    /**
     * Tests getCar() with an index much larger than available.
     */
    @Test
    public void testGetCarIndexTooLarge() {
        assertThrows(IndexOutOfBoundsException.class, () -> track.getCar(10),
            "An index of 10 should throw IndexOutOfBoundsException when there are fewer cars");
    }

    // ==================== Tests for getSpaceTypeAtPosition(PositionVector) ====================

    /**
     * Tests getSpaceTypeAtPosition() with a valid position within bounds.
     */
    @Test
    public void testGetSpaceTypeValidPosition() {
        final PositionVector pos = new PositionVector(0, 0);
        assertEquals(SpaceType.WALL, track.getSpaceTypeAtPosition(pos),
            "Position (0,0) should be WALL");
    }

    /**
     * Tests getSpaceTypeAtPosition() with a negative coordinate.
     */
    @Test
    public void testGetSpaceTypeNegativeCoordinates() {
        final PositionVector pos = new PositionVector(-1, 2);
        assertEquals(SpaceType.WALL, track.getSpaceTypeAtPosition(pos),
            "Negative x-coordinate should result in WALL");
    }

    /**
     * Tests getSpaceTypeAtPosition() with x-coordinate out-of-bounds.
     */
    @Test
    public void testGetSpaceTypeXOutOfBounds() {
        final PositionVector pos = new PositionVector(track.getWidth(), 2);
        assertEquals(SpaceType.WALL, track.getSpaceTypeAtPosition(pos),
            "x-coordinate equal to width should be out-of-bound (WALL)");
    }

    /**
     * Tests getSpaceTypeAtPosition() with y-coordinate out-of-bounds.
     */
    @Test
    public void testGetSpaceTypeYOutOfBounds() {
        final PositionVector pos = new PositionVector(2, track.getHeight());
        assertEquals(SpaceType.WALL, track.getSpaceTypeAtPosition(pos),
            "y-coordinate equal to height should be out-of-bound (WALL)");
    }

    /**
     * Tests getSpaceTypeAtPosition() with a null position.
     */
    @Test
    public void testGetSpaceTypeNullPosition() {
        assertThrows(NullPointerException.class, () -> track.getSpaceTypeAtPosition(null),
            "Passing a null position should throw NullPointerException");
    }

    // ==================== Tests for getCharRepresentationAtPosition(int, int) ====================

    /**
     * Tests getCharRepresentationAtPosition() where an active car is located.
     */
    @Test
    public void testGetCharRepresentationActiveCar() {
        final char repr = track.getCharRepresentationAtPosition(1, 1);
        assertEquals('A', repr, "Active car 'A' should be represented by its id at (1,1)");
    }

    /**
     * Tests getCharRepresentationAtPosition() where a crashed car is located.
     */
    @Test
    public void testGetCharRepresentationCrashedCar() {
        track.getCar(0).crash(new PositionVector(1, 1));
        final char repr = track.getCharRepresentationAtPosition(1, 1);
        assertEquals(TrackSpecification.CRASH_INDICATOR, repr,
            "Crashed car should be represented by CRASH_INDICATOR");
    }

    /**
     * Tests getCharRepresentationAtPosition() for a position with no car.
     */
    @Test
    public void testGetCharRepresentationEmptySpace() {
        final char repr = track.getCharRepresentationAtPosition(2, 2);
        assertEquals(SpaceType.TRACK.getSpaceChar(), repr,
            "Empty track space should be represented by its space character");
    }

    // ==================== Tests for toString() ====================

    /**
     * Tests that toString() returns a string representation of the track.
     */
    @Test
    public void testToString() {
        final String trackStr = track.toString();
        final String[] lines = trackStr.split(System.lineSeparator());
        assertEquals(track.getHeight(), lines.length,
            "toString() should produce as many lines as track height");
        assertEquals("#####", lines[0],
            "The first row of the track should be \"#####\"");
    }
}
