package archiver_ui.console.input;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.LinkedList;
import java.util.function.Predicate;

public class FormBuilder {
    private InputStream inputStream = System.in;
    private PrintStream printStream = System.out;
    private final List<InputField> fieldList = new LinkedList<>();

    public FormBuilder() {
    }

    public FormBuilder(InputStream inputStream, PrintStream printStream) {
        this.inputStream = inputStream;
        this.printStream = printStream;
    }

    public FormBuilder setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }

    public FormBuilder setPrintStream(PrintStream printStream) {
        this.printStream = printStream;
        return this;
    }

    public FormBuilder addField(InputField field) {
        this.fieldList.add(field);
        return this;
    }

    public FormBuilder addField(String label, String name, Predicate<String> predicate, String errorMessage) {
        this.fieldList.add(
                new InputField(inputStream, printStream, label, name, predicate, errorMessage)
        );
        return this;
    }

    public FormBuilder addField(String label, String name, Predicate<String> predicate,String errorMessage, boolean isMultiple, String splitter) {
        this.fieldList.add(
                new InputField(inputStream, printStream, label, name, predicate, errorMessage, isMultiple, splitter)
        );
        return this;
    }

    public InputForm build() {
        return new InputForm(fieldList);
    }
}
