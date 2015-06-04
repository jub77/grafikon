package net.parostroj.timetable.gui.pm;

import net.parostroj.timetable.gui.data.OutputSettings;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.BooleanPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.support.OnChange;

public class OutputSettingsPM extends AbstractPM implements IPM<OutputSettings> {

    final BooleanPM doubleSidedPrint = new BooleanPM();
    final BooleanPM generateTitlePage = new BooleanPM();
    final BooleanPM showTechTimes = new BooleanPM();

    private OutputSettings settings;

    public OutputSettingsPM() {
        PMManager.setup(this);
    }

    public void init(OutputSettings settings) {
        this.doubleSidedPrint.setBoolean(settings.isTwoSidedPrint());
        this.generateTitlePage.setBoolean(settings.isGenerateTitlePageTT());
        this.showTechTimes.setBoolean(settings.isStShowTechTime());
        this.settings = settings;
    }

    @OnChange(path = "doubleSidedPrint")
    public void changedDoubleSided() {
        if (this.settings != null) {
            this.settings.setTwoSidedPrint(doubleSidedPrint.getBoolean());
        }
    }

    @OnChange(path = "generateTitlePage")
    public void changedGenerateTitlePage() {
        if (this.settings != null) {
            this.settings.setGenerateTitlePageTT(generateTitlePage.getBoolean());
        }
    }

    @OnChange(path = "showTechTimes")
    public void changedShowTechTimes() {
        if (this.settings != null) {
            this.settings.setStShowTechTime(showTechTimes.getBoolean());
        }
    }
}
