/*
 * ElementSelectionDialog.java
 *
 * Created on 23.9.2009, 21:49:51
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.Window;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.utils.ResourceLoader;

/**
 * Dialog for selecting elements.
 *
 * @author jub
 */
public class ElementSelectionDialog<T> extends javax.swing.JDialog {

    private boolean ok = false;

    /** Creates new form ElementSelectionDialog */
    public ElementSelectionDialog(Window parent, boolean modal) {
        super(parent, modal ? ModalityType.DOCUMENT_MODAL : ModalityType.MODELESS);
        initComponents();
    }

    /**
     * shows dialog and returns list of selected elements.
     *
     * @see #selectElements(Collection, Collection)
     *
     * @param list list of elements
     * @return list of selected elements
     */
    public List<T> selectElements(Collection<? extends T> list) {
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
    public List<T> selectElements(Collection<? extends T> list, Collection<? extends T> selected) {
        elementSelectionPanel.setListForSelection(this.wrapElements(list));
        if (selected != null) {
            elementSelectionPanel.addSelected(this.wrapElements(selected));
        }
        ok = false;
        setVisible(true);
        if (ok)
            return this.getElements(elementSelectionPanel.getSelectedList());
        else {
            return null;
        }
    }

    private List<T> getElements(List<Wrapper<T>> wList) {
        List<T> elements = new LinkedList<T>();
        for (Wrapper<T> w : wList) {
            elements.add(w.getElement());
        }
        return elements;
    }

    private void initComponents() {
        elementSelectionPanel = new net.parostroj.timetable.gui.components.ElementSelectionPanel<T>();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(evt -> cancelButtonActionPerformed(evt));

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(evt -> okButtonActionPerformed(evt));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addComponent(elementSelectionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(elementSelectionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        pack();
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        ok = true;
        setVisible(false);
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        ok = false;
        setVisible(false);
    }

    protected List<Wrapper<T>> wrapElements(Iterable<? extends T> elements) {
        return Wrapper.getWrapperList(elements);
    }

    private javax.swing.JButton cancelButton;
    private net.parostroj.timetable.gui.components.ElementSelectionPanel<T> elementSelectionPanel;
    private javax.swing.JButton okButton;
}
