package platea;

import io.github.cdimascio.dotenv.Dotenv;
import platea.exceptions.*;

import javax.xml.transform.Result;
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

    public ResultSet insertJob(Job job) throws DatabaseInsertException {
        /* Returns the job's ResultSet that was just inserted, else returns null if any exception happens */
        if (job == null) throw new DatabaseInsertException("Job cannot be null");

        String query;
        PreparedStatement p;
        String configName = job.getConfig().getString("name");

        try {
            query = "INSERT INTO jobs (name, config) VALUES (?, ?)";
            p = connection.prepareStatement(query);
            p.setString(1, job.getName());
            p.setString(2, configName);
            p.executeUpdate();

            return getJob(job.getName());

        } catch (DatabaseGetException e) {
            System.out.println(e.getMessage());
            System.exit(1);

        } catch (SQLException e) {
            String state = e.getSQLState();
            if (state.equals("unique_violation"))
                throw new DatabaseInsertException("Job already exists in database");

        }

        return null;
    }

    public ResultSet insertContainer(Container container, String jobName) throws DatabaseInsertException {
        /* Returns the container's ResultSet that was just inserted, else returns null if any exception happens */
        if (container == null) throw new DatabaseInsertException("Container cannot be null");

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

        } catch (DatabaseGetException e) {
            System.out.println(e.getMessage());
            System.exit(1);

        } catch (SQLException e) {
            String state = e.getSQLState();
            if (state.equals("unique_violation"))
                throw new DatabaseInsertException("Job already exists in database");

        }

        return null;
    }
    public ResultSet getJob(String name) throws DatabaseGetException {

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
            if (state.equals("undefined_table")) throw new DatabaseGetException("Table does not exist in database");
        }

        return null;
    }
    public ResultSet getContainer(String id) throws DatabaseGetException {
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
            if (state.equals("undefined_table")) throw new DatabaseGetException("Table does not exist in database");
        }

        return null;
    }

    public void deleteJob(String name) throws DatabaseDeleteException {
        String query;
        PreparedStatement p;

        try {
            query = "DELETE FROM jobs WHERE name = ?";
            p = connection.prepareStatement(query);
            p.setString(1, name);

            p.executeUpdate();

        } catch (SQLException e) {
            String state = e.getSQLState();
            if (state.equals("undefined_table")) throw new DatabaseDeleteException("Table does not exist in database");
        }
    }

    public void deleteContainer(String id) throws DatabaseDeleteException {
        String query;
        PreparedStatement p;

        try {
            query = "DELETE FROM containers WHERE id = ?";
            p = connection.prepareStatement(query);
            p.setString(1, id);

            p.executeUpdate();

        } catch (SQLException e) {
            String state = e.getSQLState();
            if (state.equals("undefined_table")) throw new DatabaseDeleteException("Table does not exist in database");
        }
    }


}
