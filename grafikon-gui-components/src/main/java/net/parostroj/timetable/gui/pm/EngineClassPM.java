package net.parostroj.timetable.gui.pm;

import java.lang.ref.WeakReference;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.IOperationPM;
import org.beanfabrics.model.ITextPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.support.Operation;

import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.utils.ObjectsUtil;

public class EngineClassPM extends AbstractPM implements IPM<EngineClass> {

    ITextPM name = new TextPM();
    ITextPM groupKey = new TextPM();
    IOperationPM ok = new OperationPM();

    private WeakReference<EngineClass> reference;

    public EngineClassPM() {
        name.setMandatory(true);
        PMManager.setup(this);
    }

    @Override
    public void init(EngineClass engineClass) {
        name.setText(engineClass.getName());
        groupKey.setText(engineClass.getGroupKey());
        reference = new WeakReference<>(engineClass);
    }

    @Operation(path = "ok")
    public void ok() {
        EngineClass engineClass = reference != null ? reference.get() : null;
        if (engineClass != null) {
            // write values back
            engineClass.setName(name.getText());
            engineClass.setGroupKey(ObjectsUtil.checkAndTrim(groupKey.getText()));
        }
    }
}
