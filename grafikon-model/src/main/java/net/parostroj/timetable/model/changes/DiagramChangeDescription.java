package net.parostroj.timetable.model.changes;

import java.util.Arrays;
import java.util.MissingResourceException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Details of diagram change.
 *
 * @author jub
 */
public class DiagramChangeDescription {

    private static final Logger log = LoggerFactory.getLogger(DiagramChangeDescription.class);

    private String description;
    private Parameter[] params;
    private String cachedOutput;

    public DiagramChangeDescription(String description) {
        this.description = description;
    }

    public DiagramChangeDescription(String description, Parameter... params) {
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

    public Parameter[] getParams() {
        return params;
    }

    public void setParams(Parameter... params) {
        clearCached();
        this.params = params;
    }

    public void setDescription(String description, Parameter... params) {
        clearCached();
        this.description = description;
        this.params = params;
    }

    public String getFormattedDescription() {
        if (cachedOutput == null) {
            try {
                String desc = DiagramChange.getStringWithException(description);
                cachedOutput = String.format(desc, (Object[]) this.convertParams());
            } catch (MissingResourceException e) {
                log.warn("Key not found: {}", e.getKey());
                cachedOutput = DiagramChange.getString("not_found");
            } catch (Exception e) {
                log.warn("Not enough parameters for key: {}", description);
                cachedOutput = DiagramChange.getString("not_found");
            }
        }
        return cachedOutput;
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
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        return Arrays.deepEquals(this.params, other.params);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 97 * hash + Arrays.deepHashCode(this.params);
        return hash;
    }

    private void clearCached() {
        cachedOutput = null;
    }

    private String[] convertParams() {
        if (params == null)
            return new String[0];
        String[] result = new String[params.length];
        for (int i = 0; i < params.length; i++)
            result[i] = params[i].getTranslatedValue();
        return result;
    }
}
