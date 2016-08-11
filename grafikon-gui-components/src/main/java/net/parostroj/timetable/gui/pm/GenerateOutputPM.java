package net.parostroj.timetable.gui.pm;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.swing.JFileChooser;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.support.Operation;
import org.beanfabrics.validation.ValidationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.TrainDiagram;

public class GenerateOutputPM extends AbstractPM {

    public static final Logger log = LoggerFactory.getLogger(GenerateOutputPM.class);

    public enum Action {
        GENERATE, GENERATE_ALL, EDIT_PATH;
    }

    private static final ValidationState DEFAULT_ERROR = ValidationState.create("");
    private static final Supplier<Boolean> DUMMY_ACTION = () -> { log.warn("Dummy action"); return true; };

    FilePathPM path = new FilePathPM(FilePathPM.Type.DIRECTORY);
    OperationPM generate = new OperationPM();
    OperationPM generateAll = new OperationPM();
    OperationPM editPath = new OperationPM();

    // callbacks for actions
    private final Map<Action, Supplier<Boolean>> actions;

    private WeakReference<JFileChooser> chooserRef;

    public GenerateOutputPM() {
        actions = new EnumMap<>(Action.class);
        path.setMandatory(true);
        generate.getValidator().add(() -> path.isValid() ? null : DEFAULT_ERROR);
        generateAll.getValidator().add(() -> path.isValid() ? null : DEFAULT_ERROR);
        PMManager.setup(this);
    }

    @Operation(path = "generate")
    public boolean generate() {
        return actions.getOrDefault(Action.GENERATE, DUMMY_ACTION).get();
    }

    @Operation(path = "generateAll")
    public boolean generateAll() {
        return actions.getOrDefault(Action.GENERATE_ALL, DUMMY_ACTION).get();
    }

    @Operation(path = "editPath")
    public boolean pathEdit() {
        return actions.getOrDefault(Action.EDIT_PATH, DUMMY_ACTION).get();
    }

    public void setAction(Action actionType, Supplier<Boolean> action) {
        if (action == null) throw new NullPointerException("Action cannot be null");
        actions.put(actionType, action);
    }

    public void removeAction(Action actionType) {
        actions.remove(actionType);
    }

    public void init(TrainDiagram diagram, JFileChooser chooser) {
        File location = getLocationFromChooser(chooser);
        path.setText(location.getAbsolutePath());
        chooserRef = new WeakReference<>(chooser);
        setAction(Action.EDIT_PATH, () -> {
            JFileChooser ch = chooserRef.get();
            if (ch != null) {
                int result = ch.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    path.setText(this.getLocationFromChooser(chooser).getAbsolutePath());
                }
            }
            return true;
        });
    }

    private File getLocationFromChooser(JFileChooser chooser) {
        return chooser.getSelectedFile() == null ? chooser.getCurrentDirectory() : chooser.getSelectedFile();
    }

    public void writeBack() {
        if (chooserRef != null && path.isValid()) {
            JFileChooser chooser = chooserRef.get();
            if (chooser != null) {
                chooser.setSelectedFile(new File(path.getText()));
            }
        }
    }
}
