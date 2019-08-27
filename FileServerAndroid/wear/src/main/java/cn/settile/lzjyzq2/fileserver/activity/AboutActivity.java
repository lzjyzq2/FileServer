package cn.settile.lzjyzq2.fileserver.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import cn.settile.lzjyzq2.fileserver.R;

public class AboutActivity extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setAmbientEnabled();
    }

}
