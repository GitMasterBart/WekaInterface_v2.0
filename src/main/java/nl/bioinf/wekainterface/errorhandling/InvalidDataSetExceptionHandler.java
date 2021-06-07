package nl.bioinf.wekainterface.errorhandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class InvalidDataSetExceptionHandler {

    @ExceptionHandler(value = {InvalidDataSetException.class})
    public ResponseEntity<Object> handleLabelCounterException(InvalidDataSetException e){
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        InvalidDataSetException exception = new InvalidDataSetException(
                e.getMessage(),
                e,
                badRequest
        );
        return new ResponseEntity<>(exception, badRequest);
    }
}
