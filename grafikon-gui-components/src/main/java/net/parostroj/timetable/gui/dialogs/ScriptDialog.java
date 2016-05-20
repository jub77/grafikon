package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Window;
import java.util.function.BiConsumer;

import javax.swing.BorderFactory;
import javax.swing.JDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.GuiContext;
import net.parostroj.timetable.gui.GuiContextComponent;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.Script;

/**
 * Dialog for editing scripts.
 *
 * @author jub
 */
public class ScriptDialog extends javax.swing.JDialog implements GuiContextComponent {

    private static final Logger log = LoggerFactory.getLogger(ScriptDialog.class);

    private BiConsumer<Script, Window> executor;
    private int counter;

    public ScriptDialog(java.awt.Window parent, boolean modal) {
        super(parent, modal ? JDialog.DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        initComponents();
    }

    public void showDialog(Script script, BiConsumer<Script, Window> executor) {
        this.executor = executor;
        this.setScript(script);
        this.setVisible(true);
    }

    @Override
    public void registerContext(GuiContext context) {
        context.registerWindow("script.dialog", this);
    }

    public Script getScript() throws GrafikonException {
        return scriptEditBox.getScript();
    }

    private void setScript(Script script) {
        scriptEditBox.setScript(script);
    }

    public void setEditorSize(int columns, int rows) {
        scriptEditBox.setColumns(columns);
        scriptEditBox.setRows(rows);
    }

    private void initComponents() {
        scriptEditBox = new net.parostroj.timetable.gui.components.ScriptEditBox();
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
        javax.swing.JButton executeButton = new javax.swing.JButton();
        javax.swing.JButton closeButton = new javax.swing.JButton();

        setTitle(ResourceLoader.getString("script.editing")); // NOI18N

        scriptEditBox.setColumns(60);
        scriptEditBox.setRows(15);
        scriptEditBox.setScriptFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        getContentPane().add(scriptEditBox, java.awt.BorderLayout.CENTER);

        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        executeButton.setText(ResourceLoader.getString("button.execute")); // NOI18N
        executeButton.addActionListener(evt -> executeButtonActionPerformed(evt));
        buttonPanel.add(executeButton);

        closeButton.setText(ResourceLoader.getString("button.close")); // NOI18N
        closeButton.addActionListener(e -> this.setVisible(false));
        buttonPanel.add(closeButton);

        statusBar = new javax.swing.JTextField();
        statusBar.setEditable(false);

        javax.swing.JPanel statusBarEnc = new javax.swing.JPanel();
        statusBarEnc.setLayout(new BorderLayout());
        statusBarEnc.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        statusBarEnc.add(statusBar, BorderLayout.CENTER);

        javax.swing.JPanel statusBarPanel = new javax.swing.JPanel();
        statusBarPanel.setLayout(new java.awt.BorderLayout());
        statusBarPanel.add(statusBarEnc, BorderLayout.CENTER);
        statusBarPanel.add(buttonPanel, BorderLayout.EAST);

        getContentPane().add(statusBarPanel, java.awt.BorderLayout.PAGE_END);

        pack();
    }

    private void executeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            statusBar.setText("");
            counter++;
            Script script = this.getScript();
            if (executor != null) {
                long time = System.currentTimeMillis();
                executor.accept(script, this);
                statusBar.setText(String.format("%d (%dms)", counter, System.currentTimeMillis() - time));
            }
        } catch (GrafikonException e) {
            statusBar.setText(String.format("%d", counter));
            log.error("Error creating script.", e);
            String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            GuiComponentUtils.showError(message, this);
        }
    }

    private net.parostroj.timetable.gui.components.ScriptEditBox scriptEditBox;
    private javax.swing.JTextField statusBar;
}
