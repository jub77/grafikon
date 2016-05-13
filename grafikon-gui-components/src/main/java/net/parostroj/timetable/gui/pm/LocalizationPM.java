package net.parostroj.timetable.gui.pm;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.Options;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.support.Operation;

public class LocalizationPM extends AbstractPM {

    final IEnumeratedValuesPM<LocalizationType> types;
    final LocalizedStringListPM selected;

    final OperationPM ok;
    private LocalizationContext context;

    public LocalizationPM() {
        ok = new OperationPM();
        types = new EnumeratedValuesPM<>(new Options<>());
        selected = new LocalizedStringListPM();
        types.addPropertyChangeListener("text", evt -> {
            LocalizationType item = types.getValue();
            if (item != null) {
                selected.init(item);
            }
        });
        PMManager.setup(this);
    }

    public void init(LocalizationContext context) {
        this.context = context;
        for (LocalizationType type : context) {
            types.addValue(type, type.getDescription());
        }
        types.setText(types.getOptions().getValues()[0]);
    }

    @Operation(path = "ok")
    public boolean writeBack() {
        context.writeBack();
        return true;
    }
}
