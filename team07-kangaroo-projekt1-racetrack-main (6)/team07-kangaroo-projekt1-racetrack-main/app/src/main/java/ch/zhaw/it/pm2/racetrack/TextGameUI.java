package ch.zhaw.it.pm2.racetrack;

import ch.zhaw.it.pm2.racetrack.strategy.*;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Provides a text-based user interface for the Racetrack game.
 * Handles game setup, car strategy selection, and game loop execution using Text-IO.
 */
public class TextGameUI {

    private final TextIO textIO;
    private final TextTerminal<?> terminal;
    private final Config config;
    private Game game;
    private Track track;

    /**
     * Constructs a new TextGameUI instance.
     */
    public TextGameUI() {
        this.textIO = TextIoFactory.getTextIO();
        this.terminal = textIO.getTextTerminal();
        this.config = new Config();
    }

    /**
     * Starts the Racetrack game by setting up and running the game loop.
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
        textIO.newStringInputReader().withMinLength(0).read("\nPress Enter to exit");
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
     * Configures the text terminal for proper track display by setting a monospaced font if available.
     */
    private void configureTerminalForTrackDisplay() {
        if (terminal.getClass().getSimpleName().contains("Swing")) {
            try {
                Method getTextPane = terminal.getClass().getMethod("getTextPane");
                Object textPane = getTextPane.invoke(terminal);
                Font monoFont = new Font(Font.MONOSPACED, Font.PLAIN, 14);
                Method setFont = textPane.getClass().getMethod("setFont", Font.class);
                setFont.invoke(textPane, monoFont);
            } catch (Exception e) {
                terminal.println("Warning: Could not configure terminal font: " + e.getMessage());
            }
        }
    }

    /**
     * Displays the current track state with proper formatting.
     *
     * @param headerText text to display before the track; may be null
     */
    private void displayTrack(String headerText) {
        configureTerminalForTrackDisplay();
        String trackDisplay = track.toString().replace(' ', '\u00A0');
        if (headerText != null) {
            terminal.println(headerText);
        }
        terminal.println(trackDisplay);
    }

    /**
     * Displays the current track state with a default header.
     */
    private void displayTrack() {
        displayTrack("\nCurrent game state:");
    }

    /**
     * Prints game setup information such as track dimensions and car count.
     */
    private void printGameSetupInfo() {
        terminal.println("\nTrack loaded successfully. Track dimensions: "
            + track.getWidth() + "x" + track.getHeight() + " cells");
        terminal.println("Number of cars: " + track.getCarCount());
    }

