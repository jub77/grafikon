/*
 * CopyTrainDialog.java
 *
 * Created on 25. duben 2008, 22:58
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.Window;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import net.parostroj.timetable.gui.pm.CopyTrainPM;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.ResourceLoader;

import org.beanfabrics.ModelProvider;
import org.beanfabrics.Path;
import org.beanfabrics.swing.BnButton;
import org.beanfabrics.swing.BnCheckBox;
import org.beanfabrics.swing.BnTextField;

/**
 * Dialog for copying trains.
 *
 * @author jub
 */
public class CopyTrainDialog extends javax.swing.JDialog {

    private final ModelProvider provider = new ModelProvider(CopyTrainPM.class);

    public CopyTrainDialog(Window parent, boolean modal, TrainDiagram diagram, Train train) {
        super(parent, modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        initComponents();
        CopyTrainPM model = new CopyTrainPM();
        provider.setPresentationModel(model);
        model.init(diagram, train);
        pack();
    }

    private void initComponents() {
        ActionListener closeListener = evt -> setVisible(false);

        BnTextField nameTextField = new BnTextField();
        BnTextField timeTextField = new BnTextField();
        BnButton okButton = new BnButton();
        javax.swing.JButton cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        javax.swing.JLabel jLabel1 = new javax.swing.JLabel(ResourceLoader.getString("copy.train.name")); // NOI18N
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel(ResourceLoader.getString("copy.train.time")); // NOI18N

        nameTextField.setColumns(20);
        timeTextField.setColumns(20);

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(closeListener);

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(closeListener);

        BnCheckBox reversedCheckBox = new BnCheckBox();
        reversedCheckBox.setText(ResourceLoader.getString("copy.train.reversed")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addComponent(jLabel1)
                                .addComponent(jLabel2))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addComponent(reversedCheckBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(timeTextField)
                                .addComponent(nameTextField)))
                        .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(okButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(cancelButton)))
                    .addContainerGap())
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
                    .addComponent(reversedCheckBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(cancelButton)
                        .addComponent(okButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );
        getContentPane().setLayout(layout);

        nameTextField.setModelProvider(provider);
        nameTextField.setPath(new Path("number"));
        timeTextField.setModelProvider(provider);
        timeTextField.setPath(new Path("time"));
        reversedCheckBox.setModelProvider(provider);
        reversedCheckBox.setPath(new Path("reversed"));
        okButton.setModelProvider(provider);
        okButton.setPath(new Path("ok"));

        pack();
        this.setResizable(false);
    }
}
