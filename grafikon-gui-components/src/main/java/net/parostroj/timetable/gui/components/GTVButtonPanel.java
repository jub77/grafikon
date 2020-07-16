package net.parostroj.timetable.gui.components;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.events.Event.Type;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;

public class GTVButtonPanel extends JPanel {

    private static final long serialVersionUID = 1L;

	private final GraphicalTimetableView view;
    private JComboBox<Wrapper<Route>> comboBox;
    private WrapperListModel<Route> comboBoxModel;
    private JPanel panel;
    private TrainDiagram td;
    private boolean in = false;
    private boolean popup = false;
    private boolean shown = false;
    private Timer showTask = null;

    public GTVButtonPanel(GraphicalTimetableView aView, Insets borderInsets) {
        this.showTask = new Timer(250, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.setVisible(shown);
            }
        });
        this.showTask.setRepeats(false);
        this.view = aView;
        this.setOpaque(false);
        setLayout(new BorderLayout());

        JPanel backpanel = new JPanel() {
            private static final long serialVersionUID = 1L;

			@Override
            public Dimension getPreferredSize() {
                Dimension dimension = super.getPreferredSize();
                if (!panel.isVisible()) {
                    dimension.height = 10;
                }
                return dimension;
            }

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                // paint arrows
                if (!panel.isVisible()) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(Color.black);
                    g2d.drawString("vvvvvv", this.getWidth() / 2 - 20, 9);
                }
            }
        };
        backpanel.setLayout(new BorderLayout());
        backpanel.setBorder(new EmptyBorder(borderInsets));
        backpanel.setOpaque(false);

        panel = new JPanel();
        panel.setVisible(false);
        panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        AbstractLayerUI<JPanel> layerUI = new AbstractLayerUI<>() {
            private static final long serialVersionUID = 1L;

			@Override
            protected void processMouseEvent(MouseEvent e, JXLayer<? extends JPanel> l) {
                super.processMouseEvent(e, l);
                if (e.getID() == MouseEvent.MOUSE_ENTERED && !in) {
                    in = true;
                    setComponentsVisible(in);
                }
                if (e.getID() == MouseEvent.MOUSE_EXITED && l.getMousePosition() == null && in) {
                    in = false;
                    setComponentsVisible(in | popup);
                }
            }
        };
        backpanel.add(panel, BorderLayout.CENTER);
        JXLayer<JPanel> layer = new JXLayer<>(backpanel);
        layer.setUI(layerUI);
        layer.setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK);
        FlowLayout flowLayout = (FlowLayout) panel.getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        add(layer, BorderLayout.NORTH);

        JButton zoomIn = GuiComponentUtils.createButton(GuiIcon.ZOOM_IN, 2);
        zoomIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GTViewSettings settings = view.getSettings();
                float zoom = settings.get(GTViewSettings.Key.ZOOM, Float.class);
                if (zoom < 2.0f) {
                    int value = (int) (zoom * 20);
                    value++;
                    zoom = value / (float) 20;
                    settings.set(GTViewSettings.Key.ZOOM, zoom);
                    view.setSettings(settings);
                }
            }
        });
        panel.add(zoomIn);

        JButton zoomOut = GuiComponentUtils.createButton(GuiIcon.ZOOM_OUT, 2);
        zoomOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GTViewSettings settings = view.getSettings();
                float zoom = settings.get(GTViewSettings.Key.ZOOM, Float.class);
                if (zoom > 0.5f) {
                    int value = (int) (zoom * 20);
                    value--;
                    zoom = value / (float) 20;
                    settings.set(GTViewSettings.Key.ZOOM, zoom);
                    view.setSettings(settings);
                }
            }
        });
        panel.add(zoomOut);

        comboBoxModel = new WrapperListModel<>(true);
        comboBox = new JComboBox<>();
        comboBox.setModel(comboBoxModel);
        comboBox.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                if (comboBox.getSelectedIndex() != -1) {
                    view.setRoute(comboBoxModel.getSelectedObject());
                }
            }
        });
        comboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                popup = true;
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                popup = false;
                setComponentsVisible(in | popup);
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
        panel.add(comboBox);

        JButton buttonPlus = GuiComponentUtils.createButton(GuiIcon.PLUS, 2);
        buttonPlus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GTViewSettings settings = view.getSettings();
                int size = settings.get(GTViewSettings.Key.VIEW_SIZE, Integer.class);
                if (size <= 10) {
                    size++;
                    settings.set(GTViewSettings.Key.VIEW_SIZE, size);
                    view.setSettings(settings);
                }
            }
        });
        panel.add(buttonPlus);

        JButton buttonMinus = GuiComponentUtils.createButton(GuiIcon.MINUS, 2);
        buttonMinus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GTViewSettings settings = view.getSettings();
                int size = settings.get(GTViewSettings.Key.VIEW_SIZE, Integer.class);
                size--;
                if (size >= 1) {
                    settings.set(GTViewSettings.Key.VIEW_SIZE, size);
                    view.setSettings(settings);
                }
            }
        });
        panel.add(buttonMinus);

        view.addListener(new AbstractGTViewListener() {
            @Override
            public void routeSelected(Route route) {
                comboBoxModel.setSelectedObject(route);
            }

            @Override
            public void diagramChanged(TrainDiagram diagram) {
                setTrainDiagram(diagram);
            }
        });
    }

    public void setComponentsVisible(final boolean v) {
        if (shown != v) {
            if (showTask.isRunning())
                showTask.stop();
            else {
                showTask.start();
            }
            shown = v;
        }
    }

    private void fill() {
        comboBoxModel.clear();
        if (td != null) {
            comboBoxModel.setListOfWrappers(Wrapper.getWrapperList(td.getRoutes()));
            comboBoxModel.setSelectedObject(view.getRoute());
        }
        comboBox.setModel(comboBoxModel);
    }

    protected void setTrainDiagram(TrainDiagram td) {
        this.td = td;
        fill();
        if (td != null) {
            td.addListener(new Listener() {

                @Override
                public void changed(Event event) {
                    if (event.getType() == Type.ADDED
                            || event.getType() == Type.REMOVED && event.getObject() instanceof Route) {
                        fill();
                    }
                }
            });
        }
    }
}
