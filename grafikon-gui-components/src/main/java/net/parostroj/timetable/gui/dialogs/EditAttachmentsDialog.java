package net.parostroj.timetable.gui.dialogs;

import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.swing.JFileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.wrappers.BasicWrapperDelegate;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.Attachment;
import net.parostroj.timetable.model.Attachment.AttachmentType;
import net.parostroj.timetable.model.OutputTemplate;

/**
 * Dialog for editing groups.
 *
 * @author jub
 */
public class EditAttachmentsDialog extends EditItemsDialog<Attachment, OutputTemplate> {

    private static final Logger log = LoggerFactory.getLogger(EditAttachmentsDialog.class);

    private final JFileChooser chooser;

    public EditAttachmentsDialog(Window parent, boolean modal, JFileChooser chooser) {
        super(parent, modal, false, false, false);
        this.chooser = chooser;
    }

    @Override
    protected Collection<Attachment> getList() {
        return element.getAttachments().toCollection();
    }

    @Override
    protected void add(Attachment item, int index) {
        // ignore index
        element.getAttachments().add(item);
    }

    @Override
    protected void remove(Attachment item) {
        element.getAttachments().remove(item);
    }

    @Override
    protected void move(Attachment item, int oldIndex, int newIndex) {
        throw new IllegalStateException("Move not allowed");
    }

    @Override
    protected Wrapper<Attachment> createWrapper(Attachment item) {
        Wrapper<Attachment> wrapper = new Wrapper<>(item, new BasicWrapperDelegate<Attachment>() {
            @Override
            public String toString(Attachment element) {
                return String.format("%s (%d)", element.getName(),
                        element.getType() == AttachmentType.BINARY ? element.getBinary().length : element.getText()
                                .length());
            }
        });
        return wrapper;
    }

    @Override
    protected boolean deleteAllowed(Attachment item) {
        return true;
    }

    @Override
    protected Attachment createNew(String name) {
        int result = chooser.showOpenDialog(this);
        Attachment attachment = null;
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                byte[] bytes = Files.toByteArray(file);
                attachment = new Attachment(file.getName(), bytes);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                GuiComponentUtils.showError("Error reading file", this);
            }
        }
        return attachment;
    }
}
