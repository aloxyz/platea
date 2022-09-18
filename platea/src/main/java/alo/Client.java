package alo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;


public class Client {
    private HttpClient httpClient;
    
    private static Client client;
    
    //private DockerClient dockerClient;

    private Client() throws Exception {
        httpClient = HttpClient.newHttpClient();
    } 
 
    public static synchronized Client getClient() throws Exception {
        if (client == null) {
            client = new Client();
        }
        return client;
    }

    public URI uriBuilder(String path, Map<String, String> parameters) throws Exception{
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(Config.getConfig().dockerURL()).setPath(path);

        

        for (Map.Entry<String, String> pair : parameters.entrySet()) {
            builder.setParameter(pair.getKey(), pair.getValue());
        }

        return builder.build();
    }

    public HttpRequest get(String path) throws Exception {
        URI uri = uriBuilder(path, Collections.<String, String>emptyMap());

        return
        HttpRequest.newBuilder(uri)
            .timeout(Duration.ofSeconds(10))
            .GET()
            .build();
    }

    public HttpRequest post(String path, BodyPublisher body) throws Exception {
        URI uri = uriBuilder(path, Collections.<String, String>emptyMap());

        return
        HttpRequest.newBuilder(uri)
            .timeout(Duration.ofSeconds(10))
            .POST(body)
            .build();
    }

    public HttpResponse sendRequest(HttpRequest method, BodyHandler bHandler) throws Exception {
        return
        this.httpClient
        .send(method, bHandler);
    }

    public HttpResponse getResource(String path, Map<String, String> parameters) throws Exception{
        return
        sendRequest(
            get(uriBuilder(path, parameters).toString()), 
            BodyHandlers.ofString());
    }

    public Map noParameters() {
        return Collections.<String, String>emptyMap();
    }

    public BodyPublisher noBody() {
        return HttpRequest.BodyPublishers.noBody();
    }
}
    




/*
 * HttpRequest.BodyPublishers.ofString("Sample request body")
 * HttpRequest.BodyPublishers.ofByteArray(sampleData)
 * HttpRequest.BodyPublishers.fromFile(Paths.get("src/test/resources/sample.txt"))
 * 
 * BodyHandlers.ofByteArray
 * BodyHandlers.ofString
 * BodyHandlers.ofFile
 * BodyHandlers.discarding
 * BodyHandlers.replacing
 * BodyHandlers.ofLines
 * BodyHandlers.fromLineSubscriber
 * 
 * HttpResponse.statusCode(), HttpURLConnection 
 * HttpResponse.body(), return type depends on the response BodyHandler parameter passed to the send() method
 * HttpResponse.headers()
 */
    