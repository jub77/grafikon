package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import javax.swing.AbstractAction;

import net.parostroj.timetable.actions.scripts.ScriptsLoader;
import net.parostroj.timetable.actions.scripts.ScriptAction;
import net.parostroj.timetable.gui.AppPreferences;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.dialogs.ScriptDialog;
import net.parostroj.timetable.gui.dialogs.ScriptOutputDialog;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.Script;
import net.parostroj.timetable.model.Script.Language;
import net.parostroj.timetable.utils.ObjectsUtil;

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

    private static final Logger log = LoggerFactory.getLogger(ExecuteScriptAction.class);

    private final ApplicationModel model;
    private Script lastScript;

    public ExecuteScriptAction(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (ObjectsUtil.isEmpty(e.getActionCommand())) {
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
        Component parent = GuiComponentUtils.getTopLevelComponent(comp);
        parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        long time = System.currentTimeMillis();
        try {
            try {
                ScriptAction scriptAction = loader.getScriptAction(scriptId);
                scriptAction.execute(model.getDiagram(), Collections.<String, Object>singletonMap("parent", parent));
            } finally {
                parent.setCursor(Cursor.getDefaultCursor());
                log.debug("Script {} finished in {}ms", scriptId, System.currentTimeMillis() - time);
            }
        } catch (GrafikonException ex) {
            log.error("Error executing script.", ex);
            String message = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            GuiComponentUtils.showError(message, parent);
        }
    }

    private void editScriptExecution(ActionEvent e) {
        Component parent = GuiComponentUtils.getTopLevelComponent(e.getSource());
        model.getDiagram();
        ScriptDialog dialog = new ScriptDialog((Frame)parent, true);
        if (lastScript == null) {
            loadScriptFromPreferences();
        }
        dialog.setLocationRelativeTo(parent);
        dialog.registerContext(model.getGuiContext());
        dialog.showDialog(lastScript, createExecutor());
    }

    private BiFunction<Script, Window, Object> createExecutor() {
        return (selectedScript, parent) -> {
            lastScript = selectedScript;
            // binding
            Map<String, Object> binding = new HashMap<String, Object>();
            binding.put("diagram", model.getDiagram());
            CharArrayWriter output = new CharArrayWriter();
            binding.put("output", new PrintWriter(output));
            binding.put("parent", parent);
            try {
                Object result = null;
                parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                long time = System.currentTimeMillis();
                // execute
                try {
                    result = selectedScript.evaluateWithException(binding);
                    saveScriptToPreferences();
                } finally {
                    parent.setCursor(Cursor.getDefaultCursor());
                    log.debug("Script execution finished in {}ms", System.currentTimeMillis() - time);
                }
                String outString = output.toString();
                if (!outString.isEmpty()) {
                    // show in a window
                    ScriptOutputDialog outputDialog = new ScriptOutputDialog(parent, true);
                    outputDialog.setText(outString);
                    outputDialog.setLocationRelativeTo(parent);
                    outputDialog.setVisible(true);
                }
                return result;
            } catch (Exception ex) {
                log.warn("Script error: {}: {}", ex.getClass().getName(), ex.getMessage());
                String message = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                GuiComponentUtils.showError(message, parent);
                return null;
            }
        };
    }

    private void loadScriptFromPreferences() {
        String scriptStr = null;
        try {
            scriptStr = AppPreferences.getSection("scripts").get("last.script");
            scriptStr = scriptStr != null ? EscapeTool.getInstance().unescape(scriptStr) : null;
        } catch (IOException ex) {
            log.error("Error reading script from preferences.", ex);
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
            log.error("Error converting script.", e);
        }
    }

    private void saveScriptToPreferences() {
        if (lastScript != null)
            try {
                String scriptStr = lastScript.getLanguage().name() + ":" + lastScript.getSourceCode();
                scriptStr = EscapeTool.getInstance().escape(scriptStr);
                AppPreferences.getSection("scripts").put("last.script", scriptStr);
            } catch (IOException e) {
                log.error("Error writing script to preferences.", e);
            }
    }
}
