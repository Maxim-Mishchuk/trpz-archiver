package archiver_ui.console.environments.impl;

import archiver_ui.console.environments.Environment;
import archiver_ui.utils.ResourceManager;

import java.io.InputStream;
import java.io.PrintStream;

public class MainEnvironment extends Environment {
    public MainEnvironment(InputStream is, PrintStream os) {
        super(is, os);
        this.inputType = "> ";
    }

    protected void analyze(String command) {
        switch (command) {
            case "help", "helper" -> new HelperEnvironment(in, out).start();
            case "archive", "archiver" -> new ArchiverEnvironment(in, out).start();
            case "fileTransfer", "transfer", "file transfer" -> new FileTransferEnvironment(in, out).start();
            default -> incorrectCommand(command);
        }
    }
}
