package server.interfaces.impls;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import server.interfaces.IFilesStorage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

public class RemoteURLFilesStorage implements IFilesStorage {
    private static final Logger LOGGER = Logger.getLogger(RemoteURLFilesStorage.class.getName());
    private final URL remoteFileStorageUrl;

    public RemoteURLFilesStorage(URL remoteFileStorageUrl) {
        this.remoteFileStorageUrl = remoteFileStorageUrl;
    }

    @Override
    public String write(MultipartFile uploadedAudioFile, String subDirectoryName) throws IOException {
    	String requestURL = remoteFileStorageUrl.toString() + "file?path=";
		LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		// Hack for uploading multipart file to another resource
		ByteArrayResource contentsAsResource = new ByteArrayResource(uploadedAudioFile.getBytes()) {
			@Override
			public String getFilename() {
				return uploadedAudioFile.getOriginalFilename();
			}
		};
		params.add("file", contentsAsResource);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);
		ResponseEntity<String> response = new RestTemplate().exchange(requestURL, HttpMethod.POST, requestEntity, String.class);
		if (response.getStatusCode().is2xxSuccessful()) {
			// TODO: Release code...
		}
		return null;
    }

    @Override
    public byte[] load(String filePath) throws IOException {
        return new byte[0];
    }
}
