package net.parostroj.timetable.gui.components;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Path;
import org.beanfabrics.swing.BnButton;
import org.beanfabrics.swing.BnTextField;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.utils.ResourceLoader;

public class EditLocalizedStringListAddRemovePanel extends JPanel {

    public EditLocalizedStringListAddRemovePanel(Path basePath, IModelProvider provider, int gap) {
        setLayout(new BorderLayout(gap, gap));

        JPanel localized = new EditLocalizedStringListPanel(
                Path.concat(basePath, new Path("localized")),
                provider,
                gap);
        this.add(localized, BorderLayout.CENTER);

        JPanel addPanel = new JPanel();

        BnButton addButton = GuiComponentUtils.createBnButton(GuiIcon.ADD, 2);
        addButton.setModelProvider(provider);
        addButton.setPath(new Path("add"));
        addPanel.add(addButton);

        BnTextField textField = new BnTextField();
        textField.setModelProvider(provider);
        textField.setPath(new Path("newKey"));
        textField.setColumns(30);
        addPanel.add(textField);

        BnButton removeButton = GuiComponentUtils.createBnButton(GuiIcon.REMOVE, 2);
        removeButton.setModelProvider(provider);
        removeButton.setPath(new Path("remove"));
        addPanel.add(removeButton);

        this.add(addPanel, BorderLayout.SOUTH);
    }
}
