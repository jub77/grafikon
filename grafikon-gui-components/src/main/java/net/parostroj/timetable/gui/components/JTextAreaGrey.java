package net.parostroj.timetable.gui.components;

import javax.swing.JTextArea;
import javax.swing.UIManager;

public class JTextAreaGrey extends JTextArea {

    private static final long serialVersionUID = 1L;

	@Override
    public void setEditable(boolean b) {
        super.setEditable(b);
        this.setBackground(UIManager.getColor(b ? "TextArea.background" : "TextArea.disabledBackground"));
    }
}
