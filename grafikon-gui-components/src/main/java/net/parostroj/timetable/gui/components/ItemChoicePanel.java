package net.parostroj.timetable.gui.components;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.beanfabrics.Path;
import org.beanfabrics.PathEvaluation;
import org.beanfabrics.event.BnPropertyChangeEvent;
import org.beanfabrics.event.ElementsAddedEvent;
import org.beanfabrics.event.ElementsRemovedEvent;
import org.beanfabrics.event.ElementsReplacedEvent;
import org.beanfabrics.event.WeakPropertyChangeListener;
import org.beanfabrics.model.IBooleanPM;
import org.beanfabrics.model.ITextPM;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.PresentationModel;
import org.beanfabrics.swing.BnRadioButton;

/**
 * Panel with selection of element from list. The list contains presentation models
 * with nested boolean and text presentation model for checking and also the text
 * asociated with the item.
 *
 * @author jub
 */
public class ItemChoicePanel<T extends PresentationModel> extends BaseEditPanel<ListPM<T>> implements Scrollable {

    private static final long serialVersionUID = 1L;

    private static final int VISIBLE_ROW_COUNT = 8;
    private static final int SCROLL_INCREMENT = 8;

    private final Path selectedPath;
    private final Path itemPath;

    private int visibleRows = VISIBLE_ROW_COUNT;

    private Map<ITextPM, BnRadioButton> radioButtonMap = new HashMap<>();
    private ButtonGroup buttonGroup = new ButtonGroup();

    private final WeakPropertyChangeListener listener = evt -> {
        processChangeEvent((BnPropertyChangeEvent) evt);
    };

    private final WeakPropertyChangeListener itemListener = evt -> {
        processItemChangetEvent((BnPropertyChangeEvent) evt);
    };

    private final FocusAdapter scrollToSelection = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            Container parent = SwingUtilities.getUnwrappedParent(ItemChoicePanel.this);
            if (parent instanceof JViewport) {
                Rectangle bounds = e.getComponent().getBounds();
                bounds = SwingUtilities.convertRectangle(ItemChoicePanel.this, bounds, parent);
                ((JViewport) parent).scrollRectToVisible(bounds);
            }
        }
    };

    public ItemChoicePanel(Path selected, Path item) {
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
        BnRadioButton rb = radioButtonMap.get(text);
        if (rb != null) {
            rb.setText(text.getText());
        }
    }

    private void refreshItems(ListPM<T> items) {
        for (Entry<ITextPM, BnRadioButton> entry : radioButtonMap.entrySet()) {
            entry.getKey().removePropertyChangeListener(itemListener);
            buttonGroup.remove(entry.getValue());
        }
        radioButtonMap.clear();
        this.removeAll();
        if (items != null && !items.isEmpty()) {
            for (T item : items) {
                IBooleanPM bPm = this.getModelForPath(item, selectedPath, IBooleanPM.class);
                ITextPM tPm = this.getModelForPath(item, itemPath, ITextPM.class);
                tPm.addPropertyChangeListener(itemListener);

                BnRadioButton radioButton = new BnRadioButton();
                radioButton.setPresentationModel(bPm);
                radioButton.setBackground(UIManager.getColor("List.background"));
                radioButton.setBorder(BorderFactory.createEmptyBorder());

                radioButtonMap.put(tPm, radioButton);
                radioButton.setText(tPm.getText());

                radioButton.addFocusListener(scrollToSelection);

                buttonGroup.add(radioButton);
                this.add(radioButton);
            }
            this.add(Box.createVerticalGlue());
        } else {
            JLabel label = new JLabel(" ");
            label.setBorder(BorderFactory.createEmptyBorder());
			this.add(label);
        }
        this.revalidate();
        this.repaint();
    }

    private <X> X getModelForPath(PresentationModel model, Path path, Class<X> clazz) {
        PathEvaluation eval = new PathEvaluation(model, path);
        return clazz.cast(eval.getResult().getValue());
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        Dimension size = super.getPreferredSize();
        ListPM<?> pm = localProvider.getPresentationModel();
        int itemCount = pm == null ? 1 : pm.size() == 0 ? 1 : pm.size();
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
