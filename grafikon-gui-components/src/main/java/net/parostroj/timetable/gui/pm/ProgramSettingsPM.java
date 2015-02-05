package net.parostroj.timetable.gui.pm;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import net.parostroj.timetable.gui.data.ProgramSettings;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.SpeedUnit;

import org.beanfabrics.model.*;
import org.beanfabrics.support.Operation;

public class ProgramSettingsPM extends AbstractPM {

    final TextPM user = new TextPM();
    final IEnumeratedValuesPM<SpeedUnit> speed;
    final IEnumeratedValuesPM<LengthUnit> length;

    final OperationPM ok = new OperationPM();

    private WeakReference<ProgramSettings> settingsRef;

    public ProgramSettingsPM() {
        speed = new EnumeratedValuesPM<SpeedUnit>(Arrays.asList(SpeedUnit.KMPH, SpeedUnit.MPH), i -> i.toString());
        length = new EnumeratedValuesPM<LengthUnit>(LengthUnit.getScaleDependent(), i -> i.toString());
        PMManager.setup(this);
    }

    public void init(ProgramSettings settings) {
        settingsRef = new WeakReference<ProgramSettings>(settings);
        speed.setValue(settings.getSpeedUnit());
        length.setValue(settings.getLengthUnit());
        user.setText(settings.getUserName());
    }

    private void writeResult() {
        ProgramSettings settings = settingsRef.get();
        if (settings != null) {
            settings.setUserName(user.getText());
            settings.setSpeedUnit(speed.getValue());
            settings.setLengthUnit(length.getValue());
        }
    }

    @Operation(path = "ok")
    public boolean ok() {
        writeResult();
        return true;
    }
}
