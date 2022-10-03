package alo;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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


@SuppressWarnings("ALL")
public class Client {
    private final HttpClient httpClient;
    
    private static Client client;

    private Client() {
        httpClient = HttpClient.newHttpClient();
        // Map Unix socket to tcp address
        String[] cmd = {"/bin/sh", "-c", "socat -v tcp-l:2375,reuseaddr unix:/var/run/docker.sock"};
        try {
            new ProcessBuilder()
            //.inheritIO()
            .command(cmd)
            .start();
        } 
        
        catch (SecurityException e) {
            System.out.println("Could not create subprocess");
        }

        catch (IOException e) {
            System.out.println("I/O error");
        }

        catch (Exception e) {
            System.out.println("A shell error occurred");
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

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(Config.getConfig().dockerURL()).setPath(path);

        for (Map.Entry<String, String> pair : params.entrySet()) {
            builder.setParameter(pair.getKey(), pair.getValue());
        }

        try {
            tmp = builder.build();
        }

        catch (URISyntaxException e) {
            System.out.println("Could not build URI");
        }

        return tmp;
    }

    public HttpRequest get(URI uri) {
        return
        HttpRequest.newBuilder(uri)
            .timeout(Duration.ofSeconds(10))
            .GET()
            .build();
    }

    public HttpRequest post(URI uri, BodyPublisher body, String headers) {
        return
        HttpRequest.newBuilder(uri)
            .timeout(Duration.ofSeconds(10))
            .POST(body)
            .headers("Content-Type", headers)
            .build();
    }

    public HttpRequest delete(URI uri) {
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

    public Map noParameters() {
        return Collections.<String, String>emptyMap();
    }

    public BodyPublisher noBody() {
        return HttpRequest.BodyPublishers.noBody();
    }

    public HttpResponse sendRequest(HttpRequest method, BodyHandler bHandler) {
        HttpResponse tmp = null;
        try {
            tmp =
                this.httpClient
                .send(method, bHandler);
        }

        catch (InterruptedException e) {
            System.out.println("Request process was interrupted");
        }

        catch (IOException e) {
            System.out.println("I/O error");
        }
        return tmp;
    }
}
