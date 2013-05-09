package uk.ac.ebi.pride.jmztab.prideq;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * User: qingwei
 * Date: 07/01/13
 */
public class ConnectionFactory {
    private static Logger logger = Logger.getLogger(ConnectionFactory.class);

    private static BasicDataSource dataSource = null;

    public static Connection getPRIDEQConnection() throws Exception {
        String url = "jdbc:mysql://mysql-pride-quality.ebi.ac.uk:4364";

        Properties p = new Properties();
        p.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        p.setProperty("url", url);
        p.setProperty("username", "pride");
        p.setProperty("password", "NinETee9");
        p.setProperty("maxActive", "30");
        p.setProperty("maxIdle", "10");

        if (dataSource == null) {
            dataSource = (BasicDataSource) BasicDataSourceFactory.createDataSource(p);
            logger.info("create connection to " + url);
        }

        return dataSource.getConnection();
    }

    public static Connection getPRIDE2Connection() throws SQLException {
        String url = "jdbc:mysql://mysql-pride-cluster.ebi.ac.uk:4232";
        String user = "pride_ro";
        String password = "one_oh_one";
        String driver = "com.mysql.jdbc.Driver";

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }

        logger.info("create connect to " + url);
        return DriverManager.getConnection(url, user, password);
    }
}
