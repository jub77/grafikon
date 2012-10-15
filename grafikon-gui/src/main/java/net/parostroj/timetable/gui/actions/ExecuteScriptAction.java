package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;

import net.parostroj.timetable.actions.scripts.PredefinedScriptsLoader;
import net.parostroj.timetable.actions.scripts.ScriptAction;
import net.parostroj.timetable.gui.AppPreferences;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.actions.execution.ActionUtils;
import net.parostroj.timetable.gui.dialogs.ScriptDialog;
import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.Script;
import net.parostroj.timetable.model.Script.Language;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This action executes a script.
 *
 * @author jub
 */
public class ExecuteScriptAction extends AbstractAction {

    private static final Logger LOG = LoggerFactory.getLogger(ExecuteScriptAction.class);

    private final ApplicationModel model;
    private Script lastScript;

    public ExecuteScriptAction(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("".equals(e.getActionCommand())) {
            editScriptExecution(e);
        } else {
            predefinedExecution(e);
        }
    }
    
    private void predefinedExecution(ActionEvent e) {
        Component parent = ActionUtils.getTopLevelComponent(e.getSource());
        parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        long time = System.currentTimeMillis();
        try {
            try {
                ScriptAction scriptAction = PredefinedScriptsLoader.getScriptAction(e.getActionCommand());
                scriptAction.execute(model.getDiagram());
            } finally {
                parent.setCursor(Cursor.getDefaultCursor());
                LOG.debug("Script {} finished in {}ms", e.getActionCommand(), System.currentTimeMillis() - time);
            }
        } catch (GrafikonException ex) {
            LOG.error("Error executing script.", ex);
            String message = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            ActionUtils.showError(message, parent);
        }
        model.setModelChanged(true);
    }

    private void editScriptExecution(ActionEvent e) {
        Component parent = ActionUtils.getTopLevelComponent(e.getSource());
        model.getDiagram();
        ScriptDialog dialog = new ScriptDialog((Frame)parent, true);
        if (lastScript == null) {
            loadScriptFromPreferences();
        }
        dialog.setScript(lastScript);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        // selected script
        Script selectedScript = dialog.getSelectedScript();
        if (selectedScript != null) {
            lastScript = selectedScript;
            // binding
            Map<String, Object> binding = new HashMap<String, Object>();
            binding.put("diagram", model.getDiagram());
            try {
                parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                long time = System.currentTimeMillis();
                // execute
                try {
                    selectedScript.evaluateWithException(binding);
                    saveScriptToPreferences();
                } finally {
                    parent.setCursor(Cursor.getDefaultCursor());
                    LOG.debug("Script execution finished in {}ms", System.currentTimeMillis() - time);
                }
            } catch (GrafikonException ex) {
                LOG.error("Error executing script.", ex);
                String message = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                ActionUtils.showError(message, parent);
            }
            model.setModelChanged(true);
        }
    }
    
    private void loadScriptFromPreferences() {
        String scriptStr = null; 
        try {
            scriptStr = AppPreferences.getPreferences().getString("last.script", null);
        } catch (IOException ex) {
            LOG.error("Error reading script from preferences.", ex);
        }
        if (scriptStr == null) {
            // default script
            scriptStr = "GROOVY:for (train in diagram.trains) {}";
        }
        int location = scriptStr.indexOf(':');
        Language lang = Language.valueOf(scriptStr.substring(0, location));
        String scriptSource = scriptStr.substring(location + 1);
        try {
            lastScript = Script.createScript(scriptSource, lang);
        } catch (GrafikonException e) {
            LOG.error("Error converting script.", e);
        }
    }
    
    private void saveScriptToPreferences() {
        if (lastScript != null)
            try {
                AppPreferences.getPreferences().setString("last.script", lastScript.getLanguage().name() + ":" + lastScript.getSourceCode());
            } catch (IOException e) {
                LOG.error("Error writing script to preferences.", e);
            }
    }
}
