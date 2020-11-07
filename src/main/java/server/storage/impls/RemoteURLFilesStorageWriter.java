package server.storage.impls;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.*;
import org.springframework.web.multipart.MultipartFile;
import server.helpers.HttpUtils;
import server.storage.IFilesStorageWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Objects;

public class RemoteURLFilesStorageWriter implements IFilesStorageWriter {
  private final URL remoteFileStorageUrl;

  public RemoteURLFilesStorageWriter(URL remoteFileStorageUrl) {
    this.remoteFileStorageUrl = remoteFileStorageUrl;
  }

  @Override
  public String write(MultipartFile uploadedAudioFile, String subDirectoryName) throws IOException {
    String url = remoteFileStorageUrl.toString();
    if (!url.endsWith("/")) url += "/";
    HttpUtils.uploadFile(url, HttpMethod.POST, uploadedAudioFile, JsonNode.class);
    return url + URLEncoder.encode(Objects.requireNonNull(uploadedAudioFile.getOriginalFilename()), "UTF-8");
  }
}
