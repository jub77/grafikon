package net.parostroj.timetable.gui.components;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.JPanel;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Path;
import org.beanfabrics.swing.BnButton;
import org.beanfabrics.swing.BnTextField;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;

public class EditLocalizedStringListAddRemovePanel extends JPanel {

    public EditLocalizedStringListAddRemovePanel(Path basePath, IModelProvider provider, int gap, boolean move) {
        setLayout(new BorderLayout(gap, gap));

        JPanel localized = new EditLocalizedStringListPanel(
                Path.concat(basePath, new Path("localized")),
                provider,
                gap);
        this.add(localized, BorderLayout.CENTER);

        JPanel addPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) addPanel.getLayout();
        flowLayout.setHgap(0);
        flowLayout.setAlignment(FlowLayout.LEFT);
        flowLayout.setVgap(0);

        BnTextField textField = new BnTextField();
        textField.setModelProvider(provider);
        textField.setPath(new Path("newKey"));
        textField.setColumns(15);
        addPanel.add(textField);

        addPanel.add(Box.createHorizontalStrut(gap));

        BnButton addButton = GuiComponentUtils.createBnButton(GuiIcon.ADD, 2);
        addButton.setModelProvider(provider);
        addButton.setPath(new Path("add"));
        addPanel.add(addButton);

        addPanel.add(Box.createHorizontalStrut(gap));

        BnButton removeButton = GuiComponentUtils.createBnButton(GuiIcon.REMOVE, 2);
        removeButton.setModelProvider(provider);
        removeButton.setPath(new Path("remove"));
        addPanel.add(removeButton);

        if (move) {
            addPanel.add(Box.createHorizontalStrut(gap));

            BnButton up = GuiComponentUtils.createBnButton(GuiIcon.GO_UP, 2);
            up.setModelProvider(provider);
            up.setPath(new Path("moveUp"));
            addPanel.add(up);

            addPanel.add(Box.createHorizontalStrut(gap));

            BnButton down = GuiComponentUtils.createBnButton(GuiIcon.GO_DOWN, 2);
            down.setModelProvider(provider);
            down.setPath(new Path("moveDown"));
            addPanel.add(down);
        }

        this.add(addPanel, BorderLayout.SOUTH);
    }
}
