package ch.zhaw.it.pm2.racetrack;

import ch.zhaw.it.pm2.racetrack.given.ConfigSpecification;

import java.io.File;
import java.util.Objects;

/**
 * <p>Manages global configuration values and options of the application.</p>
 * <p>The default implementation provides the directories to read the racetracks and strategy files from.</p>
 * <p>By default directories are placed in the project-root (resp. working dir when starting the application).</p>
 * <ul>
 *     <li><code>&lt;project-root&gt;/tracks/</code> contains the racetrack files</li>
 *     <li><code>&lt;project-root&gt;/moves/</code> contains the move files for the Move-List strategy</li>
 *     <li><code>&lt;project-root&gt;/follower/</code> contains the coordinate files for the Path-Follower strategy</li>
 * </ul>
 * <p>The content of the directory (Array of File objects) can be read using <code>{@link File#listFiles()}</code>
 * or {@link File#list()} to get an array of filenames only.</p>
 */
public class Config implements ConfigSpecification {

    /** Directory containing the track files. */
    private File trackDirectory = checkExistingDirectoryOrThrow(new File("tracks"));

    /** Directory containing the files for the Move-List strategy. */
    private File moveDirectory = checkExistingDirectoryOrThrow(new File("moves"));

    /** Directory containing the files for the Path-Follower strategy. */
    private File followerDirectory = checkExistingDirectoryOrThrow(new File("follower"));

    /**
     * Get the directory file containing the racetrack files.
     * @return Directory file containing the racetrack files
     */
    public File getTrackDirectory() {
        return trackDirectory;
    }

    /**
     * Specify directory containing the racetrack files.
     * @param trackDirectory containing the racetrack files
     * @throws NullPointerException if directory parameter is null
     * @throws IllegalArgumentException if directory does not exist or is not a directory
     */
    public void setTrackDirectory(File trackDirectory) {
        this.trackDirectory = checkExistingDirectoryOrThrow(trackDirectory);
    }

    /**
     * Get the directory file containing the files for the Move-List strategy
     * @return Directory file containing the files for the Move-List strategy
     */
    public File getMoveDirectory() {
        return moveDirectory;
    }

    /**
     * Specify directory containing the move list strategy files.
     * @param moveDirectory containing the move list strategy files
     * @throws NullPointerException if directory parameter is null
     * @throws IllegalArgumentException if directory does not exist or is not a directory
     */
    public void setMoveDirectory(File moveDirectory) {
        this.moveDirectory = checkExistingDirectoryOrThrow(moveDirectory);
    }

    /**
     * Get the directory file containing the coordinate list files for the Path-Follower strategy
     * @return Directory file containing the coordinate list files for the Path-Follower strategy
     */
    public File getFollowerDirectory() {
        return followerDirectory;
    }

    /**
     * Specify directory containing the path follower strategy files.
     * @param followerDirectory containing the path follower strategy files
     * @throws NullPointerException if directory parameter is null
     * @throws IllegalArgumentException if directory does not exist or is not a directory
     */
    public void setFollowerDirectory(File followerDirectory) {
        this.followerDirectory = checkExistingDirectoryOrThrow(followerDirectory);
    }

    /**
     * Validate given path if it exists and is a directory.
     * @param directory directory to validate
     * @return validated directory if it exists
     * @throws NullPointerException if directory parameter is null
     * @throws IllegalArgumentException if directory does not exist or is not a directory
     */
    private File checkExistingDirectoryOrThrow(File directory) {
        Objects.requireNonNull(directory, "Directory must not be null");
        if (!directory.exists())
            throw new IllegalArgumentException("%s does not exist".formatted(directory.getAbsolutePath()));
        if (!directory.isDirectory())
            throw new IllegalArgumentException("%s is not a directory".formatted(directory.getAbsolutePath()));
        return directory;
    }
}
