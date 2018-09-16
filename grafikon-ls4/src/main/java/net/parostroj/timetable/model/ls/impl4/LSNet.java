package net.parostroj.timetable.model.ls.impl4;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.*;

/**
 * Class for storing net.
 *
 * @author jub
 */
@XmlRootElement(name = "net")
@XmlType(propOrder = {"id", "nodes", "lines", "lineClasses", "regions"})
public class LSNet {

    private String id;
    private List<LSNode> nodes;
    private List<LSLine> lines;
    private List<LSLineClass> lineClasses;
    private List<LSRegion> regions;

    public LSNet(Net net) {
        this.nodes = new LinkedList<>();
        for (Node node : net.getNodes()) {
            this.nodes.add(new LSNode(node));
        }
        this.lines = new LinkedList<>();
        for (Line line : net.getLines()) {
            this.lines.add(new LSLine(line));
        }
        this.lineClasses = new LinkedList<>();
        for (LineClass lineClass : net.getLineClasses()) {
            this.lineClasses.add(new LSLineClass(lineClass));
        }
        this.regions = new LinkedList<>();
        for (Region region : net.getRegions()) {
            this.regions.add(new LSRegion(region));
        }
    }

    public LSNet() {
    }

    @XmlElementWrapper
    @XmlElement(name = "node")
    public List<LSNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<LSNode> nodes) {
        this.nodes = nodes;
    }

    @XmlElementWrapper
    @XmlElement(name = "line")
    public List<LSLine> getLines() {
        return lines;
    }

    public void setLines(List<LSLine> lines) {
        this.lines = lines;
    }

    @XmlElementWrapper(name = "line_classes")
    @XmlElement(name = "line_class")
    public List<LSLineClass> getLineClasses() {
        return lineClasses;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLineClasses(List<LSLineClass> lineClasses) {
        this.lineClasses = lineClasses;
    }

    @XmlElementWrapper
    @XmlElement(name = "region")
    public List<LSRegion> getRegions() {
        return regions;
    }

    public void setRegions(List<LSRegion> regions) {
        this.regions = regions;
    }
}
