package ch.zhaw.it.pm2.racetrack;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Factory for creating Track instances from track files.
 * This class centralizes the logic for track creation and validation,
 * encapsulating the complex initialization logic.
 */
public class TrackFactory {
    
    /**
     * Creates a Track from the specified track file.
     *
     * @param trackFile the file containing track data
     * @return the created Track
     * @throws IOException if file reading fails
     * @throws InvalidFileFormatException if track file format is invalid
     * @throws IllegalArgumentException if trackFile is null
     * @throws NullPointerException if trackFile is null
     */
    public Track createTrack(File trackFile) throws IOException, InvalidFileFormatException {
        Objects.requireNonNull(trackFile, "Track file must not be null");
        
        if (!trackFile.exists()) {
            throw new IOException("Track file does not exist: " + trackFile.getAbsolutePath());
        }
        
        if (!trackFile.isFile()) {
            throw new IOException("Track path is not a file: " + trackFile.getAbsolutePath());
        }
        
        if (!trackFile.canRead()) {
            throw new IOException("Track file cannot be read: " + trackFile.getAbsolutePath());
        }
        
        return new Track(trackFile);
    }
    
    /**
     * Creates a Track from a file in the specified directory.
     *
     * @param trackDirectory the directory containing track files
     * @param fileName the name of the track file
     * @return the created Track
     * @throws IOException if file reading fails
     * @throws InvalidFileFormatException if track file format is invalid
     * @throws IllegalArgumentException if any parameter is null or if the file doesn't exist
     */
    public Track createTrack(File trackDirectory, String fileName) throws IOException, InvalidFileFormatException {
        Objects.requireNonNull(trackDirectory, "Track directory must not be null");
        Objects.requireNonNull(fileName, "File name must not be null");
        
        if (!trackDirectory.exists() || !trackDirectory.isDirectory()) {
            throw new IOException("Track directory does not exist or is not a directory: " + 
                                 trackDirectory.getAbsolutePath());
        }
        
        File trackFile = new File(trackDirectory, fileName);
        return createTrack(trackFile);
    }
}
