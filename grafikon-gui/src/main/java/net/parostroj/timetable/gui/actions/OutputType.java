package net.parostroj.timetable.gui.actions;

/**
 * Output type. For each menu item there is enum item with information.
 *
 * @author jub
 */
public enum OutputType {

    STARTS("starts", "starts", true), ENDS("ends", "ends", true), STATIONS("stations", "stations", true),
    STATIONS_SELECT("stations_select", "stations", true, ElementType.NODE, "stations"),
    TRAIN_UNIT_CYCLES("train_unit_cycles", "train_unit_cycles", true),
    TRAIN_UNIT_CYCLES_SELECT("train_unit_cycles_select", "train_unit_cycles", true, ElementType.TRAIN_UNIT_CYCLE, "cycles"),
    ENGINE_CYCLES("engine_cycles", "engine_cycles", true),
    ENGINE_CYCLES_SELECT("engine_cycles_select", "engine_cycles", true, ElementType.ENGINE_CYCLE, "cycles"),
    DRIVER_CYCLES("driver_cycles", "driver_cycles", true),
    DRIVER_CYCLES_SELECT("driver_cycles_select", "driver_cycles", true, ElementType.DRIVER_CYCLE, "cycles"),
    TRAINS("trains", "trains", true),
    TRAINS_SELECT_DRIVER_CYCLES("trains_select_driver_cycles", "trains", false, ElementType.DRIVER_CYCLE, "driver_cycle"),
    TRAINS_SELECT_STATION("trains_select_station", "trains", true, null, "station"),
    TRAINS_BY_DRIVER_CYCLES("trains_by_driver_cycles", "trains", false, null, "driver_cycle"),
    ALL("all", null, false, null, null);

    private String actionCommand;
    private String outputType;
    private ElementType selectionElement;
    private String selectionParam;
    private boolean outputFile;
    
    private OutputType(String actionCommand, String outputType, boolean outputFile) {
        this.actionCommand = actionCommand;
        this.outputType = outputType;
        this.outputFile = outputFile;
    }

    private OutputType(String actionCommand, String outputType, boolean outputFile, ElementType selectionElement, String selectionParam) {
        this(actionCommand, outputType, outputFile);
        this.selectionElement = selectionElement;
        this.selectionParam = selectionParam;
    }

    public String getActionCommand() {
        return actionCommand;
    }

    public String getOutputType() {
        return outputType;
    }

    public boolean isSelection() {
        return selectionElement != null;
    }

    public ElementType getSelectionElement() {
        return selectionElement;
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
