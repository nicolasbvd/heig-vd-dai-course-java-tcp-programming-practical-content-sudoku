package ch.heigvd.dai.commands.clientCLI;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(name = "PLAY", description = "Start a sudoku game with a chosen size of grid.")
public class Play implements Callable<Integer> {

    @CommandLine.Parameters(
            index = "0",
            description = "Command (e.g. PLAY).")
    protected String command;

    @CommandLine.Parameters(
            index = "1",
            description = "Argument for the command (e.g. 9).")
    protected int gridSize;

    @Override
    public Integer call() throws Exception {
        //Sudoku sudoku = new Sudoku(gridSize);
        return 0;
    }
}
