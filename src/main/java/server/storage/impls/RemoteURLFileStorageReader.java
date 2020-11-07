package server.storage.impls;

import org.apache.commons.io.IOUtils;
import server.storage.IFileStorageReader;

import java.io.IOException;
import java.net.URL;

public class RemoteURLFileStorageReader implements IFileStorageReader {
  @Override
  public byte[] load(String filePath) throws IOException {
    return IOUtils.toByteArray(new URL(filePath));
  }
}
