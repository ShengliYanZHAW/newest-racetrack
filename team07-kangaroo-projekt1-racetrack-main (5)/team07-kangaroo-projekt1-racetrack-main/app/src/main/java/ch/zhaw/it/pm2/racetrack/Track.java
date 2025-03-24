package ch.zhaw.it.pm2.racetrack;

import ch.zhaw.it.pm2.racetrack.given.TrackSpecification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the racetrack board.
 *
 * <p>The racetrack board consists of a rectangular grid of 'width' columns and 'height' rows.
 * The zero point of he grid is at the top left. The x-axis points to the right and the y-axis points downwards.</p>
 * <p>Positions on the track grid are specified using {@link PositionVector} objects. These are vectors containing an
 * x/y coordinate pair, pointing from the zero-point (top-left) to the addressed space in the grid.</p>
 *
 * <p>Each position in the grid represents a space which can hold an enum object of type {@link SpaceType}.<br>
 * Possible Space types are:
 * <ul>
 *  <li>WALL : road boundary or off track space</li>
 *  <li>TRACK: road or open track space</li>
 *  <li>FINISH_LEFT, FINISH_RIGHT, FINISH_UP, FINISH_DOWN :  finish line spaces which have to be crossed
 *      in the indicated direction to winn the race.</li>
 * </ul>
 * <p>Beside the board the track contains the list of cars, with their current state (position, velocity,...)</p>
 *
 * <p>At initialization the track grid data is read from the given track file. The track data must be a
 * rectangular block of text. Empty lines at the start are ignored. Processing stops at the first empty line
 * following a non-empty line, or at the end of the file.</p>
 * <p>Characters in the line represent SpaceTypes. The mapping of the Characters is as follows:</p>
 * <ul>
 *   <li>WALL : '#'</li>
 *   <li>TRACK: ' '</li>
 *   <li>FINISH_LEFT : '&lt;'</li>
 *   <li>FINISH_RIGHT: '&gt;'</li>
 *   <li>FINISH_UP   : '^;'</li>
 *   <li>FINISH_DOWN: 'v'</li>
 *   <li>Any other character indicates the starting position of a car.<br>
 *       The character acts as the id for the car and must be unique.<br>
 *       There are 1 to {@link TrackSpecification#MAX_CARS} allowed. </li>
 * </ul>
 *
 * <p>All lines must have the same length, used to initialize the grid width.<br/>
 * Beginning empty lines are skipped. <br/>
 * The track ends with the first empty line or the file end.<br>
 * An {@link InvalidFileFormatException} is thrown, if
 * <ul>
 *   <li>the file contains no track lines (grid height is 0)</li>
 *   <li>not all track lines have the same length</li>
 *   <li>the file contains no cars</li>
 *   <li>the file contains more than {@link TrackSpecification#MAX_CARS} cars</li>
 * </ul>
 *
 * <p>The Tracks {@link #toString()} method returns a String representing the current state of the race
 * (including car positions and status)</p>
 */
public class Track implements TrackSpecification {

    /** 2D array storing the layout of the track. */
    private final SpaceType[][] board;

    /** List of cars on the track. */
    private final List<Car> cars = new ArrayList<>();

    /** Width (number of columns) of the track grid. */
    private final int width;

    /** Height (number of rows) of the track grid. */
    private final int height;


    /**
     * Initialize a Track from the given track file.<br/>
     * See class description for structure and valid tracks.
     *
     * @param  trackFile reference to a file containing the track data
     * @throws IOException if the track file can not be opened or reading fails
     * @throws InvalidFileFormatException if the track file contains invalid data
     *         (no track lines, inconsistent length, no cars)
     * @throws IllegalArgumentException if trackFile is null
     */
    public Track(File trackFile) throws IOException, InvalidFileFormatException {
        if (trackFile == null) {
            throw new IllegalArgumentException("trackFile must not be null");
        }

        List<String> lines = readNonEmptyLines(trackFile);

        validateTrackLines(lines);

        this.width = lines.get(0).length();
        this.height = lines.size();
        this.board = new SpaceType[height][width];

        parseTrack(lines);

        validateCars();
    }

    /**
     * Reads all non-empty lines (skipping leading empty lines) until
     * the first blank line (after reading some data) or EOF.
     *
     * @param file File to read from
     * @return a List of lines found
     * @throws IOException in case of read problems
     */
    private List<String> readNonEmptyLines(File file) throws IOException {
        List<String> resultLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            boolean startedReading = false;
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip leading empty lines
                if (!startedReading && line.trim().isEmpty()) {
                    continue;
                }
                // Once we have started reading, an empty line ends the track
                if (startedReading && line.trim().isEmpty()) {
                    break;
                }
                if (!line.trim().isEmpty()) {
                    startedReading = true;
                    resultLines.add(line);
                }
            }
        }
        return resultLines;
    }

    /**
     * Checks basic validity of the track lines: non-empty and consistent length.
     *
     * @param lines List of non-empty lines read from the file.
     * @throws InvalidFileFormatException if lines are empty or inconsistent in length
     */
    private void validateTrackLines(List<String> lines) throws InvalidFileFormatException {
        if (lines.isEmpty()) {
            // No valid lines => invalid format
            throw new InvalidFileFormatException(
                InvalidFileFormatException.FormatErrorType.EMPTY_FILE,
                "Track file contains no valid track data"
            );
        }

        int firstLineLength = lines.get(0).length();
        // Verify consistent line length
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.length() != firstLineLength) {
                throw new InvalidFileFormatException(
                    InvalidFileFormatException.FormatErrorType.INCONSISTENT_LINE_LENGTH,
                    "Line " + (i + 1) + " has length " + line.length() + ", expected " + firstLineLength
                );
            }
        }
    }

    /**
     * Validates that we have at least one car and no more than MAX_CARS.
     *
     * @throws InvalidFileFormatException if no cars or too many cars
     */
    private void validateCars() throws InvalidFileFormatException {
        if (cars.isEmpty()) {
            throw new InvalidFileFormatException(
                InvalidFileFormatException.FormatErrorType.NO_CARS,
                "No cars found in track file"
            );
        }
        if (cars.size() > MAX_CARS) {
            throw new InvalidFileFormatException(
                InvalidFileFormatException.FormatErrorType.TOO_MANY_CARS,
                "Too many cars in track file: " + cars.size() + ", maximum allowed: " + MAX_CARS
            );
        }
    }

    /**
     * Parse each character from the lines into the board array.
     * If we find a car (char not matching SpaceType chars), create a Car.
     *
     * @param lines List of non-empty lines read from the file
     * @throws InvalidFileFormatException if a duplicate car ID is found
     */
    private void parseTrack(List<String> lines) throws InvalidFileFormatException {
        for (int row = 0; row < height; row++) {
            String line = lines.get(row);
            for (int col = 0; col < width; col++) {
                char c = line.charAt(col);
                SpaceType spaceType = detectSpaceType(c);
                if (spaceType != null) {
                    // It's a known SpaceType
                    board[row][col] = spaceType;
                } else {
                    // This character is a car's ID (assuming valid unique ID)
                    // Under the car, we treat the space as TRACK (commonly).
                    board[row][col] = SpaceType.TRACK;
                    // Create the car
                    PositionVector startPos = new PositionVector(col, row);
                    Car newCar = new Car(c, startPos);

                    // Check for ID duplicates
                    if (cars.stream().anyMatch(car -> car.getId() == c)) {
                        throw new InvalidFileFormatException(
                            InvalidFileFormatException.FormatErrorType.DUPLICATE_CAR_ID,
                            "Duplicate car ID found: '" + c + "'"
                        );
                    }
                    cars.add(newCar);
                }
            }
        }
    }

    /**
     * Returns the SpaceType if the character c maps to a known track type,
     * or null if c is not a recognized SpaceType character (meaning it's a car).
     *
     * @param c character to detect
     * @return the detected SpaceType or null if not a recognized space character
     */
    private SpaceType detectSpaceType(char c) {
        return SpaceType.ofChar(c).orElse(null);
    }


    /**
     * Return the height (number of rows) of the track grid.
     *
     * @return the height of the track grid
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Return the width (number of columns) of the track grid.
     *
     * @return the width of the track grid
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Return the number of cars.
     *
     * @return the number of cars
     */
    @Override
    public int getCarCount() {
        return this.cars.size();
    }

    /**
     * Get instance of specified car.
     *
     * @param carIndex the zero-based carIndex number
     * @return the car instance at the given index
     * @throws IndexOutOfBoundsException if carIndex is negative or >= carCount
     */
    @Override
    public Car getCar(int carIndex) {
        if (carIndex < 0 || carIndex >= cars.size()) {
            throw new IndexOutOfBoundsException("Invalid car index: " + carIndex);
        }
        return cars.get(carIndex);
    }

    /**
     * Return the type of space at the given position.
     * If the location is outside the track bounds, it is considered a WALL.
     *
     * @param position the coordinates of the position to examine
     * @return the type of track position at the given location
     */
    @Override
    public SpaceType getSpaceTypeAtPosition(PositionVector position) {
        int x = position.getX();
        int y = position.getY();

        // If position is out of bounds, treat it as WALL
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return SpaceType.WALL;
        }
        return board[y][x];
    }

    /**
     * Gets the character representation for the given position of the racetrack, including cars.<br/>
     * This can be used for generating the {@link #toString()} representation of the racetrack.<br/>
     * If there is an active car (not crashed) at the given position, then the car id is returned.<br/>
     * If there is a crashed car at the position, {@link #CRASH_INDICATOR} is returned.<br/>
     * Otherwise, the space character for the given position is returned
     *
     * @param row row (y-value) of the racetrack position
     * @param col column (x-value) of the racetrack position
     * @return character representing the position (col,row) on the track
     *    or {@link Car#getId()} resp. {@link #CRASH_INDICATOR}, if a car is at the given position
     */
    @Override
    public char getCharRepresentationAtPosition(int row, int col) {
        // Check if any car occupies that position
        for (Car car : cars) {
            PositionVector carPos = car.getPosition();
            if (carPos.getY() == row && carPos.getX() == col) {
                return car.isCrashed() ? CRASH_INDICATOR : car.getId();
            }
        }
        // Otherwise return the space character
        return board[row][col].getSpaceChar();
    }

    /**
     * Return a String representation of the track, including the car locations and status.
     * Spaces are replaced with non-breaking spaces to ensure consistent character width
     * when displayed in the terminal.
     *
     * @return a String representation of the track
     */
    @Override
public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int row = 0; row < height; row++) {
        for (int col = 0; col < width; col++) {
            char c = getCharRepresentationAtPosition(row, col);
            sb.append(c);
        }
        sb.append(System.lineSeparator());
    }
    return sb.toString();
}
}