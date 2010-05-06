package net.parostroj.timetable.model.ls.impl4;

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
@XmlType(propOrder={"version", "changes"})
public class LSDiagramChangeSet {

    private String version;
    private List<LSDiagramChange> changes;

    public LSDiagramChangeSet() {}

    public LSDiagramChangeSet(DiagramChangeSet set) {
        this.version = set.getVersion();
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
}
