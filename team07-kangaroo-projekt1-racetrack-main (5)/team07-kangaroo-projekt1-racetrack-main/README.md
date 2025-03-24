# team07-kangaroo-projekt1-racetrack

## Branching Strategy

To ensure organization and efficiency in the development of our project, we adopt a straightforward branching strategy based on the following workflow:

### Main Branch: `main`
- **Description**: The `main` branch is our primary branch containing stable code that is production-ready.
- **Management**: Direct commits to `main` are not allowed. All changes must be introduced via pull requests from dedicated feature branches.

### Feature Branches
- **Creation**: Each new feature should be developed in a specific branch, created from the `main` branch. The name of the feature branch should correspond to the issue ID on GitHub to facilitate tracking, for example, `feature_#123`.
- **Usage**: These branches are dedicated to the development of individual features or fixes, directly linked to open issues on GitHub.
- **Pull Request (PR)**: Once the feature is completed, a PR is created towards the `main` branch. The PR should be linked to the reference issue to facilitate review of the work done.
- **Review**: Every PR requires a review by at least one other team member before merging. This ensures that the code is scrutinized, keeping the quality and consistency of the project high.

### Managing Pull Requests
- **Review Procedures**: Reviewers should check that the code is well-structured, follows coding best practices, and that all new features are adequately tested.
- **Merging**: Only PRs that receive approval can be merged into `main`. Once merged, the related feature branch is deleted to keep the repository clean.

By incorporating this branching strategy, we aim to optimize the development process and continuously improve the quality of our software.

---

## GameTest

### 1. getCarCount()

| Precondition                   | Input                      | Expected                     | Comments                                                         |
|--------------------------------|----------------------------|------------------------------|------------------------------------------------------------------|
| Game is created with a valid track file | (none; simply call the method)   | Returns the number of cars   | For example, if the track file contains 2 car placements, it returns 2. |

---

### 2. getCurrentCarIndex()

| Precondition                   | Input                      | Expected                     | Comments                                                         |
|--------------------------------|----------------------------|------------------------------|------------------------------------------------------------------|
| Game is just started           | (none)                     | 0                            | Initially, the current car index is 0.                           |
| Game has advanced turns (switching) | (none)               | A valid index (>= 0 and < car count) | After switching, the current index should update cyclically.      |
| Game state where only one car remains active | (none)        | The index of the only active car | When all others are crashed, the winner’s index becomes current.   |

---

### 3. getCarId(int carIndex)

| Precondition                   | Input                      | Expected                     | Comments                                                         |
|--------------------------------|----------------------------|------------------------------|------------------------------------------------------------------|
| Valid track is loaded          | Negative index (e.g. -1)   | Throws IndexOutOfBoundsException | Negative indices are not accepted.                              |
| Valid track is loaded          | Valid index within range (e.g., 0, 1) | Returns the car id at that index  | Normal condition; e.g., returns 'A' or 'B'.                       |
| Valid track is loaded          | Index equal to car count   | Throws IndexOutOfBoundsException | Index equal to the number of cars is out-of-range.                |
| Valid track is loaded          | Index much larger (e.g. 10 when only 2 exist) | Throws IndexOutOfBoundsException | Out‐of‐range index is rejected.                                   |

---

### 4. getCarPosition(int carIndex) and getCarVelocity(int carIndex)

| Precondition                   | Input (carIndex)           | Expected                     | Comments                                                         |
|--------------------------------|----------------------------|------------------------------|------------------------------------------------------------------|
| Valid track is loaded          | Negative index (e.g., -1)  | Throws IndexOutOfBoundsException | Negative index is invalid.                                       |
| Valid track is loaded          | Valid index (e.g., 0)      | Returns the position/velocity of that car  | Normal condition.                                                |
| Valid track is loaded          | Index equal to car count   | Throws IndexOutOfBoundsException | Index is out of range.                                             |

---

### 5. setCarMoveStrategy(int carIndex, MoveStrategy moveStrategy)

