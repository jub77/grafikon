package net.parostroj.timetable.gui;

/**
 * Settings of the program.
 *
 * @author jub
 */
public class ProgramSettings {

    private String userName;

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
}
