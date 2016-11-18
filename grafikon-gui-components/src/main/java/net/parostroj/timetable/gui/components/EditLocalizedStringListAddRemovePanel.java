package net.parostroj.timetable.gui.components;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.JPanel;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;
import org.beanfabrics.swing.BnButton;
import org.beanfabrics.swing.BnTextField;

import net.parostroj.timetable.gui.pm.ARLocalizedStringListPM;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.utils.AttributeReference;

public class EditLocalizedStringListAddRemovePanel<T extends AttributeReference<LocalizedString>> extends JPanel implements View<ARLocalizedStringListPM<T>>, ModelSubscriber {

    private Link link;
    private ModelProvider localModelProvider;

    public EditLocalizedStringListAddRemovePanel(int gap, boolean move) {
        localModelProvider = new ModelProvider();
        link = new Link(this);

        setLayout(new BorderLayout(gap, gap));

        EditLocalizedStringListPanel<T> localized = new EditLocalizedStringListPanel<>(gap);
        localized.setModelProvider(localModelProvider);
        localized.setPath(new Path("localized"));
        localized.setMultipleSelection(true);
        this.add(localized, BorderLayout.CENTER);

        JPanel addPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) addPanel.getLayout();
        flowLayout.setHgap(0);
        flowLayout.setAlignment(FlowLayout.LEFT);
        flowLayout.setVgap(0);

        BnTextField textField = new BnTextField();
        textField.setModelProvider(localModelProvider);
        textField.setPath(new Path("newKey"));
        textField.setColumns(15);
        addPanel.add(textField);

        addPanel.add(Box.createHorizontalStrut(gap));

        BnButton addButton = GuiComponentUtils.createBnButton(GuiIcon.ADD, 2);
        addButton.setModelProvider(localModelProvider);
        addButton.setPath(new Path("add"));
        addPanel.add(addButton);

        addPanel.add(Box.createHorizontalStrut(gap));

        BnButton removeButton = GuiComponentUtils.createBnButton(GuiIcon.REMOVE, 2);
        removeButton.setModelProvider(localModelProvider);
        removeButton.setPath(new Path("remove"));
        addPanel.add(removeButton);

        if (move) {
            addPanel.add(Box.createHorizontalStrut(gap));

            BnButton up = GuiComponentUtils.createBnButton(GuiIcon.GO_UP, 2);
            up.setModelProvider(localModelProvider);
            up.setPath(new Path("moveUp"));
            addPanel.add(up);

            addPanel.add(Box.createHorizontalStrut(gap));

            BnButton down = GuiComponentUtils.createBnButton(GuiIcon.GO_DOWN, 2);
            down.setModelProvider(localModelProvider);
            down.setPath(new Path("moveDown"));
            addPanel.add(down);
        }

        this.add(addPanel, BorderLayout.SOUTH);
    }

    @Override
    public IModelProvider getModelProvider() {
        return this.link.getModelProvider();
    }

    @Override
    public void setModelProvider(IModelProvider provider) {
        this.link.setModelProvider(provider);
    }

    @Override
    public Path getPath() {
        return this.link.getPath();
    }

    @Override
    public void setPath(Path path) {
        this.link.setPath(path);
    }

    @Override
    public ARLocalizedStringListPM<T> getPresentationModel() {
        return localModelProvider.getPresentationModel();
    }

    @Override
    public void setPresentationModel(ARLocalizedStringListPM<T> pModel) {
        localModelProvider.setPresentationModel(pModel);
    }
}
