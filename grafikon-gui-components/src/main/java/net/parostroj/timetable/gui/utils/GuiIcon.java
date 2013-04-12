package net.parostroj.timetable.gui.utils;

public enum GuiIcon {
    ADD("icons/add.png"), REMOVE("icons/delete.png"), CONFIGURE_T("icons/configure_t.png"),
    ADD_DOC("icons/add_document.png"), ZOOM_IN("icons/zoom_in.png"), ZOOM_OUT("icons/zoom_out.png"),
    EDIT("icons/edit.png"), CONNECT("icons/connect.png"), SELECT("icons/select.png");

    private final String path;

    private GuiIcon(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}