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
import java.net.MalformedURLException;
import java.net.ProxySelector;
import java.net.URL;
import java.util.*;

import javax.script.ScriptEngineManager;
import javax.swing.*;

import net.parostroj.timetable.actions.scripts.ScriptAction;
import net.parostroj.timetable.gui.actions.*;
import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.ActionHandler;
import net.parostroj.timetable.gui.actions.execution.ModelAction;
import net.parostroj.timetable.gui.actions.impl.CloseableFileChooser;
import net.parostroj.timetable.gui.actions.impl.FileChooserFactory;
import net.parostroj.timetable.gui.actions.impl.ModelUtils;
import net.parostroj.timetable.gui.components.BnButtonGroup;
import net.parostroj.timetable.gui.data.OutputSettings;
import net.parostroj.timetable.gui.dialogs.*;
import net.parostroj.timetable.gui.pm.GenerateOutputPM;
import net.parostroj.timetable.gui.utils.*;
import net.parostroj.timetable.gui.utils.LanguageLoader.LanguagesType;
import net.parostroj.timetable.gui.views.DriverCycleDelegate;
import net.parostroj.timetable.gui.views.EngineCycleDelegate;
import net.parostroj.timetable.gui.views.TrainUnitCycleDelegate;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.LSFile;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSFileFactory;
import net.parostroj.timetable.model.templates.TemplateLoader;
import net.parostroj.timetable.output2.OutputWriter.Settings;
import net.parostroj.timetable.utils.Pair;
import net.parostroj.timetable.utils.ResourceLoader;
import net.parostroj.timetable.utils.VersionInfo;

