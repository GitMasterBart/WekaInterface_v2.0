package nl.bioinf.wekainterface.errorhandling;

public class LabelCounterProcessException extends IllegalArgumentException{

    public LabelCounterProcessException(String s) {
        super(s);
    }

    public LabelCounterProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}
