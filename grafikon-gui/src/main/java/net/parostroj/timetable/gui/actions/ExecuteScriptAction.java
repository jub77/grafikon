package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.actions.execution.ActionUtils;
import net.parostroj.timetable.gui.actions.execution.ModelActionUtilities;
import net.parostroj.timetable.gui.dialogs.ScriptDialog;
import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.Script;
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
        Component parent = ActionUtils.getTopLevelComponent(e.getSource());
        model.getDiagram();
        ScriptDialog dialog = new ScriptDialog((Frame)parent, true);
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
                // execute
                selectedScript.evaluateWithException(binding);
            } catch (GrafikonException ex) {
                LOG.error("Error executing script.", ex);
                String message = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                ActionUtils.showError(message, parent);
            }
            model.setModelChanged(true);
        }
    }
}
