package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Callable;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.actions.execution.*;
import net.parostroj.timetable.gui.actions.impl.CloseableFileChooser;
import net.parostroj.timetable.gui.actions.impl.FileChooserFactory;
import net.parostroj.timetable.gui.actions.impl.ModelUtils;
import net.parostroj.timetable.gui.dialogs.NewModelDialog;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.LSFile;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSFileFactory;
import net.parostroj.timetable.model.templates.TemplateLoader;
import net.parostroj.timetable.utils.ResourceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Combined new/open action.
 *
 * @author jub
 */
public class NewOpenAction extends AbstractAction {

    private static final Logger log = LoggerFactory.getLogger(NewOpenAction.class);
    private final ApplicationModel model;

    private TemplateLoader templateLoader;

    /**
     * creates a new instance
     *
     * @param model application model
     * @param owner frame
     */
    public NewOpenAction(ApplicationModel model, Frame owner, TemplateLoader templateLoader) {
        this.model = model;
        this.templateLoader = templateLoader;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Component parent = GuiComponentUtils.getTopLevelComponent(e.getSource());
        if (e.getActionCommand().equals("open")) {
            this.open(parent, null);
        } else if (e.getActionCommand().equals("new")) {
            this.create(parent);
        } else if (e.getActionCommand().startsWith("open:")) {
            this.open(parent, new File(e.getActionCommand().substring("open:".length())));
        }
    }

    private void open(final Component parent, final File preselectedFile) {
        // check changes
        final int result = ModelUtils.checkModelChangedContinue(model, parent);
        if (result == JOptionPane.CANCEL_OPTION) {
            return;
        }

        // save old diagram
        ActionContext context = new ActionContext(parent);
        if (result == JOptionPane.YES_OPTION) {
            ModelAction saveAction = SaveAction.getSaveModelAction(context, model.getOpenedFile(), parent, model);
            ActionHandler.getInstance().execute(saveAction);
        }

        ModelAction openAction = new CombinedModelAction(context) {

            private int retVal;
            private TrainDiagram diagram;
            private String errorMessage;
            private Exception errorException;
            private File selectedFile;

            @Override
            protected void eventDispatchActionBefore() {
                if (preselectedFile == null) {
                    try (CloseableFileChooser modelFileChooser = FileChooserFactory.getInstance()
                            .getFileChooser(FileChooserFactory.Type.GTM)) {
                        retVal = modelFileChooser.showOpenDialog(parent);
                        if (retVal == JFileChooser.APPROVE_OPTION) {
                            selectedFile = modelFileChooser.getSelectedFile();
                        }
                    }
                } else {
                    selectedFile = preselectedFile;
                    retVal = JFileChooser.APPROVE_OPTION;
                }
            }

            @Override
            protected void backgroundAction() {
                if (retVal != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                setWaitMessage(ResourceLoader.getString("wait.message.loadmodel"));
                setWaitDialogVisible(true);
                long time = System.currentTimeMillis();
                try {
                    try {
                        model.setOpenedFile(selectedFile);
                        log.debug("Loading: {}", selectedFile);
                        LSFile ls = LSFileFactory.getInstance().createForLoad(selectedFile);
                        diagram = ls.load(selectedFile);
                    } catch (LSException e) {
                        log.warn("Error loading model.", e);
                        if (e.getCause() instanceof FileNotFoundException) {
                            // remove from last opened
                            model.removeLastOpenedFile(selectedFile);
                            // create error message
                            errorMessage = ResourceLoader.getString("dialog.error.filenotfound");
                        } else if (e.getCause() instanceof IOException) {
                            errorMessage = ResourceLoader.getString("dialog.error.loading");
                        } else {
                            errorMessage = ResourceLoader.getString("dialog.error.loading");
                            errorException = e;
                        }
                    } catch (Exception e) {
                        log.warn("Error loading model.", e);
                        errorMessage = ResourceLoader.getString("dialog.error.loading");
                    }
                } finally {
                    log.debug("Loaded in {}ms", System.currentTimeMillis() - time);
                    setWaitDialogVisible(false);
                }
            }

            @Override
            protected void eventDispatchActionAfter() {
                if (retVal != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                if (diagram != null) {
                    model.setDiagram(diagram);
                } else {
                    String text = errorMessage + " " + selectedFile.getName();
                    if (errorException != null) {
                        text = text + "\n(" + errorException.getMessage() + ")";
                    }
                    GuiComponentUtils.showError(text, parent);
                    model.setDiagram(null);
                }
            }
        };
        ActionHandler.getInstance().execute(openAction);
    }

    private void create(final Component parent) {
        // check changes
        final int result = ModelUtils.checkModelChangedContinue(model, parent);
        if (result == JOptionPane.CANCEL_OPTION) {
            return;
        }

        // save old diagram
        ActionContext context = new ActionContext(parent);
        if (result == JOptionPane.YES_OPTION) {
            ModelAction saveAction = SaveAction.getSaveModelAction(context, model.getOpenedFile(), parent, model);
            ActionHandler.getInstance().execute(saveAction);
        }

        // new
        ModelAction newAction = new EventDispatchModelAction(context) {

            @Override
            protected void eventDispatchAction() {
                try {
                    // create new model
                    NewModelDialog newModelDialog = new NewModelDialog((Window) parent, true);
                    newModelDialog.setLocationRelativeTo(parent);
                    Callable<TrainDiagram> diagramCreator = newModelDialog.showDialog(templateLoader);
                    newModelDialog.dispose();
                    context.setAttribute("diagramCreator", diagramCreator);
                } catch (LSException error) {
                    log.warn("Cannot load template.", error);
                    JOptionPane.showMessageDialog(parent, error.getMessage(),
                            ResourceLoader.getString("dialog.error.title"), JOptionPane.ERROR_MESSAGE);
                    context.setCancelled(true);
                }
            }
        };
        ModelAction createAction = new EventDispatchAfterModelAction(context) {

            private Callable<TrainDiagram> diagramCreator;
            private TrainDiagram diagram;
            private Exception error;

            @SuppressWarnings("unchecked")
            @Override
            protected boolean check() {
                diagramCreator = (Callable<TrainDiagram>) context.getAttribute("diagramCreator");
                return diagramCreator != null;
            }

            @Override
            protected void backgroundAction() {
                setWaitMessage(ResourceLoader.getString("wait.message.loadmodel"));
                setWaitDialogVisible(true);
                long time = System.currentTimeMillis();
                try {
                    try {
                        diagram = diagramCreator.call();
                    } catch (Exception ex) {
                        error = ex;
                        return;
                    }
                } finally {
                    log.debug("Template loaded in {}ms", System.currentTimeMillis() - time);
                    setWaitDialogVisible(false);
                }
            }

            @Override
            protected void eventDispatchActionAfter() {
                if (diagram != null) {
                    model.setDiagram(diagram);
                    model.setOpenedFile(null);
                    model.setModelChanged(true);
                }

                if (error != null) {
                    log.warn("Cannot load template.", error);
                    JOptionPane.showMessageDialog(parent, error.getMessage(),
                            ResourceLoader.getString("dialog.error.title"), JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        ActionHandler.getInstance().execute(newAction);
        ActionHandler.getInstance().execute(createAction);
    }
}
