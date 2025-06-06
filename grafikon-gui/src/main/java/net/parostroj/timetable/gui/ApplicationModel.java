package net.parostroj.timetable.gui;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.*;

import javax.swing.UIManager;

import com.github.zafarkhaja.semver.Version;
import net.parostroj.timetable.gui.events.*;
import net.parostroj.timetable.gui.ini.AppPreferences;
import net.parostroj.timetable.model.TrainDiagramType;
import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.templates.DataOutputTemplateStorage;
import net.parostroj.timetable.model.templates.OutputTemplateStorage;
import net.parostroj.timetable.model.templates.OutputsLoader;
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
    private static final String MODEL_SECTION = "model";
    private static final String WEB_TEMPLATES_KEY = "web.templates";

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
    private final OutputTemplateStorage templateStorage;

    private final Instant startTime;

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
        mediator = new Mediator();
        collegue = new TrainDiagramCollegue(mediator);
        mediator.addColleague(collegue);
        mediator.addColleague(message -> {
            if (!isModelChanged() && message instanceof Event) {
                // all model changes causes model changed
                setModelChanged(true);
            }
        });
        mediator.addColleague(
                message -> setSelectedTrain(((TrainSelectionMessage) message).train(), false),
                TrainSelectionMessage.class);
        programSettings = new ProgramSettings(this::getDiagram);
        outputSettings = new OutputSettings();
        lastOpenedFiles = new LinkedList<>();
        psLoader = ScriptsLoader.newDefaultScriptsLoader();
        guiPsLoader = ScriptsLoader.newScriptsLoader("/gui_scripts");
        List<Locale> guiLocales = languageLoader.getLocales(LanguagesType.GUI);
        final Map<Locale, String> localeMap = languageLoader.createMap(guiLocales, "system");
        locale = new EnumeratedValuesPM<>(localeMap);
        final Map<String, String> lookAndFeelMap = getLookAndFeelMap();
        lookAndFeel = new EnumeratedValuesPM<>(lookAndFeelMap);
        PMManager.setup(this);
        startTime = Instant.now();
        DataOutputTemplateStorage storage = new DataOutputTemplateStorage(OutputsLoader.getDefault(), "embedded");
        storage = loadWebStorage(storage);
        storage.loadTemplates();
        templateStorage = storage;
    }

    private DataOutputTemplateStorage loadWebStorage(DataOutputTemplateStorage storage) {
        try {
            if (AppPreferences.getPreferences().getSection(MODEL_SECTION).get(WEB_TEMPLATES_KEY, Boolean.class, false)) {
                storage = new DataOutputTemplateStorage(
                        OutputsLoader.getDefaultFromUrl(URI.create(getLibraryBaseUrl()).toURL()), "web", storage);
            }
        } catch (IOException e) {
            // do nothing - ignore inaccessible preferences
        }
        return storage;
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
        this.setSelectedTrain(selectedTrain, true);
    }

    private void setSelectedTrain(Train selectedTrain, boolean message) {
        if (this.selectedTrain != selectedTrain) {
            this.selectedTrain = selectedTrain;
            if (message) {
                this.mediator.sendMessage(new TrainSelectionMessage(selectedTrain, null));
            }
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
        if (this.diagram != null) {
            this.diagram.getRuntimeInfo().setTemplateMapping(templateStorage);
            // update type
            this.diagram.getRuntimeInfo().setDiagramType(programSettings.getDiagramType());
        }

        this.setModelChanged(false);
        this.collegue.setTrainDiagram(diagram);
        this.mediator.sendMessage(new DiagramChangeMessage(diagram));
    }

    /**
     * fires specified event for this model.
     *
     * @param message message
     */
    public void fireEvent(Object message) {
        this.mediator.sendMessage(message);
        this.checkMessageModelChanged(message);
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
            this.fireEvent(new DiagramModifiedMessage(this.getDiagram()));
        }
        this.modelChanged = modelChanged;
    }

    private void checkMessageModelChanged(Object message) {
        switch (message) {
            case DiagramChangeMessage ignored -> this.setModelChanged(false);
            case DiagramSavedMessage ignored -> this.setModelChanged(false);
            default -> {}
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
        IniConfigSection section = prefs.getSection(MODEL_SECTION);
        section.put("user.name", programSettings.getUserName());
        prefs.getSection(DEBUG_SECTION).put(DEBUG_KEY, programSettings.isDebugLogging());
        section.put(WEB_TEMPLATES_KEY, programSettings.isWebTemplates());
        section.put("unit", programSettings.getLengthUnit() != null ? programSettings.getLengthUnit().getKey() : null);
        section.put("unit.speed", programSettings.getSpeedUnit() != null ? programSettings.getSpeedUnit().getKey() : null);
        section.put("diagram.type", programSettings.getDiagramType().getKey());
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
        IniConfigSection section = prefs.getSection(MODEL_SECTION);
        programSettings.setUserName(section.get("user.name"));
        programSettings.setDebugLogging(prefs.getSection(DEBUG_SECTION).get(DEBUG_KEY, Boolean.class, false));
        programSettings.setWebTemplates(section.get(WEB_TEMPLATES_KEY, Boolean.class, false));
        LengthUnit lengthUnit = LengthUnit.getByKey(section.get("unit", "mm"));
        SpeedUnit speedUnit = SpeedUnit.getByKey(section.get("unit.speed", "kmph"));
        programSettings.setLengthUnit(lengthUnit != null ? lengthUnit : LengthUnit.MM);
        programSettings.setSpeedUnit(speedUnit != null ? speedUnit : SpeedUnit.KMPH);
        TrainDiagramType diagramType = TrainDiagramType.getByKey(section.get("diagram.type"));
        programSettings.setDiagramType(diagramType != null ? diagramType : TrainDiagramType.NORMAL);
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
                this.fireEvent(new OpenedChangedMessage(OpenedChangedMessage.Type.REMOVE, removed));
            }
        } else {
            this.lastOpenedFiles.remove(file);
            this.lastOpenedFiles.addFirst(file);
        }
        this.fireEvent(new OpenedChangedMessage((OpenedChangedMessage.Type.ADD), file));
    }

    public void removeLastOpenedFile(File file) {
        if (this.lastOpenedFiles.remove(file)) {
            this.fireEvent(new OpenedChangedMessage(OpenedChangedMessage.Type.REMOVE, file));
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

    public Instant getStartTime() {
        return startTime;
    }

    private String getMajorMinorString(Version version) {
        return String.format("%d.%d", version.majorVersion(), version.minorVersion());
    }
}
