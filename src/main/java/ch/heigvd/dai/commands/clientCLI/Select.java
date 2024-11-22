package ch.heigvd.dai.commands.clientCLI;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(name = "SELECT", description = "Select the name of the chosen case and the number to include in the grid.")
public class Select implements Callable<Integer> {

    @CommandLine.Parameters(
            index = "0",
            description = "SELECT")
    protected String command;

    @CommandLine.Parameters(
            index = "1",
            description = "Chosen name case(for e.g. A1")
    protected int argument1;

    @CommandLine.Parameters(
            index = "2",
            description = "Chosen number(for e.g. 1")
    protected int argument2;

    @Override
    public Integer call() throws Exception {
        int result = 0;
        //int result = sudoku.verifyMove();
        switch (result) {
            case 0: System.out.println("Good move");break;
            case 1: System.out.println("Bad move, out of boundary");break;
            case 2: System.out.println("Bad move, case already used");break;
            case 3: System.out.println("Bad move");break;
        }
        //sudoku.changeGrid(argument1, argument2);
        return 0;

    }
}
