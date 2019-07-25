package webserver.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;

public class VersionUtil {
    static VersionUtil instance = new VersionUtil();
    Properties properties;
    private VersionUtil() {
        properties = new Properties();
        InputStream in = VersionUtil.class.getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getVersion(){
        return properties.getProperty("Manifest-Version");
    }

    public static VersionUtil getInstance() {
        return instance;
    }
}
