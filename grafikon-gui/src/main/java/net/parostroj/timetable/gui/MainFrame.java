/*
 * MainFrame.java
 *
 * Created on 26. srpen 2007, 19:40
 */
package net.parostroj.timetable.gui;

import groovy.lang.GroovyShell;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.script.ScriptEngineManager;
import javax.swing.*;

import net.parostroj.timetable.actions.scripts.ScriptAction;
import net.parostroj.timetable.gui.actions.*;
import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.ActionHandler;
import net.parostroj.timetable.gui.actions.execution.ModelAction;
import net.parostroj.timetable.gui.actions.impl.FileChooserFactory;
import net.parostroj.timetable.gui.actions.impl.ModelUtils;
import net.parostroj.timetable.gui.actions.impl.OutputCategory;
import net.parostroj.timetable.gui.components.BnButtonGroup;
import net.parostroj.timetable.gui.data.OutputSettings;
import net.parostroj.timetable.gui.dialogs.*;
import net.parostroj.timetable.gui.utils.*;
import net.parostroj.timetable.gui.utils.LanguageLoader.LanguagesType;
import net.parostroj.timetable.gui.views.DriverCycleDelegate;
import net.parostroj.timetable.gui.views.EngineCycleDelegate;
import net.parostroj.timetable.gui.views.TrainUnitCycleDelegate;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.FileLoadSave;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSFileFactory;
import net.parostroj.timetable.output2.OutputWriter.Settings;
import net.parostroj.timetable.utils.Pair;
import net.parostroj.timetable.utils.ResourceLoader;
import net.parostroj.timetable.utils.VersionInfo;

import org.beanfabrics.ModelProvider;
import org.beanfabrics.Path;
import org.beanfabrics.swing.BnCheckBoxMenuItem;
import org.ini4j.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main frame for the application.
 *
 * @author jub
 */
public class MainFrame extends javax.swing.JFrame implements ApplicationModelListener, StorableGuiData {

    private static final Logger log = LoggerFactory.getLogger(MainFrame.class);
    private static final String FRAME_TITLE = "Grafikon";

    private final ModelProvider provider = new ModelProvider();

    private ApplicationModel model;
    private FloatingWindowsList floatingDialogsList;
    private OutputAction outputAction;
    private ExecuteScriptAction executeScriptAction;

    private Map<File, JMenuItem> lastOpened;
    private final List<Component> enabled = new ArrayList<Component>();
    private final VersionInfo versionInfo;

    public MainFrame(SplashScreenInfo info) {
        versionInfo = new VersionInfo();
        log.debug("Version: {}", getVersion(VersionInfo.Type.FULL));
        this.initAndPreload(info);
        info.setText(getInfoText("Starting Grafikon..."));
        this.initializeFrame();
        info.setText(getInfoText("Grafikon started..."));
    }

    private void initAndPreload(SplashScreenInfo info) {
        // preload FileLoadSave
        info.setText(getInfoText("Registering LS..."));
        LSFileFactory.getInstance();

        // initialize groovy
        info.setText(getInfoText("Initializing Groovy..."));
        new GroovyShell().parse("");

        // initialize javascript
        info.setText(getInfoText("Initializing JavaScript..."));
        new ScriptEngineManager().getEngineByName("javascript");

        // preload file dialogs
        info.setText(getInfoText("Preloading dialogs..."));
        FileChooserFactory fcf = FileChooserFactory.getInstance();
        fcf.getFileChooser(FileChooserFactory.Type.OUTPUT_DIRECTORY);
        fcf.getFileChooser(FileChooserFactory.Type.OUTPUT);
        fcf.getFileChooser(FileChooserFactory.Type.GTM);
    }

    private String getInfoText(String txt) {
        log.debug(txt);
        return String.format("%s\n%s", getVersion(VersionInfo.Type.NORMAL), txt);
    }

    private String getVersion(VersionInfo.Type type) {
    	return versionInfo.getVersion(type);
    }

    public MainFrame() {
        versionInfo = new VersionInfo();
        this.initializeFrame();
    }

