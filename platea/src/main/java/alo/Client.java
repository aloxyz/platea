package alo;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient.Request;
import com.github.dockerjava.transport.DockerHttpClient.Response;
import java.time.Duration;


public class Client {
    private static Client client;
    private DockerClient dockerClient;

    private Client() throws Exception {
        connect();
    } 
 
    public static synchronized Client getClient() throws Exception {
        if (client == null) {
            client = new Client();
        }
        return client;
    }

    public DockerClient getDockerClient() {
        return dockerClient;
    }


    public int connect() throws Exception {
        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
            // Docker client configuration
            DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost("unix:///var/run/docker.sock").build();
            
            // Establishing a connection with the Docker socket
            DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .sslConfig(config.getSSLConfig())
            .maxConnections(100)
            .connectionTimeout(Duration.ofSeconds(30))
            .responseTimeout(Duration.ofSeconds(45))
            .build();
    
            Request request = Request.builder()
            .method(Request.Method.GET)
            .path("/_ping")
            .build();
    
            // Check response and do the rest
            Response response = httpClient.execute(request);
            DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);
            if (response.getStatusCode() == 200) {
                this.dockerClient = dockerClient;
            }
            return response.getStatusCode();
        }
}
