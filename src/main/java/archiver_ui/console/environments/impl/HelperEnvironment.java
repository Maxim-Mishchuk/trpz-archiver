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
            case "help", "helper" -> out.println("""
                    archive/archiver - to open archiver environment;
                    file transfer/fileTransfer/transfer - to open file transfer environment;
                    
                    Use that that commands in the helper environment to see detail information.
                    """);
            case "archive", "archiver" -> out.println("""
                    new - to create new archive;
                    add - to add files to existed archives;
                    remove - to remove files from existed archive;
                    show - to show list of files of existed archive;
                    extract - to extract files from existed archive;
                    """);
            case "fileTransfer", "transfer", "file transfer" -> out.println("""
                    send - to send a file to other client;
                    init - to initialize receiving files from other clients;
                    """);
            default -> out.println("incorrect command: " + command);
        }
    }
}
