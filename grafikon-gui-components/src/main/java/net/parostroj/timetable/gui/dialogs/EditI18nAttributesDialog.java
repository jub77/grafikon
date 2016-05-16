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

import net.parostroj.timetable.gui.GuiContext;
import net.parostroj.timetable.gui.GuiContextComponent;
import net.parostroj.timetable.gui.components.EditLocalizedStringListAddRemovePanel;
import net.parostroj.timetable.gui.pm.ARLocalizationType;
import net.parostroj.timetable.gui.pm.ARLocalizedStringListPM;
import net.parostroj.timetable.gui.pm.LocalizationTypeFactory;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.Attributes;
import net.parostroj.timetable.model.AttributesHolder;
import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.AttributeReference;

public class EditI18nAttributesDialog extends JDialog implements GuiContextComponent {

    private ModelProvider provider;

    public EditI18nAttributesDialog(Window parent, boolean modal) {
        super(parent, modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        provider = new ModelProvider();
        final JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(5, 5));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        contentPanel.add(new EditLocalizedStringListAddRemovePanel(new Path("this"), provider, 5), BorderLayout.CENTER);

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

    public void showDialog(TrainDiagram diagram, AttributesHolder holder) {
        this.provider.setPresentationModel(this.createPM(diagram, holder));
        this.setVisible(true);
    }

    @Override
    public void registerContext(GuiContext context) {
        context.registerWindow("localization.all", this);
    }

    private ARLocalizedStringListPM<AttributeReference<LocalizedString>> createPM(TrainDiagram diagram,
            AttributesHolder holder) {
        ARLocalizationType<AttributeReference<LocalizedString>> type = LocalizationTypeFactory.createInstance()
                .createEditFromAttributeHolder(diagram, holder, Attributes.I18N_CATEGORY);
        ARLocalizedStringListPM<AttributeReference<LocalizedString>> pm = new ARLocalizedStringListPM<>();
        pm.init(type);
        return pm;
    }
}
