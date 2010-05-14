package net.parostroj.timetable.model.changes;

import java.util.Arrays;

/**
 * Details of diagram change.
 *
 * @author jub
 */
public class DiagramChangeDescription {

    private String description;
    private String[] params;

    public DiagramChangeDescription(String description) {
        this.description = description;
    }

    public DiagramChangeDescription(String description, String... params) {
        this.description = description;
        this.params = params;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String... params) {
        this.params = params;
    }

    public void setDescription(String description, String... params) {
        this.description = description;
        this.params = params;
    }

    public String getFormattedDescription() {
        return (description != null) ? String.format(DiagramChange.getString(description), (Object[])params) : "";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DiagramChangeDescription other = (DiagramChangeDescription) obj;
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if (!Arrays.deepEquals(this.params, other.params)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 97 * hash + Arrays.deepHashCode(this.params);
        return hash;
    }
}
