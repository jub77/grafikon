package net.parostroj.timetable.gui.pm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.function.Supplier;

import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.PresentationModel;
import org.beanfabrics.model.SortKey;
import org.beanfabrics.support.Operation;
import org.beanfabrics.support.Validation;

public class ItemListPM<T extends PresentationModel> extends ListPM<T> {

    OperationPM create;
    OperationPM delete;
    OperationPM moveUp;
    OperationPM moveDown;

    private Supplier<T> createNew;
    private Collection<SortKey> sortKeys;

    private boolean sorting;

    public ItemListPM(Supplier<T> createNew) {
        super();
        this.create = new OperationPM();
        this.delete = new OperationPM();
        this.moveUp = new OperationPM();
        this.moveDown = new OperationPM();
        this.createNew = createNew;
        this.sortKeys = Collections.emptyList();
        PMManager.setup(this);
    }

    public boolean isSorted() {
        return !sortKeys.isEmpty();
    }

    public void setSorted(Collection<SortKey> sortKeys) {
        this.sortKeys = new ArrayList<>(sortKeys);
    }

    @Override
    protected void onEntriesChanged(EventObject evt) {
        super.onEntriesChanged(evt);
        if (isSorted() && !sorting) {
            sorting = true;
            try {
                this.sortBy(sortKeys);
            } finally {
                sorting = false;
            }
        }
    }

    @Operation(path = { "moveUp" })
    public void moveUp() {
        int index = this.getSelection().getMinIndex();
        this.swap(index - 1, index);
    }

    @Operation(path = { "moveDown" })
    public void moveDown() {
        int index = this.getSelection().getMinIndex();
        this.swap(index, index + 1);
    }

    @Validation(path = { "moveUp" })
    public boolean canMovekUp() {
        int[] indexes = this.getSelection().getIndexes();
        return indexes.length == 1 && indexes[0] != 0;
    }

    @Operation(path = { "delete" })
    public void delete() {
        this.removeAll(this.getSelection().toCollection());
    }

    @Operation(path = { "create" })
    public void create() {
        T element = this.createNew.get();
        this.add(element);
    }

    @Validation(path = { "moveDown" })
    public boolean canMoveDown() {
        int[] indexes = this.getSelection().getIndexes();
        return indexes.length == 1 && indexes[0] != this.size() - 1;
    }

    @Validation(path = { "delete" })
    public boolean isSelected() {
        return !this.getSelection().isEmpty();
    }
}