| Precondition                   | Input                      | Expected                     | Comments                                                         |
|--------------------------------|----------------------------|------------------------------|------------------------------------------------------------------|
| Valid game state               | Negative index (e.g., -1) with a valid strategy | Throws IndexOutOfBoundsException | Negative indices are not accepted.                              |
| Valid game state               | Valid index and valid non-null strategy | Strategy is successfully set   | Normal case.                                                     |
| Valid game state               | Valid index and null strategy | (Subsequent call to nextCarMove throws exception) | Setting a null strategy is invalid for obtaining a move later.    |
| Valid game state               | Index equal to car count   | Throws IndexOutOfBoundsException | Index out-of-range.                                               |

---

### 6. nextCarMove(int carIndex)

| Precondition                   | Input                      | Expected                     | Comments                                                         |
|--------------------------------|----------------------------|------------------------------|------------------------------------------------------------------|
| Valid game state with strategy set | Negative index (e.g., -1)  | Throws IndexOutOfBoundsException | Negative index is invalid.                                       |
| Valid game state               | Valid index but no strategy set | Throws IllegalStateException | When no strategy is assigned, an exception is expected.          |
| Valid game state               | Valid index with strategy set | Returns the Direction provided by the strategy | Normal condition (e.g., returns UP if strategy returns UP).       |
| Valid game state               | Index out-of-range (e.g., 10) | Throws IndexOutOfBoundsException | Invalid index value.                                               |

---

### 7. getWinner()

| Precondition                   | Input                      | Expected                     | Comments                                                         |
|--------------------------------|----------------------------|------------------------------|------------------------------------------------------------------|
| Game is just started           | (none)                     | NO_WINNER (typically -1)     | Before any turn, there is no winner.                             |
| Game state after one car crashes leaving one active | (none) | Returns the index of the remaining active car | Winner is determined when only one car remains active.            |
| Game state with a winning condition achieved | (none) | Returns the winning car’s index  | When a car crosses the finish line correctly, that index is returned.|
| Game with multiple active cars | (none)                    | NO_WINNER                    | If no winning condition has been met, returns NO_WINNER.           |

---

### 8. doCarTurn(Direction acceleration)

| Precondition                   | Input                      | Expected                     | Comments                                                         |
|--------------------------------|----------------------------|------------------------------|------------------------------------------------------------------|
| Valid game state with a strategy set for the current car | Null acceleration            | Throws NullPointerException         | Null acceleration is not accepted.                              |
| Valid game state with current car at (x,y) and initial velocity (vx,vy) | Valid acceleration (e.g., UP) | Car's velocity updated and new position computed accordingly | No collision occurs; normal movement.                           |
| Valid game state with a wall or collision object in path | Valid acceleration causing collision (e.g., towards WALL) | Car is marked as crashed; winner check is performed  | Collision detection; game state updated accordingly.             |
| Valid game state with negative acceleration values (e.g., UP_LEFT with vector (-1,-1)) | Valid acceleration with negative components | Car's velocity updated and new position computed accordingly | Negative components are handled properly.                        |
| (Optional) Valid game state with finish line in path | Valid acceleration crossing finish line  | Winner is set to current car’s index and move is executed | Test for win condition (if finish area is defined).               |

---

### 9. switchToNextActiveCar()

| Precondition                   | Input                      | Expected                     | Comments                                                         |
|--------------------------------|----------------------------|------------------------------|------------------------------------------------------------------|
| Valid game state with all cars active | (none)                     | currentCarIndex advances cyclically  | In a two-car game, if current is 0, it becomes 1.                |
| Valid game state with some cars crashed | (none)                     | currentCarIndex becomes index of first active car | Crashed cars are skipped.                                         |
| Valid game state with all cars crashed  | (none)                     | currentCarIndex remains unchanged      | No active car is available; index stays the same.                |

---

### 10. calculatePath(PositionVector startPosition, PositionVector endPosition)

