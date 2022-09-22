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

    private Client() throws Exception {
        httpClient = HttpClient.newHttpClient();
        // Map Unix socket to tcp address
        String[] cmd = {"/bin/sh", "-c", "socat -v tcp-l:2375,reuseaddr unix:/var/run/docker.sock"};
        new ProcessBuilder()
        //.inheritIO()
        .command(cmd)
        .start();
    } 
 
    public static synchronized Client getClient() throws Exception {
        if (client == null) {
            client = new Client();
        }
        return client;
    }

    public URI uriBuilder(String path, Map<String, String> params) throws Exception{
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(Config.getConfig().dockerURL()).setPath(path);

        for (Map.Entry<String, String> pair : params.entrySet()) {
            builder.setParameter(pair.getKey(), pair.getValue());
        }

        return builder.build();
    }

    public HttpRequest get(URI uri) throws Exception {
        return
        HttpRequest.newBuilder(uri)
            .timeout(Duration.ofSeconds(10))
            .GET()
            .build();
    }

    public HttpRequest post(URI uri, BodyPublisher body, String headers) throws Exception {
        return
        HttpRequest.newBuilder(uri)
            .timeout(Duration.ofSeconds(10))
            .POST(body)
            .headers("Content-Type", headers)
            .build();
    }

    public HttpRequest delete(URI uri) throws Exception {
        return
        HttpRequest.newBuilder(uri)
            .timeout(Duration.ofSeconds(10))
            .DELETE()
            .build();
    }

    public HttpResponse getResource(String path, Map<String, String> params) throws Exception{
        System.out.println(
            uriBuilder(path, params)
        );
        return
        sendRequest(
            get(uriBuilder(path, params)), 
            BodyHandlers.ofString());
    }

    public HttpResponse postResource(String path, Map<String, String> params, BodyPublisher body, String headers) throws Exception {
        return
        sendRequest(
            post(uriBuilder(path, params), body, headers),
            BodyHandlers.ofString());
    }

    public HttpResponse deleteResource(String path, Map<String, String> params) throws Exception {
        return
        sendRequest(delete(uriBuilder(path, params)),
        BodyHandlers.ofString());
    }

    public Map noParameters() {
        return Collections.<String, String>emptyMap();
    }

    public BodyPublisher noBody() {
        return HttpRequest.BodyPublishers.noBody();
    }

    public HttpResponse sendRequest(HttpRequest method, BodyHandler bHandler) throws Exception {
        return
        this.httpClient
        .send(method, bHandler);
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
    