package net.parostroj.timetable.model.changes;

import java.util.Objects;

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
        return translated ? DiagramChange.getStringWithoutException(value) : value;
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
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return this.translated == other.translated;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 37 * hash + (this.translated ? 1 : 0);
        return hash;
    }
}
