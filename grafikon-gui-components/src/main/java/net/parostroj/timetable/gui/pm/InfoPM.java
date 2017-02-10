package net.parostroj.timetable.gui.pm;

import java.lang.ref.WeakReference;
import java.util.Date;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.ObjectsUtil;

import org.beanfabrics.model.*;
import org.beanfabrics.support.OnChange;
import org.beanfabrics.support.Operation;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Model of information about diagram.
 *
 * @author jub
 */
public class InfoPM extends AbstractPM implements IPM<TrainDiagram> {

    final TextPM info = new TextPM();
    final TextPM routeNumbers = new TextPM();
    final TextPM routeNodes = new TextPM();
    final TextPM validity = new TextPM();
    final BooleanPM isRouteInfo = new BooleanPM();
    final TextPM saveVersion = new TextPM();
    final TextPM saveTimestamp = new TextPM();

    final OperationPM ok = new OperationPM();

    private WeakReference<TrainDiagram> diagramRef;

    private DateTimeFormatter format = DateTimeFormat.mediumDateTime();

    public InfoPM() {
        PMManager.setup(this);
    }

    @Override
    public void init(TrainDiagram diagram) {
        this.diagramRef = new WeakReference<>(diagram);
        String numbers = diagram.getAttributes().get(TrainDiagram.ATTR_ROUTE_NUMBERS, String.class);
        String nodes = diagram.getAttributes().get(TrainDiagram.ATTR_ROUTE_NODES, String.class);
        boolean isInfo = !(nodes == null && numbers == null);
        this.isRouteInfo.setBoolean(isInfo);
        this.routeNumbers.setText(numbers);
        this.routeNodes.setText(nodes);
        this.validity.setText(diagram.getAttributes().get(TrainDiagram.ATTR_ROUTE_VALIDITY, String.class));
        this.info.setText(diagram.getAttributes().get(TrainDiagram.ATTR_INFO, String.class));
        this.saveVersion.setText(Integer.toString(diagram.getSaveVersion()));
        Date timestamp = diagram.getSaveTimestamp();
        this.saveTimestamp.setText(timestamp == null ? "" : format.print(timestamp.getTime()));
        this.checkRouteInfo();
    }

    private void writeResult() {
        TrainDiagram diagram = diagramRef.get();
        if (diagram != null) {
            // save values
            String number = ObjectsUtil.checkAndTrim(this.routeNumbers.getText());
            String nodes = ObjectsUtil.checkAndTrim(this.routeNodes.getText());
            String validity = ObjectsUtil.checkAndTrim(this.validity.getText());
            String info = ObjectsUtil.checkAndTrim(this.info.getText());

            diagram.getAttributes().setRemove(TrainDiagram.ATTR_ROUTE_NUMBERS, number);
            diagram.getAttributes().setRemove(TrainDiagram.ATTR_ROUTE_NODES, nodes);
            diagram.getAttributes().setRemove(TrainDiagram.ATTR_ROUTE_VALIDITY, validity);
            diagram.getAttributes().setRemove(TrainDiagram.ATTR_INFO, info);
        }
    }

    public boolean isRouteInfo() {
        return isRouteInfo.getBoolean() == Boolean.TRUE;
    }

    @OnChange(path = "isRouteInfo")
    public void checkRouteInfo() {
        Boolean isInfo = isRouteInfo.getBoolean();
        routeNodes.setEditable(isInfo);
        routeNumbers.setEditable(isInfo);
        if (!isInfo) {
            routeNodes.setText(null);
            routeNumbers.setText(null);
        }
    }

    @Operation(path = "ok")
    public boolean ok() {
        writeResult();
        return true;
    }
}
