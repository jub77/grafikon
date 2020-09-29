package net.parostroj.timetable.gui.components;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * Fix of BnComboBox with switching model with the same options.
 *
 * @author jub
 */
public class BnComboBox extends org.beanfabrics.swing.BnComboBox {

    private static final long serialVersionUID = 1L;

    public BnComboBox() {
        super();
        this.addPropertyChangeListener("presentationModel",
                evt -> ((TextEditorComboBoxModel) getModel()).refresh());
    }

    public void automaticallyResizeOnDataChange() {
        this.getModel().addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
                // nothing
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                // nothing
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                // ensure that recalculating of size is in effect
                setPrototypeDisplayValue(null);
            }
        });
    }

    public void resizeForCurrentData() {
        this.setPrototypeDisplayValue(null);
    }
}
