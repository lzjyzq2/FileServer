package cn.settile.lzjyzq2.fileserver.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.ImageView;
import android.widget.TextView;

import cn.settile.lzjyzq2.fileserver.R;
import webserver.util.QRCodeUtil;

public class QRCodeActivity extends WearableActivity {

    private ImageView qrImageview;
    private TextView showIpTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        qrImageview = findViewById(R.id.qc_img);
        showIpTv = findViewById(R.id.showip_tv);
        Intent intent = getIntent();
        String ip = intent.getStringExtra(getString(R.string.start_qractivity_intent_flag_ip));
        if(null!=ip&&""!=ip){
            showIpTv.setText("http://"+ip);
            qrImageview.setImageBitmap(QRCodeUtil.createQRCodeBitmap("http://"+ip,320,320,"UTF-8","H","1", Color.BLACK,Color.WHITE));
        }else {
            showIpTv.setText(R.string.wifi_status_hint);
            qrImageview.setImageResource(R.drawable.icon_wifi_a_off);
        }
    }
}