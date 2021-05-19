/*
 * WaitDiag.java
 *
 * Created on 3. duben 2008, 22:03
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Timer;

import net.parostroj.timetable.gui.actions.execution.ActionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dialog for showing information of long duration operation.
 *
 * @author  jub
 */
public class WaitDialog extends javax.swing.JDialog implements PropertyChangeListener {

    private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(WaitDialog.class);
    private static final int WAIT_DIALOG_WIDTH = 300;

    private int level = 0;

    /** Creates new form WaitDiag */
    public WaitDialog(boolean modal) {
        super((Frame) null, modal);
        initComponents();
        Dimension size = progressBar.getPreferredSize();
        size.width = WAIT_DIALOG_WIDTH;
        progressBar.setPreferredSize(size);
        pack();
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        messageLabel = new javax.swing.JLabel() {
            private static final long serialVersionUID = 1L;

			@Override
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                return new Dimension(Math.max(size.width, WAIT_DIALOG_WIDTH), size.height);
            }
        };
        progressBar = new javax.swing.JProgressBar() {
            private static final long serialVersionUID = 1L;

			@Override
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                return new Dimension(Math.max(size.width, WAIT_DIALOG_WIDTH), size.height);
            }
        };

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        messageLabel.setText("Wait ...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(messageLabel, gridBagConstraints);

        progressBar.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        getContentPane().add(progressBar, gridBagConstraints);

        pack();
    }

    private javax.swing.JLabel messageLabel;
    private javax.swing.JProgressBar progressBar;

    @Override
    public void setVisible(boolean b) {
        if (b) {
            level++;
            if (level == 1)
                super.setVisible(true);
        } else {
            level--;
            if (level == 0)
                super.setVisible(false);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        log.trace("Event received: {}, {}", evt.getPropertyName(), evt.getNewValue());
        if ("state".equals(evt.getPropertyName())) {
            if (evt.getNewValue() == ActionContext.WaitDialogState.HIDE) {
                this.setVisible(false);
            } else if (evt.getNewValue() == ActionContext.WaitDialogState.SHOW) {
                final ActionContext context = (ActionContext) evt.getSource();
                Timer timer = new Timer(context.getDelay(), e -> {
                    progressBar.setValue(context.getProgress());
                    progressBar.setVisible(context.isShowProgress());
                    pack();
                    if (context.getLocationComponent() != null) {
                        setLocationRelativeTo(context.getLocationComponent());
                    } else {
                        setLocationRelativeTo(null);
                    }
                    setVisible(true);
                });
                timer.setRepeats(false);
                timer.start();
            }
        } else if ("description".equals(evt.getPropertyName())) {
            String newValue = (String) evt.getNewValue();
            if (newValue != null) {
                messageLabel.setText(newValue);
            }
        } else if ("progress".equals(evt.getPropertyName())) {
            progressBar.setValue((Integer) evt.getNewValue());
        } else if ("progressDescription".equals(evt.getPropertyName())) {
            String newValue = (String) evt.getNewValue();
            progressBar.setString(newValue);
        }
    }
}
