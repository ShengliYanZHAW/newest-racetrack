package ch.zhaw.it.pm2.racetrack.strategy;

import ch.zhaw.it.pm2.racetrack.Car;
import ch.zhaw.it.pm2.racetrack.Direction;
import ch.zhaw.it.pm2.racetrack.PositionVector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Implements a strategy that allows a car to follow a predefined list of waypoints.
 * The strategy reads waypoint coordinates from a file and computes the necessary acceleration
 * to guide the car through these points.
 *
 * The waypoint file should contain one waypoint per line in the format "(X:val, Y:val)".
 * When the car reaches a waypoint, it advances to the next. When all waypoints are processed,
 * the strategy returns Direction NONE.
 */
public class PathFollowerStrategy implements MoveStrategy {

    private final List<PositionVector> waypoints;
    private int currentWaypointIndex;
    private final Car car;

    /**
     * Constructs a new PathFollowerStrategy by reading waypoints from the specified file.
     *
     * @param waypointFile file containing the waypoints; must not be null
     * @param car reference to the car using this strategy; must not be null
     * @throws IllegalArgumentException if the waypoint file or car reference is null, or if no valid waypoints are found
     */
    public PathFollowerStrategy(File waypointFile, Car car) {
        if (waypointFile == null || car == null) {
            throw new IllegalArgumentException("Waypoint file and car reference must not be null");
        }
        this.car = car;
        this.waypoints = loadWaypoints(waypointFile);
        if (this.waypoints.isEmpty()) {
            throw new IllegalArgumentException("Waypoint file does not contain any waypoints: "
                + waypointFile.getAbsolutePath());
        }
        this.currentWaypointIndex = 0;
    }

    /**
     * Reads all waypoints from the specified file.
     *
     * @param file the file containing the waypoints
     * @return a list of PositionVector objects representing the waypoints
     * @throws IllegalArgumentException if the file is not found or contains invalid waypoint formats
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
     * Parses a line from the waypoint file into a PositionVector.
     *
     * @param line the line to parse
     * @return the corresponding PositionVector
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
     * Computes and returns the next move (acceleration) required to follow the waypoints.
     * If the current waypoint is reached, the strategy advances to the next waypoint.
     * When all waypoints are processed, returns Direction NONE.
     *
     * @return the {@link Direction} for the next move
     */
    @Override
    public Direction nextMove() {
        if (currentWaypointIndex < waypoints.size()) {
            PositionVector currentTarget = waypoints.get(currentWaypointIndex);
            if (car.getPosition().equals(currentTarget)) {
                currentWaypointIndex++;
                if (currentWaypointIndex < waypoints.size()) {
                    currentTarget = waypoints.get(currentWaypointIndex);
                } else {
                    return Direction.NONE;
                }
            }
            int desiredAx = clamp(
                currentTarget.getX() - car.getPosition().getX() - car.getVelocity().getX());
            int desiredAy = clamp(
                currentTarget.getY() - car.getPosition().getY() - car.getVelocity().getY());
            return Direction.ofVector(new PositionVector(desiredAx, desiredAy));
        }
        return Direction.NONE;
    }

    /**
     * Clamps a value to the range [-1, 1].
     *
     * @param value the value to clamp
     * @return -1 if value is less than -1, 1 if greater than 1, otherwise the value itself
     */
    private int clamp(int value) {
        if (value > 1) return 1;
        if (value < -1) return -1;
        return value;
    }
}
