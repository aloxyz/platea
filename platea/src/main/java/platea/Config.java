package platea;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;

public class Config {
    private static Config config;
    Dotenv env;

    Config() {
        try {
            final String basePath = System.getenv("HOME") + "/.config/platea";
            env = Dotenv.configure()
                    .directory(basePath + "/.env")
                    .load();

        } catch (NullPointerException e) {
            System.out.println("Could read platea config path: " + e.getMessage());
            System.exit(3);

        } catch (DotenvException e) {
            System.out.println("Could not parse dotenv: " + e.getMessage());
            System.exit(3);
        }
    }

    public static synchronized Config getConfig() {
        if (config == null) {
            config = new Config();
        }
        return config;
    }

    public Dotenv getEnv() {
        return env;
    }
}
