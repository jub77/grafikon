package net.parostroj.timetable.gui.data;

import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.SpeedUnit;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Settings of the program.
 *
 * @author jub
 */
public class ProgramSettings {

    private String userName;
    private LengthUnit lengthUnit;
    private SpeedUnit speedLengthUnit;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = ObjectsUtil.checkAndTrim(userName);
    }

    public String getUserNameOrSystemUser() {
        if (userName != null) {
            return userName;
        } else {
            return getSystemUser();
        }
    }

    public String getSystemUser() {
        return System.getProperty("user.name");
    }

    public LengthUnit getLengthUnit() {
        return lengthUnit;
    }

    public void setLengthUnit(LengthUnit lengthUnit) {
        this.lengthUnit = lengthUnit;
    }

    public SpeedUnit getSpeedUnit() {
        return speedLengthUnit;
    }

    public void setSpeedUnit(SpeedUnit speedLengthUnit) {
        this.speedLengthUnit = speedLengthUnit;
    }
}
