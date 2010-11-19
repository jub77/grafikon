package net.parostroj.timetable.gui;

import net.parostroj.timetable.actions.AfterLoadCheck;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import net.parostroj.timetable.gui.actions.OutputCategory;
import net.parostroj.timetable.gui.commands.Command;
import net.parostroj.timetable.gui.commands.CommandException;
import net.parostroj.timetable.mediator.Mediator;
import net.parostroj.timetable.mediator.TrainDiagramCollegue;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.units.LengthUnit;

/**
 * Application model.
 *
 * @author jub
 */
public class ApplicationModel implements StorableGuiData {
    
    private Set<ApplicationModelListener> listeners;
    private Train selectedTrain;
    private TrainsCycle selectedEngineCycle;
    private TrainsCycle selectedDriverCycle;
    private TrainsCycle selectedTrainUnitCycle;
    private TrainDiagram diagram;
    private Queue<Command> commandQueue;
    private boolean modelChanged;
    private File openedFile;
    private Mediator mediator;
    private TrainDiagramCollegue collegue;
    private OutputCategory outputCategory;
    private Map<String, File> outputTemplates;
    private Locale outputLocale;
    private ProgramSettings programSettings;
    
    /**
     * Default constructor.
     */
    public ApplicationModel() {
        listeners = new HashSet<ApplicationModelListener>();
        commandQueue = new LinkedList<Command>();
        mediator = new Mediator();
        collegue = new TrainDiagramCollegue();
        mediator.addColleague(collegue);
        mediator.addColleague(new ApplicationModelColleague(this));
        outputTemplates = new HashMap<String, File>();
        programSettings = new ProgramSettings();
    }

    /**
     * @return selected train
     */
    public Train getSelectedTrain() {
        return selectedTrain;
    }

