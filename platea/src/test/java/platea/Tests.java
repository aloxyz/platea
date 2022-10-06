package platea;


import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.nio.file.Files;
import java.nio.file.Paths;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Tests {

    @Test
    public void job() throws Exception {
        JSONObject config = new JSONObject(Files.readString(Paths.get("/home/alo/Documenti/platea/sample.json")));
        Job j = new Job("test_job", config);
        j.build();
    }

}

