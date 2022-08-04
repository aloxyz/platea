package alo;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class FileIO {
    public String readFile(String filePath) {
        // read file and return a String
        String data = "";
        try {        
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            StringBuilder sb = new StringBuilder();
            
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            
            data = sb.toString();
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public void wget(URL url, String path) {
        // Download a file from given URL to specified path
        try {
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(path);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();    
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
