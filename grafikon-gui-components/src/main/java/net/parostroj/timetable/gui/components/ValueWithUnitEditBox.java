package net.parostroj.timetable.gui.components;

import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import net.parostroj.timetable.model.units.Unit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.SwingConstants;

/**
 * Editing component for length.
 *
 * @author jub
 */
public class ValueWithUnitEditBox extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(ValueWithUnitEditBox.class);
    private BigDecimal value;
    private List<? extends Unit> units;

    /** Creates new form LengthEditBox */
    public ValueWithUnitEditBox() {
        this("#0.########");
    }

    public ValueWithUnitEditBox(String formatStr) {
        initComponents();

        DecimalFormat format = new  DecimalFormat(formatStr);
        format.setDecimalSeparatorAlwaysShown(false);
        format.setParseBigDecimal(true);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(BigDecimal.class);
        formatter.setMinimum(new BigDecimal(0));
        valueTextField.setFormatterFactory(new DefaultFormatterFactory(formatter));
        valueTextField.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);

        setValue(new BigDecimal(0));
    }

    public void setUnits(List<? extends Unit> units) {
        this.units = units;
        // fill combo box
        unitComboBox.removeAllItems();
        for (Unit unit : units)
            unitComboBox.addItem(unit);
    }

    public List<? extends Unit> getUnits() {
        return this.units;
    }

    public void setValueColumns(int length) {
        valueTextField.setColumns(length);
    }

    public int getValueColumns() {
        return valueTextField.getColumns();
    }

    public BigDecimal getValueInUnit(Unit unit) {
        return unit.convertFrom(getValue(), getUnit());
    }

    public void setValueInUnit(BigDecimal dValue, Unit unit) {
        this.setValue(unit.convertTo(dValue, getUnit()));
    }

    public BigDecimal getValue() {
        return getValueImpl();
    }

    private BigDecimal getValueImpl() {
        // convert to double
        try {
            valueTextField.commitEdit();
            BigDecimal dValue = this.getValueFromField();
            return dValue;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            setValueImpl(this.value);
            return this.value;
        }
    }

    public void setValue(BigDecimal value) {
        this.value = value;
        setValueImpl(value);
    }

    private void setValueImpl(BigDecimal dValue) {
        this.setValueToField(dValue);
        valueTextField.setCaretPosition(0);
    }

    public Unit getUnit() {
        return (Unit) unitComboBox.getSelectedItem();
    }

    public void setUnit(Unit unit) {
        unitComboBox.setSelectedItem(unit);
    }

    private BigDecimal getValueFromField() {
        return (BigDecimal) valueTextField.getValue();
    }

    private void setValueToField(BigDecimal dValue) {
        valueTextField.setValue(dValue);
    }

    @Override
    public void setEnabled(boolean enabled) {
        unitComboBox.setEnabled(enabled);
        valueTextField.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    private void initComponents() {
        valueTextField = new javax.swing.JFormattedTextField();
        valueTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        unitComboBox = new javax.swing.JComboBox<Unit>();

        setLayout(new java.awt.BorderLayout());
        add(valueTextField, java.awt.BorderLayout.CENTER);

        unitComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                unitComboBoxItemStateChanged(evt);
            }
        });
        add(unitComboBox, java.awt.BorderLayout.LINE_END);
    }

    private Unit deselected;

    private void unitComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.DESELECTED) {
            deselected = (Unit) evt.getItem();
        } else {
            if (deselected != null) {
                Unit selected = (Unit) evt.getItem();
                // conversion
                BigDecimal dValue = getValueImpl();
                dValue = selected.convertFrom(dValue, deselected);
                setValueImpl(dValue);
                deselected = null;
            }
        }
    }

    private javax.swing.JComboBox<Unit> unitComboBox;
    private javax.swing.JFormattedTextField valueTextField;
}
