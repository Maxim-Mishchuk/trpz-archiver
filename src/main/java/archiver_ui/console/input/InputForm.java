package archiver_ui.console.input;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InputForm {
    private final List<InputField> fields;
    private final Map<String, String> results = new HashMap<>();

    public InputForm(List<InputField> fields) {
        this.fields = fields;
    }

    public Map<String, String> execute() {
        for (InputField field: fields) {
            String res = field.start();
            results.put(field.getName(), res);
        }
        return results;
    }
}
