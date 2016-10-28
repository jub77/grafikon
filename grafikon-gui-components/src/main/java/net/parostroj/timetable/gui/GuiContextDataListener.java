package net.parostroj.timetable.gui;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Gui context with preferences
 *
 * @author jub
 */
public interface GuiContextDataListener {

    void init(Map<String, String> settings);

    Map<String, String> save();

    static GuiContextDataListener create(Consumer<Map<String, String>> init, Supplier<Map<String, String>> save) {
        return new GuiContextDataListener() {
            @Override
            public Map<String, String> save() {
                return save.get();
            }

            @Override
            public void init(Map<String, String> settings) {
                init.accept(settings);
            }
        };
    }
}
