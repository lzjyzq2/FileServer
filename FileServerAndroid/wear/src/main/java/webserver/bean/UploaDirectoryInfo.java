package webserver.bean;

import java.util.List;
import java.util.Map;

public class UploaDirectoryInfo {
    List<String> dirs;
    Map<String, List> allfileinfo;

    public UploaDirectoryInfo(List<String> dirs, Map<String, List> allfileinfo) {
        this.dirs = dirs;
        this.allfileinfo = allfileinfo;
    }

    public List<String> getDirs() {
        return dirs;
    }

    public void setDirs(List<String> dirs) {
        this.dirs = dirs;
    }

    public Map<String, List> getAllfileinfo() {
        return allfileinfo;
    }

    public void setAllfileinfo(Map<String, List> allfileinfo) {
        this.allfileinfo = allfileinfo;
    }

    public UploaDirectoryInfo() {
    }
}
