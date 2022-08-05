package alo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Database {
    String url;
    String user;
    String password;
    public Connection conn;
    
    public Database(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    Connection connect() {
        try {
            this.conn = DriverManager.getConnection(
                this.url, this.user, this.password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.conn;
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
