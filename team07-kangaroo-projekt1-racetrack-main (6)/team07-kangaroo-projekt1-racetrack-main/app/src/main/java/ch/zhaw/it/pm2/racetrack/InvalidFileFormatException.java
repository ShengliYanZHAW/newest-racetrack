package ch.zhaw.it.pm2.racetrack;

/**
 * Exception thrown when a file format is invalid for Track, MoveStrategy, or other game components.
 * This exception provides detailed information about the type of format error that occurred.
 */
public class InvalidFileFormatException extends Exception {

    private final FormatErrorType errorType;

    /**
     * Constructs a new InvalidFileFormatException with the default error type UNKNOWN.
     */
    public InvalidFileFormatException() {
        this(FormatErrorType.UNKNOWN);
    }

    /**
     * Constructs a new InvalidFileFormatException with the specified error type.
     *
     * @param errorType the type of format error that occurred.
     */
    public InvalidFileFormatException(FormatErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    /**
     * Constructs a new InvalidFileFormatException with the specified error type and custom message.
     *
     * @param errorType the type of format error that occurred.
     * @param message   a custom error message.
     */
    public InvalidFileFormatException(FormatErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    /**
     * Constructs a new InvalidFileFormatException with a custom message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause of this exception.
     */
    public InvalidFileFormatException(String message, Throwable cause) {
        super(message, cause);
        this.errorType = FormatErrorType.UNKNOWN;
    }

    /**
     * Constructs a new InvalidFileFormatException with the specified error type, custom message, and cause.
     *
     * @param errorType the type of format error that occurred.
     * @param message   a custom error message.
     * @param cause     the cause of this exception.
     */
    public InvalidFileFormatException(FormatErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    /**
     * Returns the format error type associated with this exception.
     *
     * @return the format error type.
     */
    public FormatErrorType getErrorType() {
        return errorType;
    }

    /**
     * Enum representing possible format error types.
     */
    public static enum FormatErrorType {
        EMPTY_FILE("File contains no valid track data"),
        INCONSISTENT_LINE_LENGTH("Track lines have inconsistent lengths"),
        NO_CARS("No cars found in track file"),
        TOO_MANY_CARS("Too many cars in track file"),
        DUPLICATE_CAR_ID("Duplicate car ID found in track file"),
        INVALID_WAYPOINT_FORMAT("Invalid waypoint format in file"),
        INVALID_MOVE_FORMAT("Invalid move format in file"),
        UNKNOWN("Unknown file format error");

        private final String message;

        /**
         * Constructs a new FormatErrorType with the specified error message.
         *
         * @param message the error message for this error type
         */
        FormatErrorType(String message) {
            this.message = message;
        }

        /**
         * Returns the error message associated with the format error type.
         *
         * @return the error message.
         */
        public String getMessage() {
            return message;
        }
    }
}
