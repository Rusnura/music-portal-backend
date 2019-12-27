package server.interfaces.impls;

import org.springframework.web.multipart.MultipartFile;
import server.exceptions.IncorrectAudioException;
import server.interfaces.IFilesStorage;
import server.services.SongService;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DirectoryFilesStorage implements IFilesStorage<File> {
    private static final Logger LOGGER = Logger.getLogger(DirectoryFilesStorage.class.getName());
    private final File audioFilesDirectory;

    public DirectoryFilesStorage(File audioFilesDirectory) {
        this.audioFilesDirectory = audioFilesDirectory;
    }

    @Override
    public File write(MultipartFile uploadedAudioFile, String subDirectoryName) throws IOException {
        File audioFile;
        if (audioFilesDirectory.exists() && audioFilesDirectory.canWrite()) {
            File playlistDirectory = new File(audioFilesDirectory, subDirectoryName);
            if (!playlistDirectory.exists()) {
                playlistDirectory.mkdir();
            }

            if (playlistDirectory.canWrite()) {
                try {
                    byte[] bytes = uploadedAudioFile.getBytes();
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(audioFile = new File(playlistDirectory, uploadedAudioFile.getOriginalFilename())));
                    bos.write(bytes);
                    bos.close();
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Cannot upload file: " + e.getMessage() + "!", e);
                    throw new IncorrectAudioException("Can't upload your mp3 file!");
                }
            } else {
                LOGGER.log(Level.WARNING, "Playlist directory isn't writable! Playlist directory = " + playlistDirectory.getPath() +
                        ", canRead=" + playlistDirectory.canRead() +
                        ", canWrite=" + playlistDirectory.canWrite());
                throw new IOException("Uploading directory isn't writable!");
            }
        } else {
            LOGGER.log(Level.WARNING, "Playlist directory isn't writable! Playlist directory = " + audioFilesDirectory.getPath() +
                    ", exists=" + audioFilesDirectory.exists() +
                    ", canRead=" + audioFilesDirectory.canRead() +
                    ", canWrite=" + audioFilesDirectory.canWrite());
            throw new IncorrectAudioException("User data directory isn't writable");
        }
        return audioFile;
    }

    @Override
    public byte[] load(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.canRead()) {
                return Files.readAllBytes(Paths.get(filePath));
            } else {
                throw new IOException("User file isn't a readable!");
            }
        } else {
            throw new NoSuchFileException("User file doesn't exists!");
        }
    }
}
