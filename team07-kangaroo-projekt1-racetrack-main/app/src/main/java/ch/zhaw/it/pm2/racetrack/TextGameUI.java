package ch.zhaw.it.pm2.racetrack;

import ch.zhaw.it.pm2.racetrack.strategy.*;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Provides a text-based user interface for the Racetrack game.
 * This class handles game setup, strategy selection for each car,
 * and execution of the main game loop. It uses the Text-IO library
 * for user input and output.
 */
public class TextGameUI {
    private final TextIO textIO;
    private final TextTerminal<?> terminal;
    private final Config config;
    private Game game;
    private Track track;

    /**
     * Constructs a new TextGameUI instance.
     * Initializes the TextIO components and game configuration.
     */
    public TextGameUI() {
        this.textIO = TextIoFactory.getTextIO();
        this.terminal = textIO.getTextTerminal();
        this.config = new Config();
    }

    /**
     * Starts the Racetrack game.
     * Performs game setup, runs the main game loop, and cleans up after the game.
     */
    public void start() {
        terminal.println("Welcome to Racetrack!");
        setupGame();
        runGameLoop();
        textIO.newStringInputReader()
            .withMinLength(0)
            .read("Press Enter to exit");
        textIO.dispose("Thank you for playing Racetrack!");
    }

    /**
     * The main entry point of the Racetrack game application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        TextGameUI ui = new TextGameUI();
        ui.start();
    }

    /**
     * Sets up the game by selecting a track file and configuring car strategies.
     */
    private void setupGame() {
        try {
            File trackFile = selectTrackFile();
            track = new Track(trackFile);
            game = new Game(track);
            setupCarStrategies();
        } catch (IOException | InvalidFileFormatException e) {
            terminal.println("Error setting up the game: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Prompts the user to select a track file from the available options.
     *
     * @return the selected track file
     */
    private File selectTrackFile() {
        terminal.println("Please select a track file:");
        File[] trackFiles = config.getTrackDirectory().listFiles((dir, name) -> name.endsWith(".txt"));
        if (trackFiles == null || trackFiles.length == 0) {
            terminal.println("No track files found in " + config.getTrackDirectory().getAbsolutePath());
            System.exit(1);
        }
        List<String> trackNames = Arrays.stream(trackFiles)
            .map(File::getName)
            .toList();
        String selectedTrackName = textIO.newStringInputReader()
            .withNumberedPossibleValues(trackNames)
            .read("Select a track");
        return new File(config.getTrackDirectory(), selectedTrackName);
    }

    /**
     * Configures the move strategy for each car by prompting the user.
     */
    private void setupCarStrategies() {
        terminal.println("\nNow, choose a strategy for each car:");
        for (int i = 0; i < game.getCarCount(); i++) {
            char carId = game.getCarId(i);
            terminal.println("Car '" + carId + "' at position " + game.getCarPosition(i));
            List<MoveStrategy.StrategyType> availableStrategies = Arrays.asList(
                MoveStrategy.StrategyType.DO_NOT_MOVE,
                MoveStrategy.StrategyType.USER,
                MoveStrategy.StrategyType.MOVE_LIST,
                MoveStrategy.StrategyType.PATH_FOLLOWER
            );
            MoveStrategy.StrategyType selectedStrategy = textIO.newEnumInputReader(MoveStrategy.StrategyType.class)
                .withNumberedPossibleValues(availableStrategies)
                .read("Choose a strategy for Car '" + carId + "'");
            MoveStrategy strategy = createStrategy(selectedStrategy, i);
            game.setCarMoveStrategy(i, strategy);
        }
    }

    /**
     * Creates a move strategy based on the selected strategy type.
     *
     * @param strategyType the selected strategy type
     * @return the corresponding MoveStrategy instance
     */
    private MoveStrategy createStrategy(MoveStrategy.StrategyType strategyType, int carIndex) {
        return switch (strategyType) {
            case DO_NOT_MOVE -> new DoNotMoveStrategy();
            case USER -> new UserMoveStrategy();
            case MOVE_LIST -> createMoveListStrategy();
            case PATH_FOLLOWER -> createPathFollowerStrategy(carIndex);
            default -> throw new IllegalArgumentException("Strategy not implemented: " + strategyType);
        };
    }

    /**
     * Creates a MoveListStrategy by prompting the user to select a move file.
     *
     * @return the MoveListStrategy instance, or a DoNotMoveStrategy if no file is found
     */
    private MoveStrategy createMoveListStrategy() {
        terminal.println("Select a move file for the MOVE_LIST strategy:");
        File[] moveFiles = config.getMoveDirectory().listFiles((dir, name) -> name.endsWith(".txt"));
        if (moveFiles == null || moveFiles.length == 0) {
            terminal.println("No move files found in " + config.getMoveDirectory().getAbsolutePath());
            return new DoNotMoveStrategy();
        }
        List<String> moveFileNames = Arrays.stream(moveFiles)
            .map(File::getName)
            .toList();
        String selectedFileName = textIO.newStringInputReader()
            .withNumberedPossibleValues(moveFileNames)
            .read("Select a move file");
        File moveFile = new File(config.getMoveDirectory(), selectedFileName);
        return new MoveListStrategy(moveFile);
    }

    /**
     * Creates a PathFollowerStrategy by prompting the user to select a waypoint file.
     *
     * @param carIndex index of the car for which the strategy is being created
     * @return the PathFollowerStrategy instance, or a DoNotMoveStrategy if no file is found
     */
    private MoveStrategy createPathFollowerStrategy(int carIndex) {
        terminal.println("Select a waypoint file for the PATH_FOLLOWER strategy:");
        File[] followerFiles = config.getFollowerDirectory().listFiles((dir, name) -> name.endsWith(".txt"));
        if (followerFiles == null || followerFiles.length == 0) {
            terminal.println("No waypoint files found in " + config.getFollowerDirectory().getAbsolutePath());
            return new DoNotMoveStrategy();
        }
        List<String> followerFileNames = Arrays.stream(followerFiles)
            .map(File::getName)
            .toList();
        String selectedFileName = textIO.newStringInputReader()
            .withNumberedPossibleValues(followerFileNames)
            .read("Select a waypoint file");
        File selectedFile = new File(config.getFollowerDirectory(), selectedFileName);
        // Retrieve the corresponding car instance from the track
        Car car = track.getCar(carIndex);
        return new PathFollowerStrategy(selectedFile, car);
    }


    /**
     * Runs the main game loop until a winner is determined.
     */
    private void runGameLoop() {
        while (game.getWinner() == Game.NO_WINNER) {
            terminal.println("\nCurrent game state:");
            terminal.println(track.toString());
            int currentCarIndex = game.getCurrentCarIndex();
            char currentCarId = game.getCarId(currentCarIndex);
            terminal.println("Car '" + currentCarId + "' turn. Position: " +
                game.getCarPosition(currentCarIndex) + ", Velocity: " +
                game.getCarVelocity(currentCarIndex));
            Direction nextMove = game.nextCarMove(currentCarIndex);
            terminal.println("Car '" + currentCarId + "' will accelerate: " + nextMove);
            game.doCarTurn(nextMove);
            if (game.getWinner() != Game.NO_WINNER) {
                announceWinner();
                break;
            }
            game.switchToNextActiveCar();
        }
    }

    /**
     * Announces the winner of the game.
     */
    private void announceWinner() {
        int winnerIndex = game.getWinner();
        char winnerId = game.getCarId(winnerIndex);
        terminal.println("\n🏁 GAME OVER 🏁");
        terminal.println("Car '" + winnerId + "' has won the race!");
        terminal.println("\nFinal game state:");
        terminal.println(track.toString());
    }
}
