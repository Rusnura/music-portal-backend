package server.storage;

import org.springframework.util.StringUtils;
import server.storage.impls.*;
import java.io.File;
import java.net.URI;
import java.util.logging.Logger;

public class FileStorageFactory {
  private static final Logger LOGGER = Logger.getLogger(FileStorageFactory.class.getName());

  public static IFilesStorageWriter getFileWriter(String type, String path) {
    LOGGER.info("Try to register " + type + "[" + path + "] storage!");
    if (!StringUtils.hasText(type))
      return null;

    switch (type.toLowerCase()) {
      case "url":
          return new RemoteURLFilesStorageWriter(URI.create(path));

      case "file-service":
          return new FileServiceFilesStorageWriter(URI.create(path));

      case "local":
        try {
          File storageDirectory = new File(path);
          if (!storageDirectory.exists() || !storageDirectory.canWrite())
            throw new IllegalArgumentException("Please, check that directory: " + path + " readable and writable!");
          return new DirectoryFilesStorageWriter(storageDirectory);
        } catch (IllegalArgumentException e) {
          LOGGER.warning("Cannot register local FileStorageWriter. Error: " + e);
        }
      break;

      default:
        LOGGER.warning("Cannot recognize file storage type.");
      break;
    }
    return null;
  }

  public static IFileStorageReader getFileReader(String path) {
    if (path.startsWith("http://") || path.startsWith("https://")) {
      return new RemoteURLFileStorageReader();
    } else {
      return new DirectoryFilesStorageReader();
    }
  }
}
