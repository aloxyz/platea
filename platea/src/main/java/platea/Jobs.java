package platea;

import org.json.JSONObject;
import platea.exceptions.CreateJobException;
import platea.exceptions.database.GetException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static platea.Database.getDatabase;

public class Jobs {

    public static void list() throws RuntimeException {
        try {
            ResultSet rs = getDatabase().getJobs();
            ArrayList<Job> jobs = new ArrayList<>();

            // Set up jobs ArrayList from database records
            while (rs.next()) {
                String jobName = rs.getString("name");
                jobs.add(new Job(jobName));
            }

            for (Job job : jobs) {
                System.out.println(ConsoleColors.BLUE_BRIGHT + job.getName() + ConsoleColors.RESET);
                System.out.format("%-36s %-8s\n", "Name", "Status");

                for (String id : job.getContainers()) {
                    Container container = new Container(id);

                    JSONObject inspectContainerJsonObject =
                            new JSONObject((container.inspect().body().toString()));

                    String status =
                            ((JSONObject) inspectContainerJsonObject.get("State"))
                                    .getString("Status");

                    System.out.format("%-36s", container.getName());


                    if (status.equals("running")) System.out.print(ConsoleColors.GREEN);
                    else if (status.equals("created")) System.out.print(ConsoleColors.WHITE);
                    else System.out.print(ConsoleColors.RED);

                    System.out.println(status + ConsoleColors.RESET);
                }
            }
        } catch (CreateJobException | GetException | SQLException e) {
            throw new RuntimeException("Something went wrong: " + e.getMessage());
        }
    }
}
