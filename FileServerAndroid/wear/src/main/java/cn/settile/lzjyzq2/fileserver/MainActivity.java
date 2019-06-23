package cn.settile.lzjyzq2.fileserver;

import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.io.IOException;

import webserver.WebServer;

public class MainActivity extends WearableActivity implements View.OnClickListener {

    private TextView mTextView;
    private static WebServer webServer;
    private static final String TAG = "MainActivity";
    private static boolean serverflag = false;

    private Button startserverbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Enables Always-on
        setAmbientEnabled();
        initServer();
        initView();
    }

    private void initServer() {
        webServer = new WebServer();
        ConnectivityManager manager = (ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE);
        if(manager!=null){
            manager.requestNetwork(new NetworkRequest.Builder().build(), new ConnectivityManager.NetworkCallback(){
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    if(networkInfo!=null){
                        if(networkInfo.isConnected()){
                            Log.e("WIFI Status",networkInfo.isConnected()+"");
                            Log.e("IP",networkInfo.getExtraInfo());
                        }
                    }
                }

                @Override
                public void onLosing(Network network, int maxMsToLive) {
                    super.onLosing(network, maxMsToLive);
                }

                @Override
                public void onLost(Network network) {
                    super.onLost(network);
                }

                @Override
                public void onUnavailable() {
                    super.onUnavailable();
                }

                @Override
                public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                }

                @Override
                public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
                    super.onLinkPropertiesChanged(network, linkProperties);
                }
            });
        }
    }

    private void initView() {
        startserverbtn = findViewById(R.id.startserver_btn);
        startserverbtn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopServer();
    }
    private void startServer(){
        if(webServer!=null&&!serverflag) {
            try {
                webServer.start();
                serverflag = true;
            } catch (Exception e) {
                Log.e(TAG,e.toString());
            }
        }
    }
    private void stopServer(){
        if(webServer!=null) {
            webServer.closeAllConnections();
            webServer.stop();
        }
        serverflag = false;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.startserver_btn:
                if(serverflag){
                    stopServer();
                    startserverbtn.setBackgroundResource(R.drawable.icon_do);
                }else {
                    startServer();
                    startserverbtn.setBackgroundResource(R.drawable.icon_done);
                }
                break;
        }
    }
}