| Precondition                   | Input (startPosition, endPosition)            | Expected                     | Comments                                                         |
|--------------------------------|-----------------------------------------------|------------------------------|------------------------------------------------------------------|
| Valid game state              | Normal positions (e.g., (0,0) to (3,4))         | Returns a list beginning with (0,0) and ending with (3,4) | Uses Bresenham's algorithm; list length is within an expected range. |
| Valid game state              | Degenerate positions where start equals end (e.g., (1,1), (1,1)) | Returns a list with a single element [(1,1)] | Degenerate line should have one point.                           |
| Valid game state              | Negative coordinates (e.g., (-2,-2) to (2,2))   | Returns a valid computed list of positions | Handles negative coordinates correctly.                           |
| Valid game state              | Null start position                            | Throws NullPointerException  | Null is not accepted.                                            |
| Valid game state              | Null end position                              | Throws NullPointerException  | Null is not accepted.                                            |
| Valid game state              | A long horizontal line (e.g., (0,0) to (1000,0))  | Returns a list with 1001 points | Performance and correctness test; step count equals difference+1.  |

---

## CarTest

### Method: getId()

| Precondition                          | Input        | Expected         | Comments                                             |
|---------------------------------------|--------------|------------------|------------------------------------------------------|
| Car is constructed with a valid id    | (none)       | Returns the id   | Normal case; e.g., if constructed with 'A', returns 'A'. |

---

### Method: getPosition()

| Precondition                                       | Input  | Expected                                 | Comments                                                        |
|----------------------------------------------------|--------|------------------------------------------|-----------------------------------------------------------------|
| Car is constructed with a valid starting position  | (none) | Returns the same starting position       | Normal case; e.g., if constructed with (5, 10), returns (5,10).   |
| Car is constructed with a null starting position   | (none) | Returns null (or later causes NPE in operations) | May be considered invalid; subsequent calls may throw exceptions. |

---

### Method: getVelocity()

| Precondition                                     | Input  | Expected                                     | Comments                                                          |
|--------------------------------------------------|--------|----------------------------------------------|-------------------------------------------------------------------|
| Car is newly constructed                          | (none) | Returns (0,0)                                | Initial velocity should be (0,0).                                 |
| Car’s velocity has been updated via accelerate    | (none) | Returns the updated velocity vector          | After acceleration, the velocity should reflect the changes.      |

---

### Method: nextPosition()

| Precondition                                              | Input  | Expected                                       | Comments                                                            |
|-----------------------------------------------------------|--------|------------------------------------------------|---------------------------------------------------------------------|
| Car is constructed with valid position and velocity (0,0)   | (none) | Returns the same as the current position         | With velocity (0,0), next position equals current position.          |
| Car is constructed with valid position and nonzero velocity | (none) | Returns current position plus velocity vector      | For example, starting at (3,4) with velocity (1, -1) returns (4,3).    |
| Car is constructed with a null starting position          | (none) | Throws NullPointerException (when adding null)      | Invalid input leads to exception in nextPosition().                  |

---

### Method: accelerate(Direction acceleration)

| Precondition                                            | Input                                                   | Expected                                    | Comments                                                                       |
|---------------------------------------------------------|---------------------------------------------------------|---------------------------------------------|--------------------------------------------------------------------------------|
| Car is constructed with valid position and velocity     | A valid Direction (e.g., Direction.UP with vector (0,-1))  | Velocity becomes (0,-1)                     | Normal case; one valid acceleration updates velocity accordingly.             |
| Car is constructed with valid position and velocity     | A valid Direction (e.g., Direction.DOWN_RIGHT with vector (1,1)) | Velocity becomes (1,1)                        | Normal case; valid acceleration.                                               |
| Car is constructed normally                             | null                                                    | Throws NullPointerException                 | Passing a null Direction is not allowed.                                     |
| Car accelerates consecutively                           | Multiple valid Directions in sequence                   | Velocity is the sum of acceleration vectors   | For example, accelerate UP then RIGHT results in (1, -1).                      |

