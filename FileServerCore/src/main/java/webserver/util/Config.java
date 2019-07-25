package webserver.util;

import java.util.ResourceBundle;

public class Config {
    private static Config config = new Config();
    private int PORT = 8080;
    private String WEBDOC = null;
    private String DISKPATH = null;
    private String UPLOAD = null;
    private Config(){
        ResourceBundle resource = ResourceBundle.getBundle("server");
        try {
            WEBDOC = resource.getString("WEBDOC");
            DISKPATH = resource.getString("DISKPATH");
            PORT = Integer.parseInt(resource.getString("PORT"));
            UPLOAD = resource.getString("UPLOAD");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Config getInstance(){
        return config;
    }

    public int getPORT() {
        return PORT;
    }

    public String getWEBDOC() {
        return WEBDOC;
    }

    public String getDISKPATH() {
        return DISKPATH;
    }

    public String getUPLOAD() {
        return UPLOAD;
    }
}
