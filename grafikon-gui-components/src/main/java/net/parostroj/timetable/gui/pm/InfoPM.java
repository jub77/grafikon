package net.parostroj.timetable.gui.pm;

import java.lang.ref.WeakReference;
import java.util.function.Consumer;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.ObjectsUtil;

import org.beanfabrics.model.*;
import org.beanfabrics.support.OnChange;
import org.beanfabrics.support.Operation;

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

    final OperationPM ok = new OperationPM();

    private WeakReference<TrainDiagram> diagramRef;

    private Consumer<Boolean> routeInfoListener;

    public InfoPM() {
        PMManager.setup(this);
    }

    public void setRouteInfoListener(Consumer<Boolean> routeInfoListener) {
        this.routeInfoListener = routeInfoListener;
    }

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

    @OnChange(path = "isRouteInfo")
    public void checkRouteInfo() {
        Boolean isInfo = isRouteInfo.getBoolean();
        routeNodes.setEditable(isInfo);
        routeNumbers.setEditable(isInfo);
        if (!isInfo) {
            routeNodes.setText(null);
            routeNumbers.setText(null);
        }
        if (routeInfoListener != null) {
            routeInfoListener.accept(isInfo);
        }
    }

    @Operation(path = "ok")
    public boolean ok() {
        writeResult();
        return true;
    }
}
