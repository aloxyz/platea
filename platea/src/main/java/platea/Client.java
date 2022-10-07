package platea;

import org.apache.http.client.utils.URIBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;


@SuppressWarnings("ALL")
public class Client {
    private final HttpClient httpClient;

    private static Client client;

    private Client() {
        httpClient = HttpClient.newHttpClient();

        // Map Unix socket to tcp address
        String[] cmd = {"/bin/sh", "-c", "socat -v tcp-l:2375,reuseaddr unix:/var/run/docker.sock"};
        File log = new File(Config.getConfig().getEnv().get("BASE_PATH") + "/docker.log");

        try {
            log.createNewFile();

            new ProcessBuilder()
                    //.inheritIO()
                    .redirectOutput(log)
                    .redirectError(log)
                    .command(cmd)
                    .start();

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public static synchronized Client getClient() {
        if (client == null) {
            client = new Client();
        }
        return client;
    }

    public URI uriBuilder(String path, Map<String, String> params) {
        URI tmp = null;

        try {
            if (path.isEmpty()) {
                throw new IllegalArgumentException("Empty URI path");
            }

            URIBuilder builder = new URIBuilder();
            builder.setScheme("http").setHost(Config.getConfig().getEnv().get("DOCKER_URL")).setPath(path);

            for (Map.Entry<String, String> pair : params.entrySet()) {
                builder.setParameter(pair.getKey(), pair.getValue());
            }

            tmp = builder.build();

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.exit(1);

        } catch (URISyntaxException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        return tmp;
    }

    public HttpRequest get(URI uri) {
        /* Generic GET method */
        return
                HttpRequest.newBuilder(uri)
                        .timeout(Duration.ofSeconds(10))
                        .GET()
                        .build();
    }

    public HttpRequest post(URI uri, BodyPublisher body, String headers) {
        /* Generic POST method */
        return
                HttpRequest.newBuilder(uri)
                        .timeout(Duration.ofSeconds(10))
                        .POST(body)
                        .headers("Content-Type", headers)
                        .build();
    }

    public HttpRequest delete(URI uri) {
        /* Generic DELETE method */
        return
                HttpRequest.newBuilder(uri)
                        .timeout(Duration.ofSeconds(10))
                        .DELETE()
                        .build();
    }

    public HttpResponse getResource(String path, Map<String, String> params) {
        return
                sendRequest(
                        get(uriBuilder(path, params)),
                        BodyHandlers.ofString());
    }

    public HttpResponse postResource(String path, Map<String, String> params, BodyPublisher body, String headers) {
        return
                sendRequest(
                        post(uriBuilder(path, params), body, headers),
                        BodyHandlers.ofString());
    }

    public HttpResponse deleteResource(String path, Map<String, String> params) {
        return
                sendRequest(delete(uriBuilder(path, params)),
                        BodyHandlers.ofString());
    }

    public HttpResponse sendRequest(HttpRequest method, BodyHandler bHandler) {
        HttpResponse tmp = null;

        try {
            tmp = this.httpClient.send(method, bHandler);

        } catch (InterruptedException | IOException e) {
            System.out.println("Error while sending request to Docker Engine: " + e.getMessage());
            System.exit(1);
        }

        return tmp;
    }

    public static HttpResponse dockerGet(String endpoint, String id, Map<String, String> params) {
        //System.out.println("GET: "+String.format("/%s/%s", endpoint, id));
        if (!id.isEmpty()) {
            return
                    Client.getClient()
                            .getResource(
                                    String.format("/%s/%s/json", endpoint, id),
                                    params);
        } else {
            return
                    Client.getClient()
                            .getResource(
                                    String.format("/%s/json", endpoint),
                                    params);
        }
    }

    public static HttpResponse dockerPost(String endpoint, String id, Map<String, String> parameters, BodyPublisher body, String headers) {
        //System.out.println("POST: "+String.format("/%s/%s", endpoint, id));
        if (!id.isEmpty()) {
            return
                    Client.getClient()
                            .postResource(
                                    String.format("/%s/%s", endpoint, id),
                                    parameters, body, headers);
        } else {
            return
                    Client.getClient()
                            .postResource(
                                    String.format("/%s", endpoint),
                                    parameters, body, headers);
        }
    }

    public static HttpResponse dockerDelete(String endpoint, String id, Map<String, String> parameters) {
        //System.out.println("DELETE: "+String.format("/%s/%s", endpoint, id));
        return
                Client.getClient()
                        .deleteResource(
                                String.format("/%s/%s", endpoint, id),
                                parameters);
    }

    public BodyPublisher noBody() {
        return HttpRequest.BodyPublishers.noBody();
    }

    public Map noParameters() {
        return Collections.<String, String>emptyMap();
    }
}
