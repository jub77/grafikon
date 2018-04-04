package net.parostroj.timetable.gui.components;

import javax.swing.UIManager;

import org.beanfabrics.swing.BnTextArea;

public class BnTextAreaGrey extends BnTextArea {

    private static final long serialVersionUID = 1L;

	@Override
    public void setEditable(boolean b) {
        super.setEditable(b);
        this.setBackground(UIManager.getColor(b ? "TextArea.background" : "TextArea.disabledBackground"));
    }
}
