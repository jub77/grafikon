package net.parostroj.timetable.gui;

import java.io.File;
import java.util.*;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.PMManager;
import org.ini4j.Ini;

import net.parostroj.timetable.actions.scripts.ScriptsLoader;
import net.parostroj.timetable.gui.actions.impl.OutputCategory;
import net.parostroj.timetable.gui.commands.Command;
import net.parostroj.timetable.gui.commands.CommandException;
import net.parostroj.timetable.gui.data.OutputSettings;
import net.parostroj.timetable.gui.data.ProgramSettings;
import net.parostroj.timetable.gui.pm.OutputSettingsPM;
import net.parostroj.timetable.mediator.Mediator;
import net.parostroj.timetable.mediator.TrainDiagramCollegue;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.SpeedUnit;
import net.parostroj.timetable.utils.Reference;

/**
 * Application model.
 *
 * @author jub
 */
public class ApplicationModel extends AbstractPM implements StorableGuiData, Reference<TrainDiagram> {

    private static final int LAST_OPENED_COUNT = 5;

    private final Set<ApplicationModelListener> listeners;
    private Train selectedTrain;
    private TrainDiagram diagram;
    private boolean modelChanged;
    private File openedFile;
    private final Mediator mediator;
    private final TrainDiagramCollegue collegue;
    private OutputCategory outputCategory;
    private final Map<String, File> outputTemplates;
    private ProgramSettings programSettings;
    private final OutputSettings outputSettings;
    private LinkedList<File> lastOpenedFiles;
    private final ScriptsLoader psLoader;
    private final ScriptsLoader guiPsLoader;

    final OutputSettingsPM outputSettingsPM = new OutputSettingsPM();

    /**
     * Default constructor.
     */
    public ApplicationModel() {
        listeners = new HashSet<ApplicationModelListener>();
        mediator = new Mediator();
        collegue = new TrainDiagramCollegue();
        mediator.addColleague(collegue);
        mediator.addColleague(new ApplicationModelColleague(this));
        outputTemplates = new HashMap<String, File>();
        programSettings = new ProgramSettings();
        outputSettings = new OutputSettings();
        lastOpenedFiles = new LinkedList<File>();
        psLoader = ScriptsLoader.newDefaultScriptsLoader();
        guiPsLoader = ScriptsLoader.newScriptsLoader("gui_scripts");
        PMManager.setup(this);
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
        if (modelChanged == true && this.modelChanged == false) {
            this.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODEL_CHANGED, this));
        }
        this.modelChanged = modelChanged;
    }

    private void checkModelChanged(ApplicationModelEvent event) {
        switch(event.getType()) {
            case SET_DIAGRAM_CHANGED:
                this.setModelChanged(false);
                break;
            case MODEL_SAVED:
                this.setModelChanged(false);
                break;
            default:
                // nothing changed for other events
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

    @Override
    public Ini.Section saveToPreferences(Ini prefs) {
        Ini.Section section = AppPreferences.getSection(prefs, "model");
        section.put("output.templates", getSerializedOutputTemplates());
        section.put("user.name", programSettings.getUserName());
        section.put("generate.tt.title.page", outputSettings.isGenerateTitlePageTT());
        section.put("two.sided.print", outputSettings.isTwoSidedPrint());
        section.put("st.show.tech.time", outputSettings.isStShowTechTime());
        section.put("unit", programSettings.getLengthUnit() != null ? programSettings.getLengthUnit().getKey() : null);
        section.put("unit.speed", programSettings.getSpeedUnit() != null ? programSettings.getSpeedUnit().getKey() : null);
        section.remove("last.opened");
        for (File file : this.lastOpenedFiles) {
            section.add("last.opened", file.getAbsolutePath());
        }
        return section;
    }

    @Override
    public Ini.Section loadFromPreferences(Ini prefs) {
        Ini.Section section = AppPreferences.getSection(prefs, "model");
        deserializeOutputTemplates(section.get("output.templates", ""));
        programSettings.setUserName(section.get("user.name"));
        outputSettings.setGenerateTitlePageTT(section.get("generate.tt.title.page", Boolean.class, false));
        outputSettings.setTwoSidedPrint(section.get("two.sided.print", Boolean.class, false));
        outputSettings.setStShowTechTime(section.get("st.show.tech.time", Boolean.class, false));
        LengthUnit lengthUnit = LengthUnit.getByKey(section.get("unit", "mm"));
        SpeedUnit speedUnit = SpeedUnit.getByKey(section.get("unit.speed", "kmph"));
        programSettings.setLengthUnit(lengthUnit != null ? lengthUnit : LengthUnit.MM);
        programSettings.setSpeedUnit(speedUnit != null ? speedUnit : SpeedUnit.KMPH);
        List<String> filenames = section.getAll("last.opened");
        if (filenames != null) {
            Collections.reverse(filenames);
            for (String filename : filenames) {
                if (filename != null) {
                    this.addLastOpenedFile(new File(filename));
                }
            }
        }
        outputSettingsPM.init(outputSettings);
        return section;
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

    public OutputSettings getOutputSettings() {
        return outputSettings;
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

    @Override
    public TrainDiagram get() {
        return getDiagram();
    }

    @Override
    public void set(TrainDiagram object) {
        setDiagram(object);
    }

    public ScriptsLoader getScriptsLoader() {
        return psLoader;
    }

    public ScriptsLoader getGuiScriptsLoader() {
        return guiPsLoader;
    }
}
