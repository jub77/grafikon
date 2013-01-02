package net.parostroj.timetable.model.ls.impl4;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.changes.DiagramChange;
import net.parostroj.timetable.model.changes.DiagramChangeSet;

/**
 * Storage for diagram change set.
 *
 * @author jub
 */
@XmlRootElement(name="change_set")
@XmlType(propOrder={"version", "author", "date", "changes"})
public class LSDiagramChangeSet {

    private String version;
    private String author;
    private Calendar date;
    private List<LSDiagramChange> changes;

    public LSDiagramChangeSet() {}

    public LSDiagramChangeSet(DiagramChangeSet set) {
        this.version = set.getVersion();
        this.author = set.getAuthor();
        this.date = set.getDate();
        this.changes = new LinkedList<LSDiagramChange>();
        for (DiagramChange change : set.getChanges()) {
            this.changes.add(new LSDiagramChange(change));
        }
    }

    @XmlElement(name="change")
    public List<LSDiagramChange> getChanges() {
        if (changes == null)
            changes = new LinkedList<LSDiagramChange>();
        return changes;
    }

    public void setChanges(List<LSDiagramChange> changes) {
        this.changes = changes;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }
}