    /**
     * Sets up the game by selecting a track file, initializing the track and game,
     * printing setup information, and configuring car strategies.
     *
     * @throws IOException if track file reading fails
     * @throws InvalidFileFormatException if track file format is invalid
     */
    private void setupGame() throws IOException, InvalidFileFormatException {
        File trackFile = selectTrackFile();
        try {
            terminal.println("\nLoading track from file: " + trackFile.getName());
            track = new Track(trackFile);
            game = new Game(track);
            printGameSetupInfo();
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
     * Prompts the user to select a track file from available options.
     *
     * @return the selected track file
     * @throws IOException if track directory access fails
     */
    private File selectTrackFile() throws IOException {
        terminal.println("\nPlease select a track file:");
        File[] trackFiles = config.getTrackDirectory().listFiles((dir, name) -> name.endsWith(".txt"));
        if (trackFiles == null || trackFiles.length == 0) {
            String dirPath = config.getTrackDirectory().getAbsolutePath();
            terminal.println("No track files found in " + dirPath);
            throw new IOException("No track files found in directory: " + dirPath);
        }
        Arrays.sort(trackFiles, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
        List<String> trackNames = Arrays.stream(trackFiles).map(File::getName).toList();
        String selectedTrackName = textIO.newStringInputReader()
            .withNumberedPossibleValues(trackNames)
            .read("Select a track");
        return new File(config.getTrackDirectory(), selectedTrackName);
    }

    /**
     * Configures the move strategy for each car by prompting the user.
     *
     * @throws InvalidFileFormatException if move list or waypoint files are invalid
     */
    private void setupCarStrategies() throws InvalidFileFormatException {
        terminal.println("\nNow, choose a strategy for each car:");
        for (int i = 0; i < game.getCarCount(); i++) {
            setupStrategyForCar(i);
        }
    }

    /**
     * Sets up the move strategy for a specific car.
     *
     * @param carIndex the index of the car
     * @throws InvalidFileFormatException if move list or waypoint files are invalid
     */
    private void setupStrategyForCar(int carIndex) throws InvalidFileFormatException {
        char carId = game.getCarId(carIndex);
        PositionVector carPos = game.getCarPosition(carIndex);
        terminal.println("\n------ Car '" + carId + "' ------");
        terminal.println("Starting position: " + carPos);
        List<MoveStrategy.StrategyType> availableStrategies = Arrays.asList(
            MoveStrategy.StrategyType.DO_NOT_MOVE,
            MoveStrategy.StrategyType.USER,
            MoveStrategy.StrategyType.MOVE_LIST,
            MoveStrategy.StrategyType.PATH_FOLLOWER,
            MoveStrategy.StrategyType.PATH_FINDER
        );
        MoveStrategy.StrategyType selectedStrategy = textIO.newEnumInputReader(MoveStrategy.StrategyType.class)
            .withNumberedPossibleValues(availableStrategies)
            .read("Choose a strategy for Car '" + carId + "'");
        MoveStrategy strategy = createStrategy(selectedStrategy, carIndex);
        game.setCarMoveStrategy(carIndex, strategy);
        terminal.println("Strategy set: " + selectedStrategy);
    }

    /**
     * Creates a move strategy based on the selected strategy type.
     *
     * @param strategyType the chosen strategy type
     * @param carIndex the index of the car
     * @return the corresponding MoveStrategy instance
     * @throws InvalidFileFormatException if move list file format is invalid
     */
    private MoveStrategy createStrategy(MoveStrategy.StrategyType strategyType, int carIndex)
        throws InvalidFileFormatException {
        return switch (strategyType) {
            case DO_NOT_MOVE -> new DoNotMoveStrategy();
            case USER -> new UserMoveStrategy();
            case MOVE_LIST -> createMoveListStrategy();
            case PATH_FOLLOWER -> createPathFollowerStrategy(carIndex);
            case PATH_FINDER -> createPathFinderStrategy(carIndex);
            default -> throw new IllegalArgumentException("Strategy not implemented: " + strategyType);
        };
    }

    /**
     * Creates a MoveListStrategy by prompting the user to select a move file.
     *
     * @return the MoveListStrategy instance, or a DoNotMoveStrategy if no file is found
     * @throws InvalidFileFormatException if move list file format is invalid
     */
    private MoveStrategy createMoveListStrategy() throws InvalidFileFormatException {
        terminal.println("Select a move file for the MOVE_LIST strategy:");
        File moveFile = selectFileFromDirectory(config.getMoveDirectory(), "Select a move file");
        if (moveFile == null) {
            terminal.println("No move files found in " + config.getMoveDirectory().getAbsolutePath());
            terminal.println("Defaulting to DO_NOT_MOVE strategy.");
            return new DoNotMoveStrategy();
        }
        return new MoveListStrategy(moveFile);
    }

    /**
     * Creates a PathFollowerStrategy by prompting the user to select a waypoint file.
     *
     * @param carIndex the index of the car for which the strategy is created
     * @return the PathFollowerStrategy instance, or a DoNotMoveStrategy if no file is found
     */
    private MoveStrategy createPathFollowerStrategy(int carIndex) {
        terminal.println("Select a waypoint file for the PATH_FOLLOWER strategy:");
        File followerFile = selectFileFromDirectory(config.getFollowerDirectory(), "Select a waypoint file");
        if (followerFile == null) {
            terminal.println("No waypoint files found in " + config.getFollowerDirectory().getAbsolutePath());
            return new DoNotMoveStrategy();
        }
        Car car = track.getCar(carIndex);
        return new PathFollowerStrategy(followerFile, car);
    }

    /**
     * Prompts the user to select a file from a given directory.
     *
     * @param directory the directory from which to select the file
     * @param prompt the prompt text to display
     * @return the selected file, or null if no file is found
     */
    private File selectFileFromDirectory(File directory, String prompt) {
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            return null;
        }
        Arrays.sort(files, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
        List<String> fileNames = Arrays.stream(files).map(File::getName).toList();
        String selectedFileName = textIO.newStringInputReader()
            .withNumberedPossibleValues(fileNames)
            .read(prompt);
        return new File(directory, selectedFileName);
    }

    /**
     * Runs the main game loop until a winner is determined.
     */
    private void runGameLoop() {
        int turnCount = 0;
        while (game.getWinner() == Game.NO_WINNER) {
            turnCount++;
            printTurnHeader(turnCount);
            if (processTurn()) {
                break;
            }
        }
    }

    /**
     * Prints the header for the current turn and displays the current track.
     *
     * @param turnCount the current turn number
     */
    private void printTurnHeader(int turnCount) {
        terminal.println("\n======== TURN " + turnCount + " ========");
        displayTrack();
    }

    /**
     * Processes a single turn and pauses for input if the game has not ended.
     *
     * @return true if the game has ended, false otherwise
     */
    private boolean processTurn() {
        boolean ended = processCarTurn();
        if (!ended) {
            pauseForNextTurn();
        }
        return ended;
    }

    /**
     * Pauses the game until the user presses Enter.
     */
    private void pauseForNextTurn() {
        terminal.println("\nPress Enter for next turn...");
        textIO.newStringInputReader().withMinLength(0).read("");
    }

    /**
     * Processes the current car's turn: displays pre-turn info, executes the turn, and displays the outcome.
     *
     * @return true if the game has ended, false otherwise
     */
    private boolean processCarTurn() {
        int currentCarIndex = game.getCurrentCarIndex();
        displayCarPreTurnInfo(currentCarIndex);
        char currentCarId = game.getCarId(currentCarIndex);
        Direction nextMove = game.nextCarMove(currentCarIndex);
        terminal.println("Car '" + currentCarId + "' will accelerate: " + nextMove);
        game.doCarTurn(nextMove);
        displayCarOutcome(currentCarIndex, currentCarId);
        if (game.getWinner() != Game.NO_WINNER) {
            announceWinner();
            return true;
        }
        game.switchToNextActiveCar();
        return false;
    }

    /**
     * Displays pre-turn information for the current car.
     *
     * @param carIndex the index of the current car
     */
    private void displayCarPreTurnInfo(int carIndex) {
        char carId = game.getCarId(carIndex);
        PositionVector position = game.getCarPosition(carIndex);
        PositionVector velocity = game.getCarVelocity(carIndex);
        terminal.println("\nCar '" + carId + "' turn");
        terminal.println("Position: " + position);
        terminal.println("Velocity: " + velocity);
    }

    /**
     * Displays updated field information (new position and velocity) for the current car.
     *
     * @param carIndex the index of the current car
     */
    private void displayUpdatedFieldInfo(int carIndex) {
        PositionVector newPosition = game.getCarPosition(carIndex);
        PositionVector newVelocity = game.getCarVelocity(carIndex);
        displayTrack();
        terminal.println("New position: " + newPosition);
        terminal.println("New velocity: " + newVelocity);
    }

    /**
     * Displays the outcome of the current car's turn.
     *
     * @param currentCarIndex the index of the current car
     * @param currentCarId the identifier of the current car
     */
    private void displayCarOutcome(int currentCarIndex, char currentCarId) {
        if (!track.getCar(currentCarIndex).isCrashed()) {
            displayUpdatedFieldInfo(currentCarIndex);
        } else {
            terminal.println("Car '" + currentCarId + "' has crashed!");
            displayTrack("\nUpdated field after crash:");
        }
    }
    
    private void announceWinner() {
        int winnerIndex = game.getWinner();
        Car winningCar = track.getCar(winnerIndex);
        char winnerId = game.getCarId(winnerIndex);
        terminal.println("\n🏁 GAME OVER 🏁");
        terminal.println("Car '" + winnerId + "' has won the race!");
        terminal.println("Car '" + winnerId + "' has done " + winningCar.getMoveCount() + " moves to win the game!");
    }

    /**
     * Creates a PathFinderStrategy for the specified car.
     *
     * @param carIndex index of the car for which the strategy is being created
     * @return the PathFinderStrategy instance, or a fallback DoNotMoveStrategy if path finding fails
     */
    private MoveStrategy createPathFinderStrategy(int carIndex) {
        Car car = track.getCar(carIndex);
        try {
            printPathFinderAttempt(car);
            MoveStrategy strategy = new PathFinderStrategy(car, track);
            printPathFinderSuccess(car);
            return strategy;
        } catch (Exception e) {
            printPathFinderFailure(e);
            return new DoNotMoveStrategy();
        }
    }

    private void printPathFinderAttempt(Car car) {
        terminal.println(String.format("\nCreating PathFinderStrategy for car '%s'...", car.getId()));
        terminal.println("This may take a moment as the algorithm searches for an optimal path.");
        terminal.println("Computing path...");
    }

    private void printPathFinderSuccess(Car car) {
        terminal.println(String.format("PathFinder strategy created successfully for car '%s'!", car.getId()));
    }

    private void printPathFinderFailure(Exception e) {
        terminal.println("Path finding failed: " + e.getMessage());
        if (e.getCause() != null) {
            terminal.println("Caused by: " + e.getCause().getMessage());
        }
        terminal.println("Falling back to DO_NOT_MOVE strategy.");
    }

}
