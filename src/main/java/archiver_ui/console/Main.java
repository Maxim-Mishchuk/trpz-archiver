package archiver_ui.console;

import archiver_ui.console.environments.impl.MainEnvironment;

public class Main {
    public static void main(String[] args) {
        new MainEnvironment(System.in, System.out).start();
    }
}
