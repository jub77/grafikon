package net.parostroj.timetable.model.save.version02;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.TimetableImage;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;

@XmlRootElement
public class LSTrainDiagram {

    private LSNode[] nodes;

    private LSTrain[] trains;

    private LSLine[] lines;
    
    private LSModelInfo info;
    
    private LSRoute[] routes;
    
    private LSTrainsCycle[] cycles;
    
    private LSImage[] images;
    
    private LSAttributes attributes;

    public LSTrainDiagram(TrainDiagram diagram, LSTransformationData data) {
        // stations and route splits
        List<LSNode> lNodes = new LinkedList<LSNode>();
        for (Node node : diagram.getNet().getNodes()) {
            LSNode lsNode = new LSNode(node, data);
            lNodes.add(lsNode);
        }

        nodes = lNodes.toArray(new LSNode[0]);

        // lines
        lines = new LSLine[diagram.getNet().getLines().size()];
        int i = 0;
        for (Line track : diagram.getNet().getLines()) {
            LSLine lsTrack = new LSLine(diagram.getNet(), track, data);
            lines[i++] = lsTrack;
        }

        // trains
        trains = new LSTrain[diagram.getTrains().size()];
        i = 0;
        for (Train train : diagram.getTrains()) {
            LSTrain lsTrain = new LSTrain(train, data);
            trains[i++] = lsTrain;
        }
        
        // routes
        routes = new LSRoute[diagram.getRoutes().size()];
        i = 0;
        for (Route route : diagram.getRoutes()) {
            LSRoute lsRoute = new LSRoute(route, data);
            routes[i++] = lsRoute;
        }
        
        // cycles
        int size = 0;
        for (List<TrainsCycle> lCycles : diagram.getCyclesMap().values()) {
            size += lCycles.size();
        }
        cycles = new LSTrainsCycle[size];
        i = 0;
        for (List<TrainsCycle> lCycles : diagram.getCyclesMap().values()) {
            for (TrainsCycle cycle : lCycles) {
                LSTrainsCycle lsCycle = new LSTrainsCycle(cycle, data, cycle.getType());
                cycles[i++] = lsCycle;
            }
        }
        
        i = 0;
        images = new LSImage[diagram.getImages().size()];
        for (TimetableImage image : diagram.getImages()) {
            LSImage lsImage = new LSImage(image);
            images[i++] = lsImage;
        }
        attributes = new LSAttributes(diagram.getAttributes(), data);
    }

    public LSTrainDiagram() {
    }

    /**
     * @return the trains
     */
    public LSTrain[] getTrains() {
        return trains;
    }

    /**
     * @param trains the trains to set
     */
    public void setTrains(LSTrain[] trains) {
        this.trains = trains;
    }

    /**
     * @return the nodes
     */
    public LSNode[] getNodes() {
        return nodes;
    }

    /**
     * @param nodes the nodes to set
     */
    public void setNodes(LSNode[] nodes) {
        this.nodes = nodes;
    }

    /**
     * @return the tracks
     */
    public LSLine[] getLines() {
        return lines;
    }

    /**
     * @param tracks the tracks to set
     */
    public void setLines(LSLine[] tracks) {
        this.lines = tracks;
    }

    public LSModelInfo getInfo() {
        return info;
    }

    public void setInfo(LSModelInfo info) {
        this.info = info;
    }

    public LSRoute[] getRoutes() {
        return routes;
    }

    public void setRoutes(LSRoute[] routes) {
        this.routes = routes;
    }

    public LSTrainsCycle[] getCycles() {
        return cycles;
    }

    public void setCycles(LSTrainsCycle[] cycles) {
        this.cycles = cycles;
    }

    public LSImage[] getImages() {
        return images;
    }

    public void setImages(LSImage[] images) {
        this.images = images;
    }

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }

    public void visit(LSVisitor visitor) {
        visitor.visit(this);

        // visit all nodes
        if (nodes != null) {
            for (LSNode lsNode : nodes) {
                lsNode.visit(visitor);
            }
        }
        // visit all lines
        if (lines != null) {
            for (LSLine lsLine : lines) {
                lsLine.visit(visitor);
            }
        }
        // visit all trains
        if (trains != null) {
            for (LSTrain lsTrain : trains) {
                lsTrain.visit(visitor);
            }
        }
        
        // visit engine cycles
        if (cycles != null) {
            for (LSTrainsCycle lsCycle : cycles) {
                lsCycle.visit(visitor);
            }
        }
        
        // model info
        if (info != null)
            info.visit(visitor);
        
        // routes
        if (routes != null) {
            for (LSRoute lSRoute : routes) {
                lSRoute.visit(visitor);
            }
        }
        
        // visit images
        if (images != null) {
            for (LSImage lsImage : images) {
                lsImage.visit(visitor);
            }
        }
    }
}
