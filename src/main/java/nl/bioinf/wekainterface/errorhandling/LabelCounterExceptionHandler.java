package nl.bioinf.wekainterface.errorhandling;

import nl.bioinf.wekainterface.model.LabelCounter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class LabelCounterExceptionHandler {

    @ExceptionHandler(value = {LabelCounterException.class})
    public ResponseEntity<Object> handleLabelCounterException(LabelCounterException e){
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        LabelCounterException exception = new LabelCounterException(
                e.getMessage(),
                e,
                badRequest
        );
        return new ResponseEntity<>(exception, badRequest);
    }
}
