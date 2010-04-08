package net.parostroj.timetable.gui.actions;

import net.parostroj.timetable.model.Node;

/**
 * Output type. For each menu item there is enum item with information.
 *
 * @author jub
 */
public enum OutputType {

    STARTS("starts", "starts", true), ENDS("ends", "ends", true), STATIONS("stations", "stations", true),
    STATIONS_SELECT("stations_select", "stations", true, Node.class, "stations");

    private String actionCommand;
    private String outputType;
    private Class<?> selectionClass;
    private String selectionParam;
    private boolean outputFile;
    
    private OutputType(String actionCommand, String outputType, boolean outputFile) {
        this.actionCommand = actionCommand;
        this.outputType = outputType;
        this.outputFile = outputFile;
    }

    private OutputType(String actionCommand, String outputType, boolean outputFile, Class<?> selectionClass, String selectionParam) {
        this(actionCommand, outputType, outputFile);
        this.selectionClass = selectionClass;
        this.selectionParam = selectionParam;
    }

    public String getActionCommand() {
        return actionCommand;
    }

    public String getOutputType() {
        return outputType;
    }

    public boolean isSelection() {
        return selectionClass != null;
    }

    public Class<?> getSelectionClass() {
        return selectionClass;
    }

    public String getSelectionParam() {
        return selectionParam;
    }

    public boolean isOutputFile() {
        return outputFile;
    }

    public static OutputType fromActionCommand(String actionCommand) {
        for (OutputType type : values()) {
            if (type.getActionCommand().equals(actionCommand))
                return type;
        }
        return null;
    }
}
