package archiver_ui.console.environments.impl;

import archiver_ui.console.environments.Environment;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class HelperEnvironment extends Environment {

    public HelperEnvironment(InputStream is, PrintStream os) {
        super(is, os);
        this.inputType = "helper> ";
    }

    protected void analyze(String command) {
        switch (command) {
            case "help", "helper" -> out.println("\n" + resourceManager.getString("helperHelp") + "\n");
            case "archive", "archiver" -> out.println("\n" + resourceManager.getString("helperArchiver") + "\n");
            case "fileTransfer", "transfer", "file transfer" -> out.println("\n" + resourceManager.getString("helperFileTransfer") + "\n");
            default -> incorrectCommand(command);
        }
    }
}
