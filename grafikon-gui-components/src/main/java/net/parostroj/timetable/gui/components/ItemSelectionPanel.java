package net.parostroj.timetable.gui.components;

import org.beanfabrics.Path;
import org.beanfabrics.PathEvaluation;
import org.beanfabrics.event.*;
import org.beanfabrics.model.*;
import org.beanfabrics.swing.BnCheckBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Panel with selection of elements from list. The list contains presentation models
 * with nested boolean and text presentation model for checking and also the text
 * asociated with the item.
 *
 * @author jub
 */
public class ItemSelectionPanel<T extends PresentationModel> extends BaseEditPanel<ListPM<T>> implements Scrollable {

    private static final long serialVersionUID = 1L;

    private static final int VISIBLE_ROW_COUNT = 8;
    private static final int SCROLL_INCREMENT = 8;

    private final Path selectedPath;
    private final Path itemPath;

    private int visibleRows = VISIBLE_ROW_COUNT;

    private Map<ITextPM, BnCheckBox> checkBoxMap = new HashMap<>();

    private final WeakPropertyChangeListener listener = evt -> {
        processChangeEvent((BnPropertyChangeEvent) evt);
    };

    private final WeakPropertyChangeListener itemListener = evt -> {
        processItemChangetEvent((BnPropertyChangeEvent) evt);
    };

    private final FocusAdapter scrollToSelection = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            Container parent = SwingUtilities.getUnwrappedParent(ItemSelectionPanel.this);
            if (parent instanceof JViewport) {
                Rectangle bounds = e.getComponent().getBounds();
                bounds = SwingUtilities.convertRectangle(ItemSelectionPanel.this, bounds, parent);
                ((JViewport) parent).scrollRectToVisible(bounds);
            }
        }
    };

    public ItemSelectionPanel(Path selected, Path item) {
        this.selectedPath = selected;
        this.itemPath = item;

        this.setBackground(UIManager.getColor("List.background"));
        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        this.setLayout(layout);
        this.setAutoscrolls(true);

        refreshItems(null);
    }

    public int getVisibleRows() {
        return visibleRows;
    }

    public void setVisibleRows(int visibleRows) {
        this.visibleRows = visibleRows;
    }

    @Override
    public void setPresentationModel(ListPM<T> newModel) {
        ListPM<T> oldModel = localProvider.getPresentationModel();
        if (oldModel != null) {
            oldModel.removePropertyChangeListener(listener);
        }
        localProvider.setPresentationModel(newModel);
        if (newModel != null) {
            newModel.addPropertyChangeListener(listener);
        }
        refreshItems(newModel);
        this.firePropertyChange("presentationModel", oldModel, newModel);
    }

    private void processChangeEvent(BnPropertyChangeEvent event) {
        EventObject cause = event.getCause();
        if (cause != null) {
            if (cause instanceof ElementsAddedEvent
                    || cause instanceof ElementsReplacedEvent
                    || cause instanceof ElementsRemovedEvent) {
                this.refreshItems(getPresentationModel());
            }
        }
    }

    private void processItemChangetEvent(BnPropertyChangeEvent event) {
        ITextPM text = (ITextPM) event.getSource();
        BnCheckBox cb = checkBoxMap.get(text);
        if (cb != null) {
            cb.setText(text.getText());
        }
    }

    private void refreshItems(ListPM<T> items) {
        for (ITextPM pm : checkBoxMap.keySet()) {
            pm.removePropertyChangeListener(itemListener);
        }
        checkBoxMap.clear();
        this.removeAll();
        if (items != null && !items.isEmpty()) {
            for (T item : items) {
                IBooleanPM bPm = this.getModelForPath(item, selectedPath, IBooleanPM.class);
                ITextPM tPm = this.getModelForPath(item, itemPath, ITextPM.class);
                tPm.addPropertyChangeListener(itemListener);

                BnCheckBox checkBox = new BnCheckBox();
                checkBox.setPresentationModel(bPm);
                checkBox.setBackground(UIManager.getColor("List.background"));
                checkBox.setBorder(BorderFactory.createEmptyBorder());

                checkBoxMap.put(tPm, checkBox);
                checkBox.setText(tPm.getText());

                checkBox.addFocusListener(scrollToSelection);

                this.add(checkBox);
            }
            this.add(Box.createVerticalGlue());
        } else {
            this.add(new JLabel(" "));
        }
        this.revalidate();
    }

    private <X> X getModelForPath(PresentationModel model, Path path, Class<X> clazz) {
        PathEvaluation eval = new PathEvaluation(model, path);
        return clazz.cast(eval.getResult().getValue());
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        Dimension size = super.getPreferredSize();
        ListPM<?> pm = localProvider.getPresentationModel();
        int itemCount = pm == null ? 1 : pm.size();
        size.height = size.height * visibleRows / itemCount;
        return size;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return SCROLL_INCREMENT;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return SCROLL_INCREMENT;
    }

    public boolean getScrollableTracksViewportWidth() {
        Container parent = SwingUtilities.getUnwrappedParent(this);
        if (parent instanceof JViewport) {
            return parent.getWidth() > getPreferredSize().width;
        }
        return false;
    }


    @Override
    public boolean getScrollableTracksViewportHeight() {
        Container parent = SwingUtilities.getUnwrappedParent(this);
        if (parent instanceof JViewport) {
            return parent.getHeight() > getPreferredSize().height;
        }
        return false;
    }
}