import org.beanfabrics.ModelProvider;
import org.beanfabrics.Path;
import org.ini4j.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.markusbernhardt.proxy.ProxySearch;

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
    private ExecuteScriptAction executeScriptAction;

    private Map<File, JMenuItem> lastOpened;
    private final List<Component> enabled = new ArrayList<>();
    private final VersionInfo versionInfo;

    public MainFrame(SplashScreenInfo info) {
        versionInfo = new VersionInfo();
        log.debug("Version: {}", versionInfo.getVersion().toCompleteVersionString());
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
        fcf.initialize();

        // init proxy selector
        ProxySearch search = ProxySearch.getDefaultProxySearch();
        ProxySelector.setDefault(search.getProxySelector());
    }

    private String getInfoText(String txt) {
        log.debug(txt);
        return String.format("%s\n%s", versionInfo.getVersion().toVersionString(), txt);
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
        lastOpened = new HashMap<>();

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
            addScriptAction(sd, ExecuteScriptAction.MODEL_PREFIX, scriptsMenuModel);
        }

        // add gui scripts
        for (ScriptAction sd : model.getGuiScriptsLoader().getScriptActions()) {
            addScriptAction(sd, ExecuteScriptAction.GUI_PREFIX, scriptsMenuGui);
        }

        statusBar.setModel(model);
    }

    private void addScriptAction(ScriptAction sd, String type, JMenu sMenu) {
        JMenuItem item = new JMenuItem();
        item.setAction(executeScriptAction);
        item.setText(sd.getLocalizedName());
        item.setActionCommand(type + sd.getId());
        sMenu.add(item);
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
                openItem = new JMenuItem(new NewOpenAction(model, MainFrame.this, null));
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
        String version = versionInfo.getVersion().toVersionString();
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
        javax.swing.JMenu viewsMenu = new javax.swing.JMenu();
        javax.swing.JMenu specialMenu = new javax.swing.JMenu();
        scriptsMenuModel = new javax.swing.JMenu();
        scriptsMenuGui = new javax.swing.JMenu();
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

        TemplateLoader loader = TemplateLoader.getDefault();
        this.addMenuItem(fileMenu, "menu.file.new", new NewOpenAction(model, this, loader), "new", false, null); // NOI18N
        try {
            this.addMenuItem(fileMenu, "menu.file.new.default.url",
                    new NewOpenAction(model, this, TemplateLoader.getFromUrl(new URL(model.getTemplatesBaseUrl()))),
                    "new", false, null); // NOI18N
        } catch (MalformedURLException mue) {
            log.warn(mue.getMessage(), mue);
        }

        fileMenu.add(new javax.swing.JSeparator());

        this.addMenuItem(fileMenu, "menu.file.open", new NewOpenAction(model, this, null), "open", false, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK)); // NOI18N
        this.addMenuItem(fileMenu, "menu.file.save", new SaveAction(model), "save", true, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK)); // NOI18N
        this.addMenuItem(fileMenu, "menu.file.saveas", new SaveAction(model), "save_as", true, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK)); // NOI18N

        fileMenu.add(new javax.swing.JSeparator());

        javax.swing.JMenu importMenu = new javax.swing.JMenu(ResourceLoader.getString("menu.file.importmenu")); // NOI18N
        fileMenu.add(importMenu);

        this.addMenuItem(importMenu, "menu.file.exportimport", new ImportAction(model, this, false, true), null); // NOI18N
        this.addMenuItem(importMenu, "menu.file.exportimport.trains", new ImportAction(model, this, true, false), null); // NOI18N
        this.addMenuItem(importMenu, "menu.file.outputs.import.replace", new ImportReplaceOutputTemplatesAction(model), null); // NOI18N
        this.addMenuItem(importMenu, "menu.file.outputs.import.replace.default.url", new ImportReplaceOutputTemplatesUrlAction(model), null); // NOI18N
        this.addMenuItem(fileMenu, "menu.file.library.export", new ExportAction(model, this), null); // NOI18N

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

        oLanguageMenuItem.setText(ResourceLoader.getString("menu.language.output") + "..."); // NOI18N

        actionMenu.add(oLanguageMenuItem);

        this.addMenuItemWithListener(actionMenu, "menu.action.user.output.templates", evt -> ouputTemplatesMenuItemActionPerformed(evt), true); // NOI18N
        this.addMenuItemWithListener(actionMenu, "menu.action.user.outputs", evt -> ouputMenuItemActionPerformed(evt), true); // NOI18N

        menuBar.add(actionMenu);

        viewsMenu.setText(ResourceLoader.getString("menu.views")); // NOI18N
        menuBar.add(viewsMenu);

        specialMenu.setText(ResourceLoader.getString("menu.special.scripts")); // NOI18N

        this.addMenuItem(specialMenu, "menu.special.execute.script", executeScriptAction, ""); // NOI18N
        specialMenu.add(new javax.swing.JSeparator());
        scriptsMenuModel.setText(ResourceLoader.getString("menu.special.predefined.scripts.model")); // NOI18N
        specialMenu.add(scriptsMenuModel);
        scriptsMenuGui.setText(ResourceLoader.getString("menu.special.predefined.scripts.gui")); // NOI18N
        specialMenu.add(scriptsMenuGui);
        specialMenu.add(new javax.swing.JSeparator());
        this.addMenuItem(specialMenu, "menu.special.recalculate", new RecalculateAction(model), null); // NOI18N
        this.addMenuItem(specialMenu, "menu.special.recalculate.stops", new RecalculateStopsAction(model), null); // NOI18N
        this.addMenuItem(specialMenu, "menu.special.remove.weights", new RemoveWeightsAction(model), null); // NOI18N

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

        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        // enabled list
        enabled.add(tabbedPane);
        enabled.add(scriptsMenuModel);
        enabled.add(scriptsMenuGui);

        setMinimumSize(new java.awt.Dimension(800, 600));
        setSize(getMinimumSize());

        trainUnitCyclesPane.setKey("cycles.trainunit");
        driverCyclesPane.setKey("cycles.driver");
        engineCyclesPane.setKey("cycles.engine");

        // add languages to menu
        LanguageMenuBuilder languageMenuBuilder = new LanguageMenuBuilder(model.getLanguageLoader());

        List<Pair<JRadioButtonMenuItem, Locale>> lItems = languageMenuBuilder.createLanguageMenuItems(ResourceLoader
                .getString("menu.language.system"), LanguagesType.GUI);
        BnButtonGroup<Locale> lBGroup = new BnButtonGroup<>();
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
        BnButtonGroup<String> lafBGroup = new BnButtonGroup<>();
        for (String key : model.lookAndFeel.getValues()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(model.lookAndFeel.getOptions().get(key));
            lafBGroup.add(item, key);
            lookAndFeelMenu.add(item);
        }
        lafBGroup.setModelProvider(provider);
        lafBGroup.setPath(new Path("lookAndFeel"));
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
        EditI18nDialog dialog = new EditI18nDialog(this, true);
        dialog.setLocationRelativeTo(this);
        dialog.registerContext(model.getGuiContext());
        dialog.showDialog(model.getDiagram());
        dialog.dispose();
    }

    private void languagesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        ElementSelectionDialog<Locale> dialog = new ElementSelectionDialog<>(GuiComponentUtils
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
        LSFile fls = null;
        try {
            fls = f.createForSave();
        } catch (LSException e) {
            log.warn("Cannot create FileLoadSave", e);
        }
        AboutDialog dialog = new AboutDialog(this, true,
                String.format(aboutBundle.getString("text"), versionInfo.getVersion().toCompleteVersionString(), fls == null ? "-" : fls.getSaveVersion()),
                getClass().getResource(aboutBundle.getString("image")), true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.dispose();
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
        File outputDirectory = null;
        try (CloseableFileChooser outputChooser = chooserFactory.getFileChooser(FileChooserFactory.Type.OUTPUT_DIRECTORY)) {
            outputDirectory = outputChooser.getSelectedFile() == null
                    ? outputChooser.getCurrentDirectory() : outputChooser.getSelectedFile();
        }
        try (CloseableFileChooser allChooser = chooserFactory.getFileChooser(FileChooserFactory.Type.ALL_FILES)) {
            dialog.showDialog(model.getDiagram(), outputDirectory, allChooser, new Settings(settings.getLocale()));
            dialog.dispose();
        }
    }

    private void ouputMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        // dialog with outputs
        EditOutputsDialog dialog = new EditOutputsDialog(this, true);
        dialog.setSettings(new Settings(model.getOutputSettings().getLocale()));
        GenerateOutputPM pm = new GenerateOutputPM(model.getLanguageLoader().getAvailableLocales(), model.getDiagram().getLocales());
        try (CloseableFileChooser chooser = FileChooserFactory.getInstance().getFileChooser(FileChooserFactory.Type.OUTPUT_DIRECTORY)) {
            pm.init(model.get(), chooser);
            dialog.setPresentationModel(pm);
            dialog.setLocationRelativeTo(this);
            dialog.registerContext(model.getGuiContext());
            dialog.showDialog(model.getDiagram());
            dialog.dispose();
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
        Locale locale = model.getLocale();
        section.put("locale.program", locale != null ? locale.toLanguageTag() : null);
        section.put("locale.output", model.getOutputSettings().getLocale() != null ? model.getOutputSettings().getLocale().toLanguageTag() : null);


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

        // load look and feel
        String laf = section.get("look.and.feel", "system");
        model.lookAndFeel.setValue(laf);

        showGTViewMenuItem.setSelected(AppPreferences.getSection(prefs, "trains").get("show.gtview", Boolean.class, true));

        // load preferences for last file chooser directories
        FileChooserFactory.getInstance().loadFromPreferences(prefs);

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
        NewOpenAction action = new NewOpenAction(model, this, null);
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
    private javax.swing.JMenu scriptsMenuModel;
    private javax.swing.JMenu scriptsMenuGui;
    private javax.swing.JCheckBoxMenuItem showGTViewMenuItem;
    private net.parostroj.timetable.gui.StatusBar statusBar;
}
