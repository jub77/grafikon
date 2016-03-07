package net.parostroj.timetable.gui.utils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JLabel;

import net.parostroj.timetable.gui.views.NetSelectionModel.Action;
import net.parostroj.timetable.gui.views.NetSelectionModel.NetSelectionListener;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.SpeedUnit;

public class NetItemInfo implements NetSelectionListener {

    private JLabel text;
    private NetItemConversionUtil util;

    public NetItemInfo(JLabel text) {
        this.text = text;
        this.util = new NetItemConversionUtil();
    }

    private void setTextImpl(String value) {
        text.setText(String.format("<html>%s</html>", value));
    }

    @Override
    public void selection(Action action, Node node, Line line) {
        switch (action) {
            case LINE_SELECTED:
                this.setTextImpl(this.createText(line));
                break;
            case NODE_SELECTED:
                this.setTextImpl(this.createText(node));
                break;
            case NOTHING_SELECTED:
                text.setText(null);
                break;
        }
    }

    private String createText(Node node) {
        return node.getName();
    }

    private String createText(Line line) {
        StringBuilder builder = new StringBuilder();
        builder.append(line.getFrom().getName()).append(" - ").append(line.getTo().getName());
        builder.append(": ").append(util.getLineLengthString(line, LengthUnit.M));
        if (line.getTopSpeed() != null) {
            builder.append(" (").append(util.getLineSpeedString(line, SpeedUnit.KMPH)).append(')');
        }
        Stream<Route> routes = util.collectRoutes(line);
        String routesStr = routes.map(route -> String.format("%s (%s - %s)", route.getName(), route.getFirst().getName(),
                route.getLast().getName())).collect(Collectors.joining(", "));
        if (!routesStr.isEmpty()) {
            builder.append(getNL()).append(routesStr);
        }
        return builder.toString();
    }

    protected String getNL() {
        return "<br>";
    }
}
