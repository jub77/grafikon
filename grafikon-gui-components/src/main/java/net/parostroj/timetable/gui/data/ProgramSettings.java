package net.parostroj.timetable.gui.data;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainDiagramType;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.SpeedUnit;
import net.parostroj.timetable.utils.ObjectsUtil;

import java.util.function.Supplier;

/**
 * Settings of the program.
 *
 * @author jub
 */
public class ProgramSettings {

    private final Supplier<TrainDiagram> diagram;
    private String userName;
    private LengthUnit lengthUnit;
    private SpeedUnit speedLengthUnit;
    private boolean debugLogging;
    private TrainDiagramType diagramType;
    private boolean webTemplates;

    public ProgramSettings(Supplier<TrainDiagram> diagram) {
        this.diagram = diagram;
    }

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

    public boolean isDebugLogging() {
        return debugLogging;
    }

    public void setDebugLogging(boolean debugLogging) {
        this.debugLogging = debugLogging;
    }

    public void setDiagramType(TrainDiagramType diagramType) {
        TrainDiagram d = diagram.get();
        this.diagramType = diagramType;
        if (d != null) {
            d.getRuntimeInfo().setDiagramType(diagramType);
        }
    }

    public TrainDiagramType getDiagramType() {
        return diagramType;
    }

    public boolean isWebTemplates() {
        return webTemplates;
    }

    public void setWebTemplates(boolean webTemplates) {
        this.webTemplates = webTemplates;
    }
}
