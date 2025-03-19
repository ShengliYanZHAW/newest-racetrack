package ch.zhaw.it.pm2.racetrack.given;

import java.io.File;

/**
 * Interface representing the mandatory functions of the configuration class.<br/>
 * IMPORTANT: This interface shall not be altered!<br/>
 * It specifies elements we use to test Racetrack for grading.<br/>
 * You may change or extend the provided default implementation<br/>
 * Full Javadoc can be found in the implementation file.
 */
public interface ConfigSpecification {

    /**
     * Get directory containing the racetrack files.
     * @return Directory file containing the racetrack files
     */
    File getTrackDirectory();

    /**
     * Specify directory containing the racetrack files.
     * @param trackDirectory containing the racetrack files
     * @throws NullPointerException if directory parameter is null
     * @throws IllegalArgumentException if directory does not exist or is not a directory
     */
    void setTrackDirectory(File trackDirectory);

    /**
     * Get directory containing the move list strategy files.
     * @return Directory file containing the files for the Move-List strategy
     */
    File getMoveDirectory();

    /**
     * Specify directory containing the move list strategy files.
     * @param moveDirectory containing the move list strategy files
     * @throws NullPointerException if directory parameter is null
     * @throws IllegalArgumentException if directory does not exist or is not a directory
     */
    void setMoveDirectory(File moveDirectory);

    /**
     * Get directory containing the coordinate list files for the Path-Follower strategy.
     * @return Directory file containing the coordinate list files for the Path-Follower strategy
     */
    File getFollowerDirectory();

    /**
     * Specify directory containing the path follower strategy files.
     * @param followerDirectory containing the path follower strategy files
     * @throws NullPointerException if directory parameter is null
     * @throws IllegalArgumentException if directory does not exist or is not a directory
     */
    void setFollowerDirectory(File followerDirectory);
}
