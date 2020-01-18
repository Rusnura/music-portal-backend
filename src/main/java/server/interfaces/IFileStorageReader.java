package server.interfaces;

import java.io.IOException;

public interface IFileStorageReader {
	byte[] load(String filePath) throws IOException;
}
