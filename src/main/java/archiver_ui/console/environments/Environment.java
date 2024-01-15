package archiver_ui.console.environments;

import archiver_ui.utils.ResourceManager;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public abstract class Environment {
    protected String inputType;
    protected final InputStream in;
    protected final PrintStream out;

    protected static final ResourceManager resourceManager = ResourceManager.INSTANCE;

    public Environment(InputStream in, PrintStream out) {
        this.in = in;
        this.out = out;
    }

    public void start() {
        Scanner scanner = new Scanner(in);

        String command;
        out.print(inputType);
        while (!(command = scanner.nextLine()).equals("exit")) {
            analyze(command);
            out.print(inputType);
        }
    }

    protected void incorrectCommand(String command) {
        if (!command.isBlank())
            out.printf(resourceManager.getString("incorrectCommand"), command);
    }

    protected abstract void analyze(String command);
}
