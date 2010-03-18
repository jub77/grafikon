/*
 * SettingsDialog.java
 *
 * Created on 22. září 2007, 18:07
 */
package net.parostroj.timetable.gui.dialogs;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.*;

/**
 * Dialog for settings modification of the train diagram.
 *
 * @author jub
 */
public class SettingsDialog extends javax.swing.JDialog {

    private static final Logger LOG = Logger.getLogger(SettingsDialog.class.getName());
    private boolean diagramChanged;
    private TrainDiagram diagram;

    /** Creates new form SettingsDialog */
    public SettingsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        diagramChanged = false;

        sortComboBox.addItem(ResourceLoader.getString("modelinfo.sort.number"));
        sortComboBox.addItem(ResourceLoader.getString("modelinfo.sort.string"));
        sortComboBox.setPrototypeDisplayValue("nnnnnnnnnnnnn");

        completeNameTemplateTextField.setColumns(50);

        pack();
    }

    public void setTrainDiagram(TrainDiagram diagram) {
        this.diagram = diagram;
        this.diagramChanged = false;

        for (Scale scale : Scale.getPredefined()) {
            scaleComboBox.addItem(scale);
        }

        // set some values for speed
        for (double d = 4.0; d <= 6.0 ;) {
            ratioComboBox.addItem(Double.toString(d));
            d += 0.5;
        }

        this.updateValues();
    }

    private void updateValues() {
        if (diagram != null) {
            // set original values ...
            scaleComboBox.setSelectedItem(diagram.getAttribute("scale"));
            ratioComboBox.setSelectedItem(((Double)diagram.getAttribute("time.scale")).toString());

            // sorting
            TrainsData trainsData = diagram.getTrainsData();
            SortPatternGroup firstGroup = trainsData.getTrainSortPattern().getGroups().get(0);
            if (firstGroup.getType() == SortPatternGroup.Type.NUMBER) {
                sortComboBox.setSelectedIndex(0);
            } else {
                sortComboBox.setSelectedIndex(1);
            }
            completeNameTemplateTextField.setText(trainsData.getTrainCompleteNameTemplate().getTemplate());
            completeNameTemplateTextField.setCaretPosition(0);
            nameTemplateTextField.setText(trainsData.getTrainNameTemplate().getTemplate());
            nameTemplateTextField.setCaretPosition(0);

            // set crossing time in minutes
            Integer transferTime = (Integer)diagram.getAttribute("station.transfer.time");
            if (transferTime != null) {
                stationTransferTextField.setText(transferTime.toString());
            } else {
                LOG.warning("Station transfer time information missing.");
                stationTransferTextField.setText("");
            }
            // set station lengths information
            lengthInAxlesCheckBox.setSelected(Boolean.TRUE.equals(diagram.getAttribute("station.length.in.axles")));
            stationLengthUnitTextField.setEnabled(!lengthInAxlesCheckBox.isSelected());
            String stationLengthUnit = (String)diagram.getAttribute("station.length.unit");
            stationLengthUnitTextField.setText(stationLengthUnit == null ? "" : stationLengthUnit);

            // weight ratios
            Double emptyRatio = (Double)diagram.getAttribute("weight.ratio.empty");
            Double loadedRatio = (Double)diagram.getAttribute("weight.ratio.loaded");

            if (emptyRatio != null)
                emptyRatioTextField.setText(emptyRatio.toString());
            if (loadedRatio != null)
                loadedRatioTextField.setText(loadedRatio.toString());

            // script
            scriptTextArea.setText(trainsData.getRunningTimeScript().getSourceCode());
            scriptTextArea.setCaretPosition(0);
        }
    }

    public boolean isDiagramChanged() {
        return diagramChanged;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        scaleComboBox = new javax.swing.JComboBox();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        ratioComboBox = new javax.swing.JComboBox();
        panel1 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        nameTemplateTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        completeNameTemplateTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        sortComboBox = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        stationTransferTextField = new javax.swing.JTextField();
        lengthInAxlesCheckBox = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        stationLengthUnitTextField = new javax.swing.JTextField();
        ratioPanel = new javax.swing.JPanel();
        javax.swing.JLabel jLabel8 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel9 = new javax.swing.JLabel();
        emptyRatioTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel10 = new javax.swing.JLabel();
        loadedRatioTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel11 = new javax.swing.JLabel();
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        scriptTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(ResourceLoader.getString("modelinfo")); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(ResourceLoader.getString("modelinfo.scales")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 0);
        getContentPane().add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 2, 5, 10);
        getContentPane().add(scaleComboBox, gridBagConstraints);

        jLabel2.setText(ResourceLoader.getString("modelinfo.ratio")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 0);
        getContentPane().add(jLabel2, gridBagConstraints);

        ratioComboBox.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 5, 10);
        getContentPane().add(ratioComboBox, gridBagConstraints);

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        panel1.add(okButton);

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        panel1.add(cancelButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 5);
        getContentPane().add(panel1, gridBagConstraints);

        jLabel3.setText(ResourceLoader.getString("edit.traintypes.nametemplate")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 0);
        getContentPane().add(jLabel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 10);
        getContentPane().add(nameTemplateTextField, gridBagConstraints);

        jLabel4.setText(ResourceLoader.getString("edit.traintypes.completenametemplate")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 0);
        getContentPane().add(jLabel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 10);
        getContentPane().add(completeNameTemplateTextField, gridBagConstraints);

        jLabel5.setText(ResourceLoader.getString("modelinfo.sort")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 0);
        getContentPane().add(jLabel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 5, 10);
        getContentPane().add(sortComboBox, gridBagConstraints);

        jLabel6.setText(ResourceLoader.getString("modelinfo.crossing")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 0);
        getContentPane().add(jLabel6, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 10);
        getContentPane().add(stationTransferTextField, gridBagConstraints);

        lengthInAxlesCheckBox.setText(ResourceLoader.getString("modelinfo.station.length.in.axles")); // NOI18N
        lengthInAxlesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lengthInAxlesCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 10);
        getContentPane().add(lengthInAxlesCheckBox, gridBagConstraints);

        jLabel7.setText(ResourceLoader.getString("modelinfo.station.length.unit")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 0);
        getContentPane().add(jLabel7, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 10);
        getContentPane().add(stationLengthUnitTextField, gridBagConstraints);

        ratioPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel8.setText(ResourceLoader.getString("modelinfo.weight.ratio")); // NOI18N
        ratioPanel.add(jLabel8);

        jLabel9.setText(ResourceLoader.getString("modelinfo.weight.ratio.empty")); // NOI18N
        ratioPanel.add(jLabel9);

        emptyRatioTextField.setColumns(10);
        ratioPanel.add(emptyRatioTextField);

        jLabel10.setText(ResourceLoader.getString("modelinfo.weight.ratio.loaded")); // NOI18N
        ratioPanel.add(jLabel10);

        loadedRatioTextField.setColumns(10);
        ratioPanel.add(loadedRatioTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        getContentPane().add(ratioPanel, gridBagConstraints);

        jLabel11.setText(ResourceLoader.getString("modelinfo.running.time.script")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        getContentPane().add(jLabel11, gridBagConstraints);

        scriptTextArea.setColumns(20);
        scriptTextArea.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        scriptTextArea.setRows(5);
        scrollPane.setViewportView(scriptTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(scrollPane, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        // get templates values
        TrainsData trainsData = diagram.getTrainsData();
        String completeName = completeNameTemplateTextField.getText();
        String name = nameTemplateTextField.getText();

        if ("".equals(name)|| "".equals(completeName)) {
            JOptionPane.showMessageDialog(this.getParent(), ResourceLoader.getString("dialog.error.emptytemplates"),
                    ResourceLoader.getString("dialog.error.title"), JOptionPane.ERROR_MESSAGE);
            LOG.log(Level.FINE, "Empty templates.");
            return;
        }

        // set scale
        Scale s = (Scale)scaleComboBox.getSelectedItem();
        // set ratio
        double sp = 1.0;
        try {
            sp = Double.parseDouble((String)ratioComboBox.getSelectedItem());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this.getParent(), ResourceLoader.getString("dialog.error.badratio"),
                    ResourceLoader.getString("dialog.error.title"), JOptionPane.ERROR_MESSAGE);
            LOG.log(Level.FINE, "Cannot covert ratio.", ex);
            return;
        }
        if (s != null)
            diagram.setAttribute("scale", s);
        diagram.setAttribute("time.scale", sp);

        // set templates
        trainsData.setTrainCompleteNameTemplate(TextTemplate.createTextTemplate(completeName, Language.MVEL));
        trainsData.setTrainNameTemplate(TextTemplate.createTextTemplate(name, Language.MVEL));

        // set sorting
        SortPattern sPattern = null;
        if (sortComboBox.getSelectedIndex() == 0) {
            sPattern = new SortPattern("(\\d*)(.*)");
            sPattern.getGroups().add(new SortPatternGroup(1, SortPatternGroup.Type.NUMBER));
            sPattern.getGroups().add(new SortPatternGroup(2, SortPatternGroup.Type.STRING));
        } else {
            sPattern = new SortPattern("(.*)");
            sPattern.getGroups().add(new SortPatternGroup(1, SortPatternGroup.Type.STRING));
        }
        trainsData.setTrainSortPattern(sPattern);

        // set transfer time
        try {
            Integer difference = Integer.valueOf(stationTransferTextField.getText());
            if (difference != null)
                diagram.setAttribute("station.transfer.time", difference);
        } catch (NumberFormatException e) {
            LOG.warning("Cannot parse station transfer time: " + stationTransferTextField.getText());
        }

        // get back values for stations lengths
        diagram.setAttribute("station.length.in.axles", Boolean.valueOf(lengthInAxlesCheckBox.isSelected()));
        if (lengthInAxlesCheckBox.isSelected())
            diagram.removeAttribute("station.length.unit");
        else
            diagram.setAttribute("station.length.unit", stationLengthUnitTextField.getText());

        // weight ratios
        try {
            Double emptyRatio = Double.valueOf(emptyRatioTextField.getText());
            Double loadedRatio = Double.valueOf(loadedRatioTextField.getText());

            diagram.setAttribute("weight.ratio.empty", emptyRatio);
            diagram.setAttribute("weight.ratio.loaded", loadedRatio);
        } catch (NumberFormatException e) {
            LOG.warning("Cannot convert weight ratios to doubles: " + e.getMessage());
        }

        // set running time script
        if (scriptTextArea.getText() != null && !scriptTextArea.getText().equals(""))
            diagram.getTrainsData().setRunningTimeScript(
                    Script.createScript(scriptTextArea.getText(),
                    diagram.getTrainsData().getRunningTimeScript().getLanguage()));

        // update model
        for (Train train : diagram.getTrains()) {
            train.recalculate();
        }
        // clear cached information for train names
        for (Train train : diagram.getTrains())
            train.clearCachedData();

        this.updateValues();

        this.setVisible(false);
        this.diagramChanged = true;
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.updateValues();
        this.setVisible(false);
        this.diagramChanged = false;
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void lengthInAxlesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lengthInAxlesCheckBoxActionPerformed
        // enable/disable custom unit for station length depending on this value
        boolean customUnitEnabled = !lengthInAxlesCheckBox.isSelected();
        stationLengthUnitTextField.setEnabled(customUnitEnabled);
    }//GEN-LAST:event_lengthInAxlesCheckBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField completeNameTemplateTextField;
    private javax.swing.JTextField emptyRatioTextField;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JCheckBox lengthInAxlesCheckBox;
    private javax.swing.JTextField loadedRatioTextField;
    private javax.swing.JTextField nameTemplateTextField;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel panel1;
    private javax.swing.JComboBox ratioComboBox;
    private javax.swing.JPanel ratioPanel;
    private javax.swing.JComboBox scaleComboBox;
    private javax.swing.JTextArea scriptTextArea;
    private javax.swing.JComboBox sortComboBox;
    private javax.swing.JTextField stationLengthUnitTextField;
    private javax.swing.JTextField stationTransferTextField;
    // End of variables declaration//GEN-END:variables

}
