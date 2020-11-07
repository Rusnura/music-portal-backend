package server.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

import javax.servlet.MultipartConfigElement;

@Configuration
public class FileUploadConfig {
  @Value("${server.upload.max-file-size-mb}")
  private String maxUploadFileSize;

  @Value("${server.upload.max-request-size-mb}")
  private String maxRequestSize;

  @Bean
  MultipartConfigElement multipartConfigElement() {
    MultipartConfigFactory factory = new MultipartConfigFactory();
    factory.setMaxFileSize(DataSize.of(Integer.parseInt(maxUploadFileSize), DataUnit.MEGABYTES));
    factory.setMaxRequestSize(DataSize.of(Integer.parseInt(maxRequestSize), DataUnit.MEGABYTES));
    return factory.createMultipartConfig();
  }
}
