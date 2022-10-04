package platea;

import io.github.cdimascio.dotenv.Dotenv;
import platea.exceptions.*;

import java.sql.*;

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
        } catch (SQLException e) {
            System.out.println(": Cannot connect to database");
        }

    }

    public static synchronized Database getDatabase() {
        if (database == null) {
            database = new Database();
        }
        return database;
    }

    public String insertInstance(Instance instance) throws DatabaseInsertInstanceException {
        if (container == null) throw new DatabaseInsertInstanceException("Instance cannot be null");

        String query;
        PreparedStatement p;

        try {
            query = "INSERT INTO instances (name) VALUES (?)";
            p = connection.prepareStatement(query);
            p.setString(1, instance.getName());
            p.executeUpdate();

            return get(Instance.class, "instances", "id");

        } catch (DatabaseGetException e) {
            System.out.println(e.getMessage());
            System.exit(0);

        } catch (SQLException e) {
            String state = e.getSQLState();
            if (state.equals("unique_violation"))
                throw new DatabaseInsertInstanceException("Instance already exists in database");

        }

        return "";
    }

    public String insertContainer(Container container) throws DatabaseInsertContainerException {
        if (container == null) throw new DatabaseInsertContainerException("Container cannot be null");

        String query;
        PreparedStatement p;

        try {
            query = "INSERT INTO containers (id, name, instance) VALUES (?, ?, ?)";
            p = connection.prepareStatement(query);
            p.setString(1, container.getId());
            p.setString(2, container.getName());
            p.setString(3, container.getInstance().getName());
            p.executeUpdate();

            return get(Container.class, "containers", "id");

        } catch (DatabaseGetException e) {
            System.out.println(e.getMessage());
            System.exit(0);

        } catch (SQLException e) {
            String state = e.getSQLState();
            if (state.equals("unique_violation"))
                throw new DatabaseInsertContainerException("Container already exists in database");
        }

        return "";
    }

    public String insertImage(Image image) throws DatabaseInsertImageException {
        if (container == null) throw new DatabaseInsertImageException("Image cannot be null");

        String query;
        PreparedStatement p;

        try {
            query = "INSERT INTO images (name) VALUES (?)";
            p = connection.prepareStatement(query);
            p = connection.prepareStatement(query);
            p.setString(1, image.getName());

            p.executeUpdate();

            return get(Image.class, "images", "name");

        } catch (DatabaseGetException e) {
            System.out.println(e.getMessage());
            System.exit(0);

        } catch (SQLException e) {
            String state = e.getSQLState();
            if (state.equals("unique_violation"))
                throw new DatabaseInsertImageException("Image already exists in database");
        }

        return "";
    }

    public <S> void delete(Class<S> clazz, String table) throws DatabaseDeleteException {
        String query;
        PreparedStatement p;

        try {
            query = "DELETE FROM ? WHERE name = ?";
            p = connection.prepareStatement(query);
            p.setString(1, table);
            p.setString(2, clazz.getName());

            p.executeUpdate();

        } catch (SQLException e) {
            String state = e.getSQLState();
            if (state.equals("undefined_table")) throw new DatabaseDeleteException("Table does not exist in database");
        }

    }

    public <S> ResultSet get(Class<S> clazz, String table) throws DatabaseGetException {
        String query;
        PreparedStatement p;
        ResultSet rs;

        try {
            query = "SELECT * FROM ? WHERE name = ?";
            p = connection.prepareStatement(query);
            p.setString(1, table);
            p.setString(2, clazz.getName());

            rs = p.executeQuery();

            if (rs.next()) {
                return rs;
            }

        } catch (SQLException e) {
            String state = e.getSQLState();
            if (state.equals("undefined_table")) throw new DatabaseGetException("Table does not exist in database");
        }

        return null;
    }

    public <S> String get(Class<S> clazz, String table, String column) throws DatabaseGetException {
        /*
         * Returns a String value of the specified column label
         */

        String query;
        PreparedStatement p;
        ResultSet rs;

        try {
            query = "SELECT * FROM ? WHERE name = ?";
            p = connection.prepareStatement(query);
            p.setString(1, table);
            p.setString(2, clazz.getName());

            rs = p.executeQuery();

            if (rs.next()) {
                return rs.getString(column);
            }

        } catch (SQLException e) {
            String state = e.getSQLState();
            if (state.equals("undefined_table")) throw new DatabaseGetException("Table does not exist in database");

        }

        return "";
    }
}
