package net.parostroj.timetable.model.changes;

import java.util.Arrays;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Details of diagram change.
 *
 * @author jub
 */
public class DiagramChangeDescription {

    private static final Logger LOG = Logger.getLogger(DiagramChangeDescription.class.getName());

    private String description;
    private String[] params;
    private String _cachedOutput;

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
        clearCached();
        this.description = description;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String... params) {
        clearCached();
        this.params = params;
    }

    public void setDescription(String description, String... params) {
        clearCached();
        this.description = description;
        this.params = params;
    }

    public String getFormattedDescription() {
        if (_cachedOutput == null) {
            try {
                String desc = DiagramChange.getStringWithException(description);
                _cachedOutput = String.format(desc, (Object[])params);
            } catch (MissingResourceException e) {
                _cachedOutput = DiagramChange.getString("not_found");
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Not enough parameters for key: {0}", description);
                _cachedOutput = DiagramChange.getString("not_found");
            }
        }
        return _cachedOutput;
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

    private void clearCached() {
        _cachedOutput = null;
    }
}
