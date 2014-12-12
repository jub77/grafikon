package net.parostroj.timetable.gui.components;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import net.parostroj.timetable.gui.components.GTViewSettings.Key;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.output2.gt.*;
import net.parostroj.timetable.output2.gt.GTDraw.Refresh;
import net.parostroj.timetable.visitors.AbstractEventVisitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Graphical timetable view.
 *
 * @author jub
 */
public class GraphicalTimetableViewDraw extends javax.swing.JPanel implements Scrollable {

    private static final Logger log = LoggerFactory.getLogger(GraphicalTimetableViewDraw.class);

    protected GTViewSettings settings;

    protected GTDrawFactory drawFactory;
    private GTDraw draw;
    protected Route route;
    protected TrainDiagram diagram;

    protected final GTStorage gtStorage = new GTStorage();

    /** Creates new form TrainGraphicalTimetableView */
    public GraphicalTimetableViewDraw() {
        initComponents();

        this.drawFactory = new NormalGTDrawFactory();
        this.settings = this.getDefaultViewSettings();

        this.setBackground(Color.WHITE);

        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                resize();
            }
        });

        addRegionCollector(TimeInterval.class, new TrainRegionCollector());
    }

    private AllEventListener currentListener;

    public void setTrainDiagram(TrainDiagram diagram) {
        if (currentListener != null && this.diagram != null) {
            this.diagram.removeAllEventListener(this.currentListener);
        }
        if (diagram == null) {
            this.diagram = null;
            this.currentListener = null;
            this.setRoute(null);
        } else {
            this.diagram = diagram;
            this.currentListener = new VisitEventListener(new AbstractEventVisitor() {

                @Override
                public void visit(TrainDiagramEvent event) {
                    switch (event.getType()) {
                        case ROUTE_ADDED: case ROUTE_REMOVED:
                            routesChanged(event);
                            break;
                        default:
                            break;
                    }
                }
            }) {
                @Override
                public void changed(GTEvent<?> event) {
                    super.changed(event);
                    for (RegionCollector<?> collector : gtStorage.collectors()) {
                        collector.processEvent(event);
                    }
                    Refresh refresh = draw.processEvent(event);
                    switch (refresh) {
                        case REPAINT:
                            repaint();
                            break;
                        case RECREATE:
                            recreateDraw();
                            break;
                        case RECREATE_WITH_TIME:
                            setTimeRange();
                            recreateDraw();
                            break;
                        default:
                            // nothing
                            break;
                    }
                }
            };
            this.diagram.addAllEventListener(this.currentListener);
            this.setTimeRange();
            if (diagram.getRoutes().size() > 0) {
                this.setRoute(diagram.getRoutes().get(0));
            } else {
                this.setRoute(null);
            }
        }
    }

    protected GTViewSettings getDefaultViewSettings() {
        GTViewSettings config = (new GTViewSettings())
                .set(Key.BORDER_X, 1.5f)
                .set(Key.BORDER_Y, 1.5f)
                .set(Key.STATION_GAP_X, 15)
                .set(Key.TYPE, GTDraw.Type.CLASSIC)
                .set(Key.TRAIN_COLORS, GTDraw.TrainColors.BY_TYPE)
                .set(Key.TRAIN_NAMES, Boolean.TRUE)
                .set(Key.ARRIVAL_DEPARTURE_DIGITS, Boolean.FALSE)
                .set(Key.EXTENDED_LINES, Boolean.FALSE)
                .set(Key.TECHNOLOGICAL_TIME, Boolean.FALSE)
                .set(Key.IGNORE_TIME_LIMITS, Boolean.FALSE)
                .set(Key.ZOOM, 1.0f)
                .set(Key.TO_TRAIN_SCROLL, Boolean.FALSE)
                .set(Key.TO_TRAIN_CHANGE_ROUTE, Boolean.FALSE)
                .set(Key.ORIENTATION, GTOrientation.LEFT_RIGHT)
                .set(Key.ORIENTATION_MENU, Boolean.TRUE);
        return config;
    }

    protected void routesChanged(TrainDiagramEvent event) {
        // check current route
        if (event.getType() == GTEventType.ROUTE_REMOVED && event.getObject().equals(this.getRoute())) {
            if (!diagram.getRoutes().isEmpty())
                this.setRoute(diagram.getRoutes().get(0));
            else
                this.setRoute(null);
        }
        if (event.getType() == GTEventType.ROUTE_ADDED && this.getRoute() == null) {
            this.setRoute((Route)event.getObject());
        }
    }

    public <T> void setRegionSelector(RegionSelector<T> selector, Class<T> clazz) {
        RegionCollector<T> collector = this.getRegionCollector(clazz);
        if (collector != null) {
            collector.setSelector(selector);
        }
    }

    public <T> void addRegionCollector(Class<T> clazz, RegionCollector<T> collector) {
        gtStorage.setCollector(clazz, collector);
    }

    public <T> void removeRegionCollector(Class<T> clazz) {
        gtStorage.removeCollector(clazz);
    }

    public <T> RegionCollector<T> getRegionCollector(Class<T> clazz) {
        return gtStorage.getCollector(clazz);
    }

    public void setParameter(String key, Object value) {
        gtStorage.setParameter(key, value);
    }

    public <T> T getParameter(String key, Class<T> clazz) {
        return gtStorage.getParameter(key, clazz);
    }

    public void removeParameter(String key) {
        gtStorage.removeParameter(key);
    }

    public void setDrawFactory(GTDrawFactory drawFactory) {
        this.drawFactory = drawFactory;
    }

    /**
     * sets route and refreshes view.
     *
     * @param route route to be set
     */
    public void setRoute(Route route) {
        if (this.route == route) {
            return;
        }
        this.route = route;
        this.recreateDraw();
    }

    /**
     * refreshes gt draw and repaints view.
     */
    public void refresh() {
        this.recreateDraw();
    }

    protected void setTimeRange() {
        if (diagram == null) {
            settings.remove(Key.START_TIME);
            settings.remove(Key.END_TIME);
        } else {
            Integer from = diagram.getAttribute(TrainDiagram.ATTR_FROM_TIME, Integer.class);
            Integer to = diagram.getAttribute(TrainDiagram.ATTR_TO_TIME, Integer.class);
            settings.setRemove(Key.START_TIME, from);
            settings.setRemove(Key.END_TIME, to);
            if (settings.contains(Key.START_TIME_OVERRIDE)) {
                settings.setRemove(Key.START_TIME, settings.get(Key.START_TIME_OVERRIDE));
            }
            if (settings.contains(Key.END_TIME_OVERRIDE)) {
                settings.setRemove(Key.END_TIME, settings.get(Key.END_TIME_OVERRIDE));
            }
        }
    }

    protected void recreateDraw() {
        for (RegionCollector<?> collector : gtStorage.collectors()) {
            collector.clear();
        }
        if (this.getRoute() == null) {
            draw = null;
        } else {
            draw = this.createDraw(this.getSettings());
        }
        this.repaint();
    }

    protected GTDraw createDraw(GTViewSettings config) {
        return this.createDraw(config, null);
    }

    protected GTDraw createDraw(GTViewSettings config, Dimension size) {
        GTDrawSettings drawSettings = config.createGTDrawSettings();
        drawSettings.set(GTDrawSettings.Key.SIZE, size == null ? this.getSize() : size);
        return drawFactory.createInstance(config.getGTDrawType(), drawSettings, this.getRoute(), gtStorage);
    }

    private void resize() {
        recreateDraw();
    }

    public Route getRoute() {
        return this.route;
    }

    protected TrainDiagram getDiagram() {
        return diagram;
    }

    /**
     * returns copy of settings.
     *
     * @return copy of settings
     */
    public GTViewSettings getSettings() {
        return new GTViewSettings(settings);
    }

    /**
     * @return settings
     */
    GTViewSettings getSettingsInternal() {
        return settings;
    }

    public void setSettings(GTViewSettings settings) {
        if (settings == null) {
            return;
        }

        this.settings = settings;

        this.setTimeRange();
        this.recreateDraw();
    }


    private void initComponents() {
    }

    @Override
    public void paint(Graphics g) {
        long time = System.currentTimeMillis();
        super.paint(g);

        if (draw != null) {
            draw.draw((Graphics2D) g);
        } else {
            // draw information about context menu
            g.drawString(ResourceLoader.getString("gt.contextmenu.info"), 20, 20);
        }
        log.trace("Finished paint in {}ms", Long.toString(System.currentTimeMillis() - time));
    }

    public GTDraw getGtDraw() {
        return draw;
    }

    public void setDisableStationNames(Boolean disable) {
        settings.setOption(Key.DISABLE_STATION_NAMES, disable);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        int value = 0;
        if (orientation == SwingConstants.VERTICAL) {
            value = visibleRect.height / 10;
        } else {
            value = visibleRect.width / 10;
        }
        return value;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        int value = 0;
        if (orientation == SwingConstants.VERTICAL) {
            value = visibleRect.height;
        } else {
            value = visibleRect.width;
        }
        return value;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public <T> void checkAndScroll(final List<T> items, final Class<T> clazz) {
        // only for trains with TimeIntervals handled by TrainRegionCollector...
        if (clazz.equals(TimeInterval.class) && !items.isEmpty()) {
            RegionCollector<T> collector = gtStorage.getCollector(clazz);
            if (collector instanceof TrainRegionCollector) {
                TrainRegionCollector trainRegionCollector = (TrainRegionCollector) collector;
                Train selectedTrain = ((TimeInterval) items.get(0)).getTrain();
                if (selectedTrain != null) {
                    if (!trainRegionCollector.containsTrain(selectedTrain) &&
                            settings.getOption(Key.TO_TRAIN_CHANGE_ROUTE)) {
                        Route newRoute = selectedTrain.getBestRouteMatch();
                        if (newRoute != null) {
                            this.setRoute(newRoute);
                        }
                    }
                    if (settings.getOption(Key.TO_TRAIN_SCROLL)) {
                        GuiComponentUtils.runLaterInEDT(new Runnable() {
                            @Override
                            public void run() {
                                scrollToItem(items, clazz);
                            }

                        });
                    }
                }
            }
        }
    }

    public <T> void scrollToItem(List<T> items, Class<T> clazz) {
        RegionCollector<T> collector = gtStorage.getCollector(clazz);
        if (collector != null) {
            Rectangle region = collector.getRectangleForItems(items);
            if (region != null) {
                this.scrollRectToVisible(region);
            }
        }
    }
}
