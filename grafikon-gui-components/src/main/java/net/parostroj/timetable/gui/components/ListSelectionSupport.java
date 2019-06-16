package net.parostroj.timetable.gui.components;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
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
    private final Supplier<? extends ListPM<?>> listSupplier;
    private final Supplier<T> selectedSupplier;

    public ListSelectionSupport(View<T> view, Supplier<? extends ListPM<T>> listSupplier) {
        this(Collections.singletonList(view), listSupplier);
    }

    public ListSelectionSupport(Collection<? extends View<T>> views, Supplier<? extends ListPM<T>> listSupplier) {
        this.views = views;
        this.listSupplier = listSupplier;
        this.selectedSupplier = () -> {
        	ListPM<T> list = listSupplier.get();
            Selection<T> selection = list.getSelection();
            T selected = selection.getIndexes().length == 1 ? selection.getFirst() : null;
            return selected;
        };
        this.listListener = new ListAdapter() {
            @Override
            public void elementsDeselected(ElementsDeselectedEvent evt) {
                updateSelected(selectedSupplier.get());
            }

            @Override
            public void elementsSelected(ElementsSelectedEvent evt) {
                updateSelected(selectedSupplier.get());
            }
        };
    }

    public <V extends PresentationModel> ListSelectionSupport(View<T> view, Supplier<? extends ListPM<V>> listSupplier, Function<V, T> getter) {
    	this(Collections.singletonList(view), listSupplier, getter);
    }

    public <V extends PresentationModel> ListSelectionSupport(Collection<? extends View<T>> views, Supplier<? extends ListPM<V>> listSupplier, Function<V, T> getter) {
        this.views = views;
        this.listSupplier = listSupplier;
        this.selectedSupplier = () -> {
        	ListPM<V> list = listSupplier.get();
            Selection<V> selection = list.getSelection();
            V selected = selection.getIndexes().length == 1 ? selection.getFirst() : null;
            return getter.apply(selected);
        };
        this.listListener = new ListAdapter() {
            @Override
            public void elementsDeselected(ElementsDeselectedEvent evt) {
                updateSelected(selectedSupplier.get());
            }

            @Override
            public void elementsSelected(ElementsSelectedEvent evt) {
                updateSelected(selectedSupplier.get());
            }
        };
    }

    private void updateSelected(T selected) {
        views.forEach(view -> {
            view.setPresentationModel(selected);
        });
    }

    public ListListener getListListener() {
		return listListener;
	}

    @Override
    public void modelGained(ModelProviderEvent evt) {
        listSupplier.get().addListListener(listListener);
        updateSelected(selectedSupplier.get());
    }

    @Override
    public void modelLost(ModelProviderEvent evt) {
        listSupplier.get().removeListListener(listListener);
        views.forEach(view -> view.setPresentationModel(null));
    }
}
