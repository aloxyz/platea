package alo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

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

    public static File makeTar(String src, String dest, String name) throws Exception {
        File source = new File(src);
        File destination = new File(dest);

        Archiver archiver = 
            ArchiverFactory.createArchiver(
                ArchiveFormat.TAR, CompressionType.GZIP);
        
        return 
        archiver.create(name, destination, source);
    }

    public static void extractArchive(String archivePath, String destinationPath) throws Exception {
        File archive = new File(archivePath);
        File destination = new File(destinationPath);
    
        Archiver archiver = 
        ArchiverFactory
        .createArchiver(ArchiveFormat.ZIP);

        archiver.extract(archive, destination);
    }


    public static String StreamToString(InputStream stream) throws Exception {
        InputStreamReader isr = new InputStreamReader(stream);
        BufferedReader br = new BufferedReader(isr);
        StringBuffer sb = new StringBuffer();

        String str;
        while((str = br.readLine())!= null){
            sb.append(str);
        }
        return sb.toString();
    }
}
