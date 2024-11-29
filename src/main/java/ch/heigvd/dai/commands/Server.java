package ch.heigvd.dai.commands;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(name = "server", description = "Start the server part of the network game.")
public class Server implements Callable<Integer> {

  @CommandLine.Option(
      names = {"-p", "--port"},
      description = "Port to use (default: ${DEFAULT-VALUE}).",
      defaultValue = "1236")
  protected int port;

  public enum ClientCommand {
    PLAY,
    SELECT,
  }

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

  @Override
  public Integer call() {
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      System.out.println("[Server] Listening on port " + port);

      while (!serverSocket.isClosed()) {
        try (Socket socket = serverSocket.accept();
             Reader reader = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
             BufferedReader in = new BufferedReader(reader);
             Writer writer =
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
             BufferedWriter out = new BufferedWriter(writer)) {
          System.out.println(
                  "[Server] New client connected from "
                          + socket.getInetAddress().getHostAddress()
                          + ":"
                          + socket.getPort());

          // Run REPL until client disconnects
          while (!socket.isClosed()) {
            // Read response from client
            String clientRequest = in.readLine();
            // If clientRequest is null, the client has disconnected
            // The server can close the connection and wait for a new client
            if (clientRequest == null) {
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
                                  + ServerCommand.ERROR
                                  + ".");
                  response = ServerCommand.ERROR + " Missing <gridSize> parameter. Please try again.";
                  break;
                }
                //out.write(import_sudoku(clientRequestParts[1]));
                response = ServerCommand.OK + " OK ";
              }
              case SELECT -> {
                if (clientRequestParts.length != 3) {
                  System.out.println("[Server] " + command + " command received without <case name> or without <number to play> parameter. Replying with "
                          + ServerCommand.ERROR
                          + ".");
                  response = ServerCommand.ERROR + " Missing <case name> or <number to play> parameter. Please try again.";
                  break;
                }

                String caseName = clientRequestParts[1];
                String numberToPlay = clientRequestParts[2];

                /*
                int move = verifyMove(caseName, NumberToPlay);
                switch(move){
                  case 0: response = ServerCommand.CORRECT_MOVE + " CORRECT MOVE " + caseName + " " + numberToPlay;
                          sudoku.changeGrid(caseName, numberToPlay);
                          break;
                  case 1: response = ServerCommand.WRONG_MOVE + " WRONG MOVE ";
                          break;
                  case 2: response = ServerCommand.ALREADY_PLACED + " ALREADY PLACED ";
                          break;
                  case 3: response = ServerCommand.OUT_OF_BOUNDS + " OUT OF BOUNDS ";
                          break;
                  case 4: response = ServerCommand.COMPLETED + " COMPLETED ";
                          sudoku.changeGrid(caseName, numberToPlay);
                }
                */

              }
              case null, default ->{
                System.out.println("[Server] Unknown command sent by client, reply with "
                        + ServerCommand.ERROR
                        + ".");
                response = ServerCommand.ERROR + " Unknown command. Please try again.";

              }
            }

            // Send response to client
            if(response != null) {
              out.write(response);
              out.flush();
            }
          }

          System.out.println("[Server] Closing connection");
        } catch (IOException e) {
          System.out.println("[Server] IO exception: " + e);
        }
      }
    } catch (IOException e) {
      System.out.println("[Server] IO exception: " + e);

    }
    return 0;
  }
}