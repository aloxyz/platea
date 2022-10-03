package platea;

import java.net.http.HttpResponse;

interface IEntity {
    public HttpResponse inspect() throws Exception;

    public HttpResponse delete(String force) throws Exception;
    
    public String getName();
    
    public Instance getInstance();
}
