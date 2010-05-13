package net.parostroj.timetable.model.changes;

import java.util.Calendar;
import java.util.List;

/**
 * Set of changes with specified name.
 *
 * @author jub
 */
public interface DiagramChangeSet {

    List<DiagramChange> getChanges();

    String getVersion();

    void setVersion(String version);

    String getAuthor();
    
    void setAuthor(String author);

    Calendar getDate();

    void setDate(Calendar date);
}
