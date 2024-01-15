package archiver_ui.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceManager {
    private final String root = "location/lang";
    private ResourceBundle currentBundle = ResourceBundle.getBundle(root);
    public static final ResourceManager INSTANCE = new ResourceManager();
    private ResourceManager() {}

    public Locale getLocale() {
        return currentBundle.getLocale();
    }
    public void changeResource(Locale locale) {
        currentBundle = ResourceBundle.getBundle(root, locale);
    }

    public String getString(String key) {
        return currentBundle.getString(key);
    }
}
