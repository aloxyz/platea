package alo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

public class FileIO {
    public static String readFile(String filePath) {
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

    public static void wget(String url, String path) {
        // Download a file from given URL to specified path
        try {
            URL endpoint = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(endpoint.openStream());
            FileOutputStream fos = new FileOutputStream(path);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();    
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void extractTarball(String tarballPath, String destinationPath) throws Exception {
        File tarball = new File(tarballPath);
        File destination = new File(destinationPath);
    
        Archiver archiver = 
        ArchiverFactory
        .createArchiver("tar", "gz");

        archiver.extract(tarball, destination);
    }
}
