package net.parostroj.timetable.gui.components;

import javax.swing.JTextArea;
import javax.swing.UIManager;

public class JTextAreaGrey extends JTextArea {

    @Override
    public void setEditable(boolean b) {
        super.setEditable(b);
        this.setBackground(UIManager.getColor(b ? "TextArea.background" : "TextArea.disabledBackground"));
    }
}
