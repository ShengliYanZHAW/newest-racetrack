# Racetrack Game - Enhanced README

## Project Description

Racetrack is a text-based implementation of a classic strategy game that simulates a car race on a grid-based track. Players control cars by specifying acceleration vectors, adhering to simple physics principles. The game follows these core mechanics:

- Cars maintain their velocity from turn to turn (inertia)
- Players can adjust acceleration in the x and y directions by -1, 0, or 1 each turn
- Cars must avoid walls and other cars to prevent crashes
- The first car to cross the finish line in the correct direction wins

This implementation offers several movement strategies for cars:
- **User Input**: Players choose acceleration using a numpad layout
- **Move List**: Cars follow pre-defined moves from a file
- **Path Follower**: Cars automatically navigate to specified waypoints
- **Do Not Move**: Cars maintain constant velocity

## Installation & Setup

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/team07-kangaroo-projekt1-racetrack.git
   ```

2. Navigate to the project directory:
   ```
   cd team07-kangaroo-projekt1-racetrack
   ```

3. Build and run the project using Gradle:
   ```
   ./gradlew run
   ```

## How to Play

1. When you start the game, you'll be prompted to select a track file from the available options
2. For each car on the track, choose a movement strategy
3. Follow the on-screen instructions during gameplay
4. For user-controlled cars, use the numpad layout to specify acceleration:
   ```
   7 8 9     7=up-left,     8=up,              9=up-right
   4 5 6     4=left,        5=no acceleration, 6=right
   1 2 3     1=down-left,   2=down,            3=down-right
   ```
5. The game continues until a car crosses the finish line in the correct direction or is the last car remaining

## Project Structure

The project follows a modular architecture with clear separation of concerns:

- **Model**: `Track`, `Car`, `PositionVector`, `Direction`, `SpaceType` represent the game state
- **Controller**: `Game` manages game logic and updates the model based on user input
- **View**: `TextGameUI` handles user interaction using the Text-IO library
- **Strategy**: Various implementations of the `MoveStrategy` interface determine car behavior

### Key Directories:
- `app/src/main/java/ch/zhaw/it/pm2/racetrack/`: Core game implementation
- `app/src/test/java/ch/zhaw/it/pm2/racetrack/`: Test cases
- `app/tracks/`: Track definition files
- `app/moves/`: Move list files for the MOVE_LIST strategy
- `app/follower/`: Waypoint files for the PATH_FOLLOWER strategy

## Class Diagram

Our class diagram focuses on the core components and their relationships. We chose to display only the most essential classes, methods, and attributes that contribute to understanding the system architecture, while omitting implementation details that would unnecessarily complicate the diagram.

The diagram shows:
- The central `Game` class that coordinates game flow
- The `Track` class that manages the game board and cars
- The `Car` class with its position and movement properties
- The strategy pattern implementation through the `MoveStrategy` interface
- Key utility classes like `PositionVector` and enums like `Direction` and `SpaceType`

The relationships demonstrate how these components interact, highlighting the MVC architecture and strategy pattern implementation. This level of abstraction provides a clear overview of the system design while remaining comprehensible.

[Note: The actual class diagram should be created and linked here]

## Branching Strategy

To ensure organization and efficiency in the development of our project, we adopt a straightforward branching strategy based on the following workflow:

### Main Branch: `main`
- **Description**: The `main` branch is our primary branch containing stable code that is production-ready.
- **Management**: Direct commits to `main` are not allowed. All changes must be introduced via pull requests from dedicated feature branches.

### Feature Branches
- **Creation**: Each new feature should be developed in a specific branch, created from the `main` branch. 
- **Usage**: These branches are dedicated to the development of individual features or fixes. We do not enforce a strict naming convention for feature branches.
- **Pull Request (PR)**: Once the feature is completed, a PR is created towards the `main` branch. The PR should be linked to the reference issue to facilitate review of the work done.
- **Review**: Every PR requires a review by at least one other team member before merging. This ensures that the code is scrutinized, keeping the quality and consistency of the project high.

### Managing Pull Requests
- **Review Procedures**: Reviewers should check that the code is well-structured, follows coding best practices, and that all new features are adequately tested.
- **Merging**: Only PRs that receive approval can be merged into `main`. Once merged, the related feature branch is deleted to keep the repository clean.

By incorporating this branching strategy, we aim to optimize the development process and continuously improve the quality of our software.

---


[Equivalence Classes](docs/equivalenceclass.md)

[Classes's Diagram](docs/classesdiagram.md)

