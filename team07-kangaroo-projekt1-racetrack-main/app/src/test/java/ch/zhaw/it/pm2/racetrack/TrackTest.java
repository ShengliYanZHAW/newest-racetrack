package ch.zhaw.it.pm2.racetrack;

import ch.zhaw.it.pm2.racetrack.given.TrackSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Track class that relies on dynamically generated track files.
 * Each test writes out track data lines of consistent length unless testing invalid scenarios.
 */
public class TrackTest {

    /**
     * Utility method to create a file with the given content in the specified directory.
     *
     * @param dir     the temporary directory where the file will be created
     * @param content the string content to write into the file
     * @return the File object representing the newly created file
     * @throws IOException if unable to write the file
     */
    private File createTempTrackFile(Path dir, String content) throws IOException {
        Path filePath = dir.resolve("testTrack.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write(content);
        }
        return filePath.toFile();
    }

    /**
     * Tests a minimal valid track with one car 'A'.
     * All lines must have the same length to avoid InvalidFileFormatException.
     */
    @Test
    void testValidTrackSingleCar(@TempDir Path tempDir) throws IOException {
        // 3 lines, each length 4
        String trackContent =
            """
            ####
            #A #
            ####
            """;

        File trackFile = createTempTrackFile(tempDir, trackContent);

        Track track = null;
        try {
            track = new Track(trackFile);
        } catch (InvalidFileFormatException e) {
            fail("Expected valid track, but got InvalidFileFormatException.");
        }

        assertNotNull(track, "Track should have been created successfully.");
        assertEquals(3, track.getHeight(), "Track height should be 3");
        assertEquals(4, track.getWidth(), "Track width should be 4");
        assertEquals(1, track.getCarCount(), "Should have exactly 1 car");

        // Check the car's position
        Car firstCar = track.getCar(0);
        assertEquals('A', firstCar.getId(), "Car ID should be 'A'");
        assertEquals(new PositionVector(1, 1), firstCar.getPosition(),
            "Car should start at (1,1)");

        // Check space type under the car
        assertEquals(SpaceType.TRACK,
            track.getSpaceTypeAtPosition(new PositionVector(1,1)),
            "Under the car should be TRACK by default");

        // Check an out-of-bounds => WALL
        assertEquals(SpaceType.WALL,
            track.getSpaceTypeAtPosition(new PositionVector(-1, 2)),
            "Out-of-bounds should return WALL");
    }

    /**
     * Tests a track that contains no car characters (only recognized track/wall characters).
     * Expects an InvalidFileFormatException because there are zero cars.
     */
    @Test
    void testTrackWithNoCars(@TempDir Path tempDir) throws IOException {
        // All recognized spaces, no car ID
        // 3 lines, each length 4
        String trackContent =
            """
            ####
            #  #
            ####
            """;

        File trackFile = createTempTrackFile(tempDir, trackContent);

        assertThrows(InvalidFileFormatException.class,
            () -> new Track(trackFile),
            "Should throw InvalidFileFormatException when no cars are present");
    }

    /**
     * Tests a track with inconsistent line lengths.
     * The first line is length 4, the second is length 5 => should fail.
     */
    @Test
    void testInconsistentLineLengths(@TempDir Path tempDir) throws IOException {
        // Second line has 5 columns (#, #, space, #, #) vs. first line 4 columns
        String trackContent =
            """
            ####
            ## ##
            ####
            """;

        File trackFile = createTempTrackFile(tempDir, trackContent);

        // Expect InvalidFileFormatException due to inconsistent line length
        assertThrows(InvalidFileFormatException.class,
            () -> new Track(trackFile),
            "Should throw InvalidFileFormatException due to inconsistent line lengths");
    }

    /**
     * Tests a completely empty file, which should result in no valid lines => InvalidFileFormatException.
     */
    @Test
    void testEmptyFile(@TempDir Path tempDir) throws IOException {
        // An empty file => no lines
        String trackContent = "";

        File trackFile = createTempTrackFile(tempDir, trackContent);

        // Expect InvalidFileFormatException due to no lines
        assertThrows(InvalidFileFormatException.class,
            () -> new Track(trackFile),
            "Should throw InvalidFileFormatException for empty files");
    }

    /**
     * Tests getCharRepresentation with a small 3-line track (4 columns each).
     * Two distinct cars: 'X' and 'Y' in the first line.
     */
    @Test
    void testGetCharRepresentation(@TempDir Path tempDir) throws IOException, InvalidFileFormatException {
        // Exactly 3 lines, each length 4
        //   "XY  "
        //   "#  #"
        //   "####"
        String trackContent = "XY  \n#  #\n####";

        File trackFile = createTempTrackFile(tempDir, trackContent);
        Track track = new Track(trackFile);  // should NOT throw InvalidFileFormatException now

        assertEquals(2, track.getCarCount(), "Should have 2 cars");

        // The 'X' car is at position (0,0), 'Y' at (1,0)
        char c1 = track.getCharRepresentationAtPosition(0, 0);
        assertEquals('X', c1, "Car 'X' should appear at (0,0)");

        char c2 = track.getCharRepresentationAtPosition(0, 1);
        assertEquals('Y', c2, "Car 'Y' should appear at (1,0)");

        // Check a track space with no car
        char c3 = track.getCharRepresentationAtPosition(1, 1);
        assertEquals(' ', c3, "SpaceType TRACK should appear as ' ' when no car is there");

        // Check toString() has "XY  " in the top line
        String trackString = track.toString();
        assertTrue(trackString.startsWith("XY  "),
            "Expected 'XY  ' in the top row of track output");
    }


    /**
     * Tests that an InvalidFileFormatException is thrown if there are more than
     * TrackSpecification.MAX_CARS (9). Here we try to place 10 distinct cars in a single line.
     */
    @Test
    void testMaximumCars(@TempDir Path tempDir) throws IOException {
        // We'll place 10 distinct cars: A,B,C,D,E,F,G,H,I,J (10 total)
        // Add a second line to avoid early termination.
        StringBuilder lineBuilder = new StringBuilder();
        lineBuilder.append("ABCDEFGHIJ").append("\n");
        // This second line must match the same length (10 columns) to avoid an inconsistent-line error:
        lineBuilder.append("          ");

        File trackFile = createTempTrackFile(tempDir, lineBuilder.toString());

        // Expect InvalidFileFormatException for exceeding MAX_CARS
        assertThrows(InvalidFileFormatException.class,
            () -> new Track(trackFile),
            "Should fail when there are more than TrackSpecification.MAX_CARS");
    }
}
