package net.parostroj.timetable.gui;

import de.skuzzle.semantic.Version;
import java.io.File;
import java.util.*;

import javax.swing.UIManager;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.PMManager;

import net.parostroj.timetable.actions.scripts.ScriptsLoader;
import net.parostroj.timetable.gui.actions.UrlConstants;
import net.parostroj.timetable.gui.data.OutputSettings;
import net.parostroj.timetable.gui.data.ProgramSettings;
import net.parostroj.timetable.gui.ini.IniConfig;
import net.parostroj.timetable.gui.ini.IniConfigSection;
import net.parostroj.timetable.gui.ini.StorableGuiData;
import net.parostroj.timetable.gui.pm.EnumeratedValuesPM;
import net.parostroj.timetable.gui.pm.IEnumeratedValuesPM;
import net.parostroj.timetable.gui.utils.LanguageLoader;
import net.parostroj.timetable.gui.utils.LanguageLoader.LanguagesType;
import net.parostroj.timetable.mediator.Mediator;
import net.parostroj.timetable.mediator.TrainDiagramCollegue;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.SpeedUnit;
import net.parostroj.timetable.utils.Reference;
import net.parostroj.timetable.utils.ResourceLoader;
import net.parostroj.timetable.utils.VersionInfo;

/**
 * Application model.
 *
 * @author jub
 */
public class ApplicationModel extends AbstractPM implements StorableGuiData, Reference<TrainDiagram> {

    private static final int LAST_OPENED_COUNT = 5;

    private static final String DEBUG_SECTION = "debug";
    private static final String DEBUG_KEY = "debug";
    private static final String LAST_OPENED_KEY = "last.opened";

    private final Set<ApplicationModelListener> listeners;
    private Train selectedTrain;
    private TrainDiagram diagram;
    private boolean modelChanged;
    private File openedFile;
    private final Mediator mediator;
    private final TrainDiagramCollegue collegue;
    private final ProgramSettings programSettings;
    private final OutputSettings outputSettings;
    private final LinkedList<File> lastOpenedFiles;
    private final ScriptsLoader psLoader;
    private final ScriptsLoader guiPsLoader;
    private final LanguageLoader languageLoader;
    private final Version currentVersion;
    private final VersionInfo versionInfo;

    final IEnumeratedValuesPM<Locale> locale;
    final IEnumeratedValuesPM<String> lookAndFeel;

    final GuiContextImpl guiContext;

    /**
     * Default constructor.
     */
    public ApplicationModel() {
        versionInfo = new VersionInfo();
        currentVersion = versionInfo.getVersion();
        guiContext = new GuiContextImpl();
        languageLoader = LanguageLoader.getInstance();
        listeners = new HashSet<>();
        mediator = new Mediator();
        collegue = new TrainDiagramCollegue();
        mediator.addColleague(collegue);
        mediator.addColleague(new ApplicationModelColleague(this));
        programSettings = new ProgramSettings();
        outputSettings = new OutputSettings();
        lastOpenedFiles = new LinkedList<>();
        psLoader = ScriptsLoader.newDefaultScriptsLoader();
        guiPsLoader = ScriptsLoader.newScriptsLoader("gui_scripts");
        List<Locale> guiLocales = languageLoader.getLocales(LanguagesType.GUI);
        final Map<Locale, String> localeMap = languageLoader.createMap(guiLocales, "system");
        locale = new EnumeratedValuesPM<>(localeMap);
        final Map<String, String> lookAndFeelMap = getLookAndFeelMap();
        lookAndFeel = new EnumeratedValuesPM<>(lookAndFeelMap);
        PMManager.setup(this);
    }

    private Map<String, String> getLookAndFeelMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("system", ResourceLoader.getString("menu.lookandfeel.system"));
        for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
            map.put(laf.getClassName(), laf.getName());
        }
        return map;
    }

    public GuiContext getGuiContext() {
        return guiContext;
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
     * @return if the model is changed
     */
    public boolean isModelChanged() {
        return modelChanged;
    }

    /**
     * @param modelChanged model is changed
     */
    public void setModelChanged(boolean modelChanged) {
        if (modelChanged && !this.modelChanged) {
            this.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODEL_CHANGED, this));
        }
        this.modelChanged = modelChanged;
    }

    private void checkModelChanged(ApplicationModelEvent event) {
        switch(event.getType()) {
            case SET_DIAGRAM_CHANGED:
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

    @Override
    public IniConfigSection saveToPreferences(IniConfig prefs) {
        IniConfigSection section = prefs.getSection("model");
        section.put("user.name", programSettings.getUserName());
        prefs.getSection(DEBUG_SECTION).put(DEBUG_KEY, programSettings.isDebugLogging());
        section.put("unit", programSettings.getLengthUnit() != null ? programSettings.getLengthUnit().getKey() : null);
        section.put("unit.speed", programSettings.getSpeedUnit() != null ? programSettings.getSpeedUnit().getKey() : null);
        section.remove(LAST_OPENED_KEY);
        for (File file : this.lastOpenedFiles) {
            section.add(LAST_OPENED_KEY, file.getAbsolutePath());
        }
        guiContext.saveToPreferences(prefs);
        return section;
    }

    @Override
    public IniConfigSection loadFromPreferences(IniConfig prefs) {
        guiContext.loadFromPreferences(prefs);
        IniConfigSection section = prefs.getSection("model");
        programSettings.setUserName(section.get("user.name"));
        programSettings.setDebugLogging(prefs.getSection(DEBUG_SECTION).get(DEBUG_KEY, Boolean.class, false));
        LengthUnit lengthUnit = LengthUnit.getByKey(section.get("unit", "mm"));
        SpeedUnit speedUnit = SpeedUnit.getByKey(section.get("unit.speed", "kmph"));
        programSettings.setLengthUnit(lengthUnit != null ? lengthUnit : LengthUnit.MM);
        programSettings.setSpeedUnit(speedUnit != null ? speedUnit : SpeedUnit.KMPH);
        List<String> filenames = section.getAll(LAST_OPENED_KEY);
        if (filenames != null) {
            Collections.reverse(filenames);
            for (String filename : filenames) {
                if (filename != null) {
                    this.addLastOpenedFile(new File(filename));
                }
            }
        }
        return section;
    }

    public ProgramSettings getProgramSettings() {
        return programSettings;
    }

    public OutputSettings getOutputSettings() {
        return outputSettings;
    }

    public void setLocale(Locale locale) {
        this.locale.setValue(locale);
    }

    public Locale getLocale() {
        return this.locale.getValue();
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

    public LanguageLoader getLanguageLoader() {
        return languageLoader;
    }

    public String getLibraryBaseUrl() {
        return String.format(UrlConstants.LIBRARY_URL, getMajorMinorString(currentVersion));
    }

    public String getTemplatesBaseUrl() {
        return String.format(UrlConstants.TEMPLATES_URL, getMajorMinorString(currentVersion));
    }

    public VersionInfo getVersionInfo() {
        return versionInfo;
    }

    private String getMajorMinorString(Version version) {
        return String.format("%d.%d", version.getMajor(), version.getMinor());
    }
}
