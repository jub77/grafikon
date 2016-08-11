package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;
import org.beanfabrics.swing.BnButton;
import org.beanfabrics.swing.BnTextField;

import net.parostroj.timetable.gui.GuiContext;
import net.parostroj.timetable.gui.GuiContextComponent;
import net.parostroj.timetable.gui.pm.GenerateOutputPM;
import net.parostroj.timetable.gui.pm.OutputPM;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.Output;
import net.parostroj.timetable.model.TrainDiagram;

public class EditOutputsDialog extends EditItemsDialog<Output, TrainDiagram> implements GuiContextComponent, View<GenerateOutputPM>, ModelSubscriber {

    private ModelProvider provider;
    private Link link;
    private GuiContext context;

    public EditOutputsDialog(Window window, boolean modal) {
        super(window, modal, true, true, false);

        this.setMultipleSelection(true);

        this.provider = new ModelProvider();
        this.link = new Link(this);

        JPanel locationPanel = new JPanel();
        getContentPane().add(locationPanel, BorderLayout.NORTH);

        BnTextField locationTextField = new BnTextField();
        locationTextField.setModelProvider(provider);
        locationTextField.setPath(new Path("path"));

        BnButton editLocationButton = new BnButton();
        editLocationButton.setText(ResourceLoader.getString("button.select"));
        editLocationButton.setModelProvider(provider);
        editLocationButton.setPath(new Path("editPath"));

        GroupLayout locationPanelLayout = new GroupLayout(locationPanel);
        locationPanelLayout.setHorizontalGroup(
            locationPanelLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(locationPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(locationTextField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(editLocationButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        locationPanelLayout.setVerticalGroup(
            locationPanelLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(locationPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(locationPanelLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(locationTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(editLocationButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
        );
        locationPanel.setLayout(locationPanelLayout);

        JPanel generatePanel = new JPanel();
        getContentPane().add(generatePanel, BorderLayout.SOUTH);

        BnButton generateButton = new BnButton();
        generateButton.setText(ResourceLoader.getString("ot.button.output"));
        generateButton.setModelProvider(provider);
        generateButton.setPath(new Path("generate"));

        BnButton generateAllButton = new BnButton();
        generateAllButton.setText(ResourceLoader.getString("ot.button.outputall"));
        generateAllButton.setModelProvider(provider);
        generateAllButton.setPath(new Path("generateAll"));

        GroupLayout generatePanelLayout = new GroupLayout(generatePanel);
        generatePanelLayout.setHorizontalGroup(
            generatePanelLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(generatePanelLayout.createSequentialGroup()
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(generateButton)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(generateAllButton)
                    .addContainerGap())
        );
        generatePanelLayout.setVerticalGroup(
            generatePanelLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(generatePanelLayout.createSequentialGroup()
                    .addGroup(generatePanelLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(generateAllButton)
                        .addComponent(generateButton))
                    .addContainerGap())
        );
        generatePanel.setLayout(generatePanelLayout);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                getPresentationModel().writeBack();
            }
        });
        pack();
    }

    @Override
    protected Collection<Output> getList() {
        return element.getOutputs();
    }

    @Override
    protected void add(Output item, int index) {
        element.getOutputs().add(index, item);
    }

    @Override
    protected void remove(Output item) {
        element.getOutputs().remove(item);
    }

    @Override
    protected void move(Output item, int oldIndex, int newIndex) {
        element.getOutputs().move(oldIndex, newIndex);
    }

    @Override
    protected boolean deleteAllowed(Output item) {
        return item != null;
    }

    @Override
    protected Output createNew(String name) {
        NewOutputDialog dialog = new NewOutputDialog(this, true);
        OutputPM pModel = new OutputPM();
        pModel.initNew(element);
        dialog.setPresentationModel(pModel);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.dispose();
        return pModel.createNewOutput();
    }

    @Override
    protected void edit(Output item) {
        EditOutputDialog dialog = new EditOutputDialog(this, true);
        dialog.setLocationRelativeTo(this);
        dialog.registerContext(context);
        OutputPM pModel = new OutputPM();
        dialog.setPresentationModel(pModel);
        pModel.init(element, item);
        dialog.setVisible(true);
        dialog.dispose();
        // refresh item
        refresh(item);
    }

    @Override
    public void registerContext(GuiContext context) {
        context.registerWindow("output.list", this);
        this.context = context;
    }

    @Override
    public GenerateOutputPM getPresentationModel() {
        return provider.getPresentationModel();
    }

    @Override
    public void setPresentationModel(GenerateOutputPM pModel) {
        provider.setPresentationModel(pModel);
    }

    @Override
    public IModelProvider getModelProvider() {
        return link.getModelProvider();
    }

    @Override
    public void setModelProvider(IModelProvider provider) {
        link.setModelProvider(provider);
    }

    @Override
    public Path getPath() {
        return link.getPath();
    }

    @Override
    public void setPath(Path path) {
        link.setPath(path);
    }
}
