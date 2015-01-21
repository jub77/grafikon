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

import javax.swing.*;

import net.parostroj.timetable.actions.scripts.ScriptAction;
import net.parostroj.timetable.gui.actions.*;
import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.ActionHandler;
import net.parostroj.timetable.gui.actions.execution.ModelAction;
import net.parostroj.timetable.gui.actions.impl.FileChooserFactory;
import net.parostroj.timetable.gui.actions.impl.ModelUtils;
import net.parostroj.timetable.gui.actions.impl.OutputCategory;
import net.parostroj.timetable.gui.dialogs.*;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiUtils;
import net.parostroj.timetable.gui.views.DriverCycleDelegate;
import net.parostroj.timetable.gui.views.EngineCycleDelegate;
import net.parostroj.timetable.gui.views.TrainUnitCycleDelegate;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.FileLoadSave;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSFileFactory;
import net.parostroj.timetable.output2.OutputWriter.Settings;
import net.parostroj.timetable.utils.ResourceLoader;
import net.parostroj.timetable.utils.VersionInfo;

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

    private ApplicationModel model;
    private FloatingWindowsList floatingDialogsList;
    private Locale locale;
    private OutputAction outputAction;
    private ExecuteScriptAction executeScriptAction;

    private Map<File, JMenuItem> lastOpened;
    private final List<Component> enabled = new ArrayList<Component>();
    private final VersionInfo versionInfo;

    public MainFrame(SplashScreenInfo info) {
        versionInfo = new VersionInfo();
        String version = getVersion(false);
        info.setText("Starting Grafikon ...\n" + version);
        this.initializeFrame();
    }

    private String getVersion(boolean complete) {
        return complete ? versionInfo.getFullVersion() : versionInfo.getVersion();
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
        lastOpened = new HashMap<File, JMenuItem>();

        // set local before anything else
        String loadedLocale = null;
        try {
            Ini.Section section = AppPreferences.getSection("main");
            loadedLocale = section.get("locale.program");
            String templateLocale = section.get("locale.output");
            if (loadedLocale != null) {
                locale = ModelUtils.parseLocale(loadedLocale);
                Locale.setDefault(locale);
            }
            if (templateLocale != null) {
                model.setOutputLocale(ModelUtils.parseLocale(templateLocale));
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

        // add languages to menu
        LanguageMenuBuilder languageMenuBuilder = new LanguageMenuBuilder();
        List<LanguageMenuBuilder.LanguageMenuItem> languages = languageMenuBuilder.createLanguageMenuItems();
        ActionListener langListener = e -> languageRadioButtonMenuItemActionPerformed(e);
        for (LanguageMenuBuilder.LanguageMenuItem item : languages) {
            languageMenu.add(item);
            item.addActionListener(langListener);
            languageButtonGroup.add(item);
        }
        List<LanguageMenuBuilder.LanguageMenuItem> oLanguages = languageMenuBuilder.createLanguageMenuItems();
        ActionListener oLangListener = e -> outputLanguageRadioButtonMenuItemActionPerformed(e);
        for (LanguageMenuBuilder.LanguageMenuItem item : oLanguages) {
            oLanguageMenu.add(item);
            item.addActionListener(oLangListener);
            outputLbuttonGroup.add(item);
        }

        // look and feel
        for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(laf.getName());
            item.setActionCommand(laf.getClassName());
            lookAndFeelbuttonGroup.add(item);
            lookAndFeelMenu.add(item);
        }

        model.addListener(this);
        model.addListener(statusBar);

        floatingDialogsList = FloatingWindowsFactory.createDialogs(this, model.getMediator(), model);
        floatingDialogsList.addToMenuItem(viewsMenu);

        netPane.setModel(model);

        this.updateView();

        model.setDiagram(null);

        // apply preferences
        try {
            this.loadFromPreferences(AppPreferences.getPreferences());
        } catch (IOException e) {
            log.error("Error loading preferences.", e);
        }

        this.setSelectedLocale();
        this.setSelectedTemplateLocale();

        // preload file dialogs
        FileChooserFactory fcf = FileChooserFactory.getInstance();
        fcf.getFileChooser(FileChooserFactory.Type.OUTPUT_DIRECTORY);
        fcf.getFileChooser(FileChooserFactory.Type.OUTPUT);
        fcf.getFileChooser(FileChooserFactory.Type.GTM);

        // preload FileLoadSave
        LSFileFactory.getInstance();

        // initialize groovy
        new GroovyShell().parse("");

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
        String version = getVersion(false);
        if (version != null)
            title += " (" + version + ")";
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
        languageButtonGroup = new javax.swing.ButtonGroup();
        outputLbuttonGroup = new javax.swing.ButtonGroup();
        outputTypeButtonGroup = new javax.swing.ButtonGroup();
        lookAndFeelbuttonGroup = new javax.swing.ButtonGroup();
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
        languageMenu = new javax.swing.JMenu();
        lookAndFeelMenu = new javax.swing.JMenu();
        javax.swing.JMenu diagramMenu = new javax.swing.JMenu();
        javax.swing.JMenu actionMenu = new javax.swing.JMenu();
        oLanguageMenu = new javax.swing.JMenu();
        javax.swing.JMenu outputTypeMenu = new javax.swing.JMenu();
        viewsMenu = new javax.swing.JMenu();
        javax.swing.JMenu specialMenu = new javax.swing.JMenu();
        scriptsMenu = new javax.swing.JMenu();
        javax.swing.JMenu settingsMenu = new javax.swing.JMenu();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();

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

        systemLanguageRadioButtonMenuItem = this.addRadioMenuItem(languageMenu, "menu.language.system", evt -> languageRadioButtonMenuItemActionPerformed(evt), null, true); // NOI18N
        languageButtonGroup.add(systemLanguageRadioButtonMenuItem);

        fileMenu.add(languageMenu);

        lookAndFeelMenu.setText(ResourceLoader.getString("menu.lookandfeel")); // NOI18N

        JRadioButtonMenuItem lafRItem = this.addRadioMenuItem(lookAndFeelMenu, "menu.lookandfeel.system", null, "system", true); // NOI18N
        lookAndFeelbuttonGroup.add(lafRItem);

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

        this.addMenuItem(diagramMenu, "menu.groups", new EditGroupsAction(model), null);

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

        oLanguageMenu.setText(ResourceLoader.getString("menu.language.output")); // NOI18N

        oSystemLRadioButtonMenuItem = this.addRadioMenuItem(oLanguageMenu, "menu.language.program", evt -> outputLanguageRadioButtonMenuItemActionPerformed(evt), null, true); // NOI18N
        outputLbuttonGroup.add(oSystemLRadioButtonMenuItem);

        actionMenu.add(oLanguageMenu);

        outputTypeMenu.setText(ResourceLoader.getString("menu.output.type")); // NOI18N

        JRadioButtonMenuItem htmlRItem = this.addRadioMenuItem(outputTypeMenu, "menu.output.type.html", evt -> outputTypeActionPerformed(evt), "html", true); // NOI18N
        outputTypeButtonGroup.add(htmlRItem);

        JRadioButtonMenuItem htmlSelectRItem = this.addRadioMenuItem(outputTypeMenu, "menu.output.type.htmlselect", evt -> outputTypeActionPerformed(evt), "html.select", false); // NOI18N
        outputTypeButtonGroup.add(htmlSelectRItem);

        JRadioButtonMenuItem xmlRItem = this.addRadioMenuItem(outputTypeMenu, "menu.output.type.xml", evt -> outputTypeActionPerformed(evt), "xml", false); // NOI18N
        outputTypeButtonGroup.add(xmlRItem);

        JRadioButtonMenuItem pdfRItem = this.addRadioMenuItem(outputTypeMenu, "menu.output.type.pdf", evt -> outputTypeActionPerformed(evt), "pdf", false); // NOI18N
        outputTypeButtonGroup.add(pdfRItem);

        actionMenu.add(outputTypeMenu);

        genTitlePageTTCheckBoxMenuItem = this.addCheckMenuItem(actionMenu, "menu.action.traintimetables.generate.titlepage", evt -> model.getProgramSettings().setGenerateTitlePageTT(((JCheckBoxMenuItem) evt.getSource()).isSelected()), null, true); // NOI18N
        twoSidesPrintCheckBoxMenuItem = this.addCheckMenuItem(actionMenu, "menu.action.traintimetables.two.sides.print", evt -> model.getProgramSettings().setTwoSidedPrint(((JCheckBoxMenuItem) evt.getSource()).isSelected()), null, false); // NOI18N
        stShowTechTimeCheckBoxMenuItem = this.addCheckMenuItem(actionMenu, "menu.action.traintimetables.show.tech.time", evt -> model.getProgramSettings().setStShowTechTime(((JCheckBoxMenuItem) evt.getSource()).isSelected()), null, false); // NOI18N

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

        this.addMenuItemWithListener(settingsMenu, "menu.settings.columns", evt -> columnsMenuItemActionPerformed(evt), false); // NOI18N
        this.addMenuItemWithListener(settingsMenu, "menu.settings.sort.columns", evt -> sortColumnsMenuItemActionPerformed(evt), false); // NOI18N
        this.addMenuItemWithListener(settingsMenu, "menu.settings.resize.columns", evt -> resizeColumnsMenuItemActionPerformed(evt), false); // NOI18N


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
                for (TimeInterval interval : train.getTimeIntervalList()) {
                	if (interval.isNodeOwner()) {
                		train.changeStopTime(interval, converter.round(interval.getLength()));
                	}
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

    private void languageRadioButtonMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        if (systemLanguageRadioButtonMenuItem.isSelected()) {
            locale = null;
        } else if (evt.getSource() instanceof LanguageMenuBuilder.LanguageMenuItem) {
            locale = ((LanguageMenuBuilder.LanguageMenuItem)evt.getSource()).getLanguage();
        }
    }

    private void outputLanguageRadioButtonMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        if (oSystemLRadioButtonMenuItem.isSelected()) {
            model.setOutputLocale(null);
        } else if (evt.getSource() instanceof LanguageMenuBuilder.LanguageMenuItem) {
            model.setOutputLocale(((LanguageMenuBuilder.LanguageMenuItem)evt.getSource()).getLanguage());
        }
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
        EditRegionsDialog dialog = new EditRegionsDialog(this, true);
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

    private void columnsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        trainsPane.editColumns();
    }

    private void sortColumnsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        trainsPane.sortColumns();
    }

    private void resizeColumnsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        trainsPane.resizeColumns();
    }

    private void penaltyTableMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        TrainTypesCategoriesDialog dialog = new TrainTypesCategoriesDialog(this, true);
        dialog.setLocationRelativeTo(this);
        dialog.showDialog(model.getDiagram());
        dialog.dispose();
    }

    private void localizationMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        LocalizationDialog dialog = new LocalizationDialog(this, true);
        dialog.setLocationRelativeTo(this);
        dialog.showDialog(model.getDiagram());
        dialog.dispose();
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
                String.format(aboutBundle.getString("text"), getVersion(true), fls == null ? "-" : fls.getSaveVersion()),
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
        ProgramSettings settings = model.getProgramSettings();
        dialog.showDialog(model.getDiagram(), FileChooserFactory.getInstance().getFileChooser(FileChooserFactory.Type.OUTPUT_DIRECTORY),
                new Settings(settings.isGenerateTitlePageTT(), settings.isTwoSidedPrint(), settings.isStShowTechTime(), model.getOutputLocale()));
        dialog.dispose();
    }

    private void setSelectedLocale() {
        if (locale == null) {
            systemLanguageRadioButtonMenuItem.setSelected(true);
        } else {
            for (Enumeration<AbstractButton> en = languageButtonGroup.getElements(); en.hasMoreElements();) {
                AbstractButton e = en.nextElement();
                if (e instanceof LanguageMenuBuilder.LanguageMenuItem) {
                    LanguageMenuBuilder.LanguageMenuItem item = (LanguageMenuBuilder.LanguageMenuItem) e;
                    if (locale.equals(item.getLanguage())) {
                        item.setSelected(true);
                        return;
                    }
                }
            }
            systemLanguageRadioButtonMenuItem.setSelected(true);
        }
    }

    private void setSelectedTemplateLocale() {
        if (model.getOutputLocale() == null) {
            oSystemLRadioButtonMenuItem.setSelected(true);
        } else {
            for (Enumeration<AbstractButton> en = outputLbuttonGroup.getElements(); en.hasMoreElements();) {
                AbstractButton e = en.nextElement();
                if (e instanceof LanguageMenuBuilder.LanguageMenuItem) {
                    LanguageMenuBuilder.LanguageMenuItem item = (LanguageMenuBuilder.LanguageMenuItem) e;
                    if (model.getOutputLocale().equals(item.getLanguage())) {
                        item.setSelected(true);
                        return;
                    }
                }
            }
            oSystemLRadioButtonMenuItem.setSelected(true);
        }
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

        section.put("locale.program", locale != null ? locale.toString() : null);
        section.put("locale.output", model.getOutputLocale() != null ? model.getOutputLocale().toString() : null);

        // save output type
        section.put("output.type", outputTypeButtonGroup.getSelection().getActionCommand());

        // save look and feel
        section.put("look.and.feel", lookAndFeelbuttonGroup.getSelection().getActionCommand());

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
        for (Enumeration<AbstractButton> e = lookAndFeelbuttonGroup.getElements(); e.hasMoreElements();) {
            AbstractButton button = e.nextElement();
            if (button.getActionCommand().equals(laf)) {
                button.setSelected(true);
                break;
            }
        }

        showGTViewMenuItem.setSelected(AppPreferences.getSection(prefs, "trains").get("show.gtview", Boolean.class, true));

        trainsPane.loadFromPreferences(prefs);
        floatingDialogsList.loadFromPreferences(prefs);
        model.loadFromPreferences(prefs);
        trainUnitCyclesPane.loadFromPreferences(prefs);
        driverCyclesPane.loadFromPreferences(prefs);
        engineCyclesPane.loadFromPreferences(prefs);
        circulationPane.loadFromPreferences(prefs);
        freightNetPane2.loadFromPreferences(prefs);

        genTitlePageTTCheckBoxMenuItem.setSelected(model.getProgramSettings().isGenerateTitlePageTT());
        twoSidesPrintCheckBoxMenuItem.setSelected(model.getProgramSettings().isTwoSidedPrint());
        stShowTechTimeCheckBoxMenuItem.setSelected(model.getProgramSettings().isStShowTechTime());

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

    private net.parostroj.timetable.gui.panes.CirculationPane circulationPane;
    private net.parostroj.timetable.gui.panes.FreightNetPane2 freightNetPane2;
    private net.parostroj.timetable.gui.panes.TrainsCyclesPane driverCyclesPane;
    private net.parostroj.timetable.gui.panes.TrainsCyclesPane engineCyclesPane;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JCheckBoxMenuItem genTitlePageTTCheckBoxMenuItem;
    private javax.swing.JCheckBoxMenuItem stShowTechTimeCheckBoxMenuItem;
    private javax.swing.JCheckBoxMenuItem twoSidesPrintCheckBoxMenuItem;
    private javax.swing.ButtonGroup languageButtonGroup;
    private javax.swing.JMenu languageMenu;
    private javax.swing.JMenu lookAndFeelMenu;
    private javax.swing.ButtonGroup lookAndFeelbuttonGroup;
    private net.parostroj.timetable.gui.panes.NetPane netPane;
    private javax.swing.JMenu oLanguageMenu;
    private javax.swing.JRadioButtonMenuItem oSystemLRadioButtonMenuItem;
    private javax.swing.ButtonGroup outputLbuttonGroup;
    private javax.swing.ButtonGroup outputTypeButtonGroup;
    private javax.swing.JMenu scriptsMenu;
    private javax.swing.JCheckBoxMenuItem showGTViewMenuItem;
    private net.parostroj.timetable.gui.StatusBar statusBar;
    private javax.swing.JRadioButtonMenuItem systemLanguageRadioButtonMenuItem;
    private net.parostroj.timetable.gui.panes.TrainsCyclesPane trainUnitCyclesPane;
    private net.parostroj.timetable.gui.panes.TrainsPane trainsPane;
    private javax.swing.JMenu viewsMenu;
}
