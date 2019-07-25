package cn.settile.lzjyzq2.fileserver;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.util.Calendar;
import java.util.List;

import webserver.WebServer;
import webserver.util.Config;
import webserver.util.IPUtil;

public class MainActivity extends WearableActivity implements View.OnClickListener, Rationale<List<String>> {

    private static WebServer webServer;
    private static final String TAG = "MainActivity";
    private static boolean serverflag = false;
    private static String ip = null;
    private ConnectivityManager connectivityManager;

    private int dialogstatus = 0;
    private static final int REQ_CODE_PERMISSION = 9527;

    private static int LASTTIME = 1564588740;
    private String[] mypermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE
    };

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    showipTv.setText((String)msg.obj);
                    break;
            }
        }
    };
    private LinearLayout startserverBtn,showQRCodeBtn,showInfoBtn;
    private ImageView startserverImageView,showQRCodeImageView,showInfoImageView;
    private TextView showserverTv,showipTv;

    private ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback(){
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if(networkInfo!=null&&networkInfo.isConnected()){
                WifiManager wifiManager = (WifiManager)myapplication.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                ip = IPUtil.intToIp(wifiManager.getConnectionInfo().getIpAddress())+":"+ Config.getInstance().getPORT();
                if(showipTv!=null){
                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = ip;
                    handler.sendMessage(message);
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
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if(networkInfo!=null&&networkInfo.isConnected()){
                ip=null;
                showipTv.setText(R.string.wifi_status_hint);
            }
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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enables Always-on
        setAmbientEnabled();

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
            AndPermission.with(this)
                    .runtime()
                    .permission(mypermissions)
                    .rationale(this)
                    .onGranted(GrantedAction)
                    .onDenied(DeniedAction)
                    .start();
        }else {
            initServer();
            initView();
        }
    }


    private void initServer() {
        webServer = new WebServer();
        connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        if(connectivityManager!=null){
            connectivityManager.registerNetworkCallback(new NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build(), networkCallback);
        }
    }

    private void initView() {
        startserverBtn = findViewById(R.id.startserver_btn);
        showQRCodeBtn = findViewById(R.id.showqrcode_btn);
        showInfoBtn = findViewById(R.id.showinfo_btn);
        startserverBtn.setOnClickListener(this);
        showQRCodeBtn.setOnClickListener(this);
        showInfoBtn.setOnClickListener(this);
        startserverImageView = findViewById(R.id.startserver_img);
        showserverTv = findViewById(R.id.showserver_tv);
        showipTv = findViewById(R.id.showip_tv);
        if(ip!=null){
            showipTv.setText(ip);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopServer();
        System.exit(0);
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
                    startserverImageView.setBackgroundResource(R.drawable.icon_do);
                    showserverTv.setText(R.string.server_status_hint_start);
                }else {
                    startServer();
                    startserverImageView.setBackgroundResource(R.drawable.icon_done);
                    showserverTv.setText(R.string.server_status_hint_runnig);
                }
                break;
            case R.id.showinfo_btn:
                startActivity(new Intent(this,InfoActivity.class));
                break;
            case R.id.showqrcode_btn:
                Intent intent = new Intent(this,QRCodeActivity.class);
                intent.putExtra(getString(R.string.start_qractivity_intent_flag_ip),ip);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_PERMISSION:
                if (AndPermission.hasPermissions(this, mypermissions)) {
                    // 有对应的权限
                    initServer();
                    initView();
                } else {
                    // 没有对应的权限
                    finish();
                }
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void showRationale(Context context, List<String> data, RequestExecutor executor) {
        AlertDialog.Builder builder = new  AlertDialog.Builder(context);
        builder.setTitle(R.string.permissions_dialog_title);
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setMessage(R.string.permissions_dialog_message_1);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.permissions_dialog_positivebutton_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.dialogstatus=1;
                executor.execute();
            }
        });
        builder.setNegativeButton(R.string.permissions_dialog_negativebutton_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                executor.cancel();
                MainActivity.this.dialogstatus=2;
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (MainActivity.this.dialogstatus==0){
                    executor.cancel();
                }else {
                    MainActivity.this.dialogstatus=0;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showSettingDialog(){
        this.dialogstatus = 0;
        AlertDialog.Builder builder = new  AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.permissions_dialog_title);
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setCancelable(false);
        builder.setMessage(R.string.permissions_dialog_message_2);
        builder.setPositiveButton(R.string.permissions_dialog_positivebutton_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AndPermission.with(MainActivity.this)
                        .runtime()
                        .setting()
                        .start(REQ_CODE_PERMISSION);
                dialogstatus = 1;
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.permissions_dialog_negativebutton_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(dialogstatus==0){
                    finish();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    Action GrantedAction = (Action<List<String>>) data -> {
        initServer();
        initView();
    };
    Action  DeniedAction = (Action<List<String>>) data -> {
        if(AndPermission.hasAlwaysDeniedPermission(MainActivity.this,data)){
            showSettingDialog();
        }else {
            AndPermission.with(MainActivity.this)
                    .runtime()
                    .permission(mypermissions)
                    .rationale(MainActivity.this::showRationale)
                    .onGranted(GrantedAction)
                    .onDenied(MainActivity.this.DeniedAction)
                    .start();
        }
    };
}
