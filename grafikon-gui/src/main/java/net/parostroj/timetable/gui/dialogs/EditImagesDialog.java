/*
 * EditImagesDialog.java
 *
 * Created on 12. říjen 2007, 19:44
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.TimetableImage;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.IdGenerator;
import net.parostroj.timetable.utils.ResourceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * Edit dialog for images for timetable.
 *
 * @author jub
 */
public class EditImagesDialog extends javax.swing.JDialog {

    private static final Logger LOG = LoggerFactory.getLogger(EditImagesDialog.class.getName());
    private static JFileChooser fileChooserInstance;

    private TrainDiagram diagram;
    private WrapperListModel<TimetableImage> listModel;

    private synchronized static JFileChooser getFileChooser() {
        if (fileChooserInstance == null) {
            fileChooserInstance = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "jpeg", "gif", "bmp", "png");
            fileChooserInstance.addChoosableFileFilter(filter);
        }
        return fileChooserInstance;
    }

    // Code for dispatching events from components to event handlers
    private class FormListener implements java.awt.event.ActionListener, javax.swing.event.ListSelectionListener {

        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == newButton) {
                EditImagesDialog.this.newButtonActionPerformed(evt);
            }
            else if (evt.getSource() == renameButton) {
                EditImagesDialog.this.renameButtonActionPerformed(evt);
            }
            else if (evt.getSource() == deleteButton) {
                EditImagesDialog.this.deleteButtonActionPerformed(evt);
            }
        }

        public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
            if (evt.getSource() == imagesList) {
                EditImagesDialog.this.imagesListValueChanged(evt);
            }
        }
    }

    /** Creates new form EditImagesDialog */
    public EditImagesDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        // preload filechooser
        getFileChooser();
    }

    public void showDialog(TrainDiagram diagram) {
        this.diagram = diagram;
        this.updateValues();
        this.setVisible(true);
    }

    private void updateValues() {
        listModel = new WrapperListModel<TimetableImage>();
        imagesList.setModel(listModel);
        if (this.diagram != null) {
            for (TimetableImage item : this.diagram.getImages())
                listModel.addWrapper(new Wrapper<TimetableImage>(item));
        }
    }

    private boolean checkExistence(String filename, TimetableImage ignore) {
        for (TimetableImage image : diagram.getImages()) {
            if (image != ignore) {
                if (image.getFilename().equals(filename))
                    return true;
            }
        }
        return false;
    }

    private void initComponents() {
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        imagesList = new javax.swing.JList();
        newButton = GuiComponentUtils.createButton(GuiIcon.ADD, 2);
        renameButton = GuiComponentUtils.createButton(GuiIcon.EDIT, 2);
        deleteButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 2);

        setTitle(ResourceLoader.getString("images.edit.title")); // NOI18N

        FormListener formListener = new FormListener();

        imagesList.addListSelectionListener(formListener);
        scrollPane.setViewportView(imagesList);

        newButton.addActionListener(formListener);

        renameButton.setEnabled(false);
        renameButton.addActionListener(formListener);

        deleteButton.setEnabled(false);
        deleteButton.addActionListener(formListener);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.TRAILING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                        .addComponent(renameButton, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(deleteButton, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(newButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.TRAILING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(newButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(renameButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(deleteButton)))
                    .addContainerGap())
        );
        getContentPane().setLayout(layout);

        pack();
    }

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // new image based on selected file ...
        JFileChooser chooser = getFileChooser();

        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            String fileName = chooser.getSelectedFile().getName();

            if (checkExistence(fileName, null)) {
                // show error message and return
                JOptionPane.showMessageDialog(this,
                        ResourceLoader.getString("dialog.error.duplicatefile"),
                        ResourceLoader.getString("dialog.error.title"),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // get size of the image
                BufferedImage img = ImageIO.read(chooser.getSelectedFile());
                TimetableImage image = diagram.createImage(IdGenerator.getInstance().getId(), fileName, img.getWidth(), img.getHeight());

                File tempFile = File.createTempFile("gt_", ".temp");
                FileChannel ic = null;
                FileChannel oc = null;
                try {
                    ic = new FileInputStream(chooser.getSelectedFile()).getChannel();
                    oc = new FileOutputStream(tempFile).getChannel();
                    ic.transferTo(0, ic.size(), oc);
                } finally {
                    if (ic != null)
                        ic.close();
                    if (oc != null)
                        oc.close();
                }
                image.setImageFile(tempFile);
                tempFile.deleteOnExit();
                diagram.addImage(image);
                listModel.addWrapper(new Wrapper<TimetableImage>(image));
            } catch (IOException e) {
                LOG.warn("Cannot save temporary image file.", e);
                JOptionPane.showMessageDialog(this,
                        ResourceLoader.getString("dialog.error.temporaryfile"),
                        ResourceLoader.getString("dialog.error.title"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void renameButtonActionPerformed(java.awt.event.ActionEvent evt) {
        TimetableImage selected = (TimetableImage) ((Wrapper<?>)imagesList.getSelectedValue()).getElement();
        // ask for a new name
        String newName = (String) JOptionPane.showInputDialog(this, ResourceLoader.getString("images.edit.name"),
                null, JOptionPane.QUESTION_MESSAGE, null, null, selected.getFilename());
        if (newName != null && !newName.equals(selected.getFilename())) {
            TimetableImage newImage = diagram.createImage(IdGenerator.getInstance().getId(), newName, selected.getImageWidth(), selected.getImageHeight());
            if (checkExistence(newName, selected)) {
                // show error message and return
                JOptionPane.showMessageDialog(this,
                        ResourceLoader.getString("dialog.error.duplicatefile"),
                        ResourceLoader.getString("dialog.error.title"),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            newImage.setImageFile(selected.getImageFile());
            // train diagram
            diagram.removeImage(selected);
            diagram.addImage(newImage);
            // list model
            listModel.removeObject(selected);
            Wrapper<TimetableImage> newWrapper = new Wrapper<TimetableImage>(newImage);
            listModel.addWrapper(newWrapper);
            // set selected
            imagesList.setSelectedValue(newWrapper, true);
        }
    }

    private void imagesListValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            boolean selected = imagesList.getSelectedIndex() != -1;
            renameButton.setEnabled(selected);
            deleteButton.setEnabled(selected);
            renameButton.setEnabled(selected);
        }
    }

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        TimetableImage selected = (TimetableImage) ((Wrapper<?>)imagesList.getSelectedValue()).getElement();
        diagram.removeImage(selected);
        listModel.removeIndex(imagesList.getSelectedIndex());
        // remove temp file
        if (!selected.getImageFile().delete())
            LOG.debug("Cannot remove temporary file: {}", selected.getImageFile().getPath());
    }

    private javax.swing.JButton deleteButton;
    private javax.swing.JList imagesList;
    private javax.swing.JButton newButton;
    private javax.swing.JButton renameButton;
}
