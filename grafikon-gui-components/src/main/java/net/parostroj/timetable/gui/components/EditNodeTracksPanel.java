package net.parostroj.timetable.gui.components;

import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.beanfabrics.Path;
import org.beanfabrics.model.PresentationModel;
import org.beanfabrics.model.Selection;
import org.beanfabrics.swing.BnButton;
import org.beanfabrics.swing.list.BnList;
import org.beanfabrics.swing.list.CellConfig;

import net.parostroj.timetable.gui.pm.NodePM;
import net.parostroj.timetable.gui.pm.NodeTrackPM;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;

/**
 * Panel for editing tracks.
 *
 * @author jub
 */
public class EditNodeTracksPanel extends BaseEditPanel<NodePM> {

    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_VISIBLE_ROW_COUNT = 10;

    private PropertyChangeListener listener;

    public EditNodeTracksPanel() {
        BnList trackList = new BnList();
        trackList.setPath(new Path("tracks"));
        trackList.setModelProvider(localProvider);
        trackList.setCellConfig(new CellConfig(new Path("number")));
        trackList.setVisibleRowCount(DEFAULT_VISIBLE_ROW_COUNT);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BorderLayout());
        listPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
        JScrollPane trackScrollPane = new JScrollPane(trackList);
        EditNodeTrackPanel trackPanel = new EditNodeTrackPanel();
        listener = evt -> {
            Selection<NodeTrackPM> selection = getPresentationModel().getTracks().getSelection();
            trackPanel.setPresentationModel(selection.getIndexes().length == 1 ? selection.getFirst() : null);
        };
        listPanel.add(trackScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 5, 5));
        BoxLayout buttonPanelLayout = new BoxLayout(buttonPanel, BoxLayout.Y_AXIS);
        buttonPanel.setLayout(buttonPanelLayout);

        buttonPanel.add(Box.createVerticalStrut(3));

        BnButton createButton = GuiComponentUtils.createBnButton(GuiIcon.ADD, 2);
        createButton.setModelProvider(localProvider);
        createButton.setPath(new Path("tracks.create"));
        buttonPanel.add(createButton);

        buttonPanel.add(Box.createVerticalStrut(3));

        BnButton deleteButton = GuiComponentUtils.createBnButton(GuiIcon.REMOVE, 2);
        deleteButton.setModelProvider(localProvider);
        deleteButton.setPath(new Path("tracks.delete"));
        buttonPanel.add(deleteButton);

        buttonPanel.add(Box.createVerticalStrut(3));

        BnButton moveUpButton = GuiComponentUtils.createBnButton(GuiIcon.GO_UP, 2);
        moveUpButton.setModelProvider(localProvider);
        moveUpButton.setPath(new Path("tracks.moveUp"));
        buttonPanel.add(moveUpButton);

        buttonPanel.add(Box.createVerticalStrut(3));

        BnButton moveDownButton = GuiComponentUtils.createBnButton(GuiIcon.GO_DOWN, 2);
        moveDownButton.setModelProvider(localProvider);
        moveDownButton.setPath(new Path("tracks.moveDown"));
        buttonPanel.add(moveDownButton);


        this.setLayout(new BorderLayout());
        this.add(listPanel, BorderLayout.CENTER);
        this.add(trackPanel, BorderLayout.SOUTH);
        this.add(buttonPanel, BorderLayout.EAST);
    }

    @Override
    public void setPresentationModel(NodePM pModel) {
        PresentationModel oldModel = super.getPresentationModel();
        if (oldModel != null) {
            oldModel.removePropertyChangeListener("tracks", listener);
        }
        super.setPresentationModel(pModel);
        if (pModel != null) {
            pModel.addPropertyChangeListener("tracks", listener);
        }
    }
}
