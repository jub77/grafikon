package net.parostroj.timetable.gui;

import net.parostroj.timetable.model.units.LengthUnit;

/**
 * Settings of the program.
 *
 * @author jub
 */
public class ProgramSettings {

    private String userName;
    private boolean generateTitlePageTT;
    private boolean warningAutoECCorrection;
    private boolean twoSidedPrint;
    private boolean stShowTechTime;
    private LengthUnit lengthUnit;
    private LengthUnit speedLengthUnit;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNameOrSystemUser() {
        if (userName != null)
            return userName;
        else
            return getSystemUser();
    }

    public String getSystemUser() {
        return System.getProperty("user.name");
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

    public boolean isWarningAutoECCorrection() {
        return warningAutoECCorrection;
    }

    public void setWarningAutoECCorrection(boolean warningAutoECCorrection) {
        this.warningAutoECCorrection = warningAutoECCorrection;
    }

    public LengthUnit getLengthUnit() {
        return lengthUnit;
    }

    public void setLengthUnit(LengthUnit lengthUnit) {
        this.lengthUnit = lengthUnit;
    }

    public LengthUnit getSpeedLengthUnit() {
        return speedLengthUnit;
    }

    public void setSpeedLengthUnit(LengthUnit speedLengthUnit) {
        this.speedLengthUnit = speedLengthUnit;
    }

    public boolean isStShowTechTime() {
        return stShowTechTime;
    }

    public void setStShowTechTime(boolean stShowTechTime) {
        this.stShowTechTime = stShowTechTime;
    }
}
