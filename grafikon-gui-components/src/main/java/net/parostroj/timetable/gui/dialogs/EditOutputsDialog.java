package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Collection;
import java.util.Collections;

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
import org.beanfabrics.swing.BnCheckBox;
import org.beanfabrics.swing.BnTextField;

import com.google.common.collect.Collections2;
import com.google.common.io.Files;

import net.parostroj.timetable.gui.GuiContext;
import net.parostroj.timetable.gui.GuiContextComponent;
import net.parostroj.timetable.gui.GuiContextDataListener;
import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.ActionHandler;
import net.parostroj.timetable.gui.actions.execution.ModelAction;
import net.parostroj.timetable.gui.actions.execution.OutputTemplateAction;
import net.parostroj.timetable.gui.pm.GenerateOutputPM;
import net.parostroj.timetable.gui.pm.GenerateOutputPM.Action;
import net.parostroj.timetable.gui.pm.OutputPM;
import net.parostroj.timetable.gui.utils.OutputTypeUtil;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperDelegateAdapter;
import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.Output;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputWriter.Settings;

public class EditOutputsDialog extends EditItemsDialog<Output, TrainDiagram> implements GuiContextComponent, View<GenerateOutputPM>, ModelSubscriber {

    private static final String INI_KEY_CLEAR_DIRECTORY = "clear.directory";

    private ModelProvider provider;
    private Link link;
    private GuiContext context;
    private Settings settings;

    public EditOutputsDialog(Window window, boolean modal) {
        super(window, modal, false, true, false);

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

        BnCheckBox clearDirectoryCheckBox = new BnCheckBox();
        clearDirectoryCheckBox.setText(ResourceLoader.getString("ot.clear.directory"));
        clearDirectoryCheckBox.setModelProvider(provider);
        clearDirectoryCheckBox.setPath(new Path("clearDirectory"));

        GroupLayout generatePanelLayout = new GroupLayout(generatePanel);
        generatePanelLayout.setHorizontalGroup(
            generatePanelLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(generatePanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(clearDirectoryCheckBox)
                    .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(generateButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(generateAllButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        generatePanelLayout.setVerticalGroup(
            generatePanelLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(generatePanelLayout.createSequentialGroup()
                    .addGroup(generatePanelLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(generateAllButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(generateButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(clearDirectoryCheckBox))
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
        element.getOutputs().add(item);
    }

    @Override
    protected void remove(Output item) {
        element.getOutputs().remove(item);
    }

    @Override
    protected void move(Output item, int oldIndex, int newIndex) {
        throw new IllegalStateException("Move not allowed");
    }

    @Override
    protected boolean deleteAllowed(Output item) {
        return item != null;
    }

    @Override
    protected Output createNew(String name) {
        NewOutputDialog dialog = new NewOutputDialog(this, true);
        OutputPM pModel = new OutputPM(getPresentationModel().getLocales(), getPresentationModel().getModelLocales());
        pModel.initNew(element);
        dialog.setPresentationModel(pModel);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.dispose();
        return pModel.getNewOutput();
    }

    @Override
    protected void edit(Output item) {
        EditOutputDialog dialog = new EditOutputDialog(this, true);
        dialog.setLocationRelativeTo(this);
        dialog.registerContext(context);
        LocalizedString oldName = item.getName();
        OutputPM pModel = new OutputPM(getPresentationModel().getLocales(), getPresentationModel().getModelLocales());
        dialog.setPresentationModel(pModel);
        pModel.init(element, item);
        dialog.setVisible(true);
        dialog.dispose();
        // refresh item(s)
        if (!oldName.equals(item.getName())) {
            refreshAll();
        }
    }

    @Override
    public void registerContext(GuiContext context) {
        context.registerWindow("output.list", this, GuiContextDataListener.create(initMap -> {
            getPresentationModel().setClearDirectory(initMap.containsKey(INI_KEY_CLEAR_DIRECTORY));
        }, () -> {
            if (getPresentationModel().isClearDirectory()) {
                return Collections.singletonMap(INI_KEY_CLEAR_DIRECTORY, "clear");
            } else {
                return Collections.emptyMap();
            }
        }));

        this.context = context;
    }

    @Override
    public GenerateOutputPM getPresentationModel() {
        return provider.getPresentationModel();
    }

    @Override
    public void setPresentationModel(final GenerateOutputPM pModel) {
        pModel.setAction(Action.GENERATE, () -> {
            generateOutputs(pModel, this.getSelectedItems());
            return true;
        });
        pModel.setAction(Action.GENERATE_ALL, () -> {
            generateOutputs(pModel, Collections2.transform(
                    listModel.getListOfWrappers(),
                    wrapper -> wrapper.getElement()));
            return true;
        });
        provider.setPresentationModel(pModel);
    }

    private void clearDirectory(File location) {
        Files.fileTreeTraverser().postOrderTraversal(location).forEach(file -> {
            // try to delete - ignore if it doesn't succeed (successful operation is not required)
            if (!file.equals(location)) {
                file.delete();
            }
        });
    }

    private void generateOutputs(final GenerateOutputPM pModel, Collection<Output> outputs) {
        ActionContext context = new ActionContext();
        context.setLocationComponent(this);
        OutputTemplateAction action = new OutputTemplateAction(context, element, settings, pModel.getLocation(),
                outputs);
        if (pModel.isClearDirectory()) {
            ActionHandler.getInstance().execute(ModelAction.newAction(context, () -> {
                clearDirectory(pModel.getLocation());
            }));
        }
        ActionHandler.getInstance().execute(action);
    }

    @Override
    protected void selectionChanged(int selectedItemsCount) {
        GenerateOutputPM pModel = getPresentationModel();
        if (pModel != null) {
            pModel.setSelectedCount(selectedItemsCount);
        }
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

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    @Override
    protected Wrapper<Output> createWrapper(Output item) {
        return Wrapper.getWrapper(item, new WrapperDelegateAdapter<>(element -> String.format("%s: %s",
                OutputTypeUtil.convertOutputType(element.getTemplate()), element.getName().translate())));
    }
}
