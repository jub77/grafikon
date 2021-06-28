package net.parostroj.timetable.model.ls.impl3;

import java.util.LinkedList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineClass;
import net.parostroj.timetable.model.Net;
import net.parostroj.timetable.model.Node;

/**
 * Class for storing net.
 * 
 * @author jub
 */
@XmlRootElement(name = "net")
@XmlType(propOrder = {"nodes", "lines", "lineClasses"})
public class LSNet {

    private List<LSNode> nodes;
    private List<LSLine> lines;
    private List<LSLineClass> lineClasses;

    public LSNet(Net net) {
        this.nodes = new LinkedList<LSNode>();
        for (Node node : net.getNodes()) {
            this.nodes.add(new LSNode(node));
        }
        this.lines = new LinkedList<LSLine>();
        for (Line line : net.getLines()) {
            this.lines.add(new LSLine(line));
        }
        this.lineClasses = new LinkedList<LSLineClass>();
        for (LineClass lineClass : net.getLineClasses()) {
            this.lineClasses.add(new LSLineClass(lineClass));
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

    public void setLineClasses(List<LSLineClass> lineClasses) {
        this.lineClasses = lineClasses;
    }
}
