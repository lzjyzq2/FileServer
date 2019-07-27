package webserver.util;

import cn.settile.lzjyzq2.fileserver.application.myapplication;
import webserver.bean.DiskInfo;
import webserver.bean.FileInfo;
import webserver.bean.UploaDirectoryInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerFileUtils {
    static final String TAG = "ServerFileUtils";
    public static String separator = System.getProperty("file.separator");
//    public static InputStream getInputStreamForPathInAndroid(String path){
//        try {
//            InputStream inputStream = myapplication.getContext().getAssets().open(path);
//            if (inputStream!=null){
//                return inputStream;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public static InputStream getInputStreamForAssets(String path) {
        try {
            InputStream inputStream = myapplication.getContext().getAssets().open(path);
            if (inputStream != null) {
                return inputStream;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream getInputStreamForPath(String path){
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
        }catch (Exception e){
            e.printStackTrace();
        }
        return inputStream;
    }

    public static void SaveUploadFileToPath(String path, String filename, InputStream inputStream) {
        OutputStream stream = null;
        File file = new File(path + separator + filename);
        try {
            if (!file.exists()) {
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

    public static void SaveUploadFileToPath(String tempfile, String targetfile) {
        OutputStream stream = null;
        InputStream inputStream = null;
        File tmpfile = new File(tempfile);
        File file = new File(targetfile);
        try {
            if (!file.exists()) {
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


    /*       ------------------ WebAPI所用 --------------------                    */

    public static DiskInfo getDiskInfo(String path) {
        File file = new File(path);
        return new DiskInfo(file.getFreeSpace(), file.getTotalSpace() - file.getFreeSpace(), file.getTotalSpace());
    }

    public static Map<String, List> getDirectoryAllFileInfo(String path) {
        Map<String, List> allfiles = new HashMap<>();
        List<FileInfo> files = new ArrayList<>();
        List<String> directories = new ArrayList<>();
        path = getDirPath(path);
        File file = new File(path);
        File[] tempList = file.listFiles();
        if (tempList != null) {
            for (int i = 0; i < tempList.length; i++) {
                if (tempList[i].isFile() && tempList[i].getName().charAt(0) != '$') {
                    files.add(new FileInfo(tempList[i].getName(), tempList[i].length(), tempList[i].lastModified()));
                }
                if (tempList[i].isDirectory() && tempList[i].getName().charAt(0) != '$') {
                    directories.add(tempList[i].getName());
                }
            }
            allfiles.put("files", files);
            allfiles.put("directories", directories);
        }
        return allfiles;
    }

    public static UploaDirectoryInfo getUploaDirectoryInfo() {
        UploaDirectoryInfo info = new UploaDirectoryInfo();
        List<String> listdir;
        String path = Config.getInstance().getUPLOAD();
        if (path.contains("/")) {
            listdir = new ArrayList<>(Arrays.asList(path.split("/")));
        } else if (path.contains("\\\\")) {
            listdir = new ArrayList<>(Arrays.asList(path.split("\\\\")));
        } else {
            listdir = new ArrayList<>();
        }
        info.setDirs(listdir);
        info.setAllfileinfo(getDirectoryAllFileInfo(Config.getInstance().getUPLOAD() + separator));
        return info;
    }

    public static Boolean mkdir(String dir, String name) {
        File file = new File(getDirPath(dir) + name);
        if (!file.exists()) {
            file.mkdirs();
            return true;
        }
        return false;
    }

    public static InputStream DownloadFile(String path, String name) {
        return getInputStreamForPath(getDirPath(path) + name);
    }

    public static Boolean DeleteFile(String path, String name) {
        File file = new File(getDirPath(path) + name);
        if (file.exists()) {
            return DeleteFile(file);
        }
        return false;
    }

    public static Boolean DeleteFile(File file) {
        if (!file.exists()) {
            return false;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                DeleteFile(f);
            }
        }
        return file.delete();
    }

    public static Boolean RemoveTo(String oldpath, String newpath, String name) {
        File file = new File(getDirPath(oldpath) + name);
        if (file.exists())
            if (file.renameTo(new File(getDirPath(newpath) + name)))
                return true;
        return false;
    }

    public static Boolean RenameTo(String path, String oldname, String newname) {
        File file = new File(getDirPath(path) + oldname);
        if (file.exists())
            if (file.renameTo(new File(getDirPath(path) + newname)))
                return true;
        return false;
    }

    public static String getDirPath(String path) {
        if (path.charAt(path.length() - 1) != '/' || path.charAt(path.length() - 1) != '\\')
            path = path + separator;
        return path;
    }

    public static List<String> getDirs(String path) {
        List<String> directories = new ArrayList<>();
        path = getDirPath(path);
        File file = new File(path);
        File[] tempList = file.listFiles();
        if (tempList != null) {
            for (int i = 0; i < tempList.length; i++) {
                if (tempList[i].isDirectory() && tempList[i].getName().charAt(0) != '$') {
                    directories.add(tempList[i].getName());
                }
            }
        }
        return directories;
    }

    public static String getPathforArray(String[] path){
        StringBuilder pathbuilder = new StringBuilder();
        pathbuilder.append(separator);
        for (int i=0;i<path.length;i++){
            pathbuilder.append(path[i]).append(separator);
        }
        return pathbuilder.toString();
    }

}
