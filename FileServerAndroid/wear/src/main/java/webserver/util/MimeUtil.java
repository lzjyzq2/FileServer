package webserver.util;

import java.util.ResourceBundle;

public class MimeUtil {
    static MimeUtil instance = new MimeUtil();
    ResourceBundle resourceBundle = null;

    private MimeUtil() {
        resourceBundle = ResourceBundle.getBundle("mime");
    }

    public Boolean hasKeys(String key) {
        return resourceBundle.containsKey(key);
    }

    public String getMimeType(String key) {
        if (hasKeys(key))
            return resourceBundle.getString(key);
        return "*/*";
    }
    public String getMimeTypeforPath(String path){
        String type = path.substring(path.lastIndexOf('.') + 1, path.length());
        return getMimeType(type);
    }

    public static MimeUtil getInstance() {
        return instance;
    }
}
