package net.parostroj.timetable.gui.components;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import org.beanfabrics.View;
import org.beanfabrics.event.ElementsDeselectedEvent;
import org.beanfabrics.event.ElementsSelectedEvent;
import org.beanfabrics.event.ListAdapter;
import org.beanfabrics.event.ListListener;
import org.beanfabrics.event.ModelProviderEvent;
import org.beanfabrics.event.ModelProviderListener;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.PresentationModel;
import org.beanfabrics.model.Selection;

/**
 * Helper for selection of one item from the list and displaying it
 * in separate view.
 *
 * @author jub
 *
 * @param <T> model of the selection
 */
public class ListSelectionSupport<T extends PresentationModel> implements ModelProviderListener {

    private final ListListener listListener;

    private final Collection<? extends View<T>> views;
    private final Supplier<? extends ListPM<T>> listSupplier;

    public ListSelectionSupport(View<T> view, Supplier<? extends ListPM<T>> listSupplier) {
        this(Collections.singletonList(view), listSupplier);
    }

    public ListSelectionSupport(Collection<? extends View<T>> views, Supplier<? extends ListPM<T>> listSupplier) {
        this.views = views;
        this.listSupplier = listSupplier;
        this.listListener = new ListAdapter() {
            @Override
            public void elementsDeselected(ElementsDeselectedEvent evt) {
                updateSelected(listSupplier.get());
            }

            @Override
            public void elementsSelected(ElementsSelectedEvent evt) {
                updateSelected(listSupplier.get());
            }
        };
    }

    private void updateSelected(ListPM<T> list) {
        Selection<T> selection = list.getSelection();
        T selected = selection.getIndexes().length == 1 ? selection.getFirst() : null;
        views.forEach(view -> {
            view.setPresentationModel(selected);
        });
    }

    @Override
    public void modelGained(ModelProviderEvent evt) {
        listSupplier.get().addListListener(listListener);
        updateSelected(listSupplier.get());
    }

    @Override
    public void modelLost(ModelProviderEvent evt) {
        listSupplier.get().removeListListener(listListener);
        views.forEach(view -> view.setPresentationModel(null));
    }
}
