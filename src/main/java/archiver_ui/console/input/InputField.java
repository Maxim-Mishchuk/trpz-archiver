package archiver_ui.console.input;

import lombok.Data;
import lombok.Getter;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.function.Predicate;

@Getter
public class InputField {
    private final InputStream in;
    private final PrintStream out;
    private final String label;
    private final String name;
    private final Predicate<String> predicate;
    private final String errorMessage;
    private boolean isMultiple = false;
    private String splitter = "";

    public InputField(InputStream in, PrintStream out, String label, String name, Predicate<String> predicate, String errorMessage) {
        this.in = in;
        this.out = out;
        this.label = label;
        this.name = name;
        this.predicate = predicate;
        this.errorMessage = errorMessage;
    }

    public InputField(InputStream in, PrintStream out, String label, String name, Predicate<String> predicate, String errorMessage, boolean isMultiple, String splitter) {
        this(in, out, label, name, predicate, errorMessage);
        this.isMultiple = isMultiple;
        this.splitter = splitter;
    }

    public String start() {
        Scanner scanner = new Scanner(in);
        StringBuilder res = new StringBuilder();

        do {
            String currentInput;
            while (true) {
                out.print(label);
                currentInput = scanner.nextLine().trim();

                if (currentInput.isEmpty() && isMultiple) {
                    isMultiple = false;
                    res.deleteCharAt(res.length() - 1);
                    break;
                }

                if (predicate.test(currentInput)) {
                    res
                            .append(currentInput)
                            .append(splitter);
                    break;
                }
                out.println(errorMessage + "\n");
            }
        } while (isMultiple);

        return res.toString();
    }
}
