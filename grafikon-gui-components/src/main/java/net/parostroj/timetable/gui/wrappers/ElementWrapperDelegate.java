package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.model.*;

/**
 * Wrapper delegate for all base elements of train diagram.
 *
 * @author jub
 */
public class ElementWrapperDelegate extends BasicWrapperDelegate<Object> {

    @Override
    public String toString(Object element) {
        if (element instanceof EngineClass) {
            return ((EngineClass) element).getName();
        } else if (element instanceof LineClass) {
            return ((LineClass) element).getName();
        } else if (element instanceof Node) {
            return ((Node) element).getName();
        } else if (element instanceof TrainsCycle) {
            return ((TrainsCycle) element).getName();
        } else if (element instanceof TrainType) {
            LocalizedString desc = ((TrainType) element).getDesc();
            return desc != null ? desc.translate() : ((TrainType) element).getAbbr();
        } else if (element instanceof TrainTypeCategory) {
            return ((TrainTypeCategory) element).getName();
        } else if (element instanceof TimeInterval) {
            return ((TimeInterval) element).getOwner().toString();
        } else if (element instanceof TimetableImage) {
            return element.toString();
        } else if (element instanceof TextItem) {
            TextItem item = (TextItem) element;
            return new StringBuilder(item.getName()).append(" (").append(item.getType()).append(')').toString();
        } else if (element instanceof Route) {
            return element.toString();
        } else if (element instanceof Train) {
            return ((Train) element).getName();
        } else if (element instanceof OutputTemplate) {
            return ((OutputTemplate)element).getName();
        } else if (element instanceof TrainsCycleType) {
            return ((TrainsCycleType)element).getDescriptionText();
        } else if (element instanceof Group) {
            return ((Group)element).getName();
        } else if (element instanceof LineTrack) {
            return ((LineTrack) element).getNumber();
        } else if (element instanceof NodeTrack) {
            NodeTrack nt = (NodeTrack) element;
            return nt.getNumber() + (nt.isPlatform() ? " [" : "");
        } else {
            return super.toString(element);
        }
    }
}
