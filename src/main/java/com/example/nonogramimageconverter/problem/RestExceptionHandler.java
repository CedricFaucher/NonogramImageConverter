package com.example.nonogramimageconverter.problem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ImageIOException.class)
    protected ResponseEntity<Object> ImageIOException(ImageIOException ex) {
        log.error(ex.getMessage());
        APIError err = APIError.builder().message(ex.getMessage()).build();
        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongFormatException.class)
    protected ResponseEntity<Object> WrongFormatException(WrongFormatException ex) {
        log.error(ex.getMessage());
        APIError err = APIError.builder().message(ex.getMessage()).build();
        return new ResponseEntity<>(err, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
}
