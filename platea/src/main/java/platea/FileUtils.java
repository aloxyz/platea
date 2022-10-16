package platea;

import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.TimeUnit;

public class FileUtils {
    public static File wget(String url, String path) {
        // Download a file from given URL to specified path
        try {
            URL endpoint = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(endpoint.openStream());
            FileOutputStream fos = new FileOutputStream(path);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new File(path);
    }

    public static String get(String url) {
        try {
            BufferedInputStream stream = new BufferedInputStream(new URL(url).openStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();

            // Build string from InputStream with StringBuilder
            String line;
            while((line =  reader.readLine()) != null) builder.append(line);
            stream.close();

            return builder.toString();

        } catch (Exception e) {
            System.out.println("Could not elaborate url object " + url);
        }

        return null;
    }

    public static File tar(String src, String dest, String name) {
        File tmp = null;

        try {
            File source = new File(src);
            File destination = new File(dest);

            Archiver archiver =
                    ArchiverFactory.createArchiver(
                            ArchiveFormat.TAR, CompressionType.GZIP);

            tmp = archiver.create(name, destination, source);

        } catch (IOException e) {
            System.out.printf("Could not create tarball: %s%n", e.getMessage());
        }

        return tmp;
    }

    public static File unzip(String archive, String dest) {
        Archiver archiver =
                ArchiverFactory
                        .createArchiver(ArchiveFormat.ZIP);

        try {
            archiver.extract(new File(archive), new File(dest));
        } catch (IOException e) {
            System.out.println("Could not extract the zip archive");
        }

        return new File(dest);
    }

    public static File zipToTar(String archive, String dest, String name) {
        return
                tar(
                        unzip(archive, dest).getAbsolutePath(),
                        dest,
                        name
                );

    }

    public static void bash(String cmd) {
        try {
            ProcessBuilder builder = new ProcessBuilder();
            String[] command = {"/bin/sh", "-c", cmd};
            builder.command(command);
            //builder.inheritIO();

            Process p;
            p = builder.start();
            p.waitFor(20, TimeUnit.SECONDS);

        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
