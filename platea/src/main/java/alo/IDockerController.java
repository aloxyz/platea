package alo;

import java.net.http.HttpResponse;

interface IDockerController {
    public HttpResponse inspect() throws Exception;

    public HttpResponse delete() throws Exception;
    
    public String getId();
    
    public String getName();
    
    public Instance getInstance();
}
