package platea;

import io.github.cdimascio.dotenv.Dotenv;
import platea.exceptions.*;

import java.sql.*;

public class Database {
    private static Database database;
    private final Connection connection;

    Database() throws DatabaseConnectionException {
        try {
            Dotenv dotenv = Config.getConfig().getEnv();
            String url = dotenv.get("POSTGRES_URL");
            String user = dotenv.get("POSTGRES_USER");
            String password = dotenv.get("POSTGRES_PASSWORD");

            connection = DriverManager.getConnection(url, user, password);

        } catch (SQLException e) {
            throw new DatabaseConnectionException(e.getSQLState());
        }

    }

    public static synchronized Database getDatabase() throws DatabaseConnectionException {
        if (database == null) {
            database = new Database();
        }
        return database;
    }

    public String insertInstance(Instance instance) throws DatabaseInsertInstanceException {
        if (instance == null) throw new DatabaseInsertInstanceException("Instance cannot be null");

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
            System.exit(1);

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
            System.exit(1);

        } catch (SQLException e) {
            String state = e.getSQLState();
            if (state.equals("unique_violation"))
                throw new DatabaseInsertContainerException("Container already exists in database");
        }

        return "";
    }

    public String insertImage(Image image) throws DatabaseInsertImageException {
        if (image == null) throw new DatabaseInsertImageException("Image cannot be null");

        String query;
        PreparedStatement p;

        try {
            query = "INSERT INTO images (name) VALUES (?)";
            p = connection.prepareStatement(query);
            p.setString(1, image.getName());

            p.executeUpdate();

            return get(Image.class, "images", "name");

        } catch (DatabaseGetException e) {
            System.out.println(e.getMessage());
            System.exit(1);

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

    public void delete(String table, String condition) {
        /* Delete from table given a condition
         * example:
         * delete("images", "name = string") */
        String query;
        PreparedStatement p;

        try {
            query = "DELETE FROM ? WHERE ?";
            p = connection.prepareStatement(query);
            p.setString(1, table);
            p.setString(2, condition);

            p.executeUpdate();

        } catch (SQLException e) {
            String state = e.getSQLState();
            if (state.equals("undefined_table")) throw new DatabaseDeleteException("Table does not exist in database");
        }
    }

    public void deleteAllFromInstance(Instance instance) {
        for (String c : instance.getContainerNames()) {
            delete("containers", "id = " + c);
        }

        for (String i : instance.getImageNames()) {
            delete("images", "name = " + i);
        }

        delete(instance.getName(), "instances");
    }

    public <S> ResultSet get(Class<S> clazz, String table) throws DatabaseGetException {
        /*
         * Returns a ResultSet of the record given a class
         */
        if (clazz == null) throw new DatabaseGetException("Object cannot be null");
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

    public String get(Object object, String table, String column) throws DatabaseGetException {
        /*
         * Returns a String value of the specified column label
         */
        if (object == null) throw new DatabaseGetException("Object cannot be null");

        String query;
        PreparedStatement p;
        ResultSet rs;

        try {
            query = "SELECT * FROM ? WHERE name = ?";
            p = connection.prepareStatement(query);
            p.setString(1, table);
            p.setString(2, object.getName());

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

    public String controlledInsert(Object object, String table, String column) {
        /*If image record not in database, insert into database*/
        try {
            String name = get(object, table, column);
            if (name.isEmpty()) {

                switch (object.getClass().getName()) {
                    case "platea.Instance":
                        insertInstance((Instance) object);
                        break;

                    case "platea.Container":
                        insertContainer((Container) object);
                        break;

                    case "platea.Image":
                        insertImage((Image) object);
                        break;
                }
            }
        } catch (DatabaseGetException |
                 DatabaseInsertImageException |
                 DatabaseInsertInstanceException |
                 DatabaseInsertContainerException e) {
            System.out.println(e.getMessage());
        }

        return name;
    }
}
