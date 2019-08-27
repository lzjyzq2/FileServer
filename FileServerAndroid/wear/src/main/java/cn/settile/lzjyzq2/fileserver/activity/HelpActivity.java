package cn.settile.lzjyzq2.fileserver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.settile.lzjyzq2.fileserver.R;
import cn.settile.lzjyzq2.fileserver.adapter.HelpListAdapter;

public class HelpActivity extends WearableActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private HelpListAdapter helpListAdapter;
    List<String> data;
    private String TAG = "HelpActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        initView();
    }

    private void initView() {
        data = new ArrayList<>();
        data.add("使用步骤");
        data.add("上传路径");
        data.add("权限");
        data.add("网络");
        data.add("注意事项");
        data.add("微光模式");
        data.add("关于");
        helpListAdapter = new HelpListAdapter(this,R.layout.helpitem_layout,data);
        listView = findViewById(R.id.helplist);
        listView.setOnItemClickListener(this);
        listView.setAdapter(helpListAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        switch (data.get(position)){
            case "使用步骤":
                intent = new Intent(this,HelpInfoActivity.class);
                intent.putExtra("form",getString(R.string.help_useinfo_title));
                startActivity(intent);
                break;
            case "上传路径":
                intent = new Intent(this,HelpInfoActivity.class);
                intent.putExtra("form",getString(R.string.help_uploadpath_title));
                startActivity(intent);
                break;
            case "权限":
                intent = new Intent(this,HelpInfoActivity.class);
                intent.putExtra("form",getString(R.string.help_permission_title));
                startActivity(intent);
                break;
            case "网络":
                intent = new Intent(this,HelpInfoActivity.class);
                intent.putExtra("form",getString(R.string.help_network_title));
                startActivity(intent);
                break;
            case "注意事项":
                intent = new Intent(this,HelpInfoActivity.class);
                intent.putExtra("form",getString(R.string.help_careful_title));
                startActivity(intent);
                break;
            case "微光模式":
                intent = new Intent(this,HelpInfoActivity.class);
                intent.putExtra("form",getString(R.string.help_twilight_mode_title));
                startActivity(intent);
                break;
            case "关于":
                intent = new Intent(this,AboutActivity.class);
                startActivity(intent);
                break;
        }
    }
}
