package net.parostroj.timetable.gui.components;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.*;

import javax.swing.*;

import net.parostroj.timetable.gui.components.GTViewSettings.Key;
import net.parostroj.timetable.gui.components.GTViewSettings.Type;
import net.parostroj.timetable.gui.dialogs.EditRoutesDialog;
import net.parostroj.timetable.gui.dialogs.GTViewZoomDialog;
import net.parostroj.timetable.gui.dialogs.RouteSelectionDialog;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.TrainDiagramEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Graphical timetable view - with interaction.
 *
 * @author jub
 */
public class GraphicalTimetableView extends GraphicalTimetableViewDraw  {

    private static final Logger log = LoggerFactory.getLogger(GraphicalTimetableView.class);

    private final static int INITIAL_WIDTH = 4;
    private final static int SELECTION_RADIUS = 5;
    private final static int WIDTH_STEPS = 10;
    private final static int ROUTE_COUNT = 20;
    private final static int MIN_WIDTH = 1000;
    private final static int MAX_WIDTH = 10000;
    private final static int WIDTH_TO_HEIGHT_RATIO = 5;

    static {
        ToolTipManager.sharedInstance().setReshowDelay(0);
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
    }

    public static interface RSListener {
        public void routeSelected(Route route);
    }

    private interface ToolTipHelper {
        public Collection<TrainsCycleItem> getEngineCycles(TimeInterval interval);
        public Collection<TrainsCycleItem> getTrainUnitCycles(TimeInterval interval);
        public Collection<TrainsCycleItem> getDriverCycles(TimeInterval interval);
    }

    private static class RouteRadioButtonMenuItem extends JRadioButtonMenuItem {

        private final Route route;

        public RouteRadioButtonMenuItem(Wrapper<Route> routeWrapper) {
            super(routeWrapper.toString());
            this.route = routeWrapper.getElement();
        }

        public Route getRoute() {
            return route;
        }
    }

    private RSListener rsListener;

    private TextTemplate toolTipTemplateLine;
    private TextTemplate toolTipTemplateNode;
    private TimeInterval lastToolTipInterval;
    private final Map<String, Object> toolTipformattingMap = new HashMap<String, Object>();

    private Dimension preferredSize = new Dimension(MIN_WIDTH, MIN_WIDTH / WIDTH_TO_HEIGHT_RATIO);

