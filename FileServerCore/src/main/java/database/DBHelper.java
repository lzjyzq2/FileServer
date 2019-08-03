package database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;
import database.bean.TempFile;
import database.bean.UserBean;

import java.sql.SQLException;

public class DBHelper {
    private static final String TAG = "DBHelper";
    private static Dao TempFiledao;
    private static Dao UserBeanDao;

    public static Dao getTempFileDao() throws SQLException {
        if (TempFiledao == null) {
            TempFiledao = DaoManager.createDao(getConnectionSource(),
                    TempFile.class);
        }
        return TempFiledao;
    }

    public static Dao getUserBeanDao() throws SQLException {
        if (UserBeanDao == null) {
            UserBeanDao = DaoManager.createDao(getConnectionSource(),
                    UserBean.class);
        }
        return UserBeanDao;
    }

    public static void init() {
        try {
            TableUtils.createTableIfNotExists(getConnectionSource(), TempFile.class);
//            if (TableUtils.createTableIfNotExists(getConnectionSource(), UserBean.class) > 0) {
//                getUserBeanDao().create(new UserBean());
//            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static ConnectionSource getConnectionSource() throws SQLException {
        String connectionString = "jdbc:sqlite:data.db";
        return new JdbcConnectionSource(connectionString);
    }
}
