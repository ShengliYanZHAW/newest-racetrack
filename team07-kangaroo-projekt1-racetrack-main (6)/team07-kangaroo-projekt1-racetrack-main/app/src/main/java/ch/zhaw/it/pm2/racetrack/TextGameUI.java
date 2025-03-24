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
    /** The TextIO instance for handling all text I/O operations. */
    private final TextIO textIO;

    /** The text terminal for output display. */
    private final TextTerminal<?> terminal;

    /** The configuration for the game. */
    private final Config config;

    /** The game controller. */
    private Game game;

    /** The track used for the game. */
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
        terminal.println("======================");

        try {
            setupGame();
            runGameLoop();
        } catch (Exception e) {
            terminal.println("Error during game execution: " + e.getMessage());
            if (e.getCause() != null) {
                terminal.println("Caused by: " + e.getCause().getMessage());
            }
        }

        textIO.newStringInputReader()
            .withMinLength(0)
            .read("\nPress Enter to exit");
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
     * Configures the text terminal for proper track display.
     * This ensures consistent character width and proper display of the track.
     */
    private void configureTerminalForTrackDisplay() {
        // If using the Swing-based terminal from Text-IO, we can set a monospaced font
        if (terminal.getClass().getSimpleName().contains("Swing")) {
            try {
                // Use reflection to access and configure the terminal's swing component
                java.lang.reflect.Method getTextPane = terminal.getClass().getMethod("getTextPane");
                Object textPane = getTextPane.invoke(terminal);

                // Set a monospaced font with consistent character width
                java.awt.Font monoFont = new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 14);
                java.lang.reflect.Method setFont = textPane.getClass().getMethod("setFont", java.awt.Font.class);
                setFont.invoke(textPane, monoFont);
            } catch (Exception e) {
                // Silently ignore if reflection fails - this is just an enhancement
            }
        }
    }

    /**
     * Displays the current state of the track with proper formatting.
     * Ensures consistent character width for track elements by replacing
     * spaces with non-breaking spaces just for display purposes.
     *
     * @param headerText optional text to display before the track (can be null)
     */
    private void displayTrack(String headerText) {
        configureTerminalForTrackDisplay();

        // Get the track display string
        String trackDisplay = track.toString();

        // Replace regular spaces with non-breaking spaces for display only
        // This doesn't affect the underlying track representation
        trackDisplay = trackDisplay.replace(' ', '\u00A0');

        if (headerText != null) {
            terminal.println(headerText);
        }
        terminal.println(trackDisplay);
    }

    /**
     * Displays the current state of the track with default header text.
     */
    private void displayTrack() {
        displayTrack("\nCurrent game state:");
    }

    /**
     * Sets up the game by selecting a track file and configuring car strategies.
     *
     * @throws IOException if there is an error reading track files
     * @throws InvalidFileFormatException if the track file format is invalid
     */
    private void setupGame() throws IOException, InvalidFileFormatException {
        File trackFile = selectTrackFile();

        try {
            terminal.println("\nLoading track from file: " + trackFile.getName());
            track = new Track(trackFile);
            game = new Game(track);

            terminal.println("\nTrack loaded successfully. Track dimensions: " +
                             track.getWidth() + "x" + track.getHeight() + " cells");
            terminal.println("Number of cars: " + track.getCarCount());

            // Display the track with enhanced formatting
            displayTrack("\nCurrent track layout:");

            setupCarStrategies();

            terminal.println("\nGame setup completed. Press Enter to start the game...");
            textIO.newStringInputReader().withMinLength(0).read("");

        } catch (InvalidFileFormatException e) {
            terminal.println("\nError loading track file: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Prompts the user to select a track file from the available options.
     *
     * @return the selected track file
     * @throws IOException if there is an error accessing the track directory
     */
    private File selectTrackFile() throws IOException {
        terminal.println("\nPlease select a track file:");
        File[] trackFiles = config.getTrackDirectory().listFiles((dir, name) -> name.endsWith(".txt"));

        if (trackFiles == null || trackFiles.length == 0) {
            terminal.println("No track files found in " + config.getTrackDirectory().getAbsolutePath());
            throw new IOException("No track files found in directory: " + config.getTrackDirectory().getAbsolutePath());
        }

        // Sort files alphabetically for a better user experience
        Arrays.sort(trackFiles, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));

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
     *
     * @throws InvalidFileFormatException if there is an error with move list or waypoint files
     */
    private void setupCarStrategies() throws InvalidFileFormatException {
        terminal.println("\nNow, choose a strategy for each car:");

        for (int i = 0; i < game.getCarCount(); i++) {
            char carId = game.getCarId(i);
            PositionVector carPos = game.getCarPosition(i);

            terminal.println("\n------ Car '" + carId + "' ------");
            terminal.println("Starting position: " + carPos);

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

            terminal.println("Strategy set: " + selectedStrategy);
        }
    }

    /**
     * Creates a move strategy based on the selected strategy type.
     *
     * @param strategyType the selected strategy type
     * @param carIndex the index of the car for which the strategy is being created
     * @return the corresponding MoveStrategy instance
     * @throws InvalidFileFormatException if there is an error with move list files
     */
    private MoveStrategy createStrategy(MoveStrategy.StrategyType strategyType, int carIndex) throws InvalidFileFormatException {
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
     * @return the MoveListStrategy instance
     * @throws InvalidFileFormatException if there is an error with the move list file format
     */
    private MoveStrategy createMoveListStrategy() throws InvalidFileFormatException {
        terminal.println("Select a move file for the MOVE_LIST strategy:");
        File[] moveFiles = config.getMoveDirectory().listFiles((dir, name) -> name.endsWith(".txt"));

        if (moveFiles == null || moveFiles.length == 0) {
            terminal.println("No move files found in " + config.getMoveDirectory().getAbsolutePath());
            terminal.println("Defaulting to DO_NOT_MOVE strategy.");
            return new DoNotMoveStrategy();
        }

        // Sort files alphabetically
        Arrays.sort(moveFiles, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));

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
        int turnCount = 0;

        while (game.getWinner() == Game.NO_WINNER) {
            turnCount++;
            terminal.println("\n======== TURN " + turnCount + " ========");

            // Use the enhanced display method
            displayTrack();

            int currentCarIndex = game.getCurrentCarIndex();
            char currentCarId = game.getCarId(currentCarIndex);
            PositionVector position = game.getCarPosition(currentCarIndex);
            PositionVector velocity = game.getCarVelocity(currentCarIndex);

            terminal.println("\nCar '" + currentCarId + "' turn");
            terminal.println("Position: " + position);
            terminal.println("Velocity: " + velocity);

            Direction nextMove = game.nextCarMove(currentCarIndex);
            terminal.println("Car '" + currentCarId + "' will accelerate: " + nextMove);

            game.doCarTurn(nextMove);

            // Show new position and velocity after the move
            if (!track.getCar(currentCarIndex).isCrashed()) {
                PositionVector newPosition = game.getCarPosition(currentCarIndex);
                PositionVector newVelocity = game.getCarVelocity(currentCarIndex);

                terminal.println("\nNew field:");
                displayTrack(); // Display the track with updated positions

                terminal.println("New position: " + newPosition);
                terminal.println("New velocity: " + newVelocity);
            } else {
                terminal.println("Car '" + currentCarId + "' has crashed!");
                displayTrack("\nUpdated field after crash:");
            }

            if (game.getWinner() != Game.NO_WINNER) {
                announceWinner();
                break;
            }

            game.switchToNextActiveCar();

            // Pause briefly between turns for readability
            terminal.println("\nPress Enter for next turn...");
            textIO.newStringInputReader().withMinLength(0).read("");
        }
    }

    /**
     * Announces the winner of the game.
     */
    private void announceWinner() {
        int winnerIndex = game.getWinner();
        Car winningCar = track.getCar(winnerIndex);
        char winnerId = game.getCarId(winnerIndex);

        terminal.println("\n🏁 GAME OVER 🏁");
        terminal.println("Car '" + winnerId + "' has won the race!");
        terminal.println("Car '" + winnerId + "' has done "+ winningCar.getMoveCount() +" moves to win the game!");
    }
}
