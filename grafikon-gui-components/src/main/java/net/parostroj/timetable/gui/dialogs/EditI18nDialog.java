package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.beanfabrics.ModelProvider;
import org.beanfabrics.Path;
import org.beanfabrics.swing.BnButton;
import org.beanfabrics.swing.BnComboBox;

import net.parostroj.timetable.gui.GuiContext;
import net.parostroj.timetable.gui.GuiContextComponent;
import net.parostroj.timetable.gui.components.EditLocalizedStringListPanel;
import net.parostroj.timetable.gui.pm.LocalizationContext;
import net.parostroj.timetable.gui.pm.LocalizationPM;
import net.parostroj.timetable.gui.pm.LocalizationType;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.utils.AttributeReference;
import net.parostroj.timetable.utils.Reference;

public class EditI18nDialog extends JDialog implements GuiContextComponent {

    private ModelProvider provider;

    public EditI18nDialog(Window parent, boolean modal) {
        super(parent, modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        provider = new ModelProvider();
        final JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(5, 5));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        contentPanel.add(new EditLocalizedStringListPanel(new Path("selected"), provider, 5), BorderLayout.CENTER);

        BnComboBox comboBox = new BnComboBox();
        comboBox.setModelProvider(provider);
        comboBox.setPath(new Path("types"));
        contentPanel.add(comboBox, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel.getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        contentPanel.add(panel, BorderLayout.SOUTH);

        BnButton okButton = new BnButton();
        okButton.setText(ResourceLoader.getString("button.ok"));
        okButton.setModelProvider(provider);
        okButton.setPath(new Path("ok"));
        okButton.addActionListener(evt -> setVisible(false));
        panel.add(okButton);

        JButton cancelButton = new JButton(ResourceLoader.getString("button.cancel"));
        cancelButton.addActionListener(evt -> setVisible(false));
        panel.add(cancelButton);

        this.getContentPane().add(contentPanel);
        this.pack();
    }

    public void showDialog(TrainDiagram diagram) {
        this.provider.setPresentationModel(this.createPM(diagram));
        this.setVisible(true);
    }

    @Override
    public void registerContext(GuiContext context) {
        context.registerWindow("localization.all", this);
    }

    private LocalizationPM createPM(TrainDiagram diagram) {
        LocalizationContext context = new LocalizationContext(createTypes(diagram));
        LocalizationPM localization = new LocalizationPM();
        localization.init(context);
        return localization;
    }

    private Collection<LocalizationType> createTypes(TrainDiagram diagram) {
        Collection<LocalizationType> types = new ArrayList<>();
        types.add(new LocalizationType(
                ResourceLoader.getString("localization.type.train.comments"),
                diagram.getTrains().stream().flatMap(train -> train.getTimeIntervalList().stream())
                        .filter(interval -> interval.getComment() != null)
                        .map(interval -> new AttributeReference<>(interval, TimeInterval.ATTR_COMMENT, LocalizedString.class))
                        .collect(Collectors.toList()),
                ref -> getTimeIntervalDesc(ref),
                diagram.getLocales()));
        types.add(new LocalizationType(
                ResourceLoader.getString("localization.type.circulation.item.comments"),
                diagram.getCycles().stream().flatMap(cycle -> cycle.getItems().stream())
                        .filter(cycle -> cycle.getComment() != null)
                        .map(cycle -> new AttributeReference<>(cycle, TrainsCycleItem.ATTR_COMMENT, LocalizedString.class))
                        .collect(Collectors.toList()),
                ref -> getCirculationItemDesc(ref),
                diagram.getLocales()));
        types.add(new LocalizationType(
                ResourceLoader.getString("localization.type.circulation.type.names"),
                diagram.getCycleTypes().stream()
                        .filter(type -> !type.isDefaultType() && type.getDisplayName() != null)
                        .map(type -> new AttributeReference<>(type, TrainsCycleType.ATTR_DISPLAY_NAME, LocalizedString.class))
                        .collect(Collectors.toList()),
                ref -> ref.get().getDefaultString(),
                diagram.getLocales()));
        return types;
    }

    private static String getCirculationItemDesc(Reference<LocalizedString> ref) {
        TrainsCycleItem circulationItem = (TrainsCycleItem) ((AttributeReference<?>) ref).getHolder();
        String circDesc = circulationItem.getCycle().getDisplayDescription();
        String trainDesc = circulationItem.getTrain().getName();
        return String.format("%s (%s: %s)", trainDesc, circulationItem.getCycle().getName(), circDesc);
    }

    private static String getTimeIntervalDesc(Reference<LocalizedString> ref) {
        TimeInterval interval = (TimeInterval) ((AttributeReference<?>) ref).getHolder();
        String trainDesc = interval.getTrain().getName();
        String nodeDesc = interval.getOwnerAsNode().getName();
        return String.format("%s (%s)", trainDesc, nodeDesc);
    }
}
