package server.storage.impls;

import org.springframework.web.multipart.MultipartFile;
import server.exceptions.IncorrectAudioException;
import server.storage.IFilesStorageWriter;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DirectoryFilesStorageWriter implements IFilesStorageWriter {
  private static final Logger LOGGER = Logger.getLogger(DirectoryFilesStorageWriter.class.getName());
  private final File audioFilesDirectory;

  public DirectoryFilesStorageWriter(File audioFilesDirectory) {
    this.audioFilesDirectory = audioFilesDirectory;
  }

  @Override
  public String write(MultipartFile uploadedAudioFile, String subDirectoryName) throws IOException {
    File audioFile;
    if (audioFilesDirectory.exists() && audioFilesDirectory.canWrite()) {
      File playlistDirectory = new File(audioFilesDirectory, subDirectoryName);
      if (!playlistDirectory.exists()) {
        if (!playlistDirectory.mkdir()) {
          LOGGER.log(Level.WARNING, "Can't create a new playlist directory ! Playlist directory = " + playlistDirectory.getPath() +
            ", exists=" + playlistDirectory.exists() +
            ", canRead=" + playlistDirectory.canRead() +
            ", canWrite=" + playlistDirectory.canWrite());
          throw new IncorrectAudioException("User data directory isn't writable");
        }
      }

      if (playlistDirectory.canWrite()) {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(audioFile = new File(playlistDirectory, Objects.requireNonNull(uploadedAudioFile.getOriginalFilename()))))) {
          byte[] bytes = uploadedAudioFile.getBytes();
          bos.write(bytes);
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
    return audioFile.getAbsolutePath();
  }
}
