package net.parostroj.timetable.gui.events;

import java.io.File;

public record OpenedChangedMessage(Type type, File file) {
    public enum Type { ADD, REMOVE }
}
