package database.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "UserBean")
public class UserBean {
    @DatabaseField(unique = true)
    private String uid;
    @DatabaseField
    private String token;
    @DatabaseField
    private long lasttime;

    public UserBean() {
    }

    public UserBean(String uid, String token, long lasttime) {
        this.uid = uid;
        this.token = token;
        this.lasttime = lasttime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getLasttime() {
        return lasttime;
    }

    public void setLasttime(long lasttime) {
        this.lasttime = lasttime;
    }
}
