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
    
    public ColorTextPane() {
        super();
    }

    public void append(Color c, String s) { // better implementation--uses
        // StyleContext
        setEditable(true);
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                StyleConstants.Foreground, c);

        int len = getDocument().getLength(); // same value as
        // getText().length();
        setCaretPosition(len); // place caret at the end (with no selection)
        setCharacterAttributes(aset, false);
        replaceSelection(s); // there is no selection, so inserts at caret
        setCaretPosition(0);
        setEditable(false);
    }
}