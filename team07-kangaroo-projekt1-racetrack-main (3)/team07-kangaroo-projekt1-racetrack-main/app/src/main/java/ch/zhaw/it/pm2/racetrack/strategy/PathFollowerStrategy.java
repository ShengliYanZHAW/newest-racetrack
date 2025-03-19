package ch.zhaw.it.pm2.racetrack.strategy;

import ch.zhaw.it.pm2.racetrack.Car;
import ch.zhaw.it.pm2.racetrack.Direction;
import ch.zhaw.it.pm2.racetrack.InvalidFileFormatException;
import ch.zhaw.it.pm2.racetrack.PositionVector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Implements a strategy that allows a car to follow a pre-defined list of waypoints.
 * The strategy reads waypoint coordinates from a file and computes the necessary
 * acceleration vectors to guide the car through these points.
 * <p>
 * The waypoint file should contain one waypoint per line, with each waypoint in
 * the format "(X:val, Y:val)" where val is an integer coordinate.
 * <p>
 * When a car reaches a waypoint, it moves on to the next waypoint. After all waypoints
 * are processed, the car maintains its current velocity (returns Direction.NONE).
 */
public class PathFollowerStrategy implements MoveStrategy {
    /** List of waypoints to follow. */
    private final List<PositionVector> waypoints;
    
    /** Index of the current waypoint being targeted. */
    private int currentWaypointIndex;
    
    /** Reference to the car using this strategy. */
    private final Car car;

    /**
     * Constructor that initializes the PATH_FOLLOWER strategy by reading waypoints from a file.
     *
     * @param waypointFile file containing the waypoints (one per line, in the format (X:val, Y:val))
     * @param car reference to the car using this strategy
     * @throws IllegalArgumentException if the waypoint file or car reference is null,
     *                                  or if the file does not contain valid waypoints.
     */
    public PathFollowerStrategy(File waypointFile, Car car) {
        if (waypointFile == null || car == null) {
            throw new IllegalArgumentException("Waypoint file and car reference must not be null");
        }
        this.car = car;
        this.waypoints = loadWaypoints(waypointFile);
        if (this.waypoints.isEmpty()) {
            throw new IllegalArgumentException("Waypoint file does not contain any waypoints: " + waypointFile.getAbsolutePath());
        }
        this.currentWaypointIndex = 0;
    }

    /**
     * Reads all the waypoints from the specified file.
     *
     * @param file file containing the waypoints
     * @return a list of PositionVector objects read from the file
     * @throws IllegalArgumentException if the file cannot be found or contains invalid waypoint formats
     */
    private List<PositionVector> loadWaypoints(File file) {
        List<PositionVector> list = new ArrayList<>();
        try (Scanner scanner = new Scanner(file)) {
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                try {
                    list.add(parseWaypoint(line));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(
                        "Invalid waypoint format at line " + lineNumber + ": " + line, e);
                }
            }
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Waypoint file not found: " + file.getAbsolutePath(), e);
        }
        return list;
    }

    /**
     * Parses a line from the file into a PositionVector object.
     *
     * @param line the line to parse
     * @return the PositionVector represented by the line
     * @throws IllegalArgumentException if the line does not match the expected format
     */
    private PositionVector parseWaypoint(String line) {
        try {
            return PositionVector.ofString(line);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid waypoint format: " + line, e);
        }
    }

    /**
     * Computes and returns the next move (acceleration) required to follow the list of waypoints.
     * If the car reaches the current waypoint, it moves to the next one.
     * If there are no more waypoints, it returns Direction.NONE to maintain constant velocity.
     *
     * @return Direction corresponding to the acceleration to apply
     */
    @Override
    public Direction nextMove() {
        if (currentWaypointIndex < waypoints.size()) {
            PositionVector currentTarget = waypoints.get(currentWaypointIndex);
            // If the car has reached the current waypoint, move to the next one
            if (car.getPosition().equals(currentTarget)) {
                currentWaypointIndex++;
                if (currentWaypointIndex < waypoints.size()) {
                    currentTarget = waypoints.get(currentWaypointIndex);
                } else {
                    return Direction.NONE;
                }
            }
            // Compute the required acceleration to reach the waypoint:
            // acceleration = waypoint - (position + velocity)
            int desiredAx = clamp(currentTarget.getX() - car.getPosition().getX() - car.getVelocity().getX());
            int desiredAy = clamp(currentTarget.getY() - car.getPosition().getY() - car.getVelocity().getY());
            return Direction.ofVector(new PositionVector(desiredAx, desiredAy));
        }
        // No remaining waypoints: maintain constant velocity
        return Direction.NONE;
    }

    /**
     * Limits a value to the range [-1, 1].
     *
     * @param value the value to clamp
     * @return the clamped value: -1 if value < -1, 1 if value > 1, otherwise value
     */
    private int clamp(int value) {
        if (value > 1) return 1;
        if (value < -1) return -1;
        return value;
    }
}