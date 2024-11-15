package ch.heigvd.dai.commands;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(name = "HELP", description = "Give the commands to play the game.")
public class Help implements Callable<Integer> {
    @Override
    public Integer call() {
        System.out.println("Welcome to the Sudoku game, here are the commands to start");
        System.out.println("PLAY <size of grid> (9 or 16)");
        System.out.println("SELECT <case name> <number to play> (if the size of grid is 9: case name : A1-I9, if the size of grid is 16 : A1 - P16");
        System.out.println("can anytime send HELP to see it again!");
        return 0;
    }
}
