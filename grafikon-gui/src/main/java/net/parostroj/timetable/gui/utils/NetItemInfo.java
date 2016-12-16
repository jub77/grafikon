package net.parostroj.timetable.gui.utils;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JLabel;

import net.parostroj.timetable.gui.data.ProgramSettings;
import net.parostroj.timetable.gui.views.NetSelectionModel.NetSelectionListener;
import net.parostroj.timetable.model.FreightColor;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.SpeedUnit;

public class NetItemInfo implements NetSelectionListener {

    private JLabel text;
    private NetItemConversionUtil util;
    private Supplier<ProgramSettings> settings;
    private WeakReference<Object> itemRef;

    public NetItemInfo(JLabel text, Supplier<ProgramSettings> settings) {
        this.text = text;
        this.settings = settings;
        this.util = new NetItemConversionUtil();
    }

    private void setTextImpl(String value) {
        text.setText(String.format("<html>%s</html>", value));
    }

    @Override
    public void selection(Object item) {
        if (item == null) {
            itemRef = null;
        } else {
            itemRef = new WeakReference<>(item);
        }
        this.updateItemImpl(item);
    }

    public void updateItem(Object item) {
        Object currentItem = getCurrentItem();
        if (currentItem != null && currentItem == item) {
            this.updateItemImpl(currentItem);
        }
    }

    private void updateItemImpl(Object item) {
        if (item == null) {
            text.setText(null);
        } else if (item instanceof Node) {
            this.setTextImpl(this.createText((Node) item));
        } else if (item instanceof Line) {
            this.setTextImpl(this.createText((Line) item));
        }
    }

    private Object getCurrentItem() {
        return itemRef != null ? itemRef.get() : null;
    }

    private String createText(Node node) {
        StringBuilder builder = new StringBuilder();
        builder.append(node.getName());
        if (node.getCompany() != null) {
            builder.append(" [").append(node.getCompany().getAbbr()).append("]");
        }
       List<Region> regions = node.getSortedRegions();
        if (regions != null) {
            Set<Region> centerRegions = node.getCenterRegions();
            String regionsStr = regions.stream()
                    .map(region -> centerRegions.contains(region) ?
                            String.format("<b>%s</b>", region.getName()) : region.getName())
                    .collect(Collectors.joining(", "));
            builder.append(getNL()).append(regionsStr);
        }
        Collection<FreightColor> colors = node.getSortedFreightColors();
        if (colors != null) {
            String colorsStr = colors.stream().map(color -> color.getName()).collect(Collectors.joining(", "));
            builder.append(getNL()).append(colorsStr);
        }
        return builder.toString();
    }

    private String createText(Line line) {
        StringBuilder builder = new StringBuilder();
        builder.append(line.getFrom().getName()).append(" - ").append(line.getTo().getName());
        builder.append(": ").append(util.getLineLengthString(line, getDefaultLengthUnit()));
        if (line.getTopSpeed() != null) {
            builder.append(" (").append(util.getLineSpeedString(line, getDefaultSpeedUnit())).append(')');
        }
        Stream<Route> routes = util.collectRoutes(line);
        String routesStr = routes.map(route -> String.format("%s (%s - %s)", route.getName(), route.getFirst().getName(),
                route.getLast().getName())).collect(Collectors.joining(getNL()));
        if (!routesStr.isEmpty()) {
            builder.append(getNL()).append(routesStr);
        }
        return builder.toString();
    }

    protected SpeedUnit getDefaultSpeedUnit() {
        ProgramSettings programSettings = this.settings.get();
        return programSettings != null ? programSettings.getSpeedUnit() : SpeedUnit.KMPH;
    }

    protected LengthUnit getDefaultLengthUnit() {
        ProgramSettings programSettings = this.settings.get();
        return programSettings != null ? programSettings.getLengthUnit() : LengthUnit.M;
    }

    protected String getNL() {
        return "<br>";
    }
}
