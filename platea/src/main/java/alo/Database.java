package alo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Database {
    private static Database database;
    Connection conn;
    String url;
    String user;
    String password;    

    public static synchronized Database getDatabase() {
        if (database == null) {
            database = new Database();
        }
        return database;
    }

    Connection connect() throws Exception {
        return
        conn = 
        DriverManager.getConnection(url, user, password);
    }

    String query(String query) {
        String response = "";
        try {
            Statement statement = this.conn.createStatement();
            ResultSet result = statement.executeQuery(query);

            /* 
            while(result.next()) {
                result.getString(arg0)
            }
            */

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
        
    }

        
    
}
