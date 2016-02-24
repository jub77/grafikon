package net.parostroj.timetable.gui.dialogs;

import java.awt.Window;
import java.util.*;

import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.utils.ResourceLoader;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 * Dialog for selecting elements (with checkboxes).
 *
 * @author jub
 */
public class ElementSelectionCheckBoxDialog<T> extends javax.swing.JDialog {

    private boolean ok = false;

    public ElementSelectionCheckBoxDialog(Window parent, boolean modal) {
        super(parent, modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        initComponents();
    }

    public void setSorted(boolean sorted) {
        elementSelectionPanel.setSorted(sorted);
    }

    /**
     * shows dialog and returns list of selected elements.
     *
     * @see #selectElements(Collection, Collection)
     *
     * @param list list of elements
     * @return list of selected elements
     */
    public Collection<T> selectElements(Collection<? extends T> list) {
        return this.selectElements(list, null);
    }

    /**
     * shows dialog and returns list of selected elements. It returns
     * <code>null</code> in case cancel was pressed.
     *
     * @param list list of element from which the selection is done
     * @param selected already selected elements
     * @return list of selected elements
     */
    public Collection<T> selectElements(Collection<? extends T> list, Collection<? extends T> selected) {
        return this.selectElements(list, selected, null);
    }

    public Collection<T> selectElements(Collection<? extends T> list, Collection<? extends T> selected, Collection<? extends T> locked) {
        elementSelectionPanel.setListForSelection(this.wrapElements(list));
        elementSelectionPanel.setSelected(selected == null ? Collections.emptyList() : selected);
        elementSelectionPanel.setLocked(locked == null ? Collections.emptyList() : locked);
        setVisible(true);
        if (ok) {
            return elementSelectionPanel.getSelected();
        } else {
            return null;
        }
    }

    private void initComponents() {
        elementSelectionPanel = new net.parostroj.timetable.gui.components.ElementSelectionCheckBoxPanel<T>();
        javax.swing.JButton cancelButton = new javax.swing.JButton();
        javax.swing.JButton okButton = new javax.swing.JButton();

        elementSelectionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        JPanel buttonPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        getContentPane().add(elementSelectionPanel, BorderLayout.CENTER);

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(e -> {
            ok = false;
            setVisible(false);
        });

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(e -> {
            ok = true;
            setVisible(false);
        });

        pack();
    }

    protected List<Wrapper<T>> wrapElements(Iterable<? extends T> elements) {
        return Wrapper.getWrapperList(elements);
    }

    private net.parostroj.timetable.gui.components.ElementSelectionCheckBoxPanel<T> elementSelectionPanel;
}
