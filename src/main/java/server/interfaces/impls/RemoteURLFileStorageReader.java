package server.interfaces.impls;

import org.apache.commons.io.IOUtils;
import server.interfaces.IFileStorageReader;
import java.io.IOException;
import java.net.URL;

public class RemoteURLFileStorageReader implements IFileStorageReader {
	@Override
	public byte[] load(String filePath) throws IOException {
		return IOUtils.toByteArray(new URL(filePath));
	}
}
