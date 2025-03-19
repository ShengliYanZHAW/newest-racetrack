package ch.zhaw.it.pm2.racetrack;

/**
 * Exception thrown when a file format is invalid for Track, MoveStrategy, or other game components.
 * This exception provides detailed information about what type of format error occurred.
 */
public class InvalidFileFormatException extends Exception {
    
    /**
     * The type of format error that occurred.
     */
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
     * @param errorType the type of format error that occurred
     */
    public InvalidFileFormatException(FormatErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    /**
     * Constructs a new InvalidFileFormatException with the specified error type and custom message.
     *
     * @param errorType the type of format error that occurred
     * @param message   a custom error message
     */
    public InvalidFileFormatException(FormatErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    /**
     * Constructs a new InvalidFileFormatException with a custom message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of this exception
     */
    public InvalidFileFormatException(String message, Throwable cause) {
        super(message, cause);
        this.errorType = FormatErrorType.UNKNOWN;
    }
    
    /**
     * Constructs a new InvalidFileFormatException with the specified error type, custom message, and cause.
     *
     * @param errorType the type of format error that occurred
     * @param message   a custom error message
     * @param cause     the cause of this exception
     */
    public InvalidFileFormatException(FormatErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    /**
     * Returns the type of format error that occurred.
     *
     * @return the error type
     */
    public FormatErrorType getErrorType() {
        return errorType;
    }

    /**
     * Enum representing possible format error types.
     */
    public enum FormatErrorType {
        /** The file is empty or contains no valid track data. */
        EMPTY_FILE("File contains no valid track data"),
        
        /** The lines in the track file have inconsistent lengths. */
        INCONSISTENT_LINE_LENGTH("Track lines have inconsistent lengths"),
        
        /** No cars were found in the track file. */
        NO_CARS("No cars found in track file"),
        
        /** The number of cars exceeds the maximum allowed. */
        TOO_MANY_CARS("Too many cars in track file"),
        
        /** A duplicate car ID was found in the track file. */
        DUPLICATE_CAR_ID("Duplicate car ID found in track file"),
        
        /** The waypoint format in the file is invalid. */
        INVALID_WAYPOINT_FORMAT("Invalid waypoint format in file"),
        
        /** The move format in the file is invalid. */
        INVALID_MOVE_FORMAT("Invalid move format in file"),
        
        /** An unknown file format error occurred. */
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
         * Returns the error message for this error type.
         *
         * @return the error message
         */
        public String getMessage() {
            return message;
        }
    }
}