package net.parostroj.timetable.gui.data;

public class OutputSettings {

    private boolean generateTitlePageTT;
    private boolean twoSidedPrint;
    private boolean stShowTechTime;

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
