package database;

import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import cn.settile.lzjyzq2.fileserver.application.myapplication;
import database.bean.TempFile;
import database.bean.UserBean;

public class DBHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = "DBHelper";
    private static DBHelper instance = new DBHelper();
    private static Dao TempFiledao;
    private static Dao UserBeanDao;

    public static DBHelper getInstance() {
        return instance;
    }

    private DBHelper(){
        super(myapplication.getContext(),"data.db",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, TempFile.class);
            TableUtils.createTable(connectionSource, UserBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }

    public Dao getTempFiledao() throws SQLException {
        if(TempFiledao == null)
            TempFiledao = DaoManager.createDao(getConnectionSource(),
                    TempFile.class);
        return TempFiledao;
    }
    public Dao getUserBeanDao() throws SQLException {
        if(UserBeanDao == null)
            UserBeanDao = DaoManager.createDao(getConnectionSource(),
                    UserBean.class);
        return UserBeanDao;
    }
}
