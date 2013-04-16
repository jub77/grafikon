package net.parostroj.timetable.gui.utils;

public enum GuiIcon {
    ADD("icons/add.png"), REMOVE("icons/delete.png"), CONFIGURE_T("icons/configure_t.png"),
    ADD_DOC("icons/add_document.png"), ZOOM_IN("icons/zoom_in.png"), ZOOM_OUT("icons/zoom_out.png"),
    EDIT("icons/edit.png"), CONNECT("icons/connect.png"), SELECT("icons/select.png"),
    GO_LEFT("icons/go_left.png"), GO_RIGHT("icons/go_right.png"), GO_UP("icons/go_UP.png"),
    GO_DOWN("icons/go_down.png"), ARROW_LEFT("icons/arrow_left.png"), ARROW_RIGHT("icons/arrow_right.png"),
    ARROW_UP("icons/arrow_UP.png"), ARROW_DOWN("icons/arrow_down.png"), DARROW_LEFT("icons/arrow_left_double.png"),
    DARROW_RIGHT("icons/arrow_right_double.png"), VIEW_SORT("icons/view_sort.png"), COPY("icons/copy.png"),
    PLUS("icons/plus.png"), MINUS("icons/minus.png");

    private final String path;

    private GuiIcon(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}