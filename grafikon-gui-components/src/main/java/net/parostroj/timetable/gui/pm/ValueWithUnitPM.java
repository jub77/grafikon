package net.parostroj.timetable.gui.pm;

import net.parostroj.timetable.model.units.Unit;
import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.BigDecimalPM;
import org.beanfabrics.model.BooleanPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.support.OnChange;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Optional;

/**
 * Presentation model of value with unit (automatic conversion).
 */
public class ValueWithUnitPM<T extends Unit> extends AbstractPM {

    final BigDecimalPM value;
    final EnumeratedValuesPM<T> unit;
    final BooleanPM used;

    private Unit lastUnit;

    public ValueWithUnitPM() {
        DecimalFormat format = new  DecimalFormat("#0.########");
        format.setDecimalSeparatorAlwaysShown(false);
        format.setParseBigDecimal(true);

        value = new BigDecimalPM();
        value.setFormat(new BigDecimalPM.Format(format));
        unit = new EnumeratedValuesPM<>();
        used = new BooleanPM();
        PMManager.setup(this);
    }

    public void init(Optional<BigDecimal> value, Collection<T> units) {
        if (units.isEmpty()) {
            throw new IllegalArgumentException("Collection of units cannot be empty");
        }
        this.used.setBoolean(value.isPresent());
        this.value.setBigDecimal(value.orElse(null));
        this.unit.addValues(units, Unit::getUnitsOfString);
        this.unit.setValue(units.stream().findFirst().orElse(null));
        this.enableDisableEditing();
    }

    /**
     * Enable/disable editing.
     */
    @OnChange(path = {"used"})
    public void enableDisableEditing() {
        boolean c = used.getBoolean();
        if (!c) {
            value.setText("");
        } else if (value.getBigDecimal() == null) {
            value.setBigDecimal(BigDecimal.valueOf(0));

        }
        value.setEditable(c);
        unit.setEditable(c);
    }

    /**
     * Recalculate value in case of change of unit.
     */
    @OnChange(path = {"unit"})
    public void recalculateValue() {
        T currentUnit = unit.getValue();
        BigDecimal currentValue = value.getBigDecimal();
        if (currentValue != null && lastUnit != null && currentUnit != null) {
            BigDecimal newValue = currentUnit.convertFrom(currentValue, lastUnit);
            value.setBigDecimal(newValue);
        }
        if (currentUnit != null) {
            lastUnit = currentUnit;
        }
    }

    public Optional<BigDecimal> getValue() {
        return Optional.ofNullable(value.getBigDecimal());
    }

    public Optional<BigDecimal> getValueInUnit(T targetUnit) {
        return Optional.ofNullable(value.getBigDecimal()).map(v -> targetUnit.convertFrom(v, unit.getValue()));
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%b", value.getBigDecimal(), unit.getValue(), used.getBoolean());
    }
}
