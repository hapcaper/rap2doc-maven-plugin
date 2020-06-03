package io.github.hapcaper.util;



import io.github.hapcaper.conf.DataBaseConf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Description:
 *
 * @author 李自豪（zihao.li01@ucarinc.com）
 * @since 2020/4/22
 */
public class MySqlUtil {

    private static DataBaseConf dataBaseConf;

    private static Connection connection;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static Connection getConnection() throws SQLException {
        if (connection != null && !connection.isClosed() && connection.isValid(5)) {
            return connection;
        } else {
            connection = DriverManager.getConnection(dataBaseConf.getUrl(), dataBaseConf.getUser(), dataBaseConf.getPassWord());
            //默认不自动提交 在执行完所有数据库操作后手动提交
            connection.setAutoCommit(false);
        }
        return connection;
    }

    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            connection = null;
        }
    }

    public static void setDataBaseConf(DataBaseConf dataBaseConf) {
        MySqlUtil.dataBaseConf = dataBaseConf;
    }
}
