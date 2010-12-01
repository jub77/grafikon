package net.parostroj.timetable.model;

/**
 * Exception.
 * 
 * @author jub
 */
public class GrafikonException extends Exception {

    public static enum Type {
        TEXT_TEMPLATE, SCRIPT;
    }

    private Type type;
    
    public GrafikonException() {
        super();
    }

    public GrafikonException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public GrafikonException(String message, Throwable cause, Type type) {
        super(message, cause);
        this.type = type;
    }

    public GrafikonException(String message) {
        super(message);
    }

    public GrafikonException(String message, Type type) {
        super(message);
        this.type = type;
    }
    
    public Type getType() {
        return type;
    }
}
