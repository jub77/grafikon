package net.parostroj.timetable.gui.components;

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
}
