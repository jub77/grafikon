package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;

import net.parostroj.timetable.actions.scripts.ScriptsLoader;
import net.parostroj.timetable.actions.scripts.ScriptAction;
import net.parostroj.timetable.gui.AppPreferences;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.actions.execution.ActionUtils;
import net.parostroj.timetable.gui.dialogs.ScriptDialog;
import net.parostroj.timetable.gui.dialogs.ScriptOutputDialog;
import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.Script;
import net.parostroj.timetable.model.Script.Language;

import org.ini4j.spi.EscapeTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This action executes a script.
 *
 * @author jub
 */
public class ExecuteScriptAction extends AbstractAction {

    public static final String MODEL_PREFIX = "model.";
    public static final String GUI_PREFIX = "gui.";

    private static final Logger LOG = LoggerFactory.getLogger(ExecuteScriptAction.class);

    private final ApplicationModel model;
    private Script lastScript;

    public ExecuteScriptAction(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == null || "".equals(e.getActionCommand())) {
            editScriptExecution(e);
        } else {
            String id = null;
            ScriptsLoader loader = null;
            if (e.getActionCommand().startsWith(MODEL_PREFIX)) {
                id = e.getActionCommand().substring(MODEL_PREFIX.length());
                loader = model.getScriptsLoader();
            } else {
                id = e.getActionCommand().substring(GUI_PREFIX.length());
                loader = model.getGuiScriptsLoader();
            }
            predefinedExecution(loader, (Component) e.getSource(), id);
        }
    }

    private void predefinedExecution(ScriptsLoader loader, Component comp, String scriptId) {
        Component parent = ActionUtils.getTopLevelComponent(comp);
        parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        long time = System.currentTimeMillis();
        try {
            try {
                ScriptAction scriptAction = loader.getScriptAction(scriptId);
                scriptAction.execute(model.getDiagram());
            } finally {
                parent.setCursor(Cursor.getDefaultCursor());
                LOG.debug("Script {} finished in {}ms", scriptId, System.currentTimeMillis() - time);
            }
        } catch (GrafikonException ex) {
            LOG.error("Error executing script.", ex);
            String message = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            ActionUtils.showError(message, parent);
        }
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
            CharArrayWriter output = new CharArrayWriter();
            binding.put("output", new PrintWriter(output));
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
                String outString = output.toString();
                if (!outString.isEmpty()) {
                    // show in a window
                    ScriptOutputDialog outputDialog = new ScriptOutputDialog((Frame) parent, true);
                    outputDialog.setText(outString);
                    outputDialog.setLocationRelativeTo(parent);
                    outputDialog.setVisible(true);
                }
            } catch (Exception ex) {
                LOG.warn("Script error: {}: {}", ex.getClass().getName(), ex.getMessage());
                String message = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                ActionUtils.showError(message, parent);
            }
        }
    }

    private void loadScriptFromPreferences() {
        String scriptStr = null;
        try {
            scriptStr = AppPreferences.getSection("scripts").get("last.script");
            scriptStr = scriptStr != null ? EscapeTool.getInstance().unescape(scriptStr) : null;
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
                String scriptStr = lastScript.getLanguage().name() + ":" + lastScript.getSourceCode();
                scriptStr = EscapeTool.getInstance().escape(scriptStr);
                AppPreferences.getSection("scripts").put("last.script", scriptStr);
            } catch (IOException e) {
                LOG.error("Error writing script to preferences.", e);
            }
    }
}
