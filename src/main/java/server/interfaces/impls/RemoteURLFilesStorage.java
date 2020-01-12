package server.interfaces.impls;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import server.interfaces.IFilesStorage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoteURLFilesStorage implements IFilesStorage {
    private static final Logger LOGGER = Logger.getLogger(RemoteURLFilesStorage.class.getName());
    private final URL remoteFileStorageUrl;

    public RemoteURLFilesStorage(URL remoteFileStorageUrl) {
        this.remoteFileStorageUrl = remoteFileStorageUrl;
    }

    @Override
    public String write(MultipartFile uploadedAudioFile, String subDirectoryName) throws IOException {
    	String url = remoteFileStorageUrl.toString();
    	if (!url.endsWith("/")) url += "/";
    	String requestURL = url + "file?path=";
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
		ResponseEntity<JsonNode> response = new RestTemplate().exchange(requestURL, HttpMethod.POST, requestEntity, JsonNode.class);
		if (!response.getStatusCode().is2xxSuccessful()) {
			LOGGER.log(Level.WARNING, "Can't upload file: " + uploadedAudioFile.getOriginalFilename() + "." +
				"File service returns non-successfully response: " + response.getBody() + ", status = " + response.getStatusCode());
			throw new IOException("Can't upload file! Response: " + response.getBody() + ", status = " + response.getStatusCode());
		}
		JsonNode uploadedFile = response.getBody();

		if (uploadedFile == null) {
			LOGGER.log(Level.WARNING, "File uploaded successfully, but response is null. " +
				"File name: " + uploadedAudioFile.getOriginalFilename() + ", status = " + response.getStatusCode());
			throw new IOException("File uploaded successfully, but response is null!");
		}

		if (!uploadedFile.has("name")) {
			LOGGER.log(Level.WARNING, "File uploaded successfully, expected `name` but `null`." +
				"File name: " + uploadedAudioFile.getOriginalFilename() + ", status = " + response.getStatusCode());
			throw new IOException("File uploaded successfully, expected `name` but `null`.");
		}
		String uploadedFileName = uploadedFile.get("name").asText();
		return url + "file?path=" + uploadedFileName;
    }

    @Override
    public byte[] load(String filePath) throws IOException {
        return IOUtils.toByteArray(new URL(filePath));
    }
}
