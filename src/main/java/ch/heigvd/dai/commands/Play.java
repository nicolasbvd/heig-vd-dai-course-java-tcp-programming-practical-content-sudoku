package ch.heigvd.dai.commands;
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
    protected int argument;

    @Override
    public Integer call() throws Exception {
        return 0;
    }
}
