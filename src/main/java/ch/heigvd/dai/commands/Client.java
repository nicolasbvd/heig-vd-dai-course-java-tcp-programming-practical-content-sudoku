package ch.heigvd.dai.commands;

import java.util.concurrent.Callable;

import ch.heigvd.dai.sudoku.Sudoku;
import picocli.CommandLine;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

@CommandLine.Command(name = "client", description = "Start the client part of the network game.")
public class Client implements Callable<Integer> {

  Sudoku sudoku;

  @CommandLine.Option(
          names = {"-H", "--host"},
          description = "Host to connect to.",
          required = true)
  protected String host;

  @CommandLine.Option(
          names = {"-p", "--port"},
          description = "Port to use (default: ${DEFAULT-VALUE}).",
          defaultValue = "1236")
  protected int port;

  public enum ClientCommand {
    HELP,
    PLAY,
    SELECT,
    QUIT
  }

  public enum ServerCommand {
    RECEIVE_GRID,
    CORRECT_MOVE,
    WRONG_MOVE,
    ALREADY_PLACED,
    OUT_OF_BOUNDS,
    COMPLETED
  }

  @Override
  public Integer call() {
    try (Socket socket = new Socket(host, port);
         BufferedReader in =
                 new BufferedReader(
                         new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
         BufferedWriter out =
                 new BufferedWriter(
                         new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));) {
      System.out.println("[Client] Connected to " + host + ":" + port);
      System.out.println();
      help();
      while (!socket.isClosed()) {
        // Read user input
        Reader inputReader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
        BufferedReader bir = new BufferedReader(inputReader);
        String userInput = bir.readLine();

        String newSize = "";
        try {
          String[] userInputParts = userInput.split(" ");
          ClientCommand command = ClientCommand.valueOf(userInputParts[0].toUpperCase());
          String request = null;

          switch (command) {
            case PLAY:
              request = ClientCommand.PLAY.toString() + " " + userInputParts[1];
              newSize = userInputParts[1];
              break;
            case SELECT:
              if(userInputParts.length > 3) {
                throw new IllegalStateException("Commands unknown");
              }
              request = ClientCommand.SELECT.toString() + " " + userInputParts[1] + " " + userInputParts[2];
              break;
            case QUIT:
              socket.close();
              continue;
            default:
              help();
          }
          if (request != null) {
            out.write(request + "\n");
            out.flush();
          }

        } catch (Exception e) {
          System.out.println("Invalid command. Please try again.");
          continue;
        }


        String serverResponse = in.readLine();

        // If serverResponse is null, the server has disconnected
        if (serverResponse == null) {
          socket.close();
          continue;
        }



        // Split response to parse message (also known as command)
        String[] serverResponseParts = serverResponse.split(" ");

        ServerCommand message = null;
        try {
          message = ServerCommand.valueOf(serverResponseParts[0]);
        } catch (IllegalArgumentException e) {
          // Do nothing
        }

        // Handle response from server
        switch (message) {
          case RECEIVE_GRID->{
            sudoku = new Sudoku(serverResponseParts[1], newSize);
            System.out.println(sudoku);
            break;
          }
          case CORRECT_MOVE -> {
            String CorrectMove = serverResponseParts[0];
            System.out.println(CorrectMove);
            sudoku.applyMove(serverResponseParts[1], serverResponseParts[2]);
            System.out.println(sudoku);
            break;
          }
          case WRONG_MOVE -> {
            String WrongMove = serverResponseParts[0];
            System.out.println(WrongMove);
            break;
          }
          case ALREADY_PLACED -> {
            String AlreadyPlaced = serverResponseParts[0];
            System.out.println(AlreadyPlaced);
            break;
          }
          case OUT_OF_BOUNDS -> {
            String OutOfBounds = serverResponseParts[0];
            System.out.println(OutOfBounds);
            break;
          }
          case COMPLETED -> {
            System.out.println(sudoku);
            System.out.println("Congratulations! You have completed the game.");
            help();
            break;
          }
          case null, default->{
            System.out.println(serverResponse);
            break;
          }
        }
      }
      System.out.println("[Client] Closing connection and quitting...");
    } catch(Exception e){
      System.out.println("[Client] Exception: " + e);
    }
    return 0;
  }

  private static void help() {
    System.out.println("Welcome to the Sudoku game, here are the commands to start");
    System.out.println("PLAY <size of grid> (9 or 16)");
    System.out.println("SELECT <case name> <number to play> (if the size of grid is 9: case name : A1-I9, if the size of grid is 16 : A1 - P16");
    System.out.println("QUIT");
    System.out.println("You can anytime send HELP to see it again!");
  }
}


