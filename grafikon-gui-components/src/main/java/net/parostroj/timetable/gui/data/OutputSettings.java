package net.parostroj.timetable.gui.data;

import java.util.Locale;

public class OutputSettings {

    private boolean generateTitlePageTT;
    private boolean twoSidedPrint;
    private boolean stShowTechTime;
    private Locale locale;

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale outputLocale) {
        this.locale = outputLocale;
    }

    public boolean isGenerateTitlePageTT() {
        return generateTitlePageTT;
    }

    public void setGenerateTitlePageTT(boolean generateTitlePageTT) {
        this.generateTitlePageTT = generateTitlePageTT;
    }

    public boolean isTwoSidedPrint() {
        return twoSidedPrint;
    }

    public void setTwoSidedPrint(boolean twoSidedPrint) {
        this.twoSidedPrint = twoSidedPrint;
    }

    public boolean isStShowTechTime() {
        return stShowTechTime;
    }

    public void setStShowTechTime(boolean stShowTechTime) {
        this.stShowTechTime = stShowTechTime;
    }
}
