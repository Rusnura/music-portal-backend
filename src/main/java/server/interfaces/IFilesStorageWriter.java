package server.interfaces;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface IFilesStorageWriter {
    String write(MultipartFile uploadedAudioFile, String subDirectoryName) throws IOException;
}
