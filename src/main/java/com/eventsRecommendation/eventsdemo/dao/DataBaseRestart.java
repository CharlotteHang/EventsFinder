//package com.eventsRecommendation.eventsdemo.dao;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.sql.Statement;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.env.Environment;
//
//public class DataBaseRestart {
//    @Autowired
//    private Environment env;
//
//    public DataBaseRestart(Environment env) {
//        this.env = env;
//    }
//
//    public static void main(String[] args) {
//        try {
//            Connection conn = null;
//            try {
//                System.out.println("Connecting to Database");
//
//                /// Ensure the driver is registered
//                ///What he forName() method does, is just return the Class object for the paramater that was loaded
//                // by the class loader. The newInstance() method then returns a new instance of the class.
//                //So then what happens is you call Class.forName(...) it returns com.mysql.jdbc.Driver.class.
//                // You then call newInstance() on that class which returns an instance of the class, whith no paramaters,
//                // so it's basically calling new com.mysql.jdbc.Driver();. or DriverManager.registerDriver()
//
//                //DriverManager.registerDriver(new Driver()); registers a new driver with the manager.  This is normally
//                //called by the driver itself in a static initializer.
//                Class.forName("com.mysql.jdbc.Driver").getConstructor().newInstance();
//                //let the driver which is contained in the DrvierManager to connect to the URL
//                conn = DriverManager.getConnection(MySQLDBUtil.URL);
//                conn.getSchema();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//            if (conn != null) {
//                System.out.println("Import is done successfully.");
//            }
//
//            Statement stmt = conn.createStatement();
//
//            dropAllTables(conn, stmt);
//            System.out.println("Drop previous tables");
//
//            createNewTables(conn, stmt);
//            System.out.println("Create new tables");
//
//            addExampleUser(conn, stmt);
//            System.out.println("Create the example user");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//    }
//
//    private static void dropAllTables(Connection conn, Statement stmt) {
//        try {
//            String sql = "DROP TABLE IF EXISTS categories";
//            stmt.executeUpdate(sql);
//            sql = "DROP TABLE IF EXISTS history";
//            stmt.executeUpdate(sql);
//            sql = "DROP TABLE IF EXISTS items";
//            stmt.executeUpdate(sql);
//            ///
//            sql = "DROP TABLE IF EXISTS sessions";
//            stmt.executeUpdate(sql);
//            sql = "DROP TABLE IF EXISTS users";
//            stmt.executeUpdate(sql);
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void createNewTables(Connection conn, Statement stmt) {
//        try {
//            String sql = "CREATE TABLE items ("
//                    + "itemId VARCHAR(255) NOT NULL,"
//                    + "name VARCHAR(255),"
//                    + "rating FLOAT,"
//                    + "address VARCHAR(255),"
//                    + "imageUrl VARCHAR(255),"
//                    + "url VARCHAR(255),"
//                    + "distance FLOAT,"
//                    + "localDate VARCHAR(255),"
//                    + "time VARCHAR(255),"
//                    + "type VARCHAR(255),"
//                    + "status VARCHAR(255),"
//                    + "PRIMARY KEY (item_id))";
//            stmt.executeUpdate(sql);
//
//            sql = "CREATE TABLE categories ("
//                    + "item_id VARCHAR(255) NOT NULL,"
//                    + "category VARCHAR(255) NOT NULL," + "PRIMARY KEY (item_id, category),"
//                    + "FOREIGN KEY (item_id) REFERENCES items(item_id))";
//            stmt.executeUpdate(sql);
//
//            sql = "CREATE TABLE users ("
//                    + "userId VARCHAR(255) NOT NULL,"
//                    + "password VARCHAR(255) NOT NULL," + "firstName VARCHAR(255),"
//                    + "lastName VARCHAR(255),"
//                    + "PRIMARY KEY (user_id))";
//            stmt.executeUpdate(sql);
//
//            sql = "CREATE TABLE history ("
//                    + "user_id VARCHAR(255) NOT NULL,"
//                    + "item_id VARCHAR(255) NOT NULL,"
//                    + "last_favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
//                    + "PRIMARY KEY (user_id, item_id),"
//                    + "FOREIGN KEY (item_id) REFERENCES items(item_id),"
//                    + "FOREIGN KEY (user_id) REFERENCES users(user_id))";
//            stmt.executeUpdate(sql);
///////
//            sql = "CREATE TABLE sessions ("
//                    + "userId VARCHAR(255) NOT NULL,"
//                    + "token VARCHAR(255) NOT NULL,"
//                    + "dateAuthenticated VARCHAR(255) NOT NULL,"
//                    + "dateOnlineSince VARCHAR(255) NOT NULL,"
//                    + "PRIMARY KEY (token),"
//                    + "FOREIGN KEY (userId) REFERENCES users(user_id))";
//            stmt.executeUpdate(sql);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void addExampleUser(Connection conn, Statement stmt) {
//        try {
//            String sql = "INSERT INTO users VALUES ("
//                    + "'1111', '3229c1097c00d497a0fd282d586be050','Charlotte', 'Hang')";
//            stmt.executeUpdate(sql);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//}
//
