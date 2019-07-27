package cn.settile.lzjyzq2.fileserver.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

import cn.settile.lzjyzq2.fileserver.R;
import ticwear.design.widget.ScalableTextView;
import webserver.util.Config;

public class HelpInfoActivity extends WearableActivity {

    private TextView content;
    private ScalableTextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_info);
        Intent intent = getIntent();
        content = findViewById(R.id.text_content);
        title = findViewById(R.id.info_title);

        switch (intent.getStringExtra("form")){
            case "上传路径":
                title.setText(R.string.help_uploadpath_title);
                content.setText(Config.getInstance().getUPLOAD());
                break;
            case "权限":
                title.setText(R.string.help_permission_title);
                content.setText(R.string.help_permission_content);
                break;
            case "网络":
                title.setText(R.string.help_network_title);
                content.setText(R.string.help_network_content);
                break;
            case "注意事项":
                title.setText(R.string.help_careful_title);
                content.setText(R.string.help_careful_content);
                break;
            case "微光模式":
                title.setText(R.string.help_twilight_mode_title);
                content.setText(R.string.help_twilight_mode_content);
                break;
            case "使用步骤":
                title.setText(R.string.help_useinfo_title);
                content.setText(R.string.help_useinfo_content);
                break;
        }
    }
}
