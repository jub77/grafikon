package net.parostroj.timetable.gui.commands;

import net.parostroj.timetable.gui.ApplicationModel;

import java.util.function.Consumer;

/**
 * Interface for commands.
 * 
 * @author jub
 */
@FunctionalInterface
public interface Command extends Consumer<ApplicationModel> {
    default void execute(ApplicationModel model) {
        this.accept(model);
    }
}
