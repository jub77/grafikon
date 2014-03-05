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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Parameter other = (Parameter) obj;
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
            return false;
        }
        if (this.translated != other.translated) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 37 * hash + (this.translated ? 1 : 0);
        return hash;
    }
}
