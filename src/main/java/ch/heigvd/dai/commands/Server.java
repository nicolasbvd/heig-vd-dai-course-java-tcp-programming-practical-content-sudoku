package ch.heigvd.dai.commands;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.heigvd.dai.sudoku.enums.MoveValidity;
import ch.heigvd.dai.sudoku.Sudoku;
import picocli.CommandLine;

@CommandLine.Command(name = "server", description = "Start the server part of the network game.")
public class Server implements Callable<Integer> {

  private static Sudoku sudoku = new Sudoku(9);

  @CommandLine.Option(
          names = {"-p", "--port"},
          description = "Port to use (default: ${DEFAULT-VALUE}).",
          defaultValue = "1236")
  protected int port;

  public enum ClientCommand {
    PLAY,
    SELECT,
  }

  private static final int SERVER_ID = (int) (Math.random() * 1000000);

  public enum ServerCommand {
    SEND_GRID,
    CORRECT_MOVE,
    WRONG_MOVE,
    ALREADY_PLACED,
    OUT_OF_BOUNDS,
    COMPLETED,
    OK,
    ERROR
  }

  private static boolean pressedPlay = false;

  @Override
  public Integer call() throws IOException {
    try (ServerSocket serverSocket = new ServerSocket(port);
         ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();) {
      System.out.println("[Server " + SERVER_ID + "] starting with id " + SERVER_ID);
      System.out.println("[Server " + SERVER_ID + "] listening on port " + port);


      while (!serverSocket.isClosed()) {
        Socket clientSocket = serverSocket.accept();
        executor.submit(new ClientHandler(clientSocket));

      }
    } catch (IOException e) {
      System.out.println("[Server] IO exception: " + e);
    }
    return 0;
  }

  static class ClientHandler implements Runnable {

    private final Socket socket;

    public ClientHandler(Socket socket) {
      this.socket = socket;
    }

    @Override
    public void run() {
      try (socket; // This allow to use try-with-resources with the socket
           Reader reader = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
           BufferedReader in = new BufferedReader(reader);
           Writer writer =
                   new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
           BufferedWriter out = new BufferedWriter(writer)) {
        System.out.println(
                "[Server "
                        + SERVER_ID
                        + "] new client connected from "
                        + socket.getInetAddress().getHostAddress()
                        + ":"
                        + socket.getPort());
        while (!socket.isClosed()) {
          // Read response from client
          String clientRequest = in.readLine();

          // If clientRequest is null, the client has disconnected
          // The server can close the connection and wait for a new client
          if (clientRequest == null) {
            System.out.println("[Client] closed");
            socket.close();
            continue;
          }

          // Split user input to parse command (also known as message)
          String[] clientRequestParts = clientRequest.split(" ");

          ClientCommand command = null;
          try {
            command = ClientCommand.valueOf(clientRequestParts[0]);
          } catch (Exception e) {
            // Do nothing
          }

          // Prepare response
          String response = null;
          // Handle request from client
          switch (command) {
            case PLAY -> {
              if (clientRequestParts.length != 2) {
                System.out.println(
                        "[Server] " + command + " command received without <gridSize> parameter. Replying with "
                                + "ERROR"
                                + ".");
                response = "ERROR" + " Missing <gridSize> parameter or too much parameters. Please try again.";
                break;
              }
              //TODO Multithreads sudoku, check win condition

              if(Objects.equals(clientRequestParts[1], "9") || Objects.equals(clientRequestParts[1], "16")){
                try {
                  pressedPlay = true;
                  response = "RECEIVE_GRID " + sudoku.importSudoku(clientRequestParts[1]);
                } catch (Exception e) {
                  e.printStackTrace();
                }
              }else{
                System.out.println(
                        "[Server] " + command + " command received with a wrong grid size. Replying with "
                                + "ERROR"
                                + ".");
                response = "ERROR" + " Choose 9 or 16 as grid size. Please try again.";
                break;
              }
            }
            case SELECT -> {
              if (clientRequestParts.length != 3) {
                System.out.println("[Server] " + command + " command received without <case name> or without <number to play> parameter. Replying with "
                        + "ERROR"
                        + ".");
                response = "ERROR" + " Missing <case name> or <number to play> parameter or too much parameters. Please try again.";
                break;
              }
              if(!pressedPlay){
                System.out.println("[Server] " + command + " sent before starting a game. Replying with "
                        + "ERROR"
                        + ".");
                response = "ERROR" + " You must start a game with PLAY <gridSize>, type HELP for more informations.";
                break;
              }

              String caseName = clientRequestParts[1];
              String numberToPlay = clientRequestParts[2];


              MoveValidity move = sudoku.verifyMove(caseName, numberToPlay);
              response = switch (move) {
                case CORRECT_MOVE -> "CORRECT_MOVE " + caseName + " " + numberToPlay;
                case WRONG_MOVE -> "WRONG_MOVE ";
                case ALREADY_PLACED -> "ALREADY_PLACED ";
                case OUT_OF_BOUNDS -> "OUT_OF_BOUNDS ";
                case COMPLETED -> "COMPLETED ";
                default -> response;
              };

            }
            case null, default -> {
              System.out.println("[Server] Unknown command sent by client, reply with "
                      + "ERROR"
                      + ".");
              response = "ERROR" + " Unknown command. Please try again.";

            }
          }

          // Send response to client
          if (response != null) {
            out.write(response + "\n");
            out.flush();
          }
        }

        System.out.println("[Server] Closing connection");
      } catch (IOException e) {
        System.out.println("[Server] IO exception: " + e);
      }
      return;
    }

  }
}
