/*
 * CirculationView.java
 *
 * Created on 22.6.2011, 13:44:02
 */
package net.parostroj.timetable.gui.components;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleType;

/**
 * View with circulations of certain type.
 *
 * @author jub
 */
public class CirculationView extends javax.swing.JPanel {
    
    private static final int DESC_SIZE = 15;
    private static final int BORDER = 5;
    
    private static final Color COLOR_1 = new Color(210, 210, 210);
    private static final Color COLOR_2 = new Color(230, 230, 230);

    private TrainDiagram diagram;
    private int count = 0;
    private int lowerLimit = -1;
    private int upperLimit = -1;
    private Dimension charSize = null;
    private TrainsCycleType type;
    private int hour = 40;

    /** Creates new form CirculationView */
    public CirculationView() {
        initComponents();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (diagram != null && type != null) {
            paintCirculations((Graphics2D) g, diagram.getCycles(type.getName()));
        }
    }

    public void setDiagram(TrainDiagram diagram) {
        this.type = null;
        this.diagram = diagram;
        this.repaintAndUpdateSize();
    }
    
    private void paintCirculations(Graphics2D g, Collection<TrainsCycle> circulations) {
        int start = BORDER + charSize.height;
        paintTimeTimeline(g);
        for (TrainsCycle circulation : circulations) {
            start += charSize.height;
            paintCirculation(g, circulation, new Point(BORDER, start));
        }
    }
    
    private void paintTimeTimeline(Graphics2D g) {
        int start = BORDER + (DESC_SIZE + 1) * charSize.width;
        Dimension size = this.getPreferredSize();
        int end = size.width - BORDER;
        int x = start - (int)((lowerLimit / (double) 3600) * hour);
        int oldX = start;
        int height = size.height - 2 * BORDER - charSize.height;
        boolean odd = true;
        for (int h = 0; h <= 24; h++) {
            g.setColor(odd ? COLOR_1 : COLOR_2);
            boolean digits = x >= start; 
            if (x > start) {
                if (x > end) {
                    x = end;
                }
                digits = x <= end;
                g.fillRect(oldX, BORDER + charSize.height, x - oldX, height);
                oldX = x;
            }
            if (digits) {
                g.setColor(Color.BLACK);
                String hStr = Integer.valueOf(h).toString();
                Rectangle2D bounds = g.getFont().getStringBounds(hStr, g.getFontRenderContext());
                g.drawString(hStr, x - (int)bounds.getWidth() / 2, BORDER + charSize.height);
                if (x == end)
                    break;
            }
            odd = !odd;
            x += hour;
        }
    }

    private void paintCirculation(Graphics2D g, TrainsCycle circulation, Point position) {
        g.setColor(Color.BLACK);
        g.drawString(circulation.getName(), position.x, position.y);
    }
    
    private void repaintAndUpdateSize() {
        int newCount = 0, newLowerLimit = 0, newUpperLimit = TimeInterval.DAY;
        if (diagram != null && type != null) {
            newCount = diagram.getCycles(type.getName()).size();
            Integer value = (Integer) diagram.getAttribute(TrainDiagram.ATTR_FROM_TIME);
            if (value != null)
                newLowerLimit = value.intValue();
            value = (Integer) diagram.getAttribute(TrainDiagram.ATTR_TO_TIME);
            if (value != null)
                newUpperLimit = value.intValue();
        }
        if (newCount != count || newLowerLimit != lowerLimit || newUpperLimit != upperLimit) {
            count = newCount;
            lowerLimit = newLowerLimit;
            upperLimit = newUpperLimit;
            this.revalidate();
        }
        this.repaint();
    }
    
    @Override
    public Dimension getPreferredSize() {
        if (charSize == null) {
            Graphics2D g = (Graphics2D) this.getGraphics();
            Rectangle2D bounds = g.getFont().getStringBounds("M", g.getFontRenderContext());
            charSize = new Dimension((int)bounds.getWidth(), (int)bounds.getHeight());
        }
        return this.getSizeImpl();
    }
    
    private Dimension getSizeImpl() {
        if (diagram == null || type == null)
            return new Dimension(0, 0);
        else {
            int height = charSize.height * (count + 1) + BORDER * 2;
            int width = charSize.width * (DESC_SIZE + 1) + BORDER * 2
                    + (int)(hour * ((upperLimit - lowerLimit) / (double) 3600));
            return new Dimension(width, height);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    public void circulationRemoved(TrainsCycle circulation) {
        this.repaintAndUpdateSize();
    }

    public void circulationAdded(TrainsCycle circulation) {
        this.repaintAndUpdateSize();
    }

    public void circulationUpdated(TrainsCycle circulation) {
        this.repaintAndUpdateSize();
    }

    public void setType(TrainsCycleType type) {
        this.type = type;
        this.repaintAndUpdateSize();
    }

    public void timeLimitsUpdated() {
        this.repaintAndUpdateSize();
    }
}
