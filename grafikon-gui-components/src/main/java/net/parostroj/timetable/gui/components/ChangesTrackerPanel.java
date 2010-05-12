package net.parostroj.timetable.gui.components;

import javax.swing.DefaultListModel;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.changes.ChangesTrackerEvent;
import net.parostroj.timetable.model.changes.ChangesTrackerListener;
import net.parostroj.timetable.model.changes.DiagramChange;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.changes.DiagramChangeDescription;
import net.parostroj.timetable.model.changes.DiagramChangeSet;

/**
 * Changes tracker panel.
 *
 * @author jub
 */
public class ChangesTrackerPanel extends javax.swing.JPanel implements ChangesTrackerListener {

    private static class ChangeSetWrapper {
        public DiagramChangeSet set;
        public boolean current;

        public ChangeSetWrapper(DiagramChangeSet set, String currentVersion) {
            this.set = set;
            current = set.getVersion().equals(currentVersion);
        }

        @Override
        public String toString() {
            return current ? String.format("%s*", set.getVersion()) : set.getVersion();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ChangeSetWrapper other = (ChangeSetWrapper) obj;
            if (this.set != other.set && (this.set == null || !this.set.equals(other.set))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 53 * hash + (this.set != null ? this.set.hashCode() : 0);
            return hash;
        }
    }

    private static class ChangeWrapper {
        public DiagramChange change;

        public ChangeWrapper(DiagramChange change) {
            this.change = change;
        }

        @Override
        public String toString() {
            return String.format("%s: %s, %s", change.getType(), change.getObject() != null ? change.getObject() : change.getType(), change.getAction());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ChangeWrapper other = (ChangeWrapper) obj;
            if (this.change != other.change && (this.change == null || !this.change.equals(other.change))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 83 * hash + (this.change != null ? this.change.hashCode() : 0);
            return hash;
        }
    }

    private TrainDiagram diagram;
    private DiagramChangeSet current;

    /** Creates new form ChangesTrackerPanel */
    public ChangesTrackerPanel() {
        initComponents();
    }

    public void setTrainDiagram(TrainDiagram diagram) {
        if (diagram != null) {
            diagram.getChangesTracker().addListener(this);
        }
        this.diagram = diagram;
        this.fillVersions();
    }

    private void fillVersions() {
        // fill list of versions
        DefaultListModel model = new DefaultListModel();
        if (diagram != null) {
            String currentVersion = diagram.getChangesTracker().isTrackingEnabled() ?
                diagram.getChangesTracker().getCurrentVersion() :
                null;
            for (DiagramChangeSet set : diagram.getChangesTracker().getChangeSets()) {
                model.addElement(new ChangeSetWrapper(set, currentVersion));
            }
        }
        versionsList.setModel(model);
        if (!model.isEmpty()) {
            Object object = model.getElementAt(model.getSize() - 1);
            versionsList.setSelectedValue(object, true);
        }
    }

    private void fillChanges(DiagramChangeSet set) {
        DefaultListModel model = new DefaultListModel();
        if (set != null) {
            for (DiagramChange change : set.getChanges()) {
                model.addElement(new ChangeWrapper(change));
            }
        }
        changesList.setModel(model);
    }

    @Override
    public void trackerChanged(ChangesTrackerEvent event) {
        switch (event.getType()) {
            case CHANGE_ADDED:
                if (event.getSet() == current)
                    ((DefaultListModel)changesList.getModel()).addElement(new ChangeWrapper(event.getChange()));
                break;
            case CHANGE_REMOVED:
                if (event.getSet() == current)
                    ((DefaultListModel)changesList.getModel()).removeElement(new ChangeWrapper(event.getChange()));
                break;
            case CHANGE_MODIFIED:
                ChangeWrapper w = (ChangeWrapper)changesList.getSelectedValue();
                if (w != null && w.change == event.getChange()) {
                    detailsTextArea.setText(this.transformChange(event.getChange()));
                }
                break;
            // may be executed more time than it is necessary
            case CURRENT_SET_CHANGED: case SET_ADDED: case SET_REMOVED: case TRACKING_DISABLED: case TRACKING_ENABLED:
                this.setTrainDiagram(diagram);
                break;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JScrollPane scrollPane1 = new javax.swing.JScrollPane();
        detailsTextArea = new javax.swing.JTextArea();
        splitPane = new javax.swing.JSplitPane();
        javax.swing.JScrollPane scrollPane2 = new javax.swing.JScrollPane();
        versionsList = new javax.swing.JList();
        javax.swing.JScrollPane scrollPane3 = new javax.swing.JScrollPane();
        changesList = new javax.swing.JList();

        setLayout(new java.awt.BorderLayout());

        detailsTextArea.setColumns(20);
        detailsTextArea.setEditable(false);
        detailsTextArea.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        detailsTextArea.setLineWrap(true);
        detailsTextArea.setRows(6);
        detailsTextArea.setWrapStyleWord(true);
        scrollPane1.setViewportView(detailsTextArea);

        add(scrollPane1, java.awt.BorderLayout.SOUTH);

        versionsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        versionsList.setPrototypeCellValue("mmmmmmm");
        versionsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                versionsListValueChanged(evt);
            }
        });
        scrollPane2.setViewportView(versionsList);

        splitPane.setLeftComponent(scrollPane2);

        changesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        changesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                changesListValueChanged(evt);
            }
        });
        scrollPane3.setViewportView(changesList);

        splitPane.setRightComponent(scrollPane3);

        add(splitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void versionsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_versionsListValueChanged
        if (!evt.getValueIsAdjusting()) {
            // fill list
            ChangeSetWrapper w = (ChangeSetWrapper)versionsList.getSelectedValue();
            this.fillChanges(w != null ? w.set : null);
            current = w != null ? w.set : null;
        }
    }//GEN-LAST:event_versionsListValueChanged

    private void changesListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_changesListValueChanged
        if (!evt.getValueIsAdjusting()) {
            // show change
            ChangeWrapper w = (ChangeWrapper)changesList.getSelectedValue();
            if (w == null)
                detailsTextArea.setText("");
            else
                detailsTextArea.setText(this.transformChange(w.change));
        }
    }//GEN-LAST:event_changesListValueChanged

    private String transformChange(DiagramChange change) {
        StringBuilder b = new StringBuilder();
        b.append(change.getType()).append(":\n");
        b.append("  ").append(change.getObject() != null ? change.getObject() : change.getType()).append('\n');
        if (change.getAction() != null)
            b.append(ResourceLoader.getString("tracker.action")).append(":\n  ").append(change.getAction()).append('\n');
        if (change.getDescriptions() != null) {
            b.append(ResourceLoader.getString("tracker.description")).append(":");
            for (DiagramChangeDescription d : change.getDescriptions()) {
                b.append('\n').append(d.getFormattedDescription());
            }
        }
        return b.toString();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList changesList;
    private javax.swing.JTextArea detailsTextArea;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JList versionsList;
    // End of variables declaration//GEN-END:variables
}
