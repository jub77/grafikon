package net.parostroj.timetable.gui.components;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.beanfabrics.Path;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.PresentationModel;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.swing.BnButton;
import org.beanfabrics.swing.list.BnList;
import org.beanfabrics.swing.list.CellConfig;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;

/**
 * Panel with buttons for set of items (no buttons for changing position).
 * It is expected that the list is automatically sorted.
 *
 * @author jub
 *
 * @param <V> type of item in the set
 */
public class ItemSetEditPanel<V extends PresentationModel> extends BaseEditPanel<ListPM<V>> {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    public ItemSetEditPanel(Path item, int visibleRows) {
        this.setLayout(new BorderLayout());

        BnList list = new BnList();
        list.setPrototypeCellValue(new TextPM("MMMMMMMMMM"));
        list.setModelProvider(localProvider);
        list.setPath(new Path("this"));
        list.setCellConfig(new CellConfig(item));
        list.setVisibleRowCount(visibleRows);

        JScrollPane scrollPane = new JScrollPane(list);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        BoxLayout buttonPanelLayout = new BoxLayout(buttonPanel, BoxLayout.Y_AXIS);
        buttonPanel.setLayout(buttonPanelLayout);

        BnButton createButton = GuiComponentUtils.createBnButton(GuiIcon.ADD, 2);
        createButton.setModelProvider(localProvider);
        createButton.setPath(new Path("create"));
        buttonPanel.add(createButton);

        buttonPanel.add(Box.createVerticalStrut(3));

        BnButton deleteButton = GuiComponentUtils.createBnButton(GuiIcon.REMOVE, 2);
        deleteButton.setModelProvider(localProvider);
        deleteButton.setPath(new Path("delete"));
        buttonPanel.add(deleteButton);

        this.add(scrollPane, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.EAST);
    }
}
