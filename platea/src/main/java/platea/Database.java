package platea;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class Database {
    private static Database database;
    private Connection connection;
    private String url;
    private String user;
    private String password;    

    Database() {
        try {
            Dotenv dotenv = Config.getConfig().getEnv();
            url = dotenv.get("POSTGRES_URL");
            user = dotenv.get("POSTGRES_USER");
            password = dotenv.get("POSTGRES_PASSWORD");
    
            connection = DriverManager.getConnection(url, user, password);
        } 
        
        catch (SQLException e) {
            System.out.println(": Cannot connect to database");
        } 

    }
    
    public static synchronized Database getDatabase() {
        if (database == null) {
            database = new Database();
        }
        return database;
    }

    public boolean getInstance(String instanceName) throws Exception {
        /*
         * Returns true if exists in database, else return false
         */
        String query;
        PreparedStatement p;
        ResultSet rs;

        query = "SELECT * FROM instances WHERE name = ?";
        p = connection.prepareStatement(query);
        p.setString(1, instanceName);
        rs = p.executeQuery();
        return rs.next();

    }

    public void instanceHandler(Instance instance) {
        /*
         * Returns true if exists in database, else return false
         */
        String query;
        PreparedStatement p;
        ResultSet rs;

        try {
            query = "SELECT 1 FROM instances WHERE name = ?";

            p = connection.prepareStatement(query);
            
            p.setString(1, instance.getName());
    
            rs = p.executeQuery();

            if (!rs.next()) {
                query = "INSERT INTO instances (name) VALUES (?)";
                p = connection.prepareStatement(query);
                p.setString(1, instance.getName());
    
                p.executeUpdate();
            }
        }
        catch (SQLException e) {
            System.out.println("Database error");
        }
    }

    public String getContainer(Container container) throws Exception {
        String query;
        PreparedStatement p;
        ResultSet rs;

        query = "SELECT * FROM containers WHERE name = ?";
        p = connection.prepareStatement(query);
        p.setString(1, container.getName());

        rs = p.executeQuery();

        if (rs.next() == true) {
            return rs.getString("id");
        }

        return "";
    }

    public String getImage(Image image) throws Exception {
        String query;
        PreparedStatement p;
        ResultSet rs;

        query = "SELECT * FROM images WHERE name = ?";
        p = connection.prepareStatement(query);
        p.setString(1, image.getName());

        rs = p.executeQuery();

        if (rs.next() == true) {
            return rs.getString("name");
        }

        return "";
    }

    public String insertContainer(Container container) throws DatabaseException {
        if (container == null) throw new DatabaseException("Container cannot be null");

        String query;
        PreparedStatement p;
        ResultSet rs;

        try {
            query = "INSERT INTO containers (id, name, instance) VALUES (?, ?, ?)";
            p = connection.prepareStatement(query);
            p.setString(1, container.getId());
            p.setString(2, container.getName());
            p.setString(3, container.getInstance().getName());

            p.executeUpdate();
            query = "SELECT id FROM containers WHERE name = ?";
            p = connection.prepareStatement(query);
            p.setString(1, container.getName());

            rs = p.executeQuery();
            rs.next();

            return rs.getString("id");

        } catch (SQLException e) {
            String state = e.getSQLState();
            if (state.equals("unique_violation")) throw new DatabaseException("Container already exists in database");
        }

        return "";
    }

    public String insertImage(Image image) throws Exception {
        String query;
        PreparedStatement p;
        ResultSet rs;

        query = "INSERT INTO images (name) VALUES (?)";
        p = connection.prepareStatement(query);
        p.setString(1, image.getName());

        p.executeUpdate();

        query = "SELECT name FROM images WHERE name = ?";
        p = connection.prepareStatement(query);
        p.setString(1, image.getName());

        rs = p.executeQuery();    
        rs.next();            
        return rs.getString("name");
    }
    
    public <S> void delete(Class<S> clazz, String table) {
        String query;
        PreparedStatement p;

        try {
            if (!table.equals("instances") || !table.equals("containers") || !table.equals("images")) {
                throw new DatabaseException("Table " + table + " does not exist in database");
            }

            query = "DELETE FROM ? WHERE name = ?";
            p = connection.prepareStatement(query);
            p.setString(1, table);
            p.setString(2, clazz.getName());

            p.executeUpdate();  

        }

        catch (DatabaseException e) {
            System.out.println(e.getMessage());
        }
        catch (SQLException e) {
            System.out.println("Database error");
        }
    }

    @Deprecated
    public void deleteContainer(Container container) throws Exception {
        String query;
        PreparedStatement p;

        query = "DELETE FROM containers WHERE name = ?";
        p = connection.prepareStatement(query);
        p.setString(1, container.getName());

        p.executeUpdate();
    }

    @Deprecated
    public void deleteInstance(Instance instance) {
        String query;
        PreparedStatement p;

        try {
            query = "DELETE FROM instances WHERE name = ?";
            p = connection.prepareStatement(query);
            p.setString(1, instance.getName());
    
            p.executeUpdate();    
        }

        catch (SQLException e) {
            System.out.println("Database error");
            e.printStackTrace();
        }
    }

    @Deprecated
    public void deleteImage(Image image) throws Exception {
        String query;
        PreparedStatement p;


        query = "DELETE FROM images WHERE name = ?";
        p = connection.prepareStatement(query);
        p.setString(1, image.getName());
        
        p.executeUpdate();
    }
}
