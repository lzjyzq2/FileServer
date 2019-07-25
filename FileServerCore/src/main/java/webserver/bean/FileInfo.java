package webserver.bean;

public class FileInfo {
    String name;
    long size;
    long time;

    public FileInfo(String name, long size, long time) {
        this.name = name;
        this.size = size;
        this.time = time;
    }

    public FileInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