    /**
     * initializes frame.
     */
    private void initializeFrame() {
        model = new ApplicationModel();
        provider.setPresentationModel(model);
        lastOpened = new HashMap<File, JMenuItem>();

        // set local before anything else
        String loadedLocale = null;
        try {
            Ini.Section section = AppPreferences.getSection("main");
            loadedLocale = section.get("locale.program");
            String templateLocale = section.get("locale.output");
            if (loadedLocale != null) {
                Locale locale = ModelUtils.parseLocale(loadedLocale);
                model.setLocale(locale);
                Locale.setDefault(locale);
            } else {
                model.setLocale(null);
            }
            if (templateLocale != null) {
                model.getOutputSettings().setLocale(ModelUtils.parseLocale(templateLocale));
            }
        } catch (IOException e) {
            log.warn("Cannot load preferences.", e);
        }

        outputAction = new OutputAction(model, this);
        executeScriptAction = new ExecuteScriptAction(model);

        initComponents();

        this.addWindowListener(new MainFrameWindowListener(model, this));

        trainsPane.setModel(model);
        engineCyclesPane.setModel(new EngineCycleDelegate(model), interval -> {
            TrainsCycleType type = interval.getTrain().getDiagram().getEngineCycleType();
            if (!interval.getTrain().isCovered(type, interval)) {
                return Color.black;
            } else {
                return Color.gray;
            }
        });
        trainUnitCyclesPane.setModel(new TrainUnitCycleDelegate(model), interval -> {
            TrainsCycleType type = interval.getTrain().getDiagram().getTrainUnitCycleType();
            if (!interval.getTrain().isCovered(type, interval)) {
                return Color.black;
            } else {
                return Color.gray;
            }
        });
        driverCyclesPane.setModel(new DriverCycleDelegate(model), interval -> {
            TrainsCycleType type = interval.getTrain().getDiagram().getDriverCycleType();
            if (!interval.getTrain().isCovered(type, interval)) {
                return Color.black;
            } else {
                return Color.gray;
            }
        });
        circulationPane.setModel(model);
        freightNetPane2.setModel(model);

        model.addListener(this);
        model.addListener(statusBar);

        netPane.setModel(model);

        this.updateView();

        model.setDiagram(null);

        // apply preferences
        try {
            this.loadFromPreferences(AppPreferences.getPreferences());
        } catch (IOException e) {
            log.error("Error loading preferences.", e);
        }

        // add predefined scripts
        for (ScriptAction sd : model.getScriptsLoader().getScriptActions()) {
            addScriptAction(sd, ExecuteScriptAction.MODEL_PREFIX);
        }

        // add gui scripts
        for (ScriptAction sd : model.getGuiScriptsLoader().getScriptActions()) {
            addScriptAction(sd, ExecuteScriptAction.GUI_PREFIX);
        }

        statusBar.setModel(model);
    }

    private void addScriptAction(ScriptAction sd, String type) {
        JMenuItem item = new JMenuItem();
        item.setAction(executeScriptAction);
        item.setText(sd.getLocalizedName());
        item.setActionCommand(type + sd.getId());
        scriptsMenu.add(item);
    }

    @Override
    public void modelChanged(ApplicationModelEvent event) {
        switch (event.getType()) {
            case SET_DIAGRAM_CHANGED:
                this.updateView();
                this.setTitleChanged(false);
                break;
            case MODEL_CHANGED:
                this.setTitleChanged(true);
                break;
            case MODEL_SAVED:
                this.setTitleChanged(false);
                break;
            case ADD_LAST_OPENED:
                this.addLastOpenedFile((File) event.getObject());
                break;
            case REMOVE_LAST_OPENED:
                this.removeLastOpened((File) event.getObject());
                break;
            default:
                // nothing for the rest
                break;
        }
    }

    private void removeLastOpened(final File file) {
        GuiComponentUtils.runLaterInEDT(() -> {
            JMenuItem removed = lastOpened.remove(file);
            if (removed != null) {
                fileMenu.remove(removed);
                refreshLastOpenedFiles();
            }
        });
    }

    private void addLastOpenedFile(final File file) {
        GuiComponentUtils.runLaterInEDT(() -> {
            JMenuItem openItem = null;
            if (!lastOpened.containsKey(file)) {
                openItem = new JMenuItem(new NewOpenAction(model, MainFrame.this));
                openItem.setText("x " + file.getName());
                openItem.setActionCommand("open:" + file.getAbsoluteFile());
                lastOpened.put(file, openItem);
            } else {
                openItem = lastOpened.get(file);
                fileMenu.remove(openItem);
            }
            fileMenu.add(openItem, fileMenu.getItemCount() - 1 - lastOpened.size());
            refreshLastOpenedFiles();
        });
    }

    private void refreshLastOpenedFiles() {
        int menuItems = fileMenu.getItemCount();
        int fileItems = lastOpened.size();
        // regenerate mnemonics
        for (int i = fileItems; i > 0; i--) {
            JMenuItem item = fileMenu.getItem(menuItems - i - 2);
            item.setText(String.format("%d %s", fileItems - i + 1, item.getText().substring(2)));
            item.setMnemonic(Character.forDigit(fileItems - i + 1, 10));
        }
    }

    private void setTitleChanged(boolean b) {
        this.setTitle(this.getTitleString(b));
    }

    private String getTitleString(boolean b) {
        String title = FRAME_TITLE;
        String version = getVersion(VersionInfo.Type.NORMAL);
        if (version != null) {
            title += " (" + version + ")";
        }
        if (model != null && model.getDiagram() != null) {
            if (model.getOpenedFile() == null) {
                title += " - " + ResourceLoader.getString("title.new");
            } else {
                title += " - " + model.getOpenedFile().getName();
            }
            if (b) {
                title += " *";
            }
        }
        return title;
    }

