package webserver.util;

import java.net.ServerSocket;
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
            ServerSocket serverSocket =  new ServerSocket(0); //读取空闲的可用端口
            PORT =  serverSocket.getLocalPort(); //Integer.parseInt(resource.getString("PORT"));
            serverSocket.close();
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
