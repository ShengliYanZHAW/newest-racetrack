package ch.zhaw.it.pm2.racetrack;

import ch.zhaw.it.pm2.racetrack.strategy.*;
import java.io.File;
import java.util.Map;
import java.util.Objects;

/**
 * Factory for creating move strategies based on strategy type and parameters.
 * This class centralizes the logic for instantiating different strategy implementations,
 * ensuring proper parameter validation and error handling.
 */
public class StrategyFactory {
    
    private final Config config;
    
    /**
     * Constructs a new StrategyFactory with the specified configuration.
     *
     * @param config the game configuration
     * @throws NullPointerException if config is null
     */
    public StrategyFactory(Config config) {
        this.config = Objects.requireNonNull(config, "Config must not be null");
    }
    
    /**
     * Creates a move strategy based on the specified type and parameters.
     *
     * @param strategyType the type of strategy to create
     * @param params additional parameters for strategy creation
     * @return the created MoveStrategy
     * @throws InvalidFileFormatException if strategy creation fails due to invalid file format
     * @throws IllegalArgumentException if required parameters are missing or invalid
     * @throws NullPointerException if strategyType is null
     */
    public MoveStrategy createStrategy(MoveStrategy.StrategyType strategyType, Map<String, Object> params) 
            throws InvalidFileFormatException {
        Objects.requireNonNull(strategyType, "Strategy type must not be null");
        
        switch (strategyType) {
            case DO_NOT_MOVE:
                return new DoNotMoveStrategy();
                
            case USER:
                return new UserMoveStrategy();
                
            case MOVE_LIST:
                File moveFile = getMoveFile(params);
                return new MoveListStrategy(moveFile);
                
            case PATH_FOLLOWER:
                File followerFile = getFollowerFile(params);
                Car car = getCar(params);
                return new PathFollowerStrategy(followerFile, car);
                
            case PATH_FINDER:
                Car pathFinderCar = getCar(params);
                Track track = getTrack(params);
                return new PathFinderStrategy(pathFinderCar, track);
                
            default:
                throw new IllegalArgumentException("Strategy not implemented: " + strategyType);
        }
    }
    
    /**
     * Extracts the move file from the parameter map.
     *
     * @param params the parameter map
     * @return the move file
     * @throws IllegalArgumentException if the move file parameter is missing or invalid
     */
    private File getMoveFile(Map<String, Object> params) {
        if (params == null) {
            throw new IllegalArgumentException("Parameters map must not be null");
        }
        
        Object fileObj = params.get("moveFile");
        if (fileObj instanceof File) {
            return (File) fileObj;
        } else if (fileObj instanceof String) {
            // If a string is provided, interpret it as a filename in the moves directory
            return new File(config.getMoveDirectory(), (String) fileObj);
        }
        
        throw new IllegalArgumentException("Move file not provided or invalid");
    }
    
    /**
     * Extracts the follower file from the parameter map.
     *
     * @param params the parameter map
     * @return the follower file
     * @throws IllegalArgumentException if the follower file parameter is missing or invalid
     */
    private File getFollowerFile(Map<String, Object> params) {
        if (params == null) {
            throw new IllegalArgumentException("Parameters map must not be null");
        }
        
        Object fileObj = params.get("followerFile");
        if (fileObj instanceof File) {
            return (File) fileObj;
        } else if (fileObj instanceof String) {
            // If a string is provided, interpret it as a filename in the follower directory
            return new File(config.getFollowerDirectory(), (String) fileObj);
        }
        
        throw new IllegalArgumentException("Follower file not provided or invalid");
    }
    
    /**
     * Extracts the car from the parameter map.
     *
     * @param params the parameter map
     * @return the car
     * @throws IllegalArgumentException if the car parameter is missing or invalid
     */
    private Car getCar(Map<String, Object> params) {
        if (params == null) {
            throw new IllegalArgumentException("Parameters map must not be null");
        }
        
        Object carObj = params.get("car");
        if (carObj instanceof Car) {
            return (Car) carObj;
        }
        
        throw new IllegalArgumentException("Car not provided or invalid");
    }
    
    /**
     * Extracts the track from the parameter map.
     *
     * @param params the parameter map
     * @return the track
     * @throws IllegalArgumentException if the track parameter is missing or invalid
     */
    private Track getTrack(Map<String, Object> params) {
        if (params == null) {
            throw new IllegalArgumentException("Parameters map must not be null");
        }
        
        Object trackObj = params.get("track");
        if (trackObj instanceof Track) {
            return (Track) trackObj;
        }
        
        throw new IllegalArgumentException("Track not provided or invalid");
    }
}