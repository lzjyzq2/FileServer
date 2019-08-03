package cn.settile.lzjyzq2.fileserver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import cn.settile.lzjyzq2.fileserver.R;

public class HelpListAdapter extends ArrayAdapter{
    List<String> list;
    int resource;
    public HelpListAdapter(@NonNull Context context, int resource,List<String> list) {
        super(context, resource);
        this.list = list;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return list.size();
    }


    @Nullable
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view  = LayoutInflater.from(getContext()).inflate(resource,null,false);
        TextView title = view.findViewById(R.id.item_title);
        title.setText(list.get(position));
        return view;
    }
}