---

### Method: move()

| Precondition                                               | Input  | Expected                                           | Comments                                                       |
|------------------------------------------------------------|--------|----------------------------------------------------|----------------------------------------------------------------|
| Car is constructed with valid starting position and velocity | (none) | New position = current position + current velocity  | Normal case; for example, if position=(2,3) and velocity=(1,0), returns (3,3). |
| Car is constructed with valid data                         | (none) | If position is null, move() likely throws a NullPointerException | If car was constructed with null, move() may not work properly.  |

---

### Method: crash(PositionVector crashPosition)

| Precondition                                               | Input                                               | Expected                                              | Comments                                                                               |
|------------------------------------------------------------|-----------------------------------------------------|-------------------------------------------------------|----------------------------------------------------------------------------------------|
| Car is constructed with valid starting position            | A valid crash position (e.g., (4,4))                  | Position is updated to (4,4) and isCrashed() returns true  | Normal crash behavior; car is marked as crashed.                                        |
| Car is constructed normally                                | null                                                | Position becomes null and isCrashed() returns true         | Passing null is not ideal, but per implementation, crash() sets position to null.         |

---

### Method: isCrashed()

| Precondition                          | Input         | Expected            | Comments                                                        |
|---------------------------------------|---------------|---------------------|-----------------------------------------------------------------|
| Car is newly constructed               | (none)      | Returns false       | Initially, the car should not be crashed.                      |
| After crash() is called with a valid position  | (none) | Returns true        | Once crash() is invoked, isCrashed() must return true.           |

---

## TrackTest

### Constructor Track(File trackFile)

| Precondition                           | Input (Range/Values)                                          | Expected                                | Comments                                                                                           |
|----------------------------------------|---------------------------------------------------------------|-----------------------------------------|----------------------------------------------------------------------------------------------------|
| Valid file required                    | File = null                                                   | Throws IllegalArgumentException         | A null file is not allowed.                                                                        |
| Valid file required                    | An empty file (no non‐empty lines)                             | Throws InvalidFileFormatException       | No valid track lines found.                                                                        |
| Valid file required                    | A file with inconsistent line lengths                         | Throws InvalidFileFormatException       | All lines must have the same length.                                                               |
| Valid file required                    | A file with a valid rectangular grid but no car characters       | Throws InvalidFileFormatException       | At least one car must be present.                                                                  |
| Valid file required                    | A file with a valid grid but more than MAX_CARS (i.e. >9 cars)    | Throws InvalidFileFormatException       | The number of cars exceeds the allowed maximum (MAX_CARS is 9).                                    |
| Valid file provided                    | A file with a valid rectangular grid and proper car placements  | Successfully creates a Track object     | For example, a 5×5 grid with walls on the borders and at least one car.                             |

---

### getHeight()

| Precondition                         | Input                           | Expected                  | Comments                                                       |
|--------------------------------------|---------------------------------|---------------------------|----------------------------------------------------------------|
| Valid track is created               | Track with n non‐empty lines (e.g. n=5)  | Returns n (e.g. 5)         | Height equals the number of non‐empty lines in the file.         |
| Invalid track file                   | (Empty file)                    | Constructor throws exception  | If file is empty, the constructor fails.                     |

---

### getWidth()

| Precondition                         | Input                           | Expected                  | Comments                                                       |
|--------------------------------------|---------------------------------|---------------------------|----------------------------------------------------------------|
| Valid track is created               | First non‐empty line has m characters (e.g. m=5) | Returns m (e.g. 5)         | Width equals the length of the first non‐empty line.             |
| Invalid track file                   | (Empty file)                    | Constructor throws exception  | If file is empty, the constructor fails.                     |

---

### getCarCount()

