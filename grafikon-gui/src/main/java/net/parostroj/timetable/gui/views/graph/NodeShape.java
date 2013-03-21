package net.parostroj.timetable.gui.views.graph;

import org.w3c.dom.Element;

import com.mxgraph.shape.mxStencil;

/**
 * Makes width and height of the shape visible.
 *
 * @author cz2b10k5
 */
public class NodeShape extends mxStencil {

    private final String name;

    public NodeShape(Element description) {
        super(description);
        name = description.getAttribute("name");
    }

    public int getWidth() {
        return (int) w0;
    }

    public int getHeight() {
        return (int) h0;
    }

    public String getName() {
        return name;
    }
}