    public GraphicalTimetableView() {
        this.initComponents();

        this.addSizesToMenu();
        // binding for tool tips
        toolTipformattingMap.put("helper", new ToolTipHelper() {

            @Override
            public Collection<TrainsCycleItem> getEngineCycles(TimeInterval interval) {
                return interval.getTrain().getCycleItemsForInterval(diagram.getEngineCycleType(), interval);
            }

            @Override
            public Collection<TrainsCycleItem> getTrainUnitCycles(TimeInterval interval) {
                return interval.getTrain().getCycleItemsForInterval(diagram.getTrainUnitCycleType(), interval);
            }

            @Override
            public Collection<TrainsCycleItem> getDriverCycles(TimeInterval interval) {
                return interval.getTrain().getCycleItemsForInterval(diagram.getDriverCycleType(), interval);
            }
        });
        // tool tips
        ToolTipManager.sharedInstance().registerComponent(this);
        try {
            toolTipTemplateLine = TextTemplate.createTextTemplate(ResourceLoader.getString("gt.desc.interval.line"), TextTemplate.Language.GROOVY);
            toolTipTemplateNode = TextTemplate.createTextTemplate(ResourceLoader.getString("gt.desc.interval.node"), TextTemplate.Language.GROOVY);
        } catch (GrafikonException e) {
            log.error("Error creating template for time interval.", e);
        }
        // routes menu
        routesMenuItem = new JMenuItem(ResourceLoader.getString("gt.routes") + "...");
        routesMenu.add(routesMenuItem);
        routesMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // show list of routes
                RouteSelectionDialog dialog = new RouteSelectionDialog((Window) GraphicalTimetableView.this.getTopLevelAncestor(), true);
                dialog.setLocationRelativeTo(GraphicalTimetableView.this.getParent());
                dialog.setListValues(diagram.getRoutes(), getRoute());
                dialog.setListener(new RouteSelectionDialog.RSListener() {
                    @Override
                    public void routeSelected(Route route) {
                        // set selected route
                        setRoute(route);
                    }
                });
                dialog.setVisible(true);
            }
        });
        // set size
        this.setGTWidth(settings);
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        if (trainRegionCollector == null)
            return null;
        List<TimeInterval> intervals = trainRegionCollector.getItemsForPoint(event.getX(), event.getY(), SELECTION_RADIUS);

        if (lastToolTipInterval == null) {
            if (!intervals.isEmpty())
                lastToolTipInterval = intervals.get(0);
        } else {
            lastToolTipInterval = null;
        }
        return lastToolTipInterval !=null ? this.formatTimeInterval() : null;
    }

    private String formatTimeInterval() {
        toolTipformattingMap.put("interval", lastToolTipInterval);
        if (lastToolTipInterval.isLineOwner())
            return toolTipTemplateLine.evaluate(toolTipformattingMap);
        else
            return toolTipTemplateNode.evaluate(toolTipformattingMap);
    }

    private void createMenuForRoutes(List<Route> routes) {
        routesGroup = new ButtonGroup();
        routesMenu.removeAll();
        // sort routes
        routes = new ArrayList<Route>(routes);
        Collections.sort(routes, new Comparator<Route>() {
            @Override
            public int compare(Route o1, Route o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        int i = 0;
        for (Route lRoute : routes) {
            if (i++ >= ROUTE_COUNT) {
                // add item for list of routes
                routesMenu.add(new javax.swing.JSeparator());
                routesMenu.add(routesMenuItem);
                break;
            }
            RouteRadioButtonMenuItem item = new RouteRadioButtonMenuItem(Wrapper.getWrapper(lRoute));
            routesMenu.add(item);
            routesGroup.add(item);
            if (lRoute == this.getRoute())
                item.setSelected(true);
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    RouteRadioButtonMenuItem button = (RouteRadioButtonMenuItem)e.getSource();
                    setRoute(button.getRoute());
                }
            });
        }
    }

    private void activateRouteMenuItem(Route route) {
        routesGroup.clearSelection();
        for (int i =0; i < routesMenu.getItemCount(); i++) {
            JMenuItem item = routesMenu.getItem(i);
            if (item instanceof RouteRadioButtonMenuItem) {
                RouteRadioButtonMenuItem rItem = (RouteRadioButtonMenuItem)item;
                if (rItem.getRoute().equals(route))
                    rItem.setSelected(true);
            }
        }
    }

    private void addSizesToMenu() {
        ButtonGroup group = new ButtonGroup();
        for (int i = 1; i <= WIDTH_STEPS; i++) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(Integer.toString(i));
            item.setActionCommand(Integer.toString(i));
            int currentSize = settings.get(Key.VIEW_SIZE, Integer.class);
            if (i == currentSize)
                item.setSelected(true);
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int size = Integer.parseInt(e.getActionCommand());
                    settings.set(Key.VIEW_SIZE, size);
                    setGTWidth(settings);
                    recreateDraw();
                }
            });
            group.add(item);
            sizesMenu.add(item);
        }
    }

    protected void setGTWidth(GTViewSettings config) {
        Integer start = null;
        Integer end = null;
        int size = config.get(Key.VIEW_SIZE, Integer.class);
        if (!config.getOption(Key.IGNORE_TIME_LIMITS)) {
            start = config.get(Key.START_TIME, Integer.class);
            end = config.get(Key.END_TIME, Integer.class);
        }
        if (start == null)
            start = 0;
        if (end == null)
            end = TimeInterval.DAY;
        double ratio = (double)(end - start) / TimeInterval.DAY;
        int newWidth = MIN_WIDTH + (size - 1) * ((MAX_WIDTH - MIN_WIDTH) / (WIDTH_STEPS - 1));
        newWidth = (int) (newWidth * ratio);
        int newHeight = newWidth / WIDTH_TO_HEIGHT_RATIO;
        preferredSize = new Dimension(newWidth, newHeight);
        this.revalidate();
    }

    @Override
    public void setTrainDiagram(TrainDiagram diagram) {
        super.setTrainDiagram(diagram);
        if (diagram == null) {

        } else {
            this.createMenuForRoutes(diagram.getRoutes());
            this.setComponentPopupMenu(popupMenu);
        }
    }

    private void initComponents() {
        popupMenu = new javax.swing.JPopupMenu();
        routesMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem routesEditMenuItem = new javax.swing.JMenuItem();
        javax.swing.JSeparator jSeparator1 = new javax.swing.JSeparator();
        javax.swing.JMenu typesMenu = new javax.swing.JMenu();
        classicMenuItem = new javax.swing.JRadioButtonMenuItem();
        withTracksMenuItem = new javax.swing.JRadioButtonMenuItem();
        sizesMenu = new javax.swing.JMenu();
        javax.swing.JMenu preferencesMenu = new javax.swing.JMenu();
        addigitsCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        extendedLinesCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        trainNamesCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        techTimeCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        ignoreTimeLimitsCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        javax.swing.ButtonGroup typesButtonGroup = new javax.swing.ButtonGroup();
        routesGroup = new javax.swing.ButtonGroup();

        routesMenu.setText(ResourceLoader.getString("gt.routes")); // NOI18N
        popupMenu.add(routesMenu);

        routesEditMenuItem.setText(ResourceLoader.getString("gt.routes.edit")); // NOI18N
        routesEditMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (diagram == null) {
                    return;
                }
                getRouteDialog().setLocationRelativeTo(GraphicalTimetableView.this.getParent());
                getRouteDialog().showDialog(diagram);
            }
        });
        popupMenu.add(routesEditMenuItem);
        popupMenu.add(jSeparator1);

        typesMenu.setText(ResourceLoader.getString("gt.type")); // NOI18N

        typesButtonGroup.add(classicMenuItem);
        classicMenuItem.setSelected(true);
        classicMenuItem.setText(ResourceLoader.getString("gt.classic")); // NOI18N
        classicMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settings.set(Key.TYPE, Type.CLASSIC);
                recreateDraw();
            }
        });
        typesMenu.add(classicMenuItem);

        typesButtonGroup.add(withTracksMenuItem);
        withTracksMenuItem.setText(ResourceLoader.getString("gt.withtracks")); // NOI18N
        withTracksMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settings.set(Key.TYPE, Type.WITH_TRACKS);
                recreateDraw();
            }
        });
        typesMenu.add(withTracksMenuItem);

        popupMenu.add(typesMenu);

        sizesMenu.setText(ResourceLoader.getString("gt.sizes")); // NOI18N
        popupMenu.add(sizesMenu);

        preferencesMenu.setText(ResourceLoader.getString("gt.preferences")); // NOI18N

        addigitsCheckBoxMenuItem.setText(ResourceLoader.getString("gt.addigits")); // NOI18N
        addigitsCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifyPreferencesCheckBox(evt);
            }
        });
        preferencesMenu.add(addigitsCheckBoxMenuItem);

        extendedLinesCheckBoxMenuItem.setText(ResourceLoader.getString("gt.extendedlines")); // NOI18N
        extendedLinesCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifyPreferencesCheckBox(evt);
            }
        });
        preferencesMenu.add(extendedLinesCheckBoxMenuItem);

        trainNamesCheckBoxMenuItem.setSelected(true);
        trainNamesCheckBoxMenuItem.setText(ResourceLoader.getString("gt.trainnames")); // NOI18N
        trainNamesCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifyPreferencesCheckBox(evt);
            }
        });
        preferencesMenu.add(trainNamesCheckBoxMenuItem);

        techTimeCheckBoxMenuItem.setText(ResourceLoader.getString("gt.technological.time")); // NOI18N
        techTimeCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifyPreferencesCheckBox(evt);
            }
        });
        preferencesMenu.add(techTimeCheckBoxMenuItem);

        ignoreTimeLimitsCheckBoxMenuItem.setText(ResourceLoader.getString("gt.ignore.time.limits")); // NOI18N
        ignoreTimeLimitsCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifyPreferencesCheckBox(evt);
            }
        });
        preferencesMenu.add(ignoreTimeLimitsCheckBoxMenuItem);

        popupMenu.add(preferencesMenu);

        javax.swing.JMenuItem zoomMenuItem = new javax.swing.JMenuItem(ResourceLoader.getString("gt.zoom") + "...");
        zoomMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // select zoom
                GTViewZoomDialog dialog = new GTViewZoomDialog((Window) getTopLevelAncestor(), true);
                dialog.setLocationRelativeTo(getParent());
                Float oldZoom = settings.get(Key.ZOOM, Float.class);
                Float newZoom = dialog.showDialog(oldZoom);
                if (newZoom != null && newZoom.floatValue() != oldZoom.floatValue()) {
                    settings.set(Key.ZOOM, newZoom);
                    recreateDraw();
                }
            }
        });
        popupMenu.add(zoomMenuItem);

        setDoubleBuffered(false);
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (!SwingUtilities.isLeftMouseButton(evt)) {
                    return;
                }
                // collector/selector
                for (RegionCollector<?> collector : gtStorage.collectors()) {
                    if (evt.getClickCount() % 2 == 0) {
                        // indicates double click
                        if (collector.editSelected()) {
                            return;
                        }
                    } else {
                        collector.selectItems(evt.getX(), evt.getY(), SELECTION_RADIUS);
                    }
                }
            }
        });
        setLayout(null);
    }

    private EditRoutesDialog getRouteDialog() {
        if (editRoutesDialog == null)
            editRoutesDialog = new EditRoutesDialog((Window)this.getTopLevelAncestor(), true);
        return editRoutesDialog;
    }

    private void modifyPreferencesCheckBox(java.awt.event.ActionEvent event) {
        settings.set(Key.ARRIVAL_DEPARTURE_DIGITS, addigitsCheckBoxMenuItem.isSelected());
        settings.set(Key.EXTENDED_LINES, extendedLinesCheckBoxMenuItem.isSelected());
        settings.set(Key.TECHNOLOGICAL_TIME, techTimeCheckBoxMenuItem.isSelected());
        settings.set(Key.IGNORE_TIME_LIMITS, ignoreTimeLimitsCheckBoxMenuItem.isSelected());
        settings.set(Key.TRAIN_NAMES, trainNamesCheckBoxMenuItem.isSelected());
        if (event.getSource() == ignoreTimeLimitsCheckBoxMenuItem) {
            this.setTimeRange();
        }
        // recreate draw
        this.recreateDraw();
    }

    @Override
    public void setSettings(GTViewSettings settings) {
        super.setSettings(settings);

        switch (settings.get(Key.TYPE, Type.class)) {
            case CLASSIC:
                classicMenuItem.setSelected(true);
                break;
            case WITH_TRACKS:
                withTracksMenuItem.setSelected(true);
                break;
        }

        String sizeStr = Integer.toString(settings.get(Key.VIEW_SIZE, Integer.class));
        for (Object elem : sizesMenu.getMenuComponents()) {
            if (elem instanceof JRadioButtonMenuItem) {
                JRadioButtonMenuItem item = (JRadioButtonMenuItem)elem;
                if (item.getActionCommand().equals(sizeStr)) {
                    item.setSelected(true);
                    break;
                }
            }
        }
        trainNamesCheckBoxMenuItem.setSelected(settings.getOption(GTViewSettings.Key.TRAIN_NAMES));
        addigitsCheckBoxMenuItem.setSelected(settings.getOption(GTViewSettings.Key.ARRIVAL_DEPARTURE_DIGITS));
        techTimeCheckBoxMenuItem.setSelected(settings.getOption(GTViewSettings.Key.TECHNOLOGICAL_TIME));
        extendedLinesCheckBoxMenuItem.setSelected(settings.getOption(GTViewSettings.Key.EXTENDED_LINES));
        ignoreTimeLimitsCheckBoxMenuItem.setSelected(settings.getOption(Key.IGNORE_TIME_LIMITS));
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension pSize = new Dimension(preferredSize);
        if (getParent() instanceof JViewport) {
            JViewport viewport = (JViewport) getParent();
            Dimension eSize = viewport.getExtentSize();
            pSize.height = eSize.height;
            if (eSize.width > pSize.width)
                pSize.width = eSize.width;
        }
        return pSize;
    }

    /**
     * @return the rsListener
     */
    public RSListener getRsListener() {
        return rsListener;
    }

    /**
     * @param rsListener the rsListener to set
     */
    public void setRsListener(RSListener rsListener) {
        this.rsListener = rsListener;
    }

    @Override
    public void setRoute(Route route) {
        if (this.route == route) {
            return;
        }
        super.setRoute(route);
        this.activateRouteMenuItem(route);
        if (rsListener != null) {
            rsListener.routeSelected(route);
        }
    }

    @Override
    protected void routesChanged(TrainDiagramEvent event) {
        // changed list of routes
        this.createMenuForRoutes(diagram.getRoutes());

        super.routesChanged(event);
    }

    @Override
    protected void setTimeRange() {
        super.setTimeRange();
        this.setGTWidth(settings);
    }

    @Override
    protected GTViewSettings getDefaultViewSettings() {
        GTViewSettings config = super.getDefaultViewSettings();
        config.set(Key.VIEW_SIZE, INITIAL_WIDTH);
        return config;
    }

    protected javax.swing.JPopupMenu popupMenu;
    private javax.swing.ButtonGroup routesGroup;
    private javax.swing.JMenu routesMenu;
    private javax.swing.JMenu sizesMenu;
    private final JMenuItem routesMenuItem;
    private javax.swing.JCheckBoxMenuItem addigitsCheckBoxMenuItem;
    private javax.swing.JRadioButtonMenuItem classicMenuItem;
    private javax.swing.JCheckBoxMenuItem extendedLinesCheckBoxMenuItem;
    private javax.swing.JCheckBoxMenuItem ignoreTimeLimitsCheckBoxMenuItem;
    private javax.swing.JCheckBoxMenuItem techTimeCheckBoxMenuItem;
    private javax.swing.JCheckBoxMenuItem trainNamesCheckBoxMenuItem;
    private javax.swing.JRadioButtonMenuItem withTracksMenuItem;

    private EditRoutesDialog editRoutesDialog;
}
