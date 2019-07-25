package webserver.client;

import webserver.WebServer;
import webserver.util.Log;
import webserver.util.VersionUtil;

import java.io.IOException;
import java.util.Scanner;

public class ServerRunner{

    private static final String TAG = "ServerRunner";
    private static WebServer webServer;
    public static void main(String[] args) {
        Log.e(TAG, VersionUtil.getInstance().getVersion());
        webServer = new WebServer();
        try {
            webServer.start();
        }catch (Exception e){
            Log.e(TAG,e.toString());
        }
        Scanner scanner = new Scanner(System.in);
        String command;
        while ((command=scanner.next())!=null){
            switch (command){
                case "start":
                    if(webServer!=null&&!webServer.isAlive()) {
                        try {
                            webServer.start();
                        } catch (IOException e) {
                            Log.e(TAG,e.toString());
                        }
                    }
                    break;
                case "stop":
                    if(webServer!=null) {
                        webServer.closeAllConnections();
                        webServer.stop();
                    }
                    break;
            }
        }
    }
}
