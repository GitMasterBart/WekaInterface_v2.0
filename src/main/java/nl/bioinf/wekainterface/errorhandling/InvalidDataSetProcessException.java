package nl.bioinf.wekainterface.errorhandling;

public class InvalidDataSetProcessException extends IllegalArgumentException{

    public InvalidDataSetProcessException(String s) {
        super(s);
    }

    public InvalidDataSetProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}