    private void updateView() {
        boolean notNullDiagram = model.getDiagram() != null;
        for (Component c : enabled) {
            c.setEnabled(notNullDiagram);
        }
    }

    private void initComponents() {
        outputTypeButtonGroup = new javax.swing.ButtonGroup();
        javax.swing.JTabbedPane tabbedPane = new javax.swing.JTabbedPane();
        trainsPane = new net.parostroj.timetable.gui.panes.TrainsPane();
        engineCyclesPane = new net.parostroj.timetable.gui.panes.TrainsCyclesPane();
        trainUnitCyclesPane = new net.parostroj.timetable.gui.panes.TrainsCyclesPane();
        driverCyclesPane = new net.parostroj.timetable.gui.panes.TrainsCyclesPane();
        netPane = new net.parostroj.timetable.gui.panes.NetPane();
        circulationPane = new net.parostroj.timetable.gui.panes.CirculationPane();
        freightNetPane2 = new net.parostroj.timetable.gui.panes.FreightNetPane2();
        statusBar = new net.parostroj.timetable.gui.StatusBar();
        javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        javax.swing.JMenu languageMenu = new javax.swing.JMenu();
        javax.swing.JMenu lookAndFeelMenu = new javax.swing.JMenu();
        javax.swing.JMenu diagramMenu = new javax.swing.JMenu();
        javax.swing.JMenu actionMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem oLanguageMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu outputTypeMenu = new javax.swing.JMenu();
        javax.swing.JMenu viewsMenu = new javax.swing.JMenu();
        javax.swing.JMenu specialMenu = new javax.swing.JMenu();
        scriptsMenu = new javax.swing.JMenu();
        javax.swing.JMenu settingsMenu = new javax.swing.JMenu();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();

        floatingDialogsList = FloatingWindowsFactory.createDialogs(this, model.getMediator(), model);
        floatingDialogsList.addToMenuItem(viewsMenu);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(this.getTitleString(false));
        setLocationByPlatform(true);

        tabbedPane.addTab(ResourceLoader.getString("tab.trains"), trainsPane); // NOI18N
        tabbedPane.addTab(ResourceLoader.getString("tab.engine.cycles"), engineCyclesPane); // NOI18N
        tabbedPane.addTab(ResourceLoader.getString("tab.trainunit.cycle"), trainUnitCyclesPane); // NOI18N
        tabbedPane.addTab(ResourceLoader.getString("tab.driver.cycles"), driverCyclesPane); // NOI18N
        tabbedPane.addTab(ResourceLoader.getString("tab.net"), netPane); // NOI18N
        tabbedPane.addTab(ResourceLoader.getString("tab.circulations"), circulationPane); // NOI18N
        tabbedPane.addTab(ResourceLoader.getString("tab.freight.net"), freightNetPane2); // NOI18N

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        getContentPane().add(statusBar, BorderLayout.SOUTH);

        tabbedPane.getAccessibleContext().setAccessibleName(ResourceLoader.getString("tab.trains")); // NOI18N

        fileMenu.setText(ResourceLoader.getString("menu.file")); // NOI18N

        this.addMenuItem(fileMenu, "menu.file.new", new NewOpenAction(model, this), "new", false, null); // NOI18N

        fileMenu.add(new javax.swing.JSeparator());

        this.addMenuItem(fileMenu, "menu.file.open", new NewOpenAction(model, this), "open", false, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK)); // NOI18N
        this.addMenuItem(fileMenu, "menu.file.save", new SaveAction(model), "save", true, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK)); // NOI18N
        this.addMenuItem(fileMenu, "menu.file.saveas", new SaveAction(model), "save_as", true, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK)); // NOI18N

        fileMenu.add(new javax.swing.JSeparator());

        this.addMenuItem(fileMenu, "menu.file.exportimport", new ImportAction(model, this, false), null); // NOI18N
        this.addMenuItem(fileMenu, "menu.file.exportimport.trains", new ImportAction(model, this, true), null); // NOI18N

        fileMenu.add(new javax.swing.JSeparator());

        languageMenu.setText(ResourceLoader.getString("menu.language")); // NOI18N

        fileMenu.add(languageMenu);

        lookAndFeelMenu.setText(ResourceLoader.getString("menu.lookandfeel")); // NOI18N

        fileMenu.add(lookAndFeelMenu);
        fileMenu.add(new javax.swing.JSeparator());
        fileMenu.add(new javax.swing.JSeparator());

        this.addMenuItem(fileMenu, "menu.file.exit", new ExitAction(model, this), null, false); // NOI18N

        menuBar.add(fileMenu);

        diagramMenu.setText(ResourceLoader.getString("menu.diagram")); // NOI18N

        this.addMenuItemWithListener(diagramMenu, "menu.file.settings", evt -> settingsMenuItemActionPerformed(evt), true); // NOI18N
        this.addMenuItemWithListener(diagramMenu, "gt.routes.edit", evt -> editRoutesMenuItemActionPerformed(evt), true); // NOI18N
        this.addMenuItemWithListener(diagramMenu, "menu.file.images", evt -> imagesMenuItemActionPerformed(evt), true); // NOI18N
        this.addMenuItemWithListener(diagramMenu, "menu.file.textitems", evt -> textItemsMenuItemActionPerformed(evt), true); // NOI18N
        this.addMenuItemWithListener(diagramMenu, "menu.file.info", evt -> infoMenuItemActionPerformed(evt), true); // NOI18N
        this.addMenuItemWithListener(diagramMenu, "menu.file.traintypes", evt -> trainTypesMenuItemActionPerformed(evt), true); // NOI18N
        this.addMenuItemWithListener(diagramMenu, "menu.file.lineclasses", evt -> lineClassesMenuItemActionPerformed(evt), true); // NOI18N
        this.addMenuItemWithListener(diagramMenu, "menu.file.regions", evt -> regionsMenuItemActionPerformed(evt), true); // NOI18N
        this.addMenuItemWithListener(diagramMenu, "menu.file.weighttables", evt -> weightTablesMenuItemActionPerformed(evt), true); // NOI18N
        this.addMenuItemWithListener(diagramMenu, "menu.file.penaltytable", evt -> penaltyTableMenuItemActionPerformed(evt), true); // NOI18N
        this.addMenuItemWithListener(diagramMenu, "menu.file.localization", evt -> localizationMenuItemActionPerformed(evt), true); // NOI18N
        this.addMenuItemWithListener(diagramMenu, "localization.languages", evt -> languagesMenuItemActionPerformed(evt), true); // NOI18N

        this.addMenuItem(diagramMenu, "menu.groups", new EditGroupsAction(model), null);
        this.addMenuItemWithListener(diagramMenu, "menu.companies", evt -> companiesMenuItemActionPerformed(evt), true); // NOI18N

        menuBar.add(diagramMenu);

        actionMenu.setText(ResourceLoader.getString("menu.outputs")); // NOI18N

        this.addMenuItem(actionMenu, "menu.action.traintimetableslist", outputAction, "trains"); // NOI18N
        this.addMenuItem(actionMenu, "menu.action.nodetimetableslist", outputAction, "stations"); // NOI18N
        this.addMenuItem(actionMenu, "menu.action.eclist", outputAction, "engine_cycles"); // NOI18N
        this.addMenuItem(actionMenu, "menu.action.tuclist", outputAction, "train_unit_cycles"); // NOI18N
        this.addMenuItem(actionMenu, "menu.acion.dclist", outputAction, "driver_cycles"); // NOI18N
        this.addMenuItem(actionMenu, "menu.acion.splist", outputAction, "starts"); // NOI18N

        actionMenu.add(new javax.swing.JSeparator());

        this.addMenuItem(actionMenu, "menu.action.traintimetableslistbydc", outputAction, "trains_by_driver_cycles"); // NOI18N
        this.addMenuItem(actionMenu, "menu.action.traintimetableslistbytimefiltered", outputAction, "trains_select_station"); // NOI18N
        this.addMenuItem(actionMenu, "menu.acion.eplist", outputAction, "ends"); // NOI18N
        this.addMenuItem(actionMenu, "menu.acion.cclist", outputAction, "custom_cycles"); // NOI18N

        actionMenu.add(new javax.swing.JSeparator());

        this.addMenuItem(actionMenu, "menu.action.traintimetableslistbydc.select", outputAction, "trains_select_driver_cycles"); // NOI18N
        this.addMenuItem(actionMenu, "menu.action.traintimetableslistbyroutes.select", outputAction, "trains_select_routes"); // NOI18N
        this.addMenuItem(actionMenu, "menu.action.nodetimetableslist.select", outputAction, "stations_select"); // NOI18N
        this.addMenuItem(actionMenu, "menu.action.eclist.select", outputAction, "engine_cycles_select"); // NOI18N
        this.addMenuItem(actionMenu, "menu.action.tuclist.select", outputAction, "train_unit_cycles_select"); // NOI18N
        this.addMenuItem(actionMenu, "menu.acion.dclist.select", outputAction, "driver_cycles_select"); // NOI18N
        this.addMenuItem(actionMenu, "menu.action.cclist.select", outputAction, "custom_cycles_select"); // NOI18N

        actionMenu.add(new javax.swing.JSeparator());

        this.addMenuItem(actionMenu, "menu.action.all.html", outputAction, "all"); // NOI18N

        actionMenu.add(new javax.swing.JSeparator());

        oLanguageMenuItem.setText(ResourceLoader.getString("menu.language.output") + "..."); // NOI18N

        actionMenu.add(oLanguageMenuItem);

        outputTypeMenu.setText(ResourceLoader.getString("menu.output.type")); // NOI18N

        JRadioButtonMenuItem htmlRItem = this.addRadioMenuItem(outputTypeMenu, "menu.output.type.html", evt -> outputTypeActionPerformed(evt), "html", true); // NOI18N
        outputTypeButtonGroup.add(htmlRItem);

        JRadioButtonMenuItem xmlRItem = this.addRadioMenuItem(outputTypeMenu, "menu.output.type.xml", evt -> outputTypeActionPerformed(evt), "xml", false); // NOI18N
        outputTypeButtonGroup.add(xmlRItem);

        JRadioButtonMenuItem pdfRItem = this.addRadioMenuItem(outputTypeMenu, "menu.output.type.pdf", evt -> outputTypeActionPerformed(evt), "pdf", false); // NOI18N
        outputTypeButtonGroup.add(pdfRItem);

        actionMenu.add(outputTypeMenu);

        addBnCheckMenuItem(actionMenu, "outputSettingsPM.generateTitlePage", "menu.action.traintimetables.generate.titlepage");
        addBnCheckMenuItem(actionMenu, "outputSettingsPM.doubleSidedPrint", "menu.action.traintimetables.two.sides.print");
        addBnCheckMenuItem(actionMenu, "outputSettingsPM.showTechTimes", "menu.action.traintimetables.show.tech.time");

        actionMenu.add(new javax.swing.JSeparator());

        this.addMenuItemWithListener(actionMenu, "menu.action.user.output.templates", evt -> ouputTemplatesMenuItemActionPerformed(evt), true); // NOI18N

        menuBar.add(actionMenu);

        viewsMenu.setText(ResourceLoader.getString("menu.views")); // NOI18N
        menuBar.add(viewsMenu);

        specialMenu.setText(ResourceLoader.getString("menu.special")); // NOI18N

        this.addMenuItem(specialMenu, "menu.special.recalculate", new RecalculateAction(model), null); // NOI18N
        this.addMenuItem(specialMenu, "menu.special.recalculate.stops", new RecalculateStopsAction(model), null); // NOI18N
        this.addMenuItem(specialMenu, "menu.special.remove.weights", new RemoveWeightsAction(model), null); // NOI18N
        this.addMenuItem(specialMenu, "menu.special.execute.script", executeScriptAction, ""); // NOI18N

        scriptsMenu.setText(ResourceLoader.getString("menu.special.predefined.scripts")); // NOI18N
        specialMenu.add(scriptsMenu);

        menuBar.add(specialMenu);

        settingsMenu.setText(ResourceLoader.getString("menu.settings")); // NOI18N

        this.addMenuItemWithListener(settingsMenu, "menu.settings.columns", evt -> trainsPane.editColumns(), false); // NOI18N
        this.addMenuItemWithListener(settingsMenu, "menu.settings.sort.columns", evt -> trainsPane.sortColumns(), false); // NOI18N
        this.addMenuItemWithListener(settingsMenu, "menu.settings.resize.columns", evt -> trainsPane.resizeColumns(), false); // NOI18N


        showGTViewMenuItem = this.addCheckMenuItem(settingsMenu, "menu.settings.show.gtview", evt -> showGTViewMenuItemActionPerformed(evt), null, true); // NOI18N

        this.addMenuItemWithListener(settingsMenu, "menu.program.settings", evt -> programSettingsMenuItemActionPerformed(evt), false); // NOI18N

        menuBar.add(settingsMenu);

        helpMenu.setText(ResourceLoader.getString("menu.help")); // NOI18N

        this.addMenuItemWithListener(helpMenu, "menu.help.about", evt -> aboutMenuItemActionPerformed(evt), false); // NOI18N

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        // enabled list
        enabled.add(tabbedPane);
        enabled.add(scriptsMenu);

        setMinimumSize(new java.awt.Dimension(800, 600));
        setSize(getMinimumSize());

        trainUnitCyclesPane.setKey("cycles.trainunit");
        driverCyclesPane.setKey("cycles.driver");
        engineCyclesPane.setKey("cycles.engine");

        // add languages to menu
        LanguageMenuBuilder languageMenuBuilder = new LanguageMenuBuilder(model.getLanguageLoader());

        List<Pair<JRadioButtonMenuItem, Locale>> lItems = languageMenuBuilder.createLanguageMenuItems(ResourceLoader
                .getString("menu.language.system"), LanguagesType.GUI);
        BnButtonGroup<Locale> lBGroup = new BnButtonGroup<Locale>();
        for (Pair<JRadioButtonMenuItem, Locale> item : lItems) {
            languageMenu.add(item.first);
            lBGroup.add(item.first, item.second);
        }
        lBGroup.setModelProvider(provider);
        lBGroup.setPath(new Path("locale"));

        // output language
        oLanguageMenuItem.addActionListener(e -> {
            String system = ResourceLoader.getString("menu.language.system");
            LanguageLoader languageLoader = model.getLanguageLoader();
            List<Wrapper<Locale>> values = languageLoader.createWrappers(languageLoader.getAvailableLocales(), system);
            Collections.sort(values);
            Locale oLocale = model.getOutputSettings().getLocale();
            Wrapper<?> selected = (Wrapper<?>) JOptionPane.showInputDialog(this, null, null,
                    JOptionPane.QUESTION_MESSAGE, null, values.toArray(), oLocale == null ? Wrapper.getEmptyWrapper("") : Wrapper.getWrapper(oLocale));
            if (selected != null) {
                Locale sLocale = (Locale) selected.getElement();
                model.getOutputSettings().setLocale(sLocale);
            }
        });

        // look and feel
        BnButtonGroup<String> lafBGroup = new BnButtonGroup<String>();
        for (String key : model.lookAndFeel.getValues()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(model.lookAndFeel.getOptions().get(key));
            lafBGroup.add(item, key);
            lookAndFeelMenu.add(item);
        }
        lafBGroup.setModelProvider(provider);
        lafBGroup.setPath(new Path("lookAndFeel"));
    }

    private BnCheckBoxMenuItem addBnCheckMenuItem(javax.swing.JMenu actionMenu, String pathStr, String key) {
        BnCheckBoxMenuItem menuItem = new BnCheckBoxMenuItem(provider, new Path(pathStr));
        menuItem.setText(ResourceLoader.getString(key)); // NOI18N
        actionMenu.add(menuItem);
        return menuItem;
    }

    private JCheckBoxMenuItem addCheckMenuItem(JMenu menu, String textKey, ActionListener action, String actionCommand, boolean selected) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem();
        item.setSelected(selected);
        item.setText(ResourceLoader.getString(textKey));
        item.setActionCommand(actionCommand);
        item.addActionListener(action);
        menu.add(item);
        return item;
    }

    private JRadioButtonMenuItem addRadioMenuItem(JMenu menu, String textKey, ActionListener action, String actionCommand, boolean selected) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem();
        item.setSelected(selected);
        item.setText(ResourceLoader.getString(textKey));
        item.setActionCommand(actionCommand);
        if (action != null) {
            item.addActionListener(action);
        }
        menu.add(item);
        return item;
    }

    private JMenuItem addMenuItemWithListener(JMenu menu, String textKey, ActionListener action, boolean enableHandled) {
        JMenuItem item = new JMenuItem();
        item.setText(ResourceLoader.getString(textKey));
        item.addActionListener(action);
        menu.add(item);
        if (enableHandled) {
            enabled.add(item);
        }
        return item;
    }

    private JMenuItem addMenuItem(JMenu menu, String textKey, Action action, String actionCommand, boolean enableHandled) {
        return this.addMenuItem(menu, textKey, action, actionCommand, enableHandled, null);
    }

    private JMenuItem addMenuItem(JMenu menu, String textKey, Action action, String actionCommand) {
        return this.addMenuItem(menu, textKey, action, actionCommand, true, null);
    }

    private JMenuItem addMenuItem(JMenu menu, String textKey, Action action, String actionCommand, boolean enableHandled, KeyStroke keyStroke) {
        JMenuItem item = new JMenuItem();
        item.setAction(action);
        item.setText(ResourceLoader.getString(textKey)); // NOI18N
        if (keyStroke != null) {
            item.setAccelerator(keyStroke);
        }
        item.setActionCommand(actionCommand);
        menu.add(item);
        if (enableHandled) {
            enabled.add(item);
        }
        return item;
    }

    private void settingsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        SettingsDialog settingsDialog = new SettingsDialog(this, true);
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.showDialog(model.getDiagram());
        settingsDialog.dispose();
        // check if recalculate should be executed
        ActionContext context = new ActionContext(GuiComponentUtils.getTopLevelComponent(this));
        if (settingsDialog.isRecalculate()) {
            ModelAction action = RecalculateAction.getAllTrainsAction(context, model.getDiagram(), train -> {
                train.recalculate();
                // round correctly stops
                TimeConverter converter = train.getDiagram().getTimeConverter();
                for (TimeInterval interval : train.getNodeIntervals()) {
                    train.changeStopTime(interval, converter.round(interval.getLength()));
                }
            }, ResourceLoader.getString("wait.message.recalculate"), "Recalculate");
            ActionHandler.getInstance().execute(action);
        }
    }

    private void imagesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        EditImagesDialog imagesDialog = new EditImagesDialog(this, true);
        imagesDialog.setLocationRelativeTo(this);
        imagesDialog.showDialog(model.getDiagram());
        imagesDialog.dispose();
    }

    private void infoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        EditInfoDialog infoDialog = new EditInfoDialog(this, true);
        infoDialog.setLocationRelativeTo(this);
        infoDialog.showDialog(model.getDiagram());
        infoDialog.dispose();
    }

    private void trainTypesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        TrainTypesDialog trainTypesDialog = new TrainTypesDialog(this, true);
        trainTypesDialog.setLocationRelativeTo(this);
        trainTypesDialog.showDialog(model.getDiagram());
        trainTypesDialog.dispose();
    }

    private void lineClassesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        EditLineClassesDialog lineClassesDialog = new EditLineClassesDialog(this, true);
        lineClassesDialog.setLocationRelativeTo(this);
        lineClassesDialog.showDialog(model.getDiagram());
        lineClassesDialog.dispose();
    }

    private void regionsMenuItemActionPerformed(ActionEvent evt) {
        EditRegionsDialog dialog = new EditRegionsDialog(this, true, model.getLanguageLoader().getAvailableLocales());
        dialog.setLocationRelativeTo(this);
        dialog.showDialog(model.getDiagram());
        dialog.dispose();
    }

    private void companiesMenuItemActionPerformed(ActionEvent evt) {
        EditCompaniesDialog dialog = new EditCompaniesDialog(this, true, model.getLanguageLoader().getAvailableLocales());
        dialog.setLocationRelativeTo(this);
        dialog.showDialog(model.getDiagram());
        dialog.dispose();
    }

    private void weightTablesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        EngineClassesDialog engineClassesDialog = new EngineClassesDialog(this, true);
        engineClassesDialog.setLocationRelativeTo(this);
        engineClassesDialog.showDialog(model.getDiagram());
        engineClassesDialog.dispose();
    }

    private void penaltyTableMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        TrainTypesCategoriesDialog dialog = new TrainTypesCategoriesDialog(this, true);
        dialog.setLocationRelativeTo(this);
        dialog.showDialog(model.getDiagram());
        dialog.dispose();
    }

    private void localizationMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO implementation
    }

    private void languagesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        ElementSelectionDialog<Locale> dialog = new ElementSelectionDialog<Locale>(GuiComponentUtils
                .getWindow(this), true);
        dialog.setLocationRelativeTo(this);

        Collection<Locale> currentLocales = model.get().getLocales();
        List<Locale> newSelection = dialog.selectElements(model.getLanguageLoader().getAvailableLocales(), currentLocales);
        if (newSelection != null) {
            model.get().setAttribute(TrainDiagram.ATTR_LOCALES, newSelection);
        }
    }

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        // show about dialog
        ResourceBundle aboutBundle = ResourceBundle.getBundle("about");
        LSFileFactory f = LSFileFactory.getInstance();
        FileLoadSave fls = null;
        try {
            fls = f.createLatestForSave();
        } catch (LSException e) {
            log.warn("Cannot create FileLoadSave", e);
        }
        AboutDialog dialog = new AboutDialog(this, true,
                String.format(aboutBundle.getString("text"), getVersion(VersionInfo.Type.FULL), fls == null ? "-" : fls.getSaveVersion()),
                getClass().getResource(aboutBundle.getString("image")), true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.dispose();
    }

    private void outputTypeActionPerformed(java.awt.event.ActionEvent evt) {
        // get output type
        OutputCategory type = OutputCategory.fromString(evt.getActionCommand());
        model.setOutputCategory(type);
    }

    private void editRoutesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        EditRoutesDialog editRoutesDialog = new EditRoutesDialog(this, true);
        editRoutesDialog.setLocationRelativeTo(this);
        editRoutesDialog.showDialog(model.getDiagram());
    }

    private void textItemsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        TextItemsDialog dialog = new TextItemsDialog(this, true);
        dialog.setLocationRelativeTo(this);
        dialog.showDialog(model.getDiagram());
        dialog.dispose();
    }

    private void programSettingsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        // program settings
        ProgramSettingsDialog dialog = new ProgramSettingsDialog(this, true);
        dialog.setLocationRelativeTo(this);
        dialog.showDialog(model.getProgramSettings());
        dialog.dispose();
    }

    private void showGTViewMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        boolean state = ((JCheckBoxMenuItem) evt.getSource()).isSelected();
        trainsPane.setVisibilityOfGTView(state);
    }

    private void ouputTemplatesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        // output templates list dialog
        OutputTemplateListDialog dialog = new OutputTemplateListDialog(this, true);
        dialog.setLocationRelativeTo(this);
        dialog.registerContext(model.getGuiContext());
        OutputSettings settings = model.getOutputSettings();
        FileChooserFactory chooserFactory = FileChooserFactory.getInstance();
        dialog.showDialog(model.getDiagram(), chooserFactory.getFileChooser(FileChooserFactory.Type.OUTPUT_DIRECTORY),
                chooserFactory.getFileChooser(FileChooserFactory.Type.ALL_FILES),
                new Settings(settings.isGenerateTitlePageTT(), settings.isTwoSidedPrint(), settings.isStShowTechTime(), settings.getLocale()));
        dialog.dispose();
    }

    public void cleanUpBeforeApplicationEnd() {
        try {
            // save preferences
            this.saveToPreferences(AppPreferences.getPreferences());
            AppPreferences.storePreferences();
        } catch (IOException ex) {
            log.error("Error saving preferences.", ex);
        }
    }

    @Override
    public Ini.Section saveToPreferences(Ini prefs) {
        boolean maximized = (this.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0;

        Ini.Section section = AppPreferences.getSection(prefs, "main");

        section.put("maximized", maximized);

        if (!maximized) {
            // set position
            String positionStr = GuiUtils.getPosition(this);
            section.put("position", positionStr);
        } else {
            section.remove("position");
        }

        // save to preferences last file chooser directories
        FileChooserFactory.getInstance().saveToPreferences(prefs);

        // save locales
        Locale locale = model.getLocale();
        section.put("locale.program", locale != null ? locale.toLanguageTag() : null);
        section.put("locale.output", model.getOutputSettings().getLocale() != null ? model.getOutputSettings().getLocale().toLanguageTag() : null);

        // save output type
        section.put("output.type", outputTypeButtonGroup.getSelection().getActionCommand());

        // save look and feel
        section.put("look.and.feel", model.lookAndFeel.getValue());

        trainsPane.saveToPreferences(prefs);
        floatingDialogsList.saveToPreferences(prefs);
        model.saveToPreferences(prefs);
        trainUnitCyclesPane.saveToPreferences(prefs);
        driverCyclesPane.saveToPreferences(prefs);
        engineCyclesPane.saveToPreferences(prefs);
        circulationPane.saveToPreferences(prefs);
        freightNetPane2.saveToPreferences(prefs);
        return section;
    }

    @Override
    public Ini.Section loadFromPreferences(Ini prefs) {
        Ini.Section section = AppPreferences.getSection(prefs, "main");
        if (section.get("maximized", Boolean.class, false)) {
            // setting maximized state
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            if (section.containsKey("position")) {
                // set position
                GuiUtils.setPosition(section.get("position"), this);
            }
        }

        // load output type
        String aC = section.get("output.type", "html");
        for (Enumeration<AbstractButton> e = outputTypeButtonGroup.getElements(); e.hasMoreElements();) {
            AbstractButton button = e.nextElement();
            if (button.getActionCommand().equals(aC)) {
                button.setSelected(true);
                model.setOutputCategory(OutputCategory.fromString(button.getActionCommand()));
                break;
            }
        }

        // load look and feel
        String laf = section.get("look.and.feel", "system");
        model.lookAndFeel.setValue(laf);

        showGTViewMenuItem.setSelected(AppPreferences.getSection(prefs, "trains").get("show.gtview", Boolean.class, true));

        trainsPane.loadFromPreferences(prefs);
        floatingDialogsList.loadFromPreferences(prefs);
        model.loadFromPreferences(prefs);
        trainUnitCyclesPane.loadFromPreferences(prefs);
        driverCyclesPane.loadFromPreferences(prefs);
        engineCyclesPane.loadFromPreferences(prefs);
        circulationPane.loadFromPreferences(prefs);
        freightNetPane2.loadFromPreferences(prefs);

        return section;
    }

    public void forceLoad(File file) {
        NewOpenAction action = new NewOpenAction(model, this);
        action.actionPerformed(new ActionEvent(this, 0, "open:" + file.getAbsolutePath()));
    }

    public ApplicationModel getModel() {
        return model;
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        floatingDialogsList.setVisibleOnInit();
        // set focus back on the frame
        this.requestFocus();
    }

    private net.parostroj.timetable.gui.panes.TrainsPane trainsPane;
    private net.parostroj.timetable.gui.panes.NetPane netPane;
    private net.parostroj.timetable.gui.panes.CirculationPane circulationPane;
    private net.parostroj.timetable.gui.panes.FreightNetPane2 freightNetPane2;
    private net.parostroj.timetable.gui.panes.TrainsCyclesPane driverCyclesPane;
    private net.parostroj.timetable.gui.panes.TrainsCyclesPane engineCyclesPane;
    private net.parostroj.timetable.gui.panes.TrainsCyclesPane trainUnitCyclesPane;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu scriptsMenu;
    private javax.swing.JCheckBoxMenuItem showGTViewMenuItem;
    private javax.swing.ButtonGroup outputTypeButtonGroup;
    private net.parostroj.timetable.gui.StatusBar statusBar;
}