    /**
     * sets selected train and generates event.
     * 
     * @param selectedTrain train
     */
    public void setSelectedTrain(Train selectedTrain) {
        if (this.selectedTrain != selectedTrain) {
            this.selectedTrain = selectedTrain;

            this.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.SELECTED_TRAIN_CHANGED,this,selectedTrain));
        }
    }

    public OutputCategory getOutputCategory() {
        return outputCategory;
    }

    public void setOutputCategory(OutputCategory outputCategory) {
        this.outputCategory = outputCategory;
    }

    public TrainsCycle getSelectedEngineCycle() {
        return selectedEngineCycle;
    }

    public void setSelectedEngineCycle(TrainsCycle selectedEngineCycle) {
        this.selectedEngineCycle = selectedEngineCycle;

        this.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.SELECTED_ENGINE_CYCLE_CHANGED,this,selectedEngineCycle));
    }

    public TrainsCycle getSelectedDriverCycle() {
        return selectedDriverCycle;
    }

    public void setSelectedDriverCycle(TrainsCycle selectedDriverCycle) {
        this.selectedDriverCycle = selectedDriverCycle;

        this.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.SELECTED_DRIVER_CYCLE_CHANGED,this,selectedDriverCycle));
    }

    public TrainsCycle getSelectedTrainUnitCycle() {
        return selectedTrainUnitCycle;
    }

    public void setSelectedTrainUnitCycle(TrainsCycle selectedTrainUnitCycle) {
        this.selectedTrainUnitCycle = selectedTrainUnitCycle;

        this.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.SELECTED_TRAIN_UNIT_CYCLE_CHANGED,this,selectedTrainUnitCycle));
    }

    /**
     * @return train diagram
     */
    public TrainDiagram getDiagram() {
        return diagram;
    }

    public Mediator getMediator() {
        return mediator;
    }

    /**
     * sets train diagram and generates event.
     * 
     * @param diagram train diagram
     */
    public void setDiagram(TrainDiagram diagram) {
        // set selected train to null
        this.setSelectedTrain(null);
        this.setSelectedEngineCycle(null);
        this.setSelectedDriverCycle(null);
        this.setSelectedTrainUnitCycle(null);

        this.diagram = diagram;

        this.collegue.setTrainDiagram(diagram);
        this.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.SET_DIAGRAM_CHANGED,this));
    }

    /**
     * adds application model listener.
     * 
     * @param listener listener
     */
    public void addListener(ApplicationModelListener listener) {
        listeners.add(listener);
    }
    
    /**
     * removes application model listener.
     * 
     * @param listener listener
     */
    public void removeListener(ApplicationModelListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * fires specified event for this model.
     * 
     * @param event event to be fired
     */
    public void fireEvent(ApplicationModelEvent event) {
        for (ApplicationModelListener listener : listeners)
            listener.modelChanged(event);
        this.checkModelChanged(event);
    }
    
    /**
     * executes command and adds successfully executed command to
     * command queue (suppocrt for undo).
     * 
     * @param command command to be executed
     * @throws net.parostroj.timetable.gui.commands.CommandException 
     */
    public void applyCommand(Command command) throws CommandException {
        // execute command
        command.execute(this);
        
        // add to queue
        commandQueue.add(command);
        
        // check queue length
        if (commandQueue.size() > 100)
            commandQueue.remove();
    }

    /**
     * @return if the model is changed
     */
    public boolean isModelChanged() {
        return modelChanged;
    }

    /**
     * @param modelChanged model is changed
     */
    public void setModelChanged(boolean modelChanged) {
        if (modelChanged == true && this.modelChanged == false)
            this.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODEL_CHANGED, this));
        this.modelChanged = modelChanged;
    }

    private void checkModelChanged(ApplicationModelEvent event) {
        switch(event.getType()) {
            case DELETE_DRIVER_CYCLE: case DELETE_ENGINE_CYCLE: case DELETE_TRAIN: case DELETE_TRAIN_UNIT_CYCLE:
            case MODIFIED_DRIVER_CYCLE: case MODIFIED_ENGINE_CYCLE: case MODIFIED_LINE: case MODIFIED_NODE:
            case MODIFIED_TRAIN: case MODIFIED_TRAIN_NAME_TYPE: case MODIFIED_TRAIN_UNIT_CYCLE:
            case NEW_DRIVER_CYCLE: case NEW_ENGINE_CYCLE: case NEW_TRAIN: case NEW_TRAIN_UNIT_CYCLE:
            case ROUTES_MODIFIED: case NEW_LINE: case NEW_NODE: case MODIFIED_TRAIN_ATTRIBUTE:
            case TRAIN_TYPES_CHANGED: case LINE_CLASSES_CHANGED: case ENGINE_CLASSES_CHANGED: case DELETE_LINE:
            case DELETE_NODE:
                this.setModelChanged(true);
                break;
            case SET_DIAGRAM_CHANGED:
                this.setModelChanged(false);
                break;
            case MODEL_SAVED:
                this.setModelChanged(false);
                break;
        }
    }

    public File getOpenedFile() {
        return openedFile;
    }

    public void setOpenedFile(File openedFile) {
        this.openedFile = openedFile;
    }

    public Map<String, File> getOutputTemplates() {
        return outputTemplates;
    }

    public Locale getOutputLocale() {
        return outputLocale;
    }

    public void setOutputLocale(Locale outputLocale) {
        this.outputLocale = outputLocale;
    }

    @Override
    public void saveToPreferences(AppPreferences prefs) {
        prefs.setString("output.templates", getSerializedOutputTemplates());
        if (programSettings.getUserName() != null)
            prefs.setString("user.name", programSettings.getUserName());
        else
            prefs.remove("user.name");
        prefs.setBoolean("generate.tt.title.page", programSettings.isGenerateTitlePageTT());
        prefs.setBoolean("warning.auto.ec.correction", programSettings.isWarningAutoECCorrection());
        prefs.setString("unit", programSettings.getLengthUnit().getKey());
    }

    @Override
    public void loadFromPreferences(AppPreferences prefs) {
        deserializeOutputTemplates(prefs.getString("output.templates", ""));
        programSettings.setUserName(prefs.getString("user.name", null));
        programSettings.setGenerateTitlePageTT(prefs.getBoolean("generate.tt.title.page", false));
        programSettings.setWarningAutoECCorrection(prefs.getBoolean("warning.auto.ec.correction", true));
        programSettings.setLengthUnit(LengthUnit.getByKey(prefs.getString("unit", "mm")));
    }

    private String getSerializedOutputTemplates() {
        StringBuilder b = new StringBuilder();
        for (Map.Entry<String, File> entry : outputTemplates.entrySet()) {
            if (b.length() != 0)
                b.append('|');
            b.append(entry.getKey());
            b.append(',');
            b.append(entry.getValue().getPath());
        }
        return b.toString();
    }

    private void deserializeOutputTemplates(String string) {
        String entries[] = string.split("\\|");
        for (String entry : entries) {
            if (entry.equals(""))
                continue;
            String[] parts = entry.split(",");
            outputTemplates.put(parts[0], new File(parts[1]));
        }
    }

    public ProgramSettings getProgramSettings() {
        return programSettings;
    }

    public void setProgramSettings(ProgramSettings programSettings) {
        this.programSettings = programSettings;
    }
}
