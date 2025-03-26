package ch.zhaw.it.pm2.racetrack;

import ch.zhaw.it.pm2.racetrack.given.ConfigSpecification;

import java.io.File;
import java.util.Objects;

/**
 * Manages global configuration values and options of the application.
 * The default implementation provides directories for racetrack files, move strategy files,
 * and path follower files. By default, these directories are located in the project root
 * (or working directory) when the application starts.
 *
 * Expected directory structure:
 * - {project-root}/tracks/ contains the racetrack files.
 * - {project-root}/moves/ contains the move files for the Move-List strategy.
 * - {project-root}/follower/ contains the coordinate files for the Path-Follower strategy.
 *
 * The directory contents can be obtained using File.listFiles() or File.list().
 */
public class Config implements ConfigSpecification {

    private File trackDirectory = checkExistingDirectoryOrThrow(new File("tracks"));
    private File moveDirectory = checkExistingDirectoryOrThrow(new File("moves"));
    private File followerDirectory = checkExistingDirectoryOrThrow(new File("follower"));

    /**
     * Returns the directory containing the racetrack files.
     *
     * @return the track directory.
     */
    public File getTrackDirectory() {
        return trackDirectory;
    }

    /**
     * Sets the directory containing the racetrack files.
     *
     * @param trackDirectory the track directory to set; must not be null and must exist as a directory.
     * @throws NullPointerException if the provided directory is null.
     * @throws IllegalArgumentException if the provided directory does not exist or is not a directory.
     */
    public void setTrackDirectory(File trackDirectory) {
        this.trackDirectory = checkExistingDirectoryOrThrow(trackDirectory);
    }

    /**
     * Returns the directory containing the move strategy files.
     *
     * @return the move directory.
     */
    public File getMoveDirectory() {
        return moveDirectory;
    }

    /**
     * Sets the directory containing the move strategy files.
     *
     * @param moveDirectory the move directory to set; must not be null and must exist as a directory.
     * @throws NullPointerException if the provided directory is null.
     * @throws IllegalArgumentException if the provided directory does not exist or is not a directory.
     */
    public void setMoveDirectory(File moveDirectory) {
        this.moveDirectory = checkExistingDirectoryOrThrow(moveDirectory);
    }

    /**
     * Returns the directory containing the files for the Path-Follower strategy.
     *
     * @return the follower directory.
     */
    public File getFollowerDirectory() {
        return followerDirectory;
    }

    /**
     * Sets the directory containing the files for the Path-Follower strategy.
     *
     * @param followerDirectory the follower directory to set; must not be null and must exist as a directory.
     * @throws NullPointerException if the provided directory is null.
     * @throws IllegalArgumentException if the provided directory does not exist or is not a directory.
     */
    public void setFollowerDirectory(File followerDirectory) {
        this.followerDirectory = checkExistingDirectoryOrThrow(followerDirectory);
    }

    /**
     * Validates that the given directory exists and is a directory.
     *
     * @param directory the directory to validate; must not be null.
     * @return the validated directory.
     * @throws NullPointerException if the directory is null.
     * @throws IllegalArgumentException if the directory does not exist or is not a directory.
     */
    private static File checkExistingDirectoryOrThrow(File directory) {
        Objects.requireNonNull(directory, "Directory must not be null");
        if (!directory.exists()) {
            throw new IllegalArgumentException(String.format("%s does not exist", directory.getAbsolutePath()));
        }
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(String.format("%s is not a directory", directory.getAbsolutePath()));
        }
        return directory;
    }
}
