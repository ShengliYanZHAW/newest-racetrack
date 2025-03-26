package ch.zhaw.it.pm2.racetrack;

import ch.zhaw.it.pm2.racetrack.strategy.MoveStrategy;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

/**
 * Controls the game flow and coordinates interactions between UI, game model, and strategies.
 * Responsible for setup, execution, and termination of game sessions.
 */
public class GameController {
    private Game game;
    private Track track;
    private final StrategyFactory strategyFactory;
    private final TrackFactory trackFactory;
    private int turnsExecuted;

    /**
     * Constructs a new GameController with the specified factories.
     *
     * @param strategyFactory factory for creating move strategies
     * @param trackFactory factory for creating tracks
     * @throws NullPointerException if either parameter is null
     */
    public GameController(StrategyFactory strategyFactory, TrackFactory trackFactory) {
        this.strategyFactory = Objects.requireNonNull(strategyFactory, "Strategy factory must not be null");
        this.trackFactory = Objects.requireNonNull(trackFactory, "Track factory must not be null");
        this.turnsExecuted = 0;
    }

    /**
     * Initializes a new game with the specified track file.
     *
     * @param trackFile the file containing track data
     * @return true if initialization was successful
     * @throws IOException if track file reading fails
     * @throws InvalidFileFormatException if track file format is invalid
     * @throws IllegalArgumentException if trackFile is null
     */
    public boolean initializeGame(File trackFile) throws IOException, InvalidFileFormatException {
        this.track = trackFactory.createTrack(trackFile);
        this.game = new Game(track);
        this.turnsExecuted = 0;
        return true;
    }

    /**
     * Sets the strategy for a specific car.
     *
     * @param carIndex the index of the car
     * @param strategyType the type of strategy to use
     * @param strategyParams additional parameters for strategy creation
     * @return true if strategy was set successfully
     * @throws InvalidFileFormatException if strategy creation fails due to invalid file format
     * @throws IllegalArgumentException if required parameters are missing or invalid
     * @throws IndexOutOfBoundsException if carIndex is out of range
     */
    public boolean setCarStrategy(int carIndex, MoveStrategy.StrategyType strategyType, 
                                 Map<String, Object> strategyParams) throws InvalidFileFormatException {
        if (game == null) {
            throw new IllegalStateException("Game has not been initialized");
        }
        
        Map<String, Object> params = strategyParams != null ? strategyParams : new HashMap<>();
        
        // Add car and track to parameters if needed
        if (strategyType == MoveStrategy.StrategyType.PATH_FOLLOWER || 
            strategyType == MoveStrategy.StrategyType.PATH_FINDER) {
            params.put("car", track.getCar(carIndex));
        }
        
        if (strategyType == MoveStrategy.StrategyType.PATH_FINDER) {
            params.put("track", track);
        }
        
        MoveStrategy strategy = strategyFactory.createStrategy(strategyType, params);
        game.setCarMoveStrategy(carIndex, strategy);
        return true;
    }

    /**
     * Executes a single turn for the current car.
     *
     * @return true if the game continues, false if the game has ended
     * @throws IllegalStateException if the game hasn't been initialized
     */
    public boolean executeTurn() {
        if (game == null) {
            throw new IllegalStateException("Game has not been initialized");
        }
        
        int currentCarIndex = game.getCurrentCarIndex();
        Direction nextMove = game.nextCarMove(currentCarIndex);
        game.doCarTurn(nextMove);
        turnsExecuted++;
        
        if (game.getWinner() != Game.NO_WINNER) {
            return false; // Game has ended
        }
        
        game.switchToNextActiveCar();
        return true; // Game continues
    }

    /**
     * Returns the current game instance.
     *
     * @return the current game
     * @throws IllegalStateException if the game hasn't been initialized
     */
    public Game getGame() {
        if (game == null) {
            throw new IllegalStateException("Game has not been initialized");
        }
        return game;
    }

    /**
     * Returns the current track instance.
     *
     * @return the current track
     * @throws IllegalStateException if the track hasn't been initialized
     */
    public Track getTrack() {
        if (track == null) {
            throw new IllegalStateException("Track has not been initialized");
        }
        return track;
    }
    
    /**
     * Returns the number of turns executed in the current game.
     *
     * @return the number of turns executed
     */
    public int getTurnsExecuted() {
        return turnsExecuted;
    }
    
    /**
     * Returns the winner of the game.
     *
     * @return the index of the winning car, or Game.NO_WINNER if the game is still in progress
     * @throws IllegalStateException if the game hasn't been initialized
     */
    public int getWinner() {
        if (game == null) {
            throw new IllegalStateException("Game has not been initialized");
        }
        return game.getWinner();
    }
    
    /**
     * Returns whether the game has a winner.
     *
     * @return true if the game has a winner, false otherwise
     * @throws IllegalStateException if the game hasn't been initialized
     */
    public boolean hasWinner() {
        return getWinner() != Game.NO_WINNER;
    }
}