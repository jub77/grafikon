package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;

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
import net.parostroj.timetable.gui.pm.LocalizationTypeFactory;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.AttributeReference;
import net.parostroj.timetable.utils.Reference;

public class EditI18nDialog extends JDialog implements GuiContextComponent {

    private static final long serialVersionUID = 1L;

	private ModelProvider provider;

    public EditI18nDialog(Window parent, boolean modal) {
        super(parent, modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        provider = new ModelProvider();
        final JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(5, 5));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        EditLocalizedStringListPanel<Reference<LocalizedString>> lsPanel = new EditLocalizedStringListPanel<>(5);
        lsPanel.setModelProvider(provider);
        lsPanel.setPath(new Path("selected"));
        contentPanel.add(lsPanel, BorderLayout.CENTER);

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

    private LocalizationPM<AttributeReference<LocalizedString>> createPM(TrainDiagram diagram) {
        LocalizationContext<AttributeReference<LocalizedString>> context = createLocalizationContext(diagram);
        LocalizationPM<AttributeReference<LocalizedString>> localization = new LocalizationPM<>();
        localization.init(context);
        return localization;
    }

    private LocalizationContext<AttributeReference<LocalizedString>> createLocalizationContext(TrainDiagram diagram) {
        LocalizationContext<AttributeReference<LocalizedString>> context = new LocalizationContext<>(
                LocalizationTypeFactory.createInstance().createTypes(diagram));
        return context;
    }
}
