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
import net.parostroj.timetable.gui.pm.NodePortPM;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;

/**
 * Panel for editing ports.
 *
 * @author jub
 */
public class EditNodePortsPanel extends BaseEditPanel<NodePM> {

    private static final int DEFAULT_VISIBLE_ROW_COUNT = 10;

    private PropertyChangeListener listener;

    public EditNodePortsPanel() {
        BnList portList = new BnList();
        portList.setPath(new Path("ports"));
        portList.setModelProvider(localProvider);
        portList.setCellConfig(new CellConfig(new Path("portId")));
        portList.setVisibleRowCount(DEFAULT_VISIBLE_ROW_COUNT);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BorderLayout());
        listPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
        JScrollPane portScrollPane = new JScrollPane(portList);
        listPanel.add(portScrollPane, BorderLayout.CENTER);

        EditNodePortPanel portPanel = new EditNodePortPanel();

        listener = evt -> {
            Selection<NodePortPM> selection = getPresentationModel().getPorts().getSelection();
            portPanel.setPresentationModel(selection.getIndexes().length == 1 ? selection.getFirst() : null);
        };

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 5, 5));
        BoxLayout buttonPanelLayout = new BoxLayout(buttonPanel, BoxLayout.Y_AXIS);
        buttonPanel.setLayout(buttonPanelLayout);

        buttonPanel.add(Box.createVerticalStrut(3));

        BnButton createButton = GuiComponentUtils.createBnButton(GuiIcon.ADD, 2);
        createButton.setModelProvider(localProvider);
        createButton.setPath(new Path("ports.create"));
        buttonPanel.add(createButton);

        buttonPanel.add(Box.createVerticalStrut(3));

        BnButton deleteButton = GuiComponentUtils.createBnButton(GuiIcon.REMOVE, 2);
        deleteButton.setModelProvider(localProvider);
        deleteButton.setPath(new Path("ports.delete"));
        buttonPanel.add(deleteButton);

        this.setLayout(new BorderLayout());
        this.add(listPanel, BorderLayout.CENTER);
        this.add(portPanel, BorderLayout.SOUTH);
        this.add(buttonPanel, BorderLayout.EAST);
    }

    @Override
    public void setPresentationModel(NodePM pModel) {
        PresentationModel oldModel = super.getPresentationModel();
        if (oldModel != null) {
            oldModel.removePropertyChangeListener("ports", listener);
        }
        super.setPresentationModel(pModel);
        if (pModel != null) {
            pModel.addPropertyChangeListener("ports", listener);
        }
    }
}
