package nl.bioinf.wekainterface.errorhandling;

import org.springframework.http.HttpStatus;

import java.net.http.HttpClient;
import java.time.ZonedDateTime;

public class InvalidDataSetException extends IllegalArgumentException{
    private final String message;
    private final Throwable throwable;
    private final HttpStatus httpStatus;

    public InvalidDataSetException(String message,
                                   Throwable throwable,
                                   HttpStatus httpStatus) {
        this.message = message;
        this.throwable = throwable;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
