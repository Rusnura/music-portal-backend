package server.interfaces.impls;

import server.interfaces.IFileStorageReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class DirectoryFilesStorageReader implements IFileStorageReader {
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
