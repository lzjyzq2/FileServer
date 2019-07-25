package database.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "TempFile")
public class TempFile {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String path;
    @DatabaseField(unique = true)
    private String name;
    @DatabaseField
    private String type;

    public TempFile(int id, String path, String name, String type) {
        this.id = id;
        this.path = path;
        this.name = name;
        this.type = type;
    }

    public TempFile() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
