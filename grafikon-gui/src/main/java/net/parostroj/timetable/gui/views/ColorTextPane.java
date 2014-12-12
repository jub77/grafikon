package net.parostroj.timetable.gui.views;

import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.text.*;

/**
 * Colored text pane.
 *
 * @author jub
 */
public class ColorTextPane extends JTextPane {

    public void append(Color c, String s) {
        setEditable(true);
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        int len = getDocument().getLength();
        setCaretPosition(len);
        setCharacterAttributes(aset, false);
        replaceSelection(s);
        setCaretPosition(0);
        setEditable(false);
    }
}
