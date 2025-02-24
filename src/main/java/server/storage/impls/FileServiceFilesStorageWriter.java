package server.storage.impls;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.*;
import org.springframework.web.multipart.MultipartFile;
import server.helpers.HttpUtils;
import server.storage.IFilesStorageWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileServiceFilesStorageWriter implements IFilesStorageWriter {
  private static final Logger LOGGER = Logger.getLogger(FileServiceFilesStorageWriter.class.getName());
  private final URI remoteFileStorageUrl;

  public FileServiceFilesStorageWriter(URI remoteFileStorageUrl) {
    this.remoteFileStorageUrl = remoteFileStorageUrl;
  }

  @Override
  public String write(MultipartFile uploadedAudioFile, String subDirectoryName) throws IOException {
    String url = remoteFileStorageUrl.toString();
    if (!url.endsWith("/")) url += "/";
    String requestURL = url + subDirectoryName + "&createParentFolders=true";
    ResponseEntity<JsonNode> response = HttpUtils.uploadFile(requestURL, HttpMethod.POST, uploadedAudioFile, JsonNode.class);
    JsonNode uploadedFileJson = Objects.requireNonNull(response.getBody());
    if (!uploadedFileJson.has("name")) {
      LOGGER.log(Level.WARNING, "File uploaded successfully, expected `name` but `null`." +
        "File name: " + uploadedAudioFile.getOriginalFilename() + ", status = " + response.getStatusCode());
      throw new IOException("File uploaded successfully, expected `name` but `null`.");
    }
    String uploadedFileName = uploadedFileJson.get("name").asText();
    return url + subDirectoryName + "/" + URLEncoder.encode(uploadedFileName, "UTF-8");
  }
}
