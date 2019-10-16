package server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "Can't upload your MP3 file")
public class IncorrectAudioException extends RuntimeException {
    public IncorrectAudioException(String message) {
        super(message);
    }
}
