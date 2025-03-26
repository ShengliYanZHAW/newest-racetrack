package ch.zhaw.it.pm2.racetrack;

import ch.zhaw.it.pm2.racetrack.given.TrackSpecification;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the racetrack board. The board consists of a rectangular grid of columns and rows.
 * The origin is at the top left, with the x-axis pointing right and the y-axis pointing downward.
 * Each position is represented by a PositionVector and holds a SpaceType.
 * Valid SpaceTypes include WALL, TRACK, FINISH_LEFT, FINISH_RIGHT, FINISH_UP, and FINISH_DOWN.
 * Any other character indicates a car's starting position with a unique identifier.
 * The track file must contain a rectangular block of text; leading empty lines are ignored and reading stops at
 * the first empty line after data or at file end.
 * An InvalidFileFormatException is thrown if the file contains no track lines, inconsistent line lengths, no cars,
 * or more than TrackSpecification.MAX_CARS cars.
 * The toString method returns a string representing the current state of the race including car positions and status.
 */
public class Track implements TrackSpecification {

    private final SpaceType[][] board;
    private final List<Car> cars = new ArrayList<>();
    private final int width;
    private final int height;

    /**
     * Constructs a Track from the specified track file.
     * @param trackFile a file containing the track data; must not be null
     * @throws IOException if the file cannot be opened or read
     * @throws InvalidFileFormatException if the file contains invalid track data
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

    private List<String> readNonEmptyLines(File file) throws IOException {
        List<String> resultLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null && line.trim().isEmpty()) {}
            if (line != null) {
                resultLines.add(line);
                while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                    resultLines.add(line);
                }
            }
        }
        return resultLines;
    }

    private void validateTrackLines(List<String> lines) throws InvalidFileFormatException {
        if (lines.isEmpty()) {
            throw new InvalidFileFormatException(
                InvalidFileFormatException.FormatErrorType.EMPTY_FILE,
                "Track file contains no valid track data"
            );
        }
        int firstLineLength = lines.get(0).length();
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

    private void parseTrack(List<String> lines) throws InvalidFileFormatException {
        for (int row = 0; row < height; row++) {
            String line = lines.get(row);
            for (int col = 0; col < width; col++) {
                char c = line.charAt(col);
                SpaceType spaceType = detectSpaceType(c);
                if (spaceType != null) {
                    board[row][col] = spaceType;
                } else {
                    board[row][col] = SpaceType.TRACK;
                    PositionVector startPos = new PositionVector(col, row);
                    Car newCar = new Car(c, startPos);
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

    private SpaceType detectSpaceType(char c) {
        return SpaceType.ofChar(c).orElse(null);
    }

    /**
     * Returns the height (number of rows) of the track grid.
     * @return the height of the track grid
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the width (number of columns) of the track grid.
     * @return the width of the track grid
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the number of cars on the track.
     * @return the number of cars
     */
    @Override
    public int getCarCount() {
        return cars.size();
    }

    /**
     * Returns the car at the specified index.
     * @param carIndex the zero-based index of the car
     * @return the car instance at the specified index
     * @throws IndexOutOfBoundsException if carIndex is negative or not less than the number of cars
     */
    @Override
    public Car getCar(int carIndex) {
        if (carIndex < 0 || carIndex >= cars.size()) {
            throw new IndexOutOfBoundsException("Invalid car index: " + carIndex);
        }
        return cars.get(carIndex);
    }

    /**
     * Returns the type of space at the specified position.
     * If the position is outside the track bounds, WALL is returned.
     * @param position the position to check
     * @return the SpaceType at the given position
     */
    @Override
    public SpaceType getSpaceTypeAtPosition(PositionVector position) {
        int x = position.getX();
        int y = position.getY();
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return SpaceType.WALL;
        }
        return board[y][x];
    }

    /**
     * Returns the character representation for the specified track position.
     * If an active car occupies the position, its identifier is returned.
     * If a crashed car occupies the position, the CRASH_INDICATOR is returned.
     * Otherwise, the character corresponding to the SpaceType is returned.
     * @param row the row (y-coordinate) of the position
     * @param col the column (x-coordinate) of the position
     * @return the character representing the track position
     */
    @Override
    public char getCharRepresentationAtPosition(int row, int col) {
        for (Car car : cars) {
            PositionVector carPos = car.getPosition();
            if (carPos.getY() == row && carPos.getX() == col) {
                return car.isCrashed() ? CRASH_INDICATOR : car.getId();
            }
        }
        return board[row][col].getSpaceChar();
    }

    /**
     * Returns a string representation of the track including car positions and status.
     * @return a string representing the current state of the track
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                sb.append(getCharRepresentationAtPosition(row, col));
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}
