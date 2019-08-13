package net.parostroj.timetable.gui.pm;

import org.beanfabrics.model.ITextPM;
import org.beanfabrics.validation.ValidationRule;
import org.beanfabrics.validation.ValidationState;

/**
 * Validates if the TextPM contains whitespace at the beginning and end.
 *
 * @author jub
 */
public class EmptySpacesValidationRule implements ValidationRule {

    private static final ValidationState ERROR_STATE = ValidationState.create("");

    private ITextPM text;

    public EmptySpacesValidationRule(ITextPM text) {
        this.text = text;
    }

    @Override
    public ValidationState validate() {
        String t = text.getText();
        if (t.length() > 0) {
            if (Character.isWhitespace(t.charAt(0))
                    || Character.isWhitespace(t.charAt(t.length() - 1))) {
                return ERROR_STATE;
            }
        }
        return null;
    }
}
