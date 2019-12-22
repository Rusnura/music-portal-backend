package server.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.logging.Logger;

@RestController
public class CommonController {
    private static final Logger LOGGER = Logger.getLogger(CommonController.class.getName());

    @PostMapping(value = "/api/download")
    public ResponseEntity<?> downloadFile(@RequestParam String url) {
        // TODO: Release of downloading a song from URL
        return null;
    }
}
