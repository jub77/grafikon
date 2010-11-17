/*
 * ExportImportDialog.java
 *
 * Created on 21.4.2009, 15:41:47
 */
package net.parostroj.timetable.gui.dialogs;

import java.util.Collection;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;

/**
 * Export/Import dialog.
 *
 * @author jub
 */
public class ImportDialog extends javax.swing.JDialog {

    private TrainDiagram diagram;
    private TrainDiagram libraryDiagram;
    private static final ListModel EMPTY_LIST_MODEL = new DefaultListModel();

    private Map<ImportComponents, Set<ObjectWithId>> selectedItems;
    private Set<ObjectWithId> importedObjects;

    /** Creates new form ExportImportDialog */
    public ImportDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        // create map
        selectedItems = new EnumMap<ImportComponents, Set<ObjectWithId>>(ImportComponents.class);
        // initialize combo box with components and create sets
        for (ImportComponents comps : ImportComponents.values()) {
            componentComboBox.addItem(comps);
        }
        // initialize combobox for matching
        matchComboBox.addItem(ImportMatch.NAME);
        matchComboBox.addItem(ImportMatch.ID);
    }

    /**
     * sets diagram to be modified.
     *
     * @param diagram diagram
     * @param libraryDiagram library diagram
     */
    public void setTrainDiagrams(TrainDiagram diagram, TrainDiagram libraryDiagram) {
        this.diagram = diagram;
        this.libraryDiagram = libraryDiagram;
        for (ImportComponents comps : ImportComponents.values()) {
            selectedItems.put(comps, new LinkedHashSet<ObjectWithId>());
        }
        updateDialog();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        componentsList = new javax.swing.JList();
        removeButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        selectedComponentsList = new javax.swing.JList();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        componentComboBox = new javax.swing.JComboBox();
        javax.swing.JLabel matchLabel = new javax.swing.JLabel();
        matchComboBox = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        componentsList.setPrototypeCellValue("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
        componentsList.setVisibleRowCount(20);
        jScrollPane1.setViewportView(componentsList);

        removeButton.setText("<<");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        addButton.setText(">>");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        selectedComponentsList.setPrototypeCellValue("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
        selectedComponentsList.setVisibleRowCount(20);
        jScrollPane2.setViewportView(selectedComponentsList);

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        componentComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                componentComboBoxActionPerformed(evt);
            }
        });

        matchLabel.setText(ResourceLoader.getString("import.match")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(addButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(removeButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(componentComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(okButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(matchLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(matchComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(matchLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(matchComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(componentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addButton))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        // import things
        importedObjects = new HashSet<ObjectWithId>();
        List<Object> errors = new LinkedList<Object>();

        // for all types
        for (ImportComponents component : ImportComponents.values()) {
            Set<ObjectWithId> objects = selectedItems.get(component);
            if (objects != null) {
                Import imp = Import.getInstance(component, diagram, libraryDiagram, this.getImportMatch());
                imp.importObjects(objects);
                importedObjects.addAll(imp.getImportedObjects());
                errors.addAll(imp.getErrors());
            }
        }

        // create string ...
        if (!errors.isEmpty()) {
            StringBuilder message = new StringBuilder();
            int lineLength = 70;
            int nextLimit = lineLength;
            for (Object error : errors) {
                if (message.length() != 0)
                    message.append(", ");
                if (nextLimit < message.length()) {
                    message.append('\n');
                    nextLimit += lineLength;
                }
                message.append(this.getText(error));
            }
            JOptionPane.showConfirmDialog(this, message, 
                    ResourceLoader.getString("import.warning.title"),
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
        }
        this.setVisible(false);
}//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.setVisible(false);
        // release instances
        this.setTrainDiagrams(null, null);
        this.importedObjects = Collections.emptySet();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void componentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_componentComboBoxActionPerformed
        // update lists with certain type
        this.updateDialog();
    }//GEN-LAST:event_componentComboBoxActionPerformed

    @SuppressWarnings("unchecked")
    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        // add object to selected
        WrapperListModel left = (WrapperListModel)componentsList.getModel();
        WrapperListModel right = (WrapperListModel)selectedComponentsList.getModel();
        Object[] values = selectedComponentsList.getSelectedValues();
        for (Object value : values) {
            if (value instanceof Wrapper<?>) {
                Wrapper<?> w = (Wrapper<?>)value;
                right.removeWrapper(w);
                left.addWrapper(w);
            }
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    @SuppressWarnings("unchecked")
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        // remove object from selected
        WrapperListModel left = (WrapperListModel)componentsList.getModel();
        WrapperListModel right = (WrapperListModel)selectedComponentsList.getModel();
        Object[] values = componentsList.getSelectedValues();
        for (Object value : values) {
            if (value instanceof Wrapper<?>) {
                Wrapper<?> w = (Wrapper<?>)value;
                left.removeWrapper(w);
                right.addWrapper(w);
            }
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void updateDialog() {
        this.updateLists((ImportComponents)componentComboBox.getSelectedItem());
    }

    private void updateLists(ImportComponents comps) {
        // fill in new items
        Set<Object> all = comps != null ? comps.getObjects(libraryDiagram) : null;
        if (all == null || all.isEmpty()) {
            componentsList.setModel(EMPTY_LIST_MODEL);
            selectedComponentsList.setModel(EMPTY_LIST_MODEL);
            return;
        }
        Set<ObjectWithId> sel = selectedItems.get(comps);
        // remove already selected
        all.removeAll(sel);
        fillList(comps, componentsList, all);
        fillList(comps, selectedComponentsList, sel);
    }

    @SuppressWarnings("unchecked")
    private void fillList(ImportComponents comps, JList list, Set<? extends Object> set) {
        WrapperListModel model = new WrapperListModel(comps.getListOfWrappers((Collection<Object>)set), set, comps.sorted());
        list.setModel(model);
    }

    private ImportMatch getImportMatch() {
        return (ImportMatch)matchComboBox.getSelectedItem();
    }

    public Set<ObjectWithId> getImportedObjects() {
        return importedObjects;
    }

    public String getText(Object oid) {
        if (oid instanceof Train) {
            return ((Train) oid).getName();
        } else if (oid instanceof Node) {
            return ((Node) oid).getName();
        } else if (oid instanceof TrainType) {
            return ((TrainType) oid).getDesc();
        } else {
            return oid.toString();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox componentComboBox;
    private javax.swing.JList componentsList;
    private javax.swing.JComboBox matchComboBox;
    private javax.swing.JButton okButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JList selectedComponentsList;
    // End of variables declaration//GEN-END:variables
}
