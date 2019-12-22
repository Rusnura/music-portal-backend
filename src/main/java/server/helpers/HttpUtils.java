package server.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import sun.net.util.URLUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class HttpUtils {
    @Autowired
    private Environment environment;

    public void getFile(URL url, long downloadMaxFileSize) throws IOException {
        ReadableByteChannel remoteFileChannel = Channels.newChannel(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(File.createTempFile("music_service_", null));
        FileChannel fileChannel = fileOutputStream.getChannel();
        fileChannel.transferFrom(remoteFileChannel, 0, downloadMaxFileSize);
    }

    public static void main(String[] args) {
        HttpUtils utils = new HttpUtils();
        try {
            utils.getFile(new URL("http://alximik77.ru:81/01%20%d0%9f%d0%b5%d1%80%d0%b2%d0%b0%d1%8f%20%d0%b2%d1%81%d1%82%d1%80%d0%b5%d1%87%d0%b0.mp4"), 1024*1024*100L);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
