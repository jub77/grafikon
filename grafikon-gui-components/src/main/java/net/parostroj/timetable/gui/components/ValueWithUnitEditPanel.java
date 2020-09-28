package net.parostroj.timetable.gui.components;

import net.parostroj.timetable.gui.pm.ValueWithUnitPM;
import net.parostroj.timetable.model.units.Unit;
import org.beanfabrics.Path;
import org.beanfabrics.swing.BnCheckBox;
import org.beanfabrics.swing.BnTextField;

import javax.swing.*;
import java.awt.*;


/**
 * Panel for editing value with unit.
 *
 * @param <T> unit type
 */
public class ValueWithUnitEditPanel<T extends Unit> extends BaseEditPanel<ValueWithUnitPM<T>> {
    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_COLUMNS = 10;
    private static final String DEFAULT_UNIT_PROTOTYPE = "MMMMMM";

    public ValueWithUnitEditPanel() {
        this(DEFAULT_COLUMNS, DEFAULT_UNIT_PROTOTYPE);
    }

    public ValueWithUnitEditPanel(int columns, String unitPrototype) {
        BnTextField valueTextField = new BnTextField();
        valueTextField.setColumns(columns);
        valueTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        valueTextField.setModelProvider(localProvider);
        valueTextField.setPath(new Path("value"));

        BnComboBox unitComboBox = new BnComboBox();
        unitComboBox.setPrototypeDisplayValue(unitPrototype);
        unitComboBox.setModelProvider(localProvider);
        unitComboBox.setPath(new Path("unit"));

        BnCheckBox usedCheckBox = new BnCheckBox();
        usedCheckBox.setModelProvider(localProvider);
        usedCheckBox.setPath(new Path("used"));

        this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.add(valueTextField);
        this.add(unitComboBox);
        this.add(Box.createHorizontalStrut(3));
        this.add(usedCheckBox);
    }
}
