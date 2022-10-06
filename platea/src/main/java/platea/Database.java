package platea;

import io.github.cdimascio.dotenv.Dotenv;
import platea.exceptions.database.ConnectionException;
import platea.exceptions.database.DeleteException;
import platea.exceptions.database.GetException;
import platea.exceptions.database.InsertException;

import java.sql.*;

public class Database {
    private static Database database;
    private final Connection connection;

    Database() throws ConnectionException {
        try {
            Dotenv dotenv = Config.getConfig().getEnv();
            String url = dotenv.get("POSTGRES_URL");
            String user = dotenv.get("POSTGRES_USER");
            String password = dotenv.get("POSTGRES_PASSWORD");

            connection = DriverManager.getConnection(url, user, password);

        } catch (SQLException e) {
            throw new ConnectionException(e.getSQLState());
        }

    }

    public static synchronized Database getDatabase() {
        try {
            if (database == null) {
                database = new Database();
            }
            return database;

        } catch (ConnectionException e) {
            System.out.println("Could not connect to database: " + e.getMessage());
        }

        return null;
    }

    public ResultSet insertJob(Job job) throws InsertException {
        /* Returns the job's ResultSet that was just inserted, else returns null if any exception happens */
        if (job == null) throw new InsertException("Job cannot be null");

        String query;
        PreparedStatement p;
        String configName = job.getConfig().getString("name");

        try {
            Array containers = connection.createArrayOf("VARCHAR", job.getContainers().toArray());

            query = "INSERT INTO jobs (name, config, containers) VALUES (?, ?, ?)";
            p = connection.prepareStatement(query);
            p.setString(1, job.getName());
            p.setString(2, configName);
            p.setArray(3, containers);

            p.executeUpdate();

            return getJob(job.getName());

        } catch (GetException e) {
            System.out.println(e.getMessage());
            System.exit(1);

        } catch (SQLException e) {
            String state = e.getSQLState();
            if (state.equals("unique_violation"))
                throw new InsertException("Job already exists in database");

        }

        return null;
    }

    public ResultSet insertContainer(Container container, String jobName) throws InsertException {
        /* Returns the container's ResultSet that was just inserted, else returns null if any exception happens */
        if (container == null) throw new InsertException("Container cannot be null");

        String query;
        PreparedStatement p;

        try {
            query = "INSERT INTO containers (id, name, job) VALUES (?, ?, ?)";
            p = connection.prepareStatement(query);
            p.setString(1, container.getId());
            p.setString(2, container.getName());
            p.setString(3, jobName);
            p.executeUpdate();

            return getContainer(container.getId());

        } catch (GetException e) {
            System.out.println(e.getMessage());
            System.exit(1);

        } catch (SQLException e) {
            String state = e.getSQLState();
            if (state.equals("unique_violation"))
                throw new InsertException("Job already exists in database");

        }

        return null;
    }
    public ResultSet getJob(String name) throws GetException {

        String query;
        PreparedStatement p;
        ResultSet rs;

        try {
            query = "SELECT * FROM jobs WHERE name = ?";
            p = connection.prepareStatement(query);
            p.setString(1, name);

            rs = p.executeQuery();

            if (rs.next()) return rs;

        } catch (SQLException e) {
            String state = e.getSQLState();
            if (state.equals("undefined_table")) throw new GetException("Table does not exist in database");
        }

        return null;
    }
    public ResultSet getContainer(String id) throws GetException {
        String query;
        PreparedStatement p;
        ResultSet rs;

        try {
            query = "SELECT * FROM containers WHERE id = ?";
            p = connection.prepareStatement(query);
            p.setString(1, id);

            rs = p.executeQuery();

            if (rs.next()) return rs;

        } catch (SQLException e) {
            String state = e.getSQLState();
            if (state.equals("undefined_table")) throw new GetException("Table does not exist in database");
        }

        return null;
    }

    public void deleteJob(String name) throws DeleteException {
        String query;
        PreparedStatement p;

        try {
            query = "DELETE FROM jobs WHERE name = ?";
            p = connection.prepareStatement(query);
            p.setString(1, name);

            p.executeUpdate();

        } catch (SQLException e) {
            String state = e.getSQLState();
            if (state.equals("undefined_table")) throw new DeleteException("Table does not exist in database");
        }
    }

    public void deleteContainer(String id) throws DeleteException {
        String query;
        PreparedStatement p;

        try {
            query = "DELETE FROM containers WHERE id = ?";
            p = connection.prepareStatement(query);
            p.setString(1, id);

            p.executeUpdate();

        } catch (SQLException e) {
            String state = e.getSQLState();
            if (state.equals("undefined_table")) throw new DeleteException("Table does not exist in database");
        }
    }


}
