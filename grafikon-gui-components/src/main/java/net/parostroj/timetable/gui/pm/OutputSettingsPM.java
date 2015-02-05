package net.parostroj.timetable.gui.pm;

import net.parostroj.timetable.gui.data.ProgramSettings;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.BooleanPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.support.OnChange;

public class OutputSettingsPM extends AbstractPM {

    final BooleanPM doubleSidedPrint = new BooleanPM();
    final BooleanPM generateTitlePage = new BooleanPM();
    final BooleanPM showTechTimes = new BooleanPM();

    private ProgramSettings settings;

    public OutputSettingsPM() {
        PMManager.setup(this);
    }

    public OutputSettingsPM init(ProgramSettings settings) {
        this.doubleSidedPrint.setBoolean(settings.isTwoSidedPrint());
        this.generateTitlePage.setBoolean(settings.isGenerateTitlePageTT());
        this.showTechTimes.setBoolean(settings.isStShowTechTime());
        this.settings = settings;
        return this;
    }

    @OnChange(path = "doubleSidedPrint")
    public void changedDoubleSided() {
        if (this.settings != null) {
            System.out.println("Double - changed");
            this.settings.setTwoSidedPrint(doubleSidedPrint.getBoolean());
        }
    }

    @OnChange(path = "generateTitlePage")
    public void changedGenerateTitlePage() {
        if (this.settings != null) {
            System.out.println("Generate - changed");
            this.settings.setGenerateTitlePageTT(generateTitlePage.getBoolean());
        }
    }

    @OnChange(path = "showTechTimes")
    public void changedShowTechTimes() {
        if (this.settings != null) {
            System.out.println("Show tech - changed");
            this.settings.setStShowTechTime(showTechTimes.getBoolean());
        }
    }
}
