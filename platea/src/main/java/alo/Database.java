package alo;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpEntity;
import org.json.*;

public class Database {
    private static Database database;
    private static CloseableHttpClient client;

    Database() {
        client = HttpClients.createDefault();
    }
    

    public static synchronized Database getDatabase() throws Exception {
        if (database == null) {
            database = new Database();
        }
        return database;
    }

    public  HttpEntity postRequest(String json) throws Exception {
        HttpPost post = new HttpPost(Config.getConfig().databaseURL());

        StringEntity entity = new StringEntity(json);
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");
        post.setEntity(entity);
        
        return
        client
        .execute(post)
        .getEntity();
    }

    public HttpEntity getRequest() throws Exception {
        HttpGet request = new HttpGet(Config.getConfig().databaseURL());
        
        return
        client
        .execute(request)
        .getEntity();
    }
        
    
}