| Precondition                         | Input                           | Expected                  | Comments                                                                                   |
|--------------------------------------|---------------------------------|---------------------------|--------------------------------------------------------------------------------------------|
| Valid track is created               | Track with 1 car                | Returns 1                 | Minimal valid track.                                                                       |
| Valid track is created               | Track with 2 or more cars       | Returns actual car count  | Normal case.                                                                               |
| Valid track is created               | Track with 9 cars               | Returns 9                 | Upper boundary condition (MAX_CARS is 9).                                                  |
| Invalid track file                   | File with no car characters     | Constructor throws exception  | The file must contain at least one car.                                                     |
| Invalid track file                   | File with more than 9 cars      | Constructor throws exception  | Too many cars.                                                                              |

---

### getCar(int carIndex)

| Precondition                         | Input (carIndex)                                    | Expected                                | Comments                                                     |
|--------------------------------------|-----------------------------------------------------|-----------------------------------------|--------------------------------------------------------------|
| Valid track is created               | Negative index (e.g. -1)                            | Throws IndexOutOfBoundsException         | Negative indices are invalid.                              |
| Valid track is created               | Valid index (e.g. 0, 1, etc., within range)         | Returns the corresponding Car instance   | Normal case.                                               |
| Valid track is created               | Index equal to car count                            | Throws IndexOutOfBoundsException         | Index equal to number of cars is out-of-range.             |
| Valid track is created               | Index much larger (e.g. 10 when only 2 exist)       | Throws IndexOutOfBoundsException         | Out‐of‐range index.                                          |

---

### getSpaceTypeAtPosition(PositionVector position)

| Precondition                         | Input (PositionVector)                              | Expected                                | Comments                                                                       |
|--------------------------------------|-----------------------------------------------------|-----------------------------------------|--------------------------------------------------------------------------------|
| Valid track is created               | Valid position within bounds (0 ≤ x < width, 0 ≤ y < height) | Returns corresponding SpaceType          | Normal case.                                                                 |
| Valid track is created               | Position with negative coordinates (e.g. (-1, 3))      | Returns SpaceType.WALL                   | Out‐of‐bound positions are treated as WALL.                                  |
| Valid track is created               | Position with x ≥ width (e.g. (width, 2))             | Returns SpaceType.WALL                   | x out‐of‐range returns WALL.                                                  |
| Valid track is created               | Position with y ≥ height (e.g. (2, height))           | Returns SpaceType.WALL                   | y out‐of‐range returns WALL.                                                  |
| Valid track is created               | Null position                                        | Throws NullPointerException              | Passing null should throw an exception (if not handled).                     |

---

### getCharRepresentationAtPosition(int row, int col)

| Precondition                         | Input (row, col)                                     | Expected                                | Comments                                                                         |
|--------------------------------------|------------------------------------------------------|-----------------------------------------|----------------------------------------------------------------------------------|
| Valid track is created               | Position where an active car is located (e.g. (1,1))  | Returns car's id (e.g. 'A')              | If an active car occupies that position, return its id.                         |
| Valid track is created               | Position where a crashed car is located               | Returns CRASH_INDICATOR (e.g. 'X')         | If a car at that position is crashed, return the crash indicator.                |
| Valid track is created               | Position with no car (e.g. (2,2) in inner track area)  | Returns corresponding space character   | Normal case when no car is present.                                              |
| Valid track is created               | Out-of‐bound row or col (e.g. (-1,0))                  | Returns WALL's space character           | Out‐of‐bound positions are treated as WALL.                                     |
| Valid track is created               | Invalid row/col values (if not checked)              | May throw an exception or be handled as out‐of‐bound | The method is expected to call getSpaceTypeAtPosition which handles bounds. |

---

### toString()

| Precondition                         | Input                           | Expected                                | Comments                                                                  |
|--------------------------------------|---------------------------------|-----------------------------------------|---------------------------------------------------------------------------|
| Valid track is created               | Track with known grid and car positions | Returns a String representing the track grid with the correct number of lines and content | Should display each row on a new line, including car positions.           |
| Invalid track file (empty or invalid) | Not applicable                  | Constructor throws exception            | If the file is invalid, the object is not created and toString() is not invoked. |

---

