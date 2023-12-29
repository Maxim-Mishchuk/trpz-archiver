package archiver_ui.console;

import archiver_ui.console.environments.impl.MainEnvironment;
import archiver_ui.utils.ResourceManager;

public class Main {
    public static void main(String[] args) {
        System.out.println("\n" + ResourceManager.INSTANCE.getString("greeting") + "\n");
        new MainEnvironment(System.in, System.out).start();
    }
}
