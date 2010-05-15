package net.parostroj.timetable.model.changes;

/**
 * Parameter for diagram change description.
 *
 * @author jub
 */
public class Parameter {

    private String value;
    private boolean translated;

    public Parameter(String value, boolean translated) {
        this.value = value;
        this.translated = translated;
    }

    public Parameter(String value) {
        this(value, false);
    }

    public boolean isTranslated() {
        return translated;
    }

    public void setTranslated(boolean translated) {
        this.translated = translated;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTranslatedValue() {
        return translated ? DiagramChange.getStringWithException(value) : value;
    }
}
