package net.parostroj.timetable.gui.pm;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.Options;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.support.Operation;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.utils.Reference;

public class LocalizationPM<T extends Reference<LocalizedString>> extends AbstractPM implements IPM<LocalizationContext<T>> {

    final IEnumeratedValuesPM<LocalizationType<T>> types;
    final LocalizedStringListPM<T> selected;

    final OperationPM ok;
    private LocalizationContext<T> context;

    public LocalizationPM() {
        ok = new OperationPM();
        types = new EnumeratedValuesPM<>(new Options<>());
        selected = new LocalizedStringListPM<>();
        selected.setSorted(true);
        types.addPropertyChangeListener("text", evt -> {
            LocalizationType<T> item = types.getValue();
            if (item != null) {
                selected.init(item, item.getStrings().isEmpty() ? null : item.getStrings().iterator().next());
            }
        });
        PMManager.setup(this);
    }

    @Override
    public void init(LocalizationContext<T> context) {
        this.context = context;
        for (LocalizationType<T> type : context) {
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
