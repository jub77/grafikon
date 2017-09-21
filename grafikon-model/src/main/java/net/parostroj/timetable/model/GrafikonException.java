package net.parostroj.timetable.model;

/**
 * Exception.
 *
 * @author jub
 */
public class GrafikonException extends RuntimeException {

    public enum Type {
        TEXT_TEMPLATE, SCRIPT;
    }

    private final Type type;

    public GrafikonException() {
        super();
        type = null;
    }

    public GrafikonException(String message, Throwable cause) {
        super(message, cause);
        type = null;
    }

    public GrafikonException(String message, Throwable cause, Type type) {
        super(message, cause);
        this.type = type;
    }

    public GrafikonException(String message) {
        super(message);
        type = null;
    }

    public GrafikonException(String message, Type type) {
        super(message);
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
