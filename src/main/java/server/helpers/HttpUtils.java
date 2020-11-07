package server.helpers;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpUtils {
  private static final Logger LOGGER = Logger.getLogger(HttpUtils.class.getName());
  public static <T>ResponseEntity<T> uploadFile(String url,
                                                HttpMethod method,
                                                MultipartFile file,
                                                Class<T> responseType) throws IOException {
    LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
    // Hack for uploading multipart file to another resource
    ByteArrayResource contentsAsResource = new ByteArrayResource(file.getBytes()) {
      @Override
      public String getFilename() {
        return file.getOriginalFilename();
      }
    };
    params.add("file", contentsAsResource);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);
    ResponseEntity<T> response = new RestTemplate().exchange(url, method, requestEntity, responseType);
    if (!response.getStatusCode().is2xxSuccessful()) {
      LOGGER.log(Level.WARNING, "Can't upload file: " + file.getOriginalFilename() + "." +
        "File service returns non-successfully response: " + response.getBody() + ", status = " + response.getStatusCode());
      throw new IOException("Can't upload file! Response: " + response.getBody() + ", status = " + response.getStatusCode());
    }
    T responseBody = response.getBody();

    if (responseBody == null) {
      LOGGER.log(Level.WARNING, "File uploaded successfully, but response is null. " +
        "File name: " + file.getOriginalFilename() + ", status = " + response.getStatusCode());
      throw new IOException("File uploaded successfully, but response is null!");
    }

    return response;
  }
}
