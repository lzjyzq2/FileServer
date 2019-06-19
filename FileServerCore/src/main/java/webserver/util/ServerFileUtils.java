package webserver.util;

import webserver.bean.DiskInfo;

import java.io.*;

public class ServerFileUtils {
    static final String TAG = "ServerFileUtils";
    public static InputStream getInputStreamForPath(String path) {
        InputStream stream = null;
        File file = new File(path);
        try {
            if (file.exists()) {
                stream = new FileInputStream(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stream;
    }

    public static void SaveUploadFileToPath(String path, String filename,InputStream inputStream) {
        OutputStream stream = null;
        File file = new File(path + File.separator + filename);
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            stream = new FileOutputStream(file);
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            while ((len = inputStream.read(bs)) != -1) {
                stream.write(bs, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void SaveUploadFileToPath(String tempfile,String targetfile) {
        OutputStream stream = null;
        InputStream inputStream = null;
        File tmpfile = new File(tempfile);
        File file = new File(targetfile);
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            inputStream = new FileInputStream(tempfile);
            stream = new FileOutputStream(file);
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            while ((len = inputStream.read(bs)) != -1) {
                stream.write(bs, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static DiskInfo getDiskInfo(String path){
        File file = new File(path);
        return new DiskInfo(file.getFreeSpace(),file.getTotalSpace()-file.getFreeSpace(),file.getTotalSpace());
    }
}
