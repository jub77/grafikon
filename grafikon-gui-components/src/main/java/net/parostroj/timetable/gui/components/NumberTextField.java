package net.parostroj.timetable.gui.components;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.swing.JFormattedTextField;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberTextField extends JFormattedTextField {

    private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(NumberTextField.class);

    public NumberTextField() {
        this("#0.########");
    }

    public NumberTextField(String formatStr) {
        DecimalFormat format = new DecimalFormat(formatStr);
        format.setDecimalSeparatorAlwaysShown(false);
        format.setParseBigDecimal(true);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(BigDecimal.class);
        formatter.setMinimum(new BigDecimal(0));
        this.setFormatterFactory(new DefaultFormatterFactory(formatter));
        this.setFocusLostBehavior(JFormattedTextField.PERSIST);
        setValue(new BigDecimal(0));
        setHorizontalAlignment(SwingConstants.RIGHT);
    }

    public void setNumberValue(BigDecimal number) {
        if (number == null) {
            setText("");
        } else {
            super.setValue(number);
        }
    }

    public BigDecimal getNumberValue() {
        try {
            if (!"".equals(this.getText().trim())) {
                this.commitEdit();
                BigDecimal dValue = (BigDecimal) super.getValue();
                return dValue;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
