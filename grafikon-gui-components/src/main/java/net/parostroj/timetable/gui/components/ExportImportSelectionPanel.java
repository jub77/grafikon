package net.parostroj.timetable.gui.components;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.imports.ImportComponent;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;

import javax.swing.JComboBox;

public class ExportImportSelectionPanel extends JPanel {

    private static final long serialVersionUID = 1L;

	protected static class Selection {
        public ImportComponent type;
        public List<Wrapper<ObjectWithId>> notSelected;
        public List<Wrapper<ObjectWithId>> selected;

        public Selection(ImportComponent type, List<Wrapper<ObjectWithId>> notSelected,
                List<Wrapper<ObjectWithId>> selected) {
            this.type = type;
            this.notSelected = notSelected;
            this.selected = selected;
        }
    }

    private Map<ImportComponent, Selection> selectionMap;
    private Selection currentSelection;
    private ElementSelectionPanel<ObjectWithId> selectionPanel;
    private JComboBox<Wrapper<ImportComponent>> typeComboBox;
    private JPanel leftPanel;
    private JPanel rightPanel;

    public ExportImportSelectionPanel() {
        this.selectionPanel = new ElementSelectionPanel<>();
        this.selectionMap = new EnumMap<>(ImportComponent.class);
        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setVgap(5);
        borderLayout.setHgap(5);
        this.setLayout(borderLayout);
        this.add(selectionPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.setLayout(new BorderLayout(0, 0));

        leftPanel = new JPanel();
        FlowLayout leftFlowLayout = (FlowLayout) leftPanel.getLayout();
        leftFlowLayout.setVgap(0);
        leftFlowLayout.setHgap(0);
        leftFlowLayout.setAlignment(FlowLayout.LEFT);
        bottomPanel.add(leftPanel);

        typeComboBox = new JComboBox<>();
        leftPanel.add(typeComboBox);

        rightPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) rightPanel.getLayout();
        flowLayout.setVgap(0);
        flowLayout.setHgap(0);
        flowLayout.setAlignment(FlowLayout.RIGHT);
        bottomPanel.add(rightPanel, BorderLayout.EAST);
        typeComboBox.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.DESELECTED) {
                setType(null);
            } else if (event.getStateChange() == ItemEvent.SELECTED) {
                Object selectedItem = typeComboBox.getSelectedItem();
                setType(selectedItem == null ? null : (ImportComponent) ((Wrapper<?>) selectedItem).getElement());
            }
        });
    }

    public JPanel getRightPanel() {
        return rightPanel;
    }

    public JPanel getLeftPanel() {
        return leftPanel;
    }

    protected void setSelection(boolean sorted, List<Wrapper<ObjectWithId>> notSelected,
            List<Wrapper<ObjectWithId>> selected) {
        this.selectionPanel.setSorted(sorted, sorted);
        this.selectionPanel.setListsForSelection(notSelected, selected);
    }

    protected void setType(ImportComponent type) {
        if (type == null) {
            writeBackToSelection();
            currentSelection = null;
        } else {
            currentSelection = selectionMap.get(type);
            selectionPanel.setSorted(type.isSorted(), type.isSorted());
            selectionPanel.setListsForSelection(currentSelection.notSelected, currentSelection.selected);
        }
    }

    private void writeBackToSelection() {
        if (currentSelection != null) {
            currentSelection.selected = selectionPanel.getSelectedList();
            currentSelection.notSelected = selectionPanel.getNotSelectedList();
        }
    }

    public void setSelectionSource(ExportImportSelectionSource source) {
        typeComboBox.removeAllItems();
        currentSelection = null;
        selectionMap.clear();
        for (ImportComponent type : source.getTypes()) {
            Collection<ObjectWithId> elements = source.getElementsForType(type);
            if (!elements.isEmpty()) {
                Selection selection = new Selection(type, Wrapper.getWrapperList(elements), new ArrayList<>());
                selectionMap.put(type, selection);
                typeComboBox.addItem(Wrapper.getWrapper(type));
            }
        }
        typeComboBox.setMaximumRowCount(source.getTypes().size());
    }

    public ExportImportSelection getSelection() {
        // force write back
        this.writeBackToSelection();
        // return map of collections of objects
        return new ExportImportSelection(Maps.filterValues(
                Maps.transformValues(selectionMap, value -> Lists.transform(value.selected, item -> item.getElement())),
                item -> item.size() > 0));
    }
}
