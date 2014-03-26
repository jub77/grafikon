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

import net.parostroj.timetable.actions.scripts.PredefinedScriptsLoader;
import net.parostroj.timetable.actions.scripts.ScriptDescription;
import net.parostroj.timetable.gui.actions.*;
import net.parostroj.timetable.gui.actions.RecalculateAction.TrainAction;
import net.parostroj.timetable.gui.actions.execution.*;
import net.parostroj.timetable.gui.actions.impl.FileChooserFactory;
import net.parostroj.timetable.gui.actions.impl.ModelUtils;
import net.parostroj.timetable.gui.actions.impl.OutputCategory;
import net.parostroj.timetable.gui.components.TrainColorChooser;
import net.parostroj.timetable.gui.dialogs.*;
import net.parostroj.timetable.gui.utils.GuiUtils;
import net.parostroj.timetable.gui.views.DriverCycleDelegate;
import net.parostroj.timetable.gui.views.EngineCycleDelegate;
import net.parostroj.timetable.gui.views.TrainUnitCycleDelegate;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.FileLoadSave;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSFileFactory;
import net.parostroj.timetable.utils.ResourceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main frame for the application.
 *
 * @author jub
 */
public class MainFrame extends javax.swing.JFrame implements ApplicationModelListener, StorableGuiData {

    private static final Logger LOG = LoggerFactory.getLogger(MainFrame.class);
    private static final String FRAME_TITLE = "Grafikon";

    private ApplicationModel model;
    private FloatingWindowsList floatingDialogsList;
    private Locale locale;
    private OutputAction outputAction;
    private ExecuteScriptAction executeScriptAction;

    private Map<File, JMenuItem> lastOpened;
    private final List<Component> enabled = new ArrayList<Component>();

    public MainFrame(SplashScreenInfo info) {
        String version = getVersion(false);
        info.setText("Starting Grafikon ...\n" + version);
        this.initializeFrame();
    }

    private String getVersion(boolean complete) {
        ResourceBundle bundle = ResourceBundle.getBundle("grafikon_version");
        String version = bundle.getString(complete ? "grafikon.version" : "grafikon.version.show");
        return version;
    }

    public MainFrame() {
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
            loadedLocale = AppPreferences.getPreferences().getString("locale.program", null);
            String templateLocale = AppPreferences.getPreferences().getString("locale.output", null);
            if (loadedLocale != null) {
                locale = ModelUtils.parseLocale(loadedLocale);
                Locale.setDefault(locale);
            }
            if (templateLocale != null) {
                model.setOutputLocale(ModelUtils.parseLocale(templateLocale));
            }
        } catch (IOException e) {
            LOG.warn("Cannot load preferences.", e);
        }

        outputAction = new OutputAction(model, this);
        executeScriptAction = new ExecuteScriptAction(model);

        initComponents();

        this.addWindowListener(new MainFrameWindowListener(model, this));

        trainsPane.setModel(model);
        engineCyclesPane.setModel(new EngineCycleDelegate(model), new TrainColorChooser() {
            @Override
            public Color getIntervalColor(TimeInterval interval) {
                if (!interval.getTrain().isCovered(TrainsCycleType.ENGINE_CYCLE, interval))
                    return Color.black;
                else
                    return Color.gray;
            }
        });
        trainUnitCyclesPane.setModel(new TrainUnitCycleDelegate(model), new TrainColorChooser() {
            @Override
            public Color getIntervalColor(TimeInterval interval) {
                if (!interval.getTrain().isCovered(TrainsCycleType.TRAIN_UNIT_CYCLE, interval))
                    return Color.black;
                else
                    return Color.gray;
            }
        });
        driverCyclesPane.setModel(new DriverCycleDelegate(model), new TrainColorChooser() {
            @Override
            public Color getIntervalColor(TimeInterval interval) {
                if (!interval.getTrain().isCovered(TrainsCycleType.DRIVER_CYCLE, interval))
                    return Color.black;
                else
                    return Color.gray;
            }
        });
        circulationPane.setModel(model);

