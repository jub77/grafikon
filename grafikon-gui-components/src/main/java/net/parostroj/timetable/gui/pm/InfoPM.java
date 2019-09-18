package net.parostroj.timetable.gui.pm;

import java.lang.ref.WeakReference;
import java.time.Instant;

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
    final TextPM version = new TextPM();

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
        String versionText = "[" + diagram.getSaveVersion() + "]";
        Instant timestamp = diagram.getSaveTimestamp();
        if (timestamp != null) {
            versionText = format.print(timestamp.toEpochMilli()) + " " + versionText;
        }
        String user = diagram.getSaveUser();
        if (user != null) {
            versionText += " " + user;
        }
        this.version.setText(versionText);
        this.checkRouteInfo();
    }

    private void writeResult() {
        TrainDiagram diagram = diagramRef.get();
        if (diagram != null) {
            // save values
            String lNumber = ObjectsUtil.checkAndTrim(this.routeNumbers.getText());
            String lNodes = ObjectsUtil.checkAndTrim(this.routeNodes.getText());
            String lValidity = ObjectsUtil.checkAndTrim(this.validity.getText());
            String lInfo = ObjectsUtil.checkAndTrim(this.info.getText());

            diagram.getAttributes().setRemove(TrainDiagram.ATTR_ROUTE_NUMBERS, lNumber);
            diagram.getAttributes().setRemove(TrainDiagram.ATTR_ROUTE_NODES, lNodes);
            diagram.getAttributes().setRemove(TrainDiagram.ATTR_ROUTE_VALIDITY, lValidity);
            diagram.getAttributes().setRemove(TrainDiagram.ATTR_INFO, lInfo);
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
