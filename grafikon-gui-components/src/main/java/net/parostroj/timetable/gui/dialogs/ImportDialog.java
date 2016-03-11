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

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.*;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.imports.ImportComponent;
import net.parostroj.timetable.model.imports.ImportMatch;

/**
 * Export/Import dialog.
 *
 * @author jub
 */
public class ImportDialog extends javax.swing.JDialog {

    private static final ListModel<Wrapper<ObjectWithId>> EMPTY_LIST_MODEL = new DefaultListModel<>();

    private TrainDiagram diagram;
    private TrainDiagram libraryDiagram;
    private Predicate<ObjectWithId> filter;
    private final Map<ImportComponent, Set<ObjectWithId>> selectedItems;
    private WrapperListModel<ObjectWithId> left;
    private WrapperListModel<ObjectWithId> right;

    private boolean selected;
    private final Collection<ImportComponent> components;

    /** Creates new form ExportImportDialog */
    public ImportDialog(java.awt.Frame parent, boolean modal, Collection<ImportComponent> components) {
        super(parent, modal);
        initComponents();

        // create map
        selectedItems = new EnumMap<ImportComponent, Set<ObjectWithId>>(ImportComponent.class);
        // initialize combo box with components and create sets
        if (components == null) {
            components = Arrays.asList(ImportComponent.values());
        }
        for (ImportComponent comps : components) {
            componentComboBox.addItem(Wrapper.getWrapper(comps));
        }
        // initialize combobox for matching
        matchComboBox.addItem(Wrapper.getWrapper(ImportMatch.NAME));
        matchComboBox.addItem(Wrapper.getWrapper(ImportMatch.ID));

        selected = false;
        this.components = components;
    }

    /**
     * sets diagram to be modified.
     *
     * @param diagram diagram
     * @param libraryDiagram library diagram
     */
    public void setTrainDiagrams(TrainDiagram diagram, TrainDiagram libraryDiagram, Predicate<ObjectWithId> filter) {
        this.diagram = diagram;
        this.libraryDiagram = libraryDiagram;
        this.filter = filter;
        clear();
        updateDialog();
    }

    /**
     * clears selected and imported objects.
     */
    public void clear() {
        selected = false;
        for (ImportComponent comps : components) {
            selectedItems.put(comps, new LinkedHashSet<ObjectWithId>());
        }
    }

    private void initComponents() {
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        componentsList = new javax.swing.JList<Wrapper<ObjectWithId>>();
        removeButton = GuiComponentUtils.createButton(GuiIcon.DARROW_LEFT, 2);
        addButton = GuiComponentUtils.createButton(GuiIcon.DARROW_RIGHT, 2);
        javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        selectedComponentsList = new javax.swing.JList<Wrapper<ObjectWithId>>();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        componentComboBox = new javax.swing.JComboBox<Wrapper<ImportComponent>>();
        componentComboBox.setMaximumRowCount(ImportComponent.values().length);
        javax.swing.JLabel matchLabel = new javax.swing.JLabel();
        matchComboBox = new javax.swing.JComboBox<Wrapper<ImportMatch>>();

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        componentsList.setPrototypeCellValue(Wrapper.getPrototypeWrapper("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"));
        componentsList.setVisibleRowCount(20);
        jScrollPane1.setViewportView(componentsList);

        removeButton.addActionListener(evt -> removeButtonActionPerformed(evt));

        addButton.addActionListener(evt -> addButtonActionPerformed(evt));

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        selectedComponentsList.setPrototypeCellValue(Wrapper.getPrototypeWrapper("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"));
        selectedComponentsList.setVisibleRowCount(20);
        jScrollPane2.setViewportView(selectedComponentsList);

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(evt -> okButtonActionPerformed(evt));

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(evt -> cancelButtonActionPerformed(evt));

        componentComboBox.addActionListener(evt -> componentComboBoxActionPerformed(evt));

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
                    .addComponent(removeButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );

        pack();
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        selected = true;
        this.setVisible(false);
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.setVisible(false);
    }

    private void componentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
        // update lists with certain type
        this.updateDialog();
    }

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {
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
    }

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {
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
    }

    private void updateDialog() {
        Wrapper<?> w = (Wrapper<?>) componentComboBox.getSelectedItem();
        this.updateLists(w != null ? (ImportComponent) w.getElement() : null);
    }

    private void updateLists(ImportComponent comps) {
        // fill in new items
        Set<ObjectWithId> all = comps != null ? comps.getObjects(libraryDiagram) : null;
        if (all == null || all.isEmpty()) {
            componentsList.setModel(EMPTY_LIST_MODEL);
            selectedComponentsList.setModel(EMPTY_LIST_MODEL);
            return;
        }
        if (filter != null) {
            all = new HashSet<ObjectWithId>(Sets.filter(all, filter));
        }
        Set<ObjectWithId> sel = selectedItems.get(comps);
        // remove already selected
        all.removeAll(sel);
        left = fillList(comps, componentsList, all);
        right = fillList(comps, selectedComponentsList, sel);
    }

    private WrapperListModel<ObjectWithId> fillList(ImportComponent comps, JList<Wrapper<ObjectWithId>> list, Set<ObjectWithId> set) {
        WrapperDelegate<ObjectWithId> delegate = null;
        if (comps == ImportComponent.TRAINS_CYCLES) {
            delegate = Wrapper.convert(new TrainsCycleWrapperDelegate(true));
        }
        List<Wrapper<ObjectWithId>> wrapperList = Wrapper.getWrapperList(set, delegate);
        WrapperListModel<ObjectWithId> model = new WrapperListModel<ObjectWithId>(wrapperList, set, comps.sorted());
        list.setModel(model);
        return model;
    }

    public ImportMatch getImportMatch() {
        Wrapper<?> w = (Wrapper<?>) matchComboBox.getSelectedItem();
        return w != null ? (ImportMatch) w.getElement() : null;
    }

    public Map<ImportComponent, Set<ObjectWithId>> getSelectedItems() {
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

    private javax.swing.JButton addButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox<Wrapper<ImportComponent>> componentComboBox;
    private javax.swing.JList<Wrapper<ObjectWithId>> componentsList;
    private javax.swing.JComboBox<Wrapper<ImportMatch>> matchComboBox;
    private javax.swing.JButton okButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JList<Wrapper<ObjectWithId>> selectedComponentsList;
}
