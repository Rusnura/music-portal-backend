package server.storage;

import org.springframework.util.StringUtils;
import server.storage.impls.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

public class FileStorageFactory {
  private static final Logger LOGGER = Logger.getLogger(FileStorageFactory.class.getName());

  public static IFilesStorageWriter getFileWriter(String type, String path) {
    LOGGER.info("Try to register " + type + "[" + path + "] storage!");
    if (StringUtils.isEmpty(type))
      return null;

    switch (type.toLowerCase()) {
      case "url":
        try {
          return new RemoteURLFilesStorageWriter(new URL(path));
        } catch (MalformedURLException e) {
          LOGGER.warning("Cannot register URL FileStorageWriter. Error: " + e);
        }
      break;

      case "file-service":
        try {
          return new FileServiceFilesStorageWriter(new URL(path));
        } catch (MalformedURLException e) {
          LOGGER.warning("Cannot register URL FileStorageWriter. Error: " + e);
        }
      break;

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
