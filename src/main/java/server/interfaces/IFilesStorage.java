package server.interfaces;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface IFilesStorage {
    String write(MultipartFile uploadedAudioFile, String subDirectoryName) throws IOException;
    byte[] load(String filePath) throws IOException;
}
