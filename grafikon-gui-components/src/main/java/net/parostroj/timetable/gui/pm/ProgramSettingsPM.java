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
    final EnumeratedValuesPM<SpeedUnit> speed;
    final EnumeratedValuesPM<LengthUnit> length;
    final BooleanPM doubleSidedPrint = new BooleanPM();
    final BooleanPM generateTitlePage = new BooleanPM();
    final BooleanPM showTechTimes = new BooleanPM();

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
        doubleSidedPrint.setBoolean(settings.isTwoSidedPrint());
        generateTitlePage.setBoolean(settings.isGenerateTitlePageTT());
        showTechTimes.setBoolean(settings.isStShowTechTime());
        user.setText(settings.getUserName());
    }

    private void writeResult() {
        ProgramSettings settings = settingsRef.get();
        if (settings != null) {
            settings.setUserName(user.getText());
            settings.setSpeedUnit(speed.getValue());
            settings.setLengthUnit(length.getValue());
            settings.setTwoSidedPrint(doubleSidedPrint.getBoolean());
            settings.setGenerateTitlePageTT(generateTitlePage.getBoolean());
            settings.setStShowTechTime(showTechTimes.getBoolean());
        }
    }

    @Operation(path = "ok")
    public boolean ok() {
        writeResult();
        return true;
    }
}
