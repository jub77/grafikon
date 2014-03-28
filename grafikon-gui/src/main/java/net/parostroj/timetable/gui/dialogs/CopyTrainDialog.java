/*
 * CopyTrainDialog.java
 *
 * Created on 25. duben 2008, 22:58
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.Frame;

import net.parostroj.timetable.actions.TrainBuilder;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.IdGenerator;
import net.parostroj.timetable.utils.ResourceLoader;

import javax.swing.JCheckBox;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.GroupLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Dialog for copying trains.
 *
 * @author jub
 */
public class CopyTrainDialog extends javax.swing.JDialog {

    private final Train train;
    private boolean reversed;
    private final TrainDiagram diagram;

    public CopyTrainDialog(Frame parent, boolean modal, TrainDiagram diagram, Train train) {
        super(parent, modal);
        initComponents();
        this.train = train;
        this.diagram = diagram;
        this.reversed = false;
        if (train != null) {
            nameTextField.setText(train.getNumber());
            timeTextField.setText(train.getTrainDiagram().getTimeConverter().convertIntToText(train.getStartTime()));
            setTitle(String.format(ResourceLoader.getString("copy.train.title"), train.getName()));
        }
        pack();
    }

    private void initComponents() {
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        timeTextField = new javax.swing.JTextField();
        javax.swing.JButton okButton = new javax.swing.JButton();
        javax.swing.JButton cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jLabel1.setText(ResourceLoader.getString("copy.train.name")); // NOI18N

        nameTextField.setColumns(20);

        jLabel2.setText(ResourceLoader.getString("copy.train.time")); // NOI18N

        timeTextField.setColumns(20);

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okAction();
            }
        });

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setVisible(false);
            }
        });

        JCheckBox checkBox = new JCheckBox(ResourceLoader.getString("copy.train.reversed")); // NOI18N
        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reversed = ((JCheckBox) e.getSource()).isSelected();
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(jLabel1)
                        .addComponent(jLabel2))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(checkBox)
                        .addComponent(timeTextField)
                        .addComponent(nameTextField))
                    .addContainerGap())
                .addGroup(layout.createSequentialGroup()
                    .addGap(89)
                    .addComponent(okButton)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(cancelButton)
                    .addGap(10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(timeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(checkBox)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(cancelButton)
                        .addComponent(okButton))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        getContentPane().setLayout(layout);

        pack();
        this.setResizable(false);
    }

    private void okAction() {
        // create copy of the train
        int time = train.getTrainDiagram().getTimeConverter().convertTextToInt(timeTextField.getText());
        if (time == -1)
            // select midnight if the time is not correct
            time = 0;
        TrainBuilder builder = new TrainBuilder();
        Train newTrain = reversed ?
                builder.createReverseTrain(IdGenerator.getInstance().getId(), nameTextField.getText(), time, train) :
                builder.createTrain(IdGenerator.getInstance().getId(), nameTextField.getText(), time, train);

        // add train to diagram
        diagram.addTrain(newTrain);

        // set visible to false
        this.setVisible(false);
    }

    private javax.swing.JTextField nameTextField;
    private javax.swing.JTextField timeTextField;
}
