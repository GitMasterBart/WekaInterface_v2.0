package nl.bioinf.wekainterface.errorhandling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.text.SimpleDateFormat;

@ControllerAdvice
public class InvalidDataSetExceptionHandler {
    Logger logger = LoggerFactory.getLogger(InvalidDataSetExceptionHandler.class);
    @ExceptionHandler(value = {InvalidDataSetException.class})
    public ResponseEntity<Object> handleLabelCounterException(InvalidDataSetException e){
        logger.error(e.getMessage());
        HttpStatus badRequest = HttpStatus.INTERNAL_SERVER_ERROR;
        InvalidDataSetException exception = new InvalidDataSetException(
                e.getMessage(),
                e,
                badRequest
        );
        return new ResponseEntity<>(exception, badRequest);
    }
}