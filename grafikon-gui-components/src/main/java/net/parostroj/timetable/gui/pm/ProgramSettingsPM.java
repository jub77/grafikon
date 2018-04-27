package net.parostroj.timetable.gui.pm;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import net.parostroj.timetable.gui.data.ProgramSettings;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.SpeedUnit;

import org.beanfabrics.model.*;
import org.beanfabrics.support.Operation;

public class ProgramSettingsPM extends AbstractPM implements IPM<ProgramSettings> {

    final TextPM user = new TextPM();
    final IEnumeratedValuesPM<SpeedUnit> speed;
    final IEnumeratedValuesPM<LengthUnit> length;
    final BooleanPM debugLogging = new BooleanPM();

    final OperationPM ok = new OperationPM();

    private WeakReference<ProgramSettings> settingsRef;

    public ProgramSettingsPM() {
        speed = new EnumeratedValuesPM<>(
                EnumeratedValuesPM.createValueMap(Arrays.asList(SpeedUnit.KMPH, SpeedUnit.MPH), SpeedUnit::toString));
        length = new EnumeratedValuesPM<>(
                EnumeratedValuesPM.createValueMap(LengthUnit.getScaleDependent(), LengthUnit::toString));
        PMManager.setup(this);
    }

    @Override
    public void init(ProgramSettings settings) {
        settingsRef = new WeakReference<>(settings);
        speed.setValue(settings.getSpeedUnit());
        length.setValue(settings.getLengthUnit());
        user.setText(settings.getUserName());
        debugLogging.setBoolean(settings.isDebugLogging());
    }

    private void writeResult() {
        ProgramSettings settings = settingsRef.get();
        if (settings != null) {
            settings.setUserName(user.getText());
            settings.setSpeedUnit(speed.getValue());
            settings.setLengthUnit(length.getValue());
            settings.setDebugLogging(debugLogging.getBoolean());
        }
    }

    @Operation(path = "ok")
    public boolean ok() {
        writeResult();
        return true;
    }
}
