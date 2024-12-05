# Sudoku Game

## Overview

The Sudoku Game enables multiplayer Sudoku gameplay over a TCP network connection, supporting dynamic grid sizes and concurrent client interactions.

## Docker Usage

Pull and run the container:

```bash
# Pull the image
docker pull ghcr.io/kilianfroideva/sudoku/sudoku-app:1.0.0

# Run the container
docker run -p 1236:1236 ghcr.io/kilianfroideva/sudoku/sudoku-app:1.0.0
```

## Get the game on your computer

1. Clone the repository:

    ```bash
    git clone https://github.com/nicolasbvd/heig-vd-dai-course-java-tcp-programming-practical-content-sudoku
    ```

2. Navigate to the project directory:

    ```bash
    cd .../heig-vd-dai-course-java-tcp-programming-practical-content-sudoku
    ```

3. Build the project using Maven:

    ```bash
    mvn clean install
    ```
   
4.Build the game :
```bash
docker build -t sudoku-app
```

5.
 - To run as client on localhost the command is :
```bash
docker run -it --network="host" sudoku-app client --host=127.0.0.1
```
 - To run as server: 
```bash
docker run -p 1236:1236 sudoku-app server
```

6. Publish to Docker hub :
```bash
docker tag sudoku-app username/sudoku-app
docker push username/sudoku-app
```

## Game Commands

### 1. Start Game
```bash
PLAY <grid_size>
```
- Supported grid sizes: 9, 16
- Response: `OK` or `ERROR`

### 2. Make Move
```bash
SELECT <case_name> <number>
```
- Case names for 9x9 grid: A1 to I9
- Case names for 16x16 grid: A1 to P16
- Responses:
    - `CORRECT MOVE`
    - `WRONG MOVE`
    - `ALREADY PLACED` : There is a fixed number at the selected case
    - `OUT OF BOUNDS` : Choose a case in the grid and a number equal or smaller to the grid size

### 3. Additional Commands
- `HELP`: Display available commands
- `QUIT`: Exit game

## Game Flow

1. Connect to server
2. Request grid size with `PLAY`
3. Make moves using `SELECT`
4. Receive move validation
5. Complete game when all cells are correctly filled

## Error Handling

- Invalid grid size
- Out-of-bounds moves
- Selecting pre-filled cells
- Incorrect number placements
- Use command Select before starting a game

## Example Interaction

```bash
[Client] PLAY 9
[Server] OK

     1 2 3   4 5 6   7 8 9
    ----------------------
A  |   7   | 8     | 2    
B  | 2     | 6     |      
C  |   3 4 |     5 |      
    ----------------------
D  |       |     7 |   2  
E  |   2 5 |       | 1 6  
F  |   9   | 5     |      
    ----------------------
G  |       | 9     | 8 3  
H  |       |     6 |     7
I  |     8 |     1 |   4 2

[Client] SELECT A1 1
[Server] CORRECT MOVE
     1 2 3   4 5 6   7 8 9
    ----------------------
A  | 1 7   | 8     | 2    
B  | 2     | 6     |      
C  |   3 4 |     5 |      
    ----------------------
D  |       |     7 |   2  
E  |   2 5 |       | 1 6  
F  |   9   | 5     |      
    ----------------------
G  |       | 9     | 8 3  
H  |       |     6 |     7
I  |     8 |     1 |   4 2

[Client] SELECT C1 1
[Server] WRONG MOVE

     1 2 3   4 5 6   7 8 9
    ----------------------
A  | 1 7   | 8     | 2    
B  | 2     | 6     |      
C  |   3 4 |     5 |      
    ----------------------
D  |       |     7 |   2  
E  |   2 5 |       | 1 6  
F  |   9   | 5     |      
    ----------------------
G  |       | 9     | 8 3  
H  |       |     6 |     7
I  |     8 |     1 |   4 2

```

## Multiplayer Support

- Concurrent game sessions
- Independent game states
- Thread-safe move validation

## Requirements

- **TCP/IP** network
- **UTF-8** compatible client
- - **Java Development Kit (JDK) 8+**
- **Maven** (for building the project)
- **Git** (for cloning the repository)
