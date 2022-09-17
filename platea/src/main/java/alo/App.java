package alo;

import java.util.Collections;

public class App

{
    public static void main( String[] args) throws Exception
    {
        Client client = Client.getClient();

        client.getResource("containers/json", Collections.<String, String>emptyMap());
    }
}
