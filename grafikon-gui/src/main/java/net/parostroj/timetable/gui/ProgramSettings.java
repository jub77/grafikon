package net.parostroj.timetable.gui;

import net.parostroj.timetable.gui.components.LengthUnit;

/**
 * Settings of the program.
 *
 * @author jub
 */
public class ProgramSettings {

    private String userName;
    private boolean generateTitlePageTT;
    private boolean warningAutoECCorrection;
    private LengthUnit lengthUnit;

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
}
