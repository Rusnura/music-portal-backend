package server.interfaces.impls;

import org.springframework.web.multipart.MultipartFile;
import server.interfaces.IFilesStorage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

public class RemoteURLFilesStorage implements IFilesStorage<File> {
    private static final Logger LOGGER = Logger.getLogger(RemoteURLFilesStorage.class.getName());
    private final URL remoteFileStorageUrl;

    public RemoteURLFilesStorage(URL remoteFileStorageUrl) {
        this.remoteFileStorageUrl = remoteFileStorageUrl;
    }

    @Override
    public File write(MultipartFile uploadedAudioFile, String subDirectoryName) throws IOException {
        return null;
    }

    @Override
    public byte[] load(String filePath) throws IOException {
        return new byte[0];
    }
}