        // add languages to menu
        LanguageMenuBuilder languageMenuBuilder = new LanguageMenuBuilder();
        List<LanguageMenuBuilder.LanguageMenuItem> languages = languageMenuBuilder.createLanguageMenuItems();
        ActionListener langListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                languageRadioButtonMenuItemActionPerformed(e);
            }
        };
        for (LanguageMenuBuilder.LanguageMenuItem item : languages) {
            languageMenu.add(item);
            item.addActionListener(langListener);
            languageButtonGroup.add(item);
        }
        List<LanguageMenuBuilder.LanguageMenuItem> oLanguages = languageMenuBuilder.createLanguageMenuItems();
        ActionListener oLangListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outputLanguageRadioButtonMenuItemActionPerformed(e);
            }
        };
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
            AppPreferences.getPreferences().load();
            this.loadFromPreferences(AppPreferences.getPreferences());
        } catch (IOException e) {
            LOG.error("Error loading preferences.", e);
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
        for (ScriptDescription sd : PredefinedScriptsLoader.getPredefinedScripts()) {
            JMenuItem item = new JMenuItem();
            item.setAction(executeScriptAction);
            item.setText(sd.getLocalizedName());
            item.setActionCommand(sd.getId());
            scriptsMenu.add(item);
        }

        statusBar.setModel(model);
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
        ModelActionUtilities.runLaterInEDT(new Runnable() {

            @Override
            public void run() {
                JMenuItem removed = lastOpened.remove(file);
                if (removed != null) {
                    fileMenu.remove(removed);
                    refreshLastOpenedFiles();
                }
            }
        });
    }

    private void addLastOpenedFile(final File file) {
        ModelActionUtilities.runLaterInEDT(new Runnable() {

            @Override
            public void run() {
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
            }
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
            if (model.getOpenedFile() == null)
                title += " - " + ResourceLoader.getString("title.new");
            else
                title += " - " + model.getOpenedFile().getName();
            if (b)
                title += " *";
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
        statusBar = new net.parostroj.timetable.gui.StatusBar();
        javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem fileNewMenuItem = new javax.swing.JMenuItem();
        javax.swing.JSeparator separator3 = new javax.swing.JSeparator();
        javax.swing.JMenuItem fileOpenMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem fileSaveMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem fileSaveAsMenuItem = new javax.swing.JMenuItem();
        javax.swing.JSeparator separator1 = new javax.swing.JSeparator();
        javax.swing.JMenuItem fileImportMenuItem = new javax.swing.JMenuItem();
        javax.swing.JSeparator separator5 = new javax.swing.JSeparator();
        languageMenu = new javax.swing.JMenu();
        systemLanguageRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        lookAndFeelMenu = new javax.swing.JMenu();
        javax.swing.JRadioButtonMenuItem systemLAFRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        javax.swing.JSeparator separator2 = new javax.swing.JSeparator();
        javax.swing.JSeparator separator4 = new javax.swing.JSeparator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu diagramMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem settingsMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem groupsMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem editRoutesMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem imagesMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem textItemsMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem infoMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem trainTypesMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem lineClassesMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem weightTablesMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem penaltyTableMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu actionMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem trainTimetableListMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem nodeTimetableListMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem ecListMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem tucListMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem dcListMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem spListMenuItem = new javax.swing.JMenuItem();
        javax.swing.JSeparator jSeparator1 = new javax.swing.JSeparator();
        javax.swing.JMenuItem trainTimetableListByDcMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem trainTimetableListByTimeFilteredMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem epListMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem ccListMenuItem = new javax.swing.JMenuItem();
        javax.swing.JSeparator jSeparator2 = new javax.swing.JSeparator();
        javax.swing.JMenuItem trainTimetableListByDcSelectMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem trainTimetableListByRoutesMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem nodeTimetableListSelectMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem ecListSelectMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem tucListSelectMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem dcListSelectMenuItem = new javax.swing.JMenuItem();
        javax.swing.JSeparator jSeparator4 = new javax.swing.JSeparator();
        javax.swing.JMenuItem allHtmlMenuItem = new javax.swing.JMenuItem();
        javax.swing.JSeparator jSeparator3 = new javax.swing.JSeparator();
        oLanguageMenu = new javax.swing.JMenu();
        oSystemLRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        javax.swing.JMenu outputTypeMenu = new javax.swing.JMenu();
        javax.swing.JRadioButtonMenuItem htmlRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        javax.swing.JRadioButtonMenuItem htmlSelectRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        javax.swing.JRadioButtonMenuItem xmlRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        genTitlePageTTCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        stShowTechTimeCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        javax.swing.JSeparator jSeparator5 = new javax.swing.JSeparator();
        javax.swing.JMenuItem ouputTemplatesMenuItem = new javax.swing.JMenuItem();
        viewsMenu = new javax.swing.JMenu();
        javax.swing.JMenu specialMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem recalculateMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem recalculateStopsMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem removeWeightsMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem executeScriptMenuItem = new javax.swing.JMenuItem();
        scriptsMenu = new javax.swing.JMenu();
        javax.swing.JMenu settingsMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem columnsMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem sortColumnsMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem resizeColumnsMenuItem = new javax.swing.JMenuItem();
        showGTViewMenuItem = new javax.swing.JCheckBoxMenuItem();
        javax.swing.JMenuItem programSettingsMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(this.getTitleString(false));
        setLocationByPlatform(true);

        tabbedPane.addTab(ResourceLoader.getString("tab.trains"), trainsPane); // NOI18N
        tabbedPane.addTab(ResourceLoader.getString("tab.engine.cycles"), engineCyclesPane); // NOI18N
        tabbedPane.addTab(ResourceLoader.getString("tab.trainunit.cycle"), trainUnitCyclesPane); // NOI18N
        tabbedPane.addTab(ResourceLoader.getString("tab.driver.cycles"), driverCyclesPane); // NOI18N
        tabbedPane.addTab(ResourceLoader.getString("tab.net"), netPane); // NOI18N
        tabbedPane.addTab(ResourceLoader.getString("tab.circulations"), circulationPane); // NOI18N

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        getContentPane().add(statusBar, BorderLayout.SOUTH);

        tabbedPane.getAccessibleContext().setAccessibleName(ResourceLoader.getString("tab.trains")); // NOI18N

        fileMenu.setText(ResourceLoader.getString("menu.file")); // NOI18N

        fileNewMenuItem.setAction(new net.parostroj.timetable.gui.actions.NewOpenAction(model, this));
        fileNewMenuItem.setText(ResourceLoader.getString("menu.file.new")); // NOI18N
        fileNewMenuItem.setActionCommand("new");
        fileMenu.add(fileNewMenuItem);
        fileMenu.add(separator3);

        fileOpenMenuItem.setAction(new net.parostroj.timetable.gui.actions.NewOpenAction(model, this));
        fileOpenMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        fileOpenMenuItem.setText(ResourceLoader.getString("menu.file.open")); // NOI18N
        fileOpenMenuItem.setActionCommand("open");
        fileMenu.add(fileOpenMenuItem);

        fileSaveMenuItem.setAction(new SaveAction(model));
        fileSaveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        fileSaveMenuItem.setText(ResourceLoader.getString("menu.file.save")); // NOI18N
        fileSaveMenuItem.setActionCommand("save");
        fileMenu.add(fileSaveMenuItem);

        fileSaveAsMenuItem.setAction(new SaveAction(model));
        fileSaveAsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        fileSaveAsMenuItem.setText(ResourceLoader.getString("menu.file.saveas")); // NOI18N
        fileSaveAsMenuItem.setActionCommand("save_as");
        fileMenu.add(fileSaveAsMenuItem);
        fileMenu.add(separator1);

        fileImportMenuItem.setAction(new ImportAction(model, this, false));
        fileImportMenuItem.setText(ResourceLoader.getString("menu.file.exportimport")); // NOI18N
        fileMenu.add(fileImportMenuItem);

        javax.swing.JMenuItem fileImportGroupMenuItem = new javax.swing.JMenuItem();
        fileImportGroupMenuItem.setAction(new ImportAction(model, this, true));
        fileImportGroupMenuItem.setText(ResourceLoader.getString("menu.file.exportimport.trains")); // NOI18N
        fileMenu.add(fileImportGroupMenuItem);
        fileMenu.add(separator5);

        languageMenu.setText(ResourceLoader.getString("menu.language")); // NOI18N

        languageButtonGroup.add(systemLanguageRadioButtonMenuItem);
        systemLanguageRadioButtonMenuItem.setSelected(true);
        systemLanguageRadioButtonMenuItem.setText(ResourceLoader.getString("menu.language.system")); // NOI18N
        systemLanguageRadioButtonMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                languageRadioButtonMenuItemActionPerformed(evt);
            }
        });
        languageMenu.add(systemLanguageRadioButtonMenuItem);

        fileMenu.add(languageMenu);

        lookAndFeelMenu.setText(ResourceLoader.getString("menu.lookandfeel")); // NOI18N

        lookAndFeelbuttonGroup.add(systemLAFRadioButtonMenuItem);
        systemLAFRadioButtonMenuItem.setSelected(true);
        systemLAFRadioButtonMenuItem.setText(ResourceLoader.getString("menu.lookandfeel.system")); // NOI18N
        systemLAFRadioButtonMenuItem.setActionCommand("system");
        lookAndFeelMenu.add(systemLAFRadioButtonMenuItem);

        fileMenu.add(lookAndFeelMenu);
        fileMenu.add(separator2);
        fileMenu.add(separator4);

        exitMenuItem.setAction(new ExitAction(model, this));
        exitMenuItem.setText(ResourceLoader.getString("menu.file.exit")); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        diagramMenu.setText(ResourceLoader.getString("menu.diagram")); // NOI18N

        settingsMenuItem.setText(ResourceLoader.getString("menu.file.settings")); // NOI18N
        settingsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsMenuItemActionPerformed(evt);
            }
        });
        diagramMenu.add(settingsMenuItem);

        editRoutesMenuItem.setText(ResourceLoader.getString("gt.routes.edit")); // NOI18N
        editRoutesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editRoutesMenuItemActionPerformed(evt);
            }
        });
        diagramMenu.add(editRoutesMenuItem);

        imagesMenuItem.setText(ResourceLoader.getString("menu.file.images")); // NOI18N
        imagesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imagesMenuItemActionPerformed(evt);
            }
        });
        diagramMenu.add(imagesMenuItem);

        textItemsMenuItem.setText(ResourceLoader.getString("menu.file.textitems")); // NOI18N
        textItemsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textItemsMenuItemActionPerformed(evt);
            }
        });
        diagramMenu.add(textItemsMenuItem);

        infoMenuItem.setText(ResourceLoader.getString("menu.file.info")); // NOI18N
        infoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoMenuItemActionPerformed(evt);
            }
        });
        diagramMenu.add(infoMenuItem);

        trainTypesMenuItem.setText(ResourceLoader.getString("menu.file.traintypes")); // NOI18N
        trainTypesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trainTypesMenuItemActionPerformed(evt);
            }
        });
        diagramMenu.add(trainTypesMenuItem);

        lineClassesMenuItem.setText(ResourceLoader.getString("menu.file.lineclasses")); // NOI18N
        lineClassesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineClassesMenuItemActionPerformed(evt);
            }
        });
        diagramMenu.add(lineClassesMenuItem);

        weightTablesMenuItem.setText(ResourceLoader.getString("menu.file.weighttables")); // NOI18N
        weightTablesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weightTablesMenuItemActionPerformed(evt);
            }
        });
        diagramMenu.add(weightTablesMenuItem);

        penaltyTableMenuItem.setText(ResourceLoader.getString("menu.file.penaltytable")); // NOI18N
        penaltyTableMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                penaltyTableMenuItemActionPerformed(evt);
            }
        });
        diagramMenu.add(penaltyTableMenuItem);

        groupsMenuItem.setAction(new EditGroupsAction(model));
        groupsMenuItem.setText(ResourceLoader.getString("menu.groups") + "...");
        diagramMenu.add(groupsMenuItem);

        menuBar.add(diagramMenu);

        actionMenu.setAction(outputAction);
        actionMenu.setText(ResourceLoader.getString("menu.outputs")); // NOI18N
        actionMenu.setActionCommand("stations_select");

        trainTimetableListMenuItem.setAction(outputAction);
        trainTimetableListMenuItem.setText(ResourceLoader.getString("menu.action.traintimetableslist")); // NOI18N
        trainTimetableListMenuItem.setActionCommand("trains");
        actionMenu.add(trainTimetableListMenuItem);

        nodeTimetableListMenuItem.setAction(outputAction);
        nodeTimetableListMenuItem.setText(ResourceLoader.getString("menu.action.nodetimetableslist")); // NOI18N
        nodeTimetableListMenuItem.setActionCommand("stations");
        actionMenu.add(nodeTimetableListMenuItem);

        ecListMenuItem.setAction(outputAction);
        ecListMenuItem.setText(ResourceLoader.getString("menu.action.eclist")); // NOI18N
        ecListMenuItem.setActionCommand("engine_cycles");
        actionMenu.add(ecListMenuItem);

        tucListMenuItem.setAction(outputAction);
        tucListMenuItem.setText(ResourceLoader.getString("menu.action.tuclist")); // NOI18N
        tucListMenuItem.setActionCommand("train_unit_cycles");
        actionMenu.add(tucListMenuItem);

        dcListMenuItem.setAction(outputAction);
        dcListMenuItem.setText(ResourceLoader.getString("menu.acion.dclist")); // NOI18N
        dcListMenuItem.setActionCommand("driver_cycles");
        actionMenu.add(dcListMenuItem);

        spListMenuItem.setAction(outputAction);
        spListMenuItem.setText(ResourceLoader.getString("menu.acion.splist")); // NOI18N
        spListMenuItem.setActionCommand("starts");
        actionMenu.add(spListMenuItem);
        actionMenu.add(jSeparator1);

        trainTimetableListByDcMenuItem.setAction(outputAction);
        trainTimetableListByDcMenuItem.setText(ResourceLoader.getString("menu.action.traintimetableslistbydc")); // NOI18N
        trainTimetableListByDcMenuItem.setActionCommand("trains_by_driver_cycles");
        actionMenu.add(trainTimetableListByDcMenuItem);

        trainTimetableListByTimeFilteredMenuItem.setAction(outputAction);
        trainTimetableListByTimeFilteredMenuItem.setText(ResourceLoader.getString("menu.action.traintimetableslistbytimefiltered")); // NOI18N
        trainTimetableListByTimeFilteredMenuItem.setActionCommand("trains_select_station");
        actionMenu.add(trainTimetableListByTimeFilteredMenuItem);

        epListMenuItem.setAction(outputAction);
        epListMenuItem.setText(ResourceLoader.getString("menu.acion.eplist")); // NOI18N
        epListMenuItem.setActionCommand("ends");
        actionMenu.add(epListMenuItem);

        ccListMenuItem.setAction(outputAction);
        ccListMenuItem.setText(ResourceLoader.getString("menu.acion.cclist")); // NOI18N
        ccListMenuItem.setActionCommand("custom_cycles");
        actionMenu.add(ccListMenuItem);
        actionMenu.add(jSeparator2);

        trainTimetableListByDcSelectMenuItem.setAction(outputAction);
        trainTimetableListByDcSelectMenuItem.setText(ResourceLoader.getString("menu.action.traintimetableslistbydc.select")); // NOI18N
        trainTimetableListByDcSelectMenuItem.setActionCommand("trains_select_driver_cycles");
        actionMenu.add(trainTimetableListByDcSelectMenuItem);

        trainTimetableListByRoutesMenuItem.setAction(outputAction);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("gt_texts"); // NOI18N
        trainTimetableListByRoutesMenuItem.setText(bundle.getString("menu.action.traintimetableslistbyroutes.select")); // NOI18N
        trainTimetableListByRoutesMenuItem.setActionCommand("trains_select_routes");
        actionMenu.add(trainTimetableListByRoutesMenuItem);

        nodeTimetableListSelectMenuItem.setAction(outputAction);
        nodeTimetableListSelectMenuItem.setText(ResourceLoader.getString("menu.action.nodetimetableslist.select")); // NOI18N
        nodeTimetableListSelectMenuItem.setActionCommand("stations_select");
        actionMenu.add(nodeTimetableListSelectMenuItem);

        ecListSelectMenuItem.setAction(outputAction);
        ecListSelectMenuItem.setText(ResourceLoader.getString("menu.action.eclist.select")); // NOI18N
        ecListSelectMenuItem.setActionCommand("engine_cycles_select");
        actionMenu.add(ecListSelectMenuItem);

        tucListSelectMenuItem.setAction(outputAction);
        tucListSelectMenuItem.setText(ResourceLoader.getString("menu.action.tuclist.select")); // NOI18N
        tucListSelectMenuItem.setActionCommand("train_unit_cycles_select");
        actionMenu.add(tucListSelectMenuItem);

        dcListSelectMenuItem.setAction(outputAction);
        dcListSelectMenuItem.setText(ResourceLoader.getString("menu.acion.dclist.select")); // NOI18N
        dcListSelectMenuItem.setActionCommand("driver_cycles_select");
        actionMenu.add(dcListSelectMenuItem);
        actionMenu.add(jSeparator4);

        allHtmlMenuItem.setAction(outputAction);
        allHtmlMenuItem.setText(ResourceLoader.getString("menu.action.all.html")); // NOI18N
        allHtmlMenuItem.setActionCommand("all");
        actionMenu.add(allHtmlMenuItem);
        actionMenu.add(jSeparator3);

        oLanguageMenu.setText(ResourceLoader.getString("menu.language.output")); // NOI18N

        outputLbuttonGroup.add(oSystemLRadioButtonMenuItem);
        oSystemLRadioButtonMenuItem.setSelected(true);
        oSystemLRadioButtonMenuItem.setText(ResourceLoader.getString("menu.language.program")); // NOI18N
        oSystemLRadioButtonMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputLanguageRadioButtonMenuItemActionPerformed(evt);
            }
        });
        oLanguageMenu.add(oSystemLRadioButtonMenuItem);

        actionMenu.add(oLanguageMenu);

        outputTypeMenu.setText(ResourceLoader.getString("menu.output.type")); // NOI18N

        outputTypeButtonGroup.add(htmlRadioButtonMenuItem);
        htmlRadioButtonMenuItem.setSelected(true);
        htmlRadioButtonMenuItem.setText(ResourceLoader.getString("menu.output.type.html")); // NOI18N
        htmlRadioButtonMenuItem.setActionCommand("html");
        htmlRadioButtonMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputTypeActionPerformed(evt);
            }
        });
        outputTypeMenu.add(htmlRadioButtonMenuItem);

        outputTypeButtonGroup.add(htmlSelectRadioButtonMenuItem);
        htmlSelectRadioButtonMenuItem.setText(ResourceLoader.getString("menu.output.type.htmlselect")); // NOI18N
        htmlSelectRadioButtonMenuItem.setActionCommand("html.select");
        htmlSelectRadioButtonMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputTypeActionPerformed(evt);
            }
        });
        outputTypeMenu.add(htmlSelectRadioButtonMenuItem);

        outputTypeButtonGroup.add(xmlRadioButtonMenuItem);
        xmlRadioButtonMenuItem.setText(ResourceLoader.getString("menu.output.type.xml")); // NOI18N
        xmlRadioButtonMenuItem.setActionCommand("xml");
        xmlRadioButtonMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputTypeActionPerformed(evt);
            }
        });
        outputTypeMenu.add(xmlRadioButtonMenuItem);

        javax.swing.JRadioButtonMenuItem pdfRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        outputTypeButtonGroup.add(pdfRadioButtonMenuItem);
        pdfRadioButtonMenuItem.setText(ResourceLoader.getString("menu.output.type.pdf")); // NOI18N
        pdfRadioButtonMenuItem.setActionCommand("pdf");
        pdfRadioButtonMenuItem.addActionListener(new java.awt.event.ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt) {
                outputTypeActionPerformed(evt);
            }
        });
        outputTypeMenu.add(pdfRadioButtonMenuItem);

        actionMenu.add(outputTypeMenu);

        genTitlePageTTCheckBoxMenuItem.setSelected(true);
        genTitlePageTTCheckBoxMenuItem.setText(bundle.getString("menu.action.traintimetables.generate.titlepage")); // NOI18N
        genTitlePageTTCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boolean selected = ((JCheckBoxMenuItem) evt.getSource()).isSelected();
                model.getProgramSettings().setGenerateTitlePageTT(selected);
            }
        });
        actionMenu.add(genTitlePageTTCheckBoxMenuItem);

        twoSidesPrintCheckBoxMenuItem = new JCheckBoxMenuItem(bundle.getString("menu.action.traintimetables.two.sides.print")); //$NON-NLS-1$
        twoSidesPrintCheckBoxMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean selected = ((JCheckBoxMenuItem) e.getSource()).isSelected();
                model.getProgramSettings().setTwoSidedPrint(selected);
            }
        });
        actionMenu.add(twoSidesPrintCheckBoxMenuItem);

        stShowTechTimeCheckBoxMenuItem.setSelected(false);
        stShowTechTimeCheckBoxMenuItem.setText(bundle.getString("menu.action.traintimetables.show.tech.time")); // NOI18N
        stShowTechTimeCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean selected = ((JCheckBoxMenuItem) e.getSource()).isSelected();
                model.getProgramSettings().setStShowTechTime(selected);
            }
        });
        actionMenu.add(stShowTechTimeCheckBoxMenuItem);
        actionMenu.add(jSeparator5);

        ouputTemplatesMenuItem.setText(ResourceLoader.getString("menu.action.user.output.templates")); // NOI18N
        ouputTemplatesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ouputTemplatesMenuItemActionPerformed(evt);
            }
        });
        actionMenu.add(ouputTemplatesMenuItem);

        menuBar.add(actionMenu);

        viewsMenu.setText(ResourceLoader.getString("menu.views")); // NOI18N
        menuBar.add(viewsMenu);

        specialMenu.setText(ResourceLoader.getString("menu.special")); // NOI18N

        recalculateMenuItem.setAction(new RecalculateAction(model));
        recalculateMenuItem.setText(ResourceLoader.getString("menu.special.recalculate")); // NOI18N
        specialMenu.add(recalculateMenuItem);

        recalculateStopsMenuItem.setAction(new RecalculateStopsAction(model));
        recalculateStopsMenuItem.setText(ResourceLoader.getString("menu.special.recalculate.stops")); // NOI18N
        specialMenu.add(recalculateStopsMenuItem);

        removeWeightsMenuItem.setAction(new RemoveWeightsAction(model));
        removeWeightsMenuItem.setText(ResourceLoader.getString("menu.special.remove.weights")); // NOI18N
        specialMenu.add(removeWeightsMenuItem);

        executeScriptMenuItem.setAction(executeScriptAction);
        executeScriptMenuItem.setText(ResourceLoader.getString("menu.special.execute.script")); // NOI18N
        executeScriptMenuItem.setActionCommand("");
        specialMenu.add(executeScriptMenuItem);

        scriptsMenu.setText(ResourceLoader.getString("menu.special.predefined.scripts")); // NOI18N
        specialMenu.add(scriptsMenu);

        menuBar.add(specialMenu);

        settingsMenu.setText(ResourceLoader.getString("menu.settings")); // NOI18N

        columnsMenuItem.setText(ResourceLoader.getString("menu.settings.columns")); // NOI18N
        columnsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                columnsMenuItemActionPerformed(evt);
            }
        });
        settingsMenu.add(columnsMenuItem);

        sortColumnsMenuItem.setText(ResourceLoader.getString("menu.settings.sort.columns")); // NOI18N
        sortColumnsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortColumnsMenuItemActionPerformed(evt);
            }
        });
        settingsMenu.add(sortColumnsMenuItem);

        resizeColumnsMenuItem.setText(ResourceLoader.getString("menu.settings.resize.columns")); // NOI18N
        resizeColumnsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                resizeColumnsMenuItemActionPerformed(evt);
            }
        });
        settingsMenu.add(resizeColumnsMenuItem);

        showGTViewMenuItem.setSelected(true);
        showGTViewMenuItem.setText(ResourceLoader.getString("menu.settings.show.gtview")); // NOI18N
        showGTViewMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showGTViewMenuItemActionPerformed(evt);
            }
        });
        settingsMenu.add(showGTViewMenuItem);

        programSettingsMenuItem.setText(ResourceLoader.getString("menu.program.settings")); // NOI18N
        programSettingsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                programSettingsMenuItemActionPerformed(evt);
            }
        });
        settingsMenu.add(programSettingsMenuItem);

        menuBar.add(settingsMenu);

        helpMenu.setText(ResourceLoader.getString("menu.help")); // NOI18N

        aboutMenuItem.setText(ResourceLoader.getString("menu.help.about")); // NOI18N
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        // enabled list
        enabled.add(tabbedPane);
        enabled.add(fileSaveMenuItem);
        enabled.add(fileSaveAsMenuItem);
        enabled.add(trainTimetableListMenuItem);
        enabled.add(trainTimetableListByDcMenuItem);
        enabled.add(recalculateMenuItem);
        enabled.add(recalculateStopsMenuItem);
        enabled.add(nodeTimetableListMenuItem);
        enabled.add(ecListMenuItem);
        enabled.add(dcListMenuItem);
        enabled.add(ccListMenuItem);
        enabled.add(tucListMenuItem);
        enabled.add(settingsMenuItem);
        enabled.add(groupsMenuItem);
        enabled.add(allHtmlMenuItem);
        enabled.add(imagesMenuItem);
        enabled.add(textItemsMenuItem);
        enabled.add(infoMenuItem);
        enabled.add(spListMenuItem);
        enabled.add(epListMenuItem);
        enabled.add(trainTypesMenuItem);
        enabled.add(lineClassesMenuItem);
        enabled.add(weightTablesMenuItem);
        enabled.add(penaltyTableMenuItem);
        enabled.add(trainTimetableListByTimeFilteredMenuItem);
        enabled.add(fileImportMenuItem);
        enabled.add(fileImportGroupMenuItem);
        enabled.add(dcListSelectMenuItem);
        enabled.add(trainTimetableListByDcSelectMenuItem);
        enabled.add(nodeTimetableListSelectMenuItem);
        enabled.add(ecListSelectMenuItem);
        enabled.add(tucListSelectMenuItem);
        enabled.add(editRoutesMenuItem);
        enabled.add(trainTimetableListByRoutesMenuItem);
        enabled.add(removeWeightsMenuItem);
        enabled.add(executeScriptMenuItem);
        enabled.add(scriptsMenu);
        enabled.add(ouputTemplatesMenuItem);

        setMinimumSize(new java.awt.Dimension(800, 600));
        setSize(getMinimumSize());
    }

    private void settingsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        SettingsDialog settingsDialog = new SettingsDialog(this, true);
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.showDialog(model.getDiagram());
        settingsDialog.dispose();
        // check if recalculate should be executed
        ActionContext context = new ActionContext(ActionUtils.getTopLevelComponent(this));
        if (settingsDialog.isRecalculate()) {
            ModelAction action = RecalculateAction.getAllTrainsAction(context, model.getDiagram(), new TrainAction() {

                @Override
                public void execute(Train train) throws Exception {
                    train.recalculate();
                    // round correctly stops
                    TimeConverter converter = train.getTrainDiagram().getTimeConverter();
                    for (TimeInterval interval : train.getTimeIntervalList()) {
                    	if (interval.isNodeOwner()) {
                    		train.changeStopTime(interval, converter.round(interval.getLength()));
                    	}
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
        if (systemLanguageRadioButtonMenuItem.isSelected())
            locale = null;
        else if (evt.getSource() instanceof LanguageMenuBuilder.LanguageMenuItem) {
            locale = ((LanguageMenuBuilder.LanguageMenuItem)evt.getSource()).getLanguage();
        }
    }

    private void outputLanguageRadioButtonMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        if (oSystemLRadioButtonMenuItem.isSelected())
            model.setOutputLocale(null);
        else if (evt.getSource() instanceof LanguageMenuBuilder.LanguageMenuItem) {
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
        LineClassesDialog lineClassesDialog = new LineClassesDialog(this, true);
        lineClassesDialog.setLocationRelativeTo(this);
        lineClassesDialog.showDialog(model.getDiagram());
        lineClassesDialog.dispose();
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
        dialog.setTrainDiagram(model.getDiagram());
        dialog.updateValues();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        // show about dialog
        ResourceBundle aboutBundle = ResourceBundle.getBundle("about");
        LSFileFactory f = LSFileFactory.getInstance();
        FileLoadSave fls = null;
        try {
            fls = f.createLatestForSave();
        } catch (LSException e) {
            LOG.warn("Cannot create FileLoadSave", e);
        }
        AboutDialog dialog = new AboutDialog(this, true,
                String.format(aboutBundle.getString("text"), getVersion(true), fls == null ? "-" : fls.getSaveVersion()),
                getClass().getResource(aboutBundle.getString("image")), true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
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
        dialog.showDialog(model);
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
                new OutputTemplateAction.Settings(settings.isGenerateTitlePageTT(), settings.isTwoSidedPrint(), settings.isStShowTechTime()));
        dialog.dispose();
    }

    private void setSelectedLocale() {
        if (locale == null)
            systemLanguageRadioButtonMenuItem.setSelected(true);
        else {
            for (Enumeration<AbstractButton> en = languageButtonGroup.getElements(); en.hasMoreElements();) {
                AbstractButton e = en.nextElement();
                if (e instanceof LanguageMenuBuilder.LanguageMenuItem) {
                    LanguageMenuBuilder.LanguageMenuItem item = (LanguageMenuBuilder.LanguageMenuItem)e;
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
        if (model.getOutputLocale() == null)
            oSystemLRadioButtonMenuItem.setSelected(true);
        else {
            for (Enumeration<AbstractButton> en = outputLbuttonGroup.getElements(); en.hasMoreElements();) {
                AbstractButton e = en.nextElement();
                if (e instanceof LanguageMenuBuilder.LanguageMenuItem) {
                    LanguageMenuBuilder.LanguageMenuItem item = (LanguageMenuBuilder.LanguageMenuItem)e;
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
            AppPreferences prefs = AppPreferences.getPreferences();
            this.saveToPreferences(prefs);
            prefs.save();
        } catch (IOException ex) {
            LOG.error("Error saving preferences.", ex);
        }
    }

    @Override
    public void saveToPreferences(AppPreferences prefs) {
        boolean maximized = (this.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0;
        prefs.removeWithPrefix("main.");

        prefs.setBoolean("main.maximized", maximized);

        if (!maximized) {
            // set position
            String positionStr = GuiUtils.getPosition(this);
            prefs.setString("main.position", positionStr);
        }

        // save to preferences last file chooser directories
        FileChooserFactory.getInstance().saveToPreferences(prefs);

        // save locales
        if (locale != null)
            prefs.setString("locale.program", locale.toString());
        else
            prefs.remove("locale.program");
        if (model.getOutputLocale() != null)
            prefs.setString("locale.output", model.getOutputLocale().toString());
        else
            prefs.remove("locale.output");

        // save output type
        prefs.setString("output.type", outputTypeButtonGroup.getSelection().getActionCommand());

        // save look and feel
        prefs.setString("look.and.feel", lookAndFeelbuttonGroup.getSelection().getActionCommand());

        trainsPane.saveToPreferences(prefs);
        floatingDialogsList.saveToPreferences(prefs);
        model.saveToPreferences(prefs);
        trainUnitCyclesPane.saveToPreferences(prefs);
        driverCyclesPane.saveToPreferences(prefs);
        engineCyclesPane.saveToPreferences(prefs);
        circulationPane.saveToPreferences(prefs);
    }

    @Override
    public void loadFromPreferences(AppPreferences prefs) {
        if (prefs.getBoolean("main.maximized", false)) {
            // setting maximized state
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            if (prefs.contains("main.position"))
                // set position
                GuiUtils.setPosition(prefs.getString("main.position", null), this);
        }

        // load output type
        String aC = prefs.getString("output.type", "html");
        Enumeration<AbstractButton> e = outputTypeButtonGroup.getElements();
        while (e.hasMoreElements()) {
            AbstractButton button = e.nextElement();
            if (button.getActionCommand().equals(aC)) {
                button.setSelected(true);
                model.setOutputCategory(OutputCategory.fromString(button.getActionCommand()));
                break;
            }
        }

        // load look and feel
        String laf = prefs.getString("look.and.feel", "system");
        e = lookAndFeelbuttonGroup.getElements();
        while (e.hasMoreElements()) {
            AbstractButton button = e.nextElement();
            if (button.getActionCommand().equals(laf)) {
                button.setSelected(true);
                break;
            }
        }

        showGTViewMenuItem.setSelected(prefs.getBoolean("trains.show.gtview", true));

        trainsPane.loadFromPreferences(prefs);
        floatingDialogsList.loadFromPreferences(prefs);
        model.loadFromPreferences(prefs);
        trainUnitCyclesPane.loadFromPreferences(prefs);
        driverCyclesPane.loadFromPreferences(prefs);
        engineCyclesPane.loadFromPreferences(prefs);
        circulationPane.loadFromPreferences(prefs);

        genTitlePageTTCheckBoxMenuItem.setSelected(model.getProgramSettings().isGenerateTitlePageTT());
        twoSidesPrintCheckBoxMenuItem.setSelected(model.getProgramSettings().isTwoSidedPrint());
        stShowTechTimeCheckBoxMenuItem.setSelected(model.getProgramSettings().isStShowTechTime());
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
    private net.parostroj.timetable.gui.panes.TrainsCyclesPane driverCyclesPane;
    private net.parostroj.timetable.gui.panes.TrainsCyclesPane engineCyclesPane;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JCheckBoxMenuItem genTitlePageTTCheckBoxMenuItem;
    private javax.swing.JCheckBoxMenuItem stShowTechTimeCheckBoxMenuItem;
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
    private JCheckBoxMenuItem twoSidesPrintCheckBoxMenuItem;
}
