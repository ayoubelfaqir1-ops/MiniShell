package ma.youcode.Lineperm.Daos;

import java.sql.*;

public abstract class AbstractDao<T> implements Dao<T> {
    private static Connection cn = null;
    private static final String URL = "jdbc:sqlite:Audit.db";

    protected Connection getConnection() {

        try {
            if (cn == null || cn.isClosed()) {
                cn = DriverManager.getConnection(URL);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot open SQLite connection", e);
        }
        return cn;
    }
}
