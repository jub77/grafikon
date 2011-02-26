/*
 * ExportImportDialog.java
 *
 * Created on 21.4.2009, 15:41:47
 */
package net.parostroj.timetable.gui.dialogs;

import java.util.*;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.*;

/**
 * Export/Import dialog.
 *
 * @author jub
 */
public class ImportDialog extends javax.swing.JDialog {

    private static final ListModel EMPTY_LIST_MODEL = new DefaultListModel();

    private TrainDiagram diagram;
    private TrainDiagram libraryDiagram;
    private Map<ImportComponents, Set<ObjectWithId>> selectedItems;
    private WrapperListModel<ObjectWithId> left;
    private WrapperListModel<ObjectWithId> right;
    
    private boolean selected;

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
        
        selected = false;
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
        clear();
        updateDialog();
    }

    /**
     * clears selected and imported objects.
     */
    public void clear() {
        selected = false;
        for (ImportComponents comps : ImportComponents.values()) {
            selectedItems.put(comps, new LinkedHashSet<ObjectWithId>());
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
        selected = true;
        this.setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void componentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_componentComboBoxActionPerformed
        // update lists with certain type
        this.updateDialog();
    }//GEN-LAST:event_componentComboBoxActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        // add object to selected
        int[] values = selectedComponentsList.getSelectedIndices();
        List<Wrapper<ObjectWithId>> toBeRemoved = new LinkedList<Wrapper<ObjectWithId>>();
        for (int ind : values) {
            Wrapper<ObjectWithId> wrapper = right.getIndex(ind);
            toBeRemoved.add(wrapper);
            left.addWrapper(wrapper);
        }
        for (Wrapper<ObjectWithId> w : toBeRemoved) {
            right.removeWrapper(w);
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        // remove object from selected
        int[] values = componentsList.getSelectedIndices();
        List<Wrapper<ObjectWithId>> toBeRemoved = new LinkedList<Wrapper<ObjectWithId>>();
        for (int ind : values) {
            Wrapper<ObjectWithId> wrapper = left.getIndex(ind);
            toBeRemoved.add(wrapper);
            right.addWrapper(wrapper);
        }
        for (Wrapper<ObjectWithId> w : toBeRemoved) {
            left.removeWrapper(w);
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void updateDialog() {
        this.updateLists((ImportComponents) componentComboBox.getSelectedItem());
    }

    private void updateLists(ImportComponents comps) {
        // fill in new items
        Set<ObjectWithId> all = comps != null ? comps.getObjects(libraryDiagram) : null;
        if (all == null || all.isEmpty()) {
            componentsList.setModel(EMPTY_LIST_MODEL);
            selectedComponentsList.setModel(EMPTY_LIST_MODEL);
            return;
        }
        Set<ObjectWithId> sel = selectedItems.get(comps);
        // remove already selected
        all.removeAll(sel);
        left = fillList(comps, componentsList, all);
        right = fillList(comps, selectedComponentsList, sel);
    }

    private WrapperListModel<ObjectWithId> fillList(ImportComponents comps, JList list, Set<ObjectWithId> set) {
        WrapperListModel<ObjectWithId> model = new WrapperListModel<ObjectWithId>(comps.getListOfWrappers(set), set, comps.sorted());
        list.setModel(model);
        return model;
    }

    public ImportMatch getImportMatch() {
        return (ImportMatch) matchComboBox.getSelectedItem();
    }

    public Map<ImportComponents, Set<ObjectWithId>> getSelectedItems() {
        return selectedItems;
    }
    
    public TrainDiagram getLibraryDiagram() {
        return libraryDiagram;
    }
    
    public TrainDiagram getDiagram() {
        return diagram;
    }

    public boolean isSelected() {
        return selected;
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
