package webserver.bean;

public class DiskInfo {
    public long freespace;
    public long usedspace;
    public long totalspace;

    public DiskInfo() {
    }

    public DiskInfo(long freespace, long usedspace, long totalspace) {
        this.freespace = freespace;
        this.usedspace = usedspace;
        this.totalspace = totalspace;
    }

    public long getFreespace() {
        return freespace;
    }

    public void setFreespace(long freespace) {
        this.freespace = freespace;
    }

    public long getUsedspace() {
        return usedspace;
    }

    public void setUsedspace(long usedspace) {
        this.usedspace = usedspace;
    }

    public long getTotalspace() {
        return totalspace;
    }

    public void setTotalspace(long totalspace) {
        this.totalspace = totalspace;
    }
}
