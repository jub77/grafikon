package net.parostroj.timetable.gui.pm;

import java.util.Locale;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.ITextPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.LocalizedString.StringWithLocale;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Presentation model for string with locale.
 *
 * @author jub
 */
public class StringWithLocalePM extends AbstractPM {

    final ITextPM string;
    final ITextPM locale;

    private Locale usedLocale;

    public StringWithLocalePM(String text, Locale locale) {
        this.string = new TextPM(text);
        this.usedLocale = locale;
        this.locale = new TextPM(locale.getDisplayName(locale));
        this.locale.setEditable(false);
        PMManager.setup(this);
    }

    public StringWithLocale build() {
        String text = ObjectsUtil.checkAndTrim(string.getText());
        return text != null ? LocalizedString.newStringWithLocale(text, usedLocale) : null;
    }
}
