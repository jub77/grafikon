package net.parostroj.timetable.model.changes;

import java.util.Calendar;

/**
 * Details of diagram change.
 *
 * @author jub
 */
public class DiagramChangeDescription {

    private String description;
    private String[] params;
    private Calendar date;

    public DiagramChangeDescription(String description) {
        this.description = description;
    }

    public DiagramChangeDescription(String description, String... params) {
        this.description = description;
        this.params = params;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
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
}
