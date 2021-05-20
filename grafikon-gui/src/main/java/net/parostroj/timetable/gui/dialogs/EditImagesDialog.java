/*
 * EditImagesDialog.java
 *
 * Created on 12. říjen 2007, 19:44
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.image.BufferedImage;
import java.io.*;

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
import net.parostroj.timetable.model.TrainDiagramPartFactory;
import net.parostroj.timetable.utils.ResourceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * Edit dialog for images for timetable.
 *
 * @author jub
 */
public class EditImagesDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(EditImagesDialog.class);
    private static JFileChooser fileChooserInstance;

    private transient TrainDiagram diagram;
    private WrapperListModel<TimetableImage> listModel;

    private static synchronized JFileChooser getFileChooser() {
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
                EditImagesDialog.this.newButtonActionPerformed();
            }
            else if (evt.getSource() == renameButton) {
                EditImagesDialog.this.renameButtonActionPerformed();
            }
            else if (evt.getSource() == deleteButton) {
                EditImagesDialog.this.deleteButtonActionPerformed();
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
        listModel = new WrapperListModel<>();
        imagesList.setModel(listModel);
        if (this.diagram != null) {
            for (TimetableImage item : this.diagram.getImages()) {
                listModel.addWrapper(new Wrapper<>(item));
            }
        }
    }

    private boolean checkExistence(String filename, TimetableImage ignore) {
        for (TimetableImage image : diagram.getImages()) {
            if (image != ignore && image.getFilename().equals(filename)) {
                return true;
            }
        }
        return false;
    }

    private void initComponents() {
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        imagesList = new javax.swing.JList<>();
        imagesList.setVisibleRowCount(12);
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
                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                        .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void newButtonActionPerformed() {
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
                TrainDiagramPartFactory factory = diagram.getPartFactory();
                TimetableImage image = factory.createImage(factory.createId(), fileName, img.getWidth(), img.getHeight());

                File tempFile = File.createTempFile("gt_", ".temp");
                Files.asByteSource(chooser.getSelectedFile()).copyTo(Files.asByteSink(tempFile));
                image.setImageFile(tempFile);
                tempFile.deleteOnExit();
                diagram.getImages().add(image);
                listModel.addWrapper(new Wrapper<>(image));
            } catch (IOException e) {
                log.warn("Cannot save temporary image file.", e);
                JOptionPane.showMessageDialog(this,
                        ResourceLoader.getString("dialog.error.temporaryfile"),
                        ResourceLoader.getString("dialog.error.title"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void renameButtonActionPerformed() {
        TimetableImage selected = (TimetableImage) ((Wrapper<?>)imagesList.getSelectedValue()).getElement();
        // ask for a new name
        String newName = (String) JOptionPane.showInputDialog(this, ResourceLoader.getString("images.edit.name"),
                null, JOptionPane.QUESTION_MESSAGE, null, null, selected.getFilename());
        if (newName != null && !newName.equals(selected.getFilename())) {
            TrainDiagramPartFactory factory = diagram.getPartFactory();
            TimetableImage newImage = factory.createImage(factory.createId(), newName, selected.getImageWidth(), selected.getImageHeight());
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
            diagram.getImages().remove(selected);
            diagram.getImages().add(newImage);
            // list model
            listModel.removeObject(selected);
            Wrapper<TimetableImage> newWrapper = new Wrapper<>(newImage);
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

    private void deleteButtonActionPerformed() {
        TimetableImage selected = (TimetableImage) ((Wrapper<?>)imagesList.getSelectedValue()).getElement();
        diagram.getImages().remove(selected);
        listModel.removeIndex(imagesList.getSelectedIndex());
        // remove temp file
        if (!selected.getImageFile().delete()) {
            log.debug("Cannot remove temporary file: {}", selected.getImageFile().getPath());
        }
    }

    private javax.swing.JButton deleteButton;
    private javax.swing.JList<Wrapper<TimetableImage>> imagesList;
    private javax.swing.JButton newButton;
    private javax.swing.JButton renameButton;
}
