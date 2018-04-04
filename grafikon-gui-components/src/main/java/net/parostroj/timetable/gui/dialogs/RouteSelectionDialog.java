package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.Route;

public class RouteSelectionDialog extends JDialog {

    private static final long serialVersionUID = 1L;

	public static interface RSListener {
        public void routeSelected(Route route);
    }

    private RSListener listener;
    private final JList<Wrapper<Route>> list;
    private WrapperListModel<Route> listModel;

    public RouteSelectionDialog(java.awt.Window owner, boolean modal) {
        super(owner, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.SOUTH);
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        JButton okButton = new JButton(ResourceLoader.getString("button.ok"));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOk();
            }
        });
        panel.add(okButton);

        JButton cancelButton = new JButton(ResourceLoader.getString("button.cancel"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        panel.add(cancelButton);

        JScrollPane scrollPane = new JScrollPane();
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        list = new JList<Wrapper<Route>>();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setVisibleRowCount(20);
        scrollPane.setViewportView(list);
        this.pack();
    }

    public void setListener(RSListener listener) {
        this.listener = listener;
    }

    public void setListValues(Iterable<? extends Route> routes, Route selected) {
        listModel = new WrapperListModel<Route>(Wrapper.getWrapperList(routes));
        getList().setModel(listModel);
        if (selected != null) {
            int index = listModel.getIndexOfObject(selected);
            getList().setSelectedIndex(index);
            getList().ensureIndexIsVisible(index);
        }
        this.pack();
    }

    protected void onCancel() {
        this.setVisible(false);
    }

    protected void onOk() {
        int index = list.getSelectedIndex();
        Route route = index != -1 ? listModel.getIndex(index).getElement() : null;
        if (route != null && listener != null) {
            listener.routeSelected(route);
        }
        this.setVisible(false);
    }

    protected JList<Wrapper<Route>> getList() {
        return list;
    }
}
