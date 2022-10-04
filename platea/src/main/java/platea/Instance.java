package platea;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import platea.exceptions.DatabaseGetException;
import platea.exceptions.DatabaseInsertInstanceException;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Instance {
    private String name;
    private ArrayList<Container> containers;
    private ArrayList<Image> images;
    private JSONObject config;

    @SuppressWarnings("unchecked")
    Instance(String configPath) {
        try {
            this.config = JSONController.fileToJsonObject(Paths.get(configPath).toString());
            this.name = config.get("instanceName").toString();
            this.containers = new ArrayList<>();
            this.images = new ArrayList<>();

            JSONObject containers = (JSONObject) config.get("containers");

            containers.keySet().forEach(key -> {
                Object value = containers.get(key);
                JSONObject container = (JSONObject) value;
                JSONObject buildConfig = (JSONObject) container.get("config");

                String tmp = buildConfig.get("Image").toString();
                String name = tmp.substring(0, tmp.lastIndexOf(":"));

                String uri = container.get("endpoint").toString();

                this.images.add(new Image(name, this, uri));
                this.containers.add(new Container(name, this, buildConfig));

            });
        } catch (IOException | ParseException e) {
            System.out.println("Could not parse config file: either wrong instance name or wrong syntax");
        }
    }


    public void buildInstance() {
        try {
            if (Database.getDatabase().get(Instance.class, "instances", "name").isEmpty()) {
                Database.getDatabase().insertInstance(this);
            }

            for (Image i : images) {
                System.out.printf("Building image %s%s%s...%n", ConsoleColors.YELLOW_BOLD_BRIGHT, i.getName(), ConsoleColors.RESET);
                i.create();
            }

            for (Container c : containers) {
                System.out.printf("Creating container %s%s%s...%n", ConsoleColors.YELLOW_BOLD_BRIGHT, c.getName(), ConsoleColors.RESET);
                c.create();
            }
        } catch (DatabaseGetException e) {
            System.out.printf("Could not find instance %s: %s%n", this.name, e.getMessage());

        } catch (DatabaseInsertInstanceException e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<Container> getContainers() {
        return containers;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Map> delete() throws Exception {
        ArrayList<Map> responses = new ArrayList<>();

        if (Database.getDatabase().get(Instance.class, "instances", "name").isEmpty()) {
            try {
                responses.add(deleteContainers());
                responses.add(deleteImages());
                Database.getDatabase().deleteInstance(this);
                System.out.println("Removed instance " + ConsoleColors.RED_BOLD_BRIGHT + this.name + ConsoleColors.RESET);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Instance does not exist or has not been yet created");
        }

        return responses;
    }

    public Map<String, HttpResponse> deleteContainers() throws Exception {
        HashMap<String, HttpResponse> responses = new HashMap<>();

        for (Container c : containers) {
            responses.put(c.getId(), c.delete("true"));
            System.out.println("Deleted container " + ConsoleColors.RED_BOLD_BRIGHT + c.getName() + ConsoleColors.RESET);
        }

        return responses;
    }


    public Map<String, HttpResponse> createImages() throws Exception {
        HashMap<String, HttpResponse> responses = new HashMap<>();

        for (Image i : images) {
            System.out.println("Building image " + ConsoleColors.YELLOW_BOLD_BRIGHT + i.getName() + ConsoleColors.RESET + "...");
            responses.put(i.getName(), i.create());
        }

        return responses;
    }

    public Map<String, HttpResponse> createContainers() throws Exception {
        HashMap<String, HttpResponse> responses = new HashMap<>();

        for (Container c : containers) {
            responses.put(c.getName(), c.create());
        }

        return responses;
    }

    public Map<String, HttpResponse> deleteImages() throws Exception {
        HashMap<String, HttpResponse> responses = new HashMap<>();

        for (Image i : images) {
            responses.put(i.getName(), i.delete("true"));
            System.out.println("Deleted image " + ConsoleColors.RED_BOLD_BRIGHT + i.getName() + ConsoleColors.RESET);
        }

        return responses;
    }

    public Map<String, HttpResponse> startContainers() {
        HashMap<String, HttpResponse> responses = new HashMap<>();

        for (Container c : containers) {
            responses.put(c.getId(), c.start());
            System.out.println("Started container " + ConsoleColors.YELLOW_BRIGHT + c.getName() + ConsoleColors.RESET);
        }

        return responses;
    }

    public Map<String, HttpResponse> stopContainers() {
        HashMap<String, HttpResponse> responses = new HashMap<>();

        for (Container c : containers) {
            responses.put(c.getId(), c.stop());
            System.out.println("Stopped container " + ConsoleColors.RED_BRIGHT + c.getName() + ConsoleColors.RESET);

        }

        return responses;
    }

    public String getName() {
        return name;
    }

    public JSONObject getConfig() {
        return config;
    }

}
