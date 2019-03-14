package net.parostroj.timetable.gui.pm;

import org.beanfabrics.model.*;

/**
 * Presentation model for selection of an item. It contains the item itself
 * (presentation model) as well as a boolean pm for the selection.
 *
 * @author jub
 */
public class SelectedItemPM<T extends PresentationModel> extends AbstractPM {

    IBooleanPM selected;
    T item;

    public SelectedItemPM(boolean selected, T item) {
        this.selected = new BooleanPM();
        this.selected.setBoolean(selected);
        this.item = item;
        PMManager.setup(this);
    }

    public IBooleanPM getSelected() {
        return selected;
    }

    public T getItem() {
        return item;
    }
}
