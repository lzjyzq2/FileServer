package cn.settile.lzjyzq2.fileserver;

import android.app.Application;
import android.content.Context;

public class myapplication extends Application {
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}
