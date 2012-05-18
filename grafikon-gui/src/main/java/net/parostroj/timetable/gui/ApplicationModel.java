package net.parostroj.timetable.gui;

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
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.units.LengthUnit;

/**
 * Application model.
 *
 * @author jub
 */
public class ApplicationModel implements StorableGuiData {
    
    private static final int LAST_OPENED_COUNT = 5;
    
    private Set<ApplicationModelListener> listeners;
    private Train selectedTrain;
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
    private LinkedList<File> lastOpenedFiles;
    
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
        lastOpenedFiles = new LinkedList<File>();
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
            case DELETED_CYCLE: case NEW_CYCLE: case DELETE_TRAIN:
            case MODIFIED_LINE: case MODIFIED_NODE:
            case MODIFIED_TRAIN: case MODIFIED_TRAIN_NAME_TYPE:
            case NEW_TRAIN: case MODIFIED_CYCLE:
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
        if (openedFile != null)
            this.addLastOpenedFile(openedFile);
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
        prefs.setBoolean("two.sided.print", programSettings.isTwoSidedPrint());
        prefs.setBoolean("warning.auto.ec.correction", programSettings.isWarningAutoECCorrection());
        prefs.setString("unit", programSettings.getLengthUnit().getKey());
        prefs.removeWithPrefix("last.opened.");
        int i = 0;
        for (File file : this.lastOpenedFiles) {
            prefs.setString("last.opened." + (i++), file.getAbsolutePath());
        }
    }

    @Override
    public void loadFromPreferences(AppPreferences prefs) {
        deserializeOutputTemplates(prefs.getString("output.templates", ""));
        programSettings.setUserName(prefs.getString("user.name", null));
        programSettings.setGenerateTitlePageTT(prefs.getBoolean("generate.tt.title.page", false));
        programSettings.setTwoSidedPrint(prefs.getBoolean("two.sided.print", false));
        programSettings.setWarningAutoECCorrection(prefs.getBoolean("warning.auto.ec.correction", true));
        LengthUnit lengthUnit = LengthUnit.getByKey(prefs.getString("unit", "mm"));
        programSettings.setLengthUnit(lengthUnit != null ? lengthUnit : LengthUnit.MM);
        for (int i = LAST_OPENED_COUNT - 1; i >= 0; i--) {
            String filename = prefs.getString("last.opened." + i, null);
            if (filename != null) {
                File file = new File(filename);
                if (file.exists())
                    this.addLastOpenedFile(new File(filename));
            }
        }
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

    public LinkedList<File> getLastOpenedFiles() {
        return lastOpenedFiles;
    }

    public void setLastOpenedFiles(LinkedList<File> lastOpenedFiles) {
        this.lastOpenedFiles = lastOpenedFiles;
    }
    
    public void addLastOpenedFile(File file) {
        if (!this.lastOpenedFiles.contains(file)) {
            this.lastOpenedFiles.addFirst(file);
            if (this.lastOpenedFiles.size() > LAST_OPENED_COUNT) {
                File removed = this.lastOpenedFiles.removeLast();
                this.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.REMOVE_LAST_OPENED, this, removed));
            }
        } else {
            this.lastOpenedFiles.remove(file);
            this.lastOpenedFiles.addFirst(file);
        }
        this.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.ADD_LAST_OPENED, this, file));
    }
    
    public void removeLastOpenedFile(File file) {
        if (this.lastOpenedFiles.remove(file)) {
            this.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.REMOVE_LAST_OPENED, this, file));
        }
    }
}
