package cn.settile.lzjyzq2.fileserver.activity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.provider.Settings;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import cn.settile.lzjyzq2.fileserver.R;
import cn.settile.lzjyzq2.fileserver.application.myapplication;
import ticwear.design.app.AlertDialog;
import ticwear.design.widget.SimpleSwitch;
import webserver.WebServer;
import webserver.util.Config;
import webserver.util.IPUtil;

public class MainActivity extends WearableActivity implements View.OnClickListener, Rationale<List<String>> {

    private static WebServer webServer;
    private static final String TAG = "MainActivity";
    protected static boolean serverflag = false;
    private static String ip = null;
    private ConnectivityManager connectivityManager;
    private WifiManager wifiManager;
    private int dialogstatus = 0;
    private static final int REQ_CODE_PERMISSION = 9527;

    private static int LASTTIME = 1564588740;
    private String[] mypermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE
    };

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    wifi_status_tv.setText("http://"+(String)msg.obj);
                    break;
            }
        }
    };
    private LinearLayout showQRCodeBtn,showHelpBtn;
    private TextView server_status_tv, wifi_status_tv,mainactivity_title;
    private SimpleSwitch start_server_switch;

    private ImageView wifi_img,help_img;


    private ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback(){
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if(networkInfo!=null&&(networkInfo.isConnectedOrConnecting()||networkInfo.isAvailable())){
                ip = IPUtil.intToIp(wifiManager.getConnectionInfo().getIpAddress())+":"+ Config.getInstance().getPORT();
                if(wifi_status_tv !=null){
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
            if(networkInfo!=null&&!(networkInfo.isConnected()||networkInfo.isConnectedOrConnecting()||networkInfo.isAvailable())){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ip=null;
                        wifi_status_tv.setText(R.string.wifi_status_hint);
                    }
                });
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
        }
        initServer();
        initView();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        if (wifi_img!=null){
            wifi_img.setImageResource(R.drawable.icon_wifi_a_on);
        }
        if (help_img!=null) {
            help_img.setImageResource(R.drawable.icon_help_a_on);
        }
        if(mainactivity_title!=null){
            mainactivity_title.setText(R.string.mainactivity_title1);
        }
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        if (wifi_img!=null){
            wifi_img.setImageResource(R.drawable.icon_wifi_a_off);
        }
        if (help_img!=null) {
            help_img.setImageResource(R.drawable.icon_help_a_off);
        }
        if(mainactivity_title!=null){
            mainactivity_title.setText(R.string.mainactivity_title);
        }
    }

    private void initServer() {
        webServer = new WebServer();
        wifiManager = (WifiManager) myapplication.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        if(connectivityManager!=null){
            connectivityManager.registerNetworkCallback(new NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build(), networkCallback);
        }
    }

    private void initView() {
        start_server_switch = findViewById(R.id.server_start_switch);
        showQRCodeBtn = findViewById(R.id.show_qrcode_btn);
        showHelpBtn = findViewById(R.id.show_help_btn);
        showQRCodeBtn.setOnClickListener(this);
        showHelpBtn.setOnClickListener(this);
        server_status_tv = findViewById(R.id.server_status_tv);
        wifi_status_tv = findViewById(R.id.wifi_status_tv);

        mainactivity_title = findViewById(R.id.mainactivity_title);
        wifi_img = findViewById(R.id.wifi_img);
        help_img = findViewById(R.id.help_img);
        if(ip!=null){
            wifi_status_tv.setText("http://"+ip);
        }
        start_server_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    try {
                        startServer();
                    } catch (IOException e) {
                        e.printStackTrace();
                        serverflag = false;
                        start_server_switch.setChecked(false);
                        return;
                    }
                    server_status_tv.setText(R.string.server_status_hint_runnig);
                    showQRCodeBtn.setVisibility(View.VISIBLE);
                    AnimatorSet animatorSet = new AnimatorSet();  //组合动画
                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(showQRCodeBtn, "scaleX", 0f, 1f);
                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(showQRCodeBtn, "scaleY", 0f, 1f);
                    ObjectAnimator alpha = ObjectAnimator.ofFloat(showQRCodeBtn,"alpha",0f,100f);
                    animatorSet.setDuration(300);  //动画时间
                    animatorSet.setInterpolator(new DecelerateInterpolator());  //设置插值器
                    animatorSet.play(scaleX).with(scaleY).with(alpha);  //同时执行
                    animatorSet.start();  //启动动画
                }else {
                    stopServer();
                    server_status_tv.setText(R.string.server_status_hint_start);
                    showQRCodeBtn.setVisibility(View.GONE);
                }
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopServer();
    }
    private void startServer() throws IOException {
        if(webServer!=null&&!serverflag) {
            webServer.start();
            serverflag = true;
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
            case R.id.show_help_btn:
                startActivity(new Intent(this, HelpActivity.class));
                break;
            case R.id.show_qrcode_btn:
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
                } else {
                    // 没有对应的权限
                    finish();
                }
                break;
        }
    }

    @Override
    public void showRationale(Context context, List<String> data, RequestExecutor executor) {
        AlertDialog.Builder builder = new  AlertDialog.Builder(context);
        builder.setTitle(R.string.permissions_dialog_title);
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setMessage(R.string.permissions_dialog_message_1);
        builder.setCancelable(false);
        builder.setPositiveButtonIcon(R.drawable.tic_ic_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.dialogstatus=1;
                executor.execute();
            }
        });
        builder.setNegativeButtonIcon(R.drawable.tic_ic_btn_cancel, new DialogInterface.OnClickListener() {
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
        builder.setPositiveButtonIcon(R.drawable.tic_ic_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AndPermission.with(MainActivity.this)
                        .runtime()
                        .setting()
                        .start(REQ_CODE_PERMISSION);
                dialogstatus = 1;
            }
        });
        builder.setNegativeButtonIcon(R.drawable.tic_ic_btn_cancel, new DialogInterface.OnClickListener() {
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
                    MainActivity.this.finish();
                }else{
                    dialogstatus = 0;
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
