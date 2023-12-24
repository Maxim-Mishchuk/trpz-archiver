package archiver_ui.console;

import archiver_ui.console.environments.impl.MainEnvironment;
import org.apache.commons.compress.utils.FileNameUtils;

public class Main {
    public static void main(String[] args) {
//        new MainEnvironment(System.in, System.out).start();
        String path = "test.tar.gz";
        String name = FileNameUtils.getBaseName(path);

        while (!FileNameUtils.getExtension(name).isBlank()) {
            name = FileNameUtils.getBaseName(name);
        }

        String extension = FileNameUtils.getExtension(path);

        System.out.println(name);
        System.out.println(extension);
    }
}
