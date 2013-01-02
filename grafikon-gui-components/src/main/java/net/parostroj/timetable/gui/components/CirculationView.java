/*
 * CirculationView.java
 *
 * Created on 22.6.2011, 13:44:02
 */
package net.parostroj.timetable.gui.components;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import net.parostroj.timetable.gui.actions.execution.SaveImageAction;
import net.parostroj.timetable.model.*;

/**
 * View with circulations of certain type.
 *
 * @author jub
 */
public class CirculationView extends javax.swing.JPanel implements SaveImageAction.Image {
    
    private static class Layout {

        private static final double BORDER = 1d;
        private static final double TITLE = 1.2d;
        private static final double ROW = 2.5d;
        private static final int DESCRIPTION = 15;
        private static final double SMALL_FONT = 0.8d;
        private static final int START_WIDTH = 7;
        private static final double STEP_RATIO = 2.0d;
        
        public boolean init;
        public int title;
        public int border;
        public int row;
        public int description;
        public double step;
        public int rows;
        public int fromTime;
        public int toTime;
        public Dimension size = new Dimension(0, 0);
        public Dimension letter;
        public Dimension letterSmall;
        public int titleGap;
        public int rowGap;
        public int rowGapSmall;
        public int textOffset;
        public int textOffsetSmall;
        public int stepWidth = 5;
        public Font smallFont;
        
        public int getRow(int rowIndex) {
            return border + rowIndex * this.row + this.title;
        }
        
        public void updateValues(Graphics2D g) {
            if (!this.init) {
                TextLayout tl1 = new TextLayout("M", g.getFont(), g.getFontRenderContext());
                TextLayout tl2 = new TextLayout("Čy", g.getFont(), g.getFontRenderContext());
                
                smallFont = g.getFont().deriveFont(g.getFont().getSize() * (float)SMALL_FONT);
                TextLayout stl = new TextLayout("Čy", smallFont, g.getFontRenderContext());
                
                Rectangle2D b1 = tl1.getBounds();
                Rectangle2D b2 = tl2.getBounds();
                Rectangle2D bs = stl.getBounds();
                this.textOffset = (int) (b2.getHeight() + b2.getY());
                this.textOffsetSmall = (int) (bs.getHeight() + bs.getY());
                this.letter = new Dimension((int) b1.getWidth(), (int) b2.getHeight());
                this.letterSmall = new Dimension((int) bs.getWidth(), (int) bs.getHeight());
                this.init = true;
            }
            this.border = (int) (this.letter.height * BORDER);
            this.row = (int) (this.letter.height * ROW);
            this.description = this.letter.width * (DESCRIPTION + 1);
            this.step = this.letter.width * ((stepWidth * STEP_RATIO + START_WIDTH) / 3600d);
            this.title = (int) (this.letter.height * TITLE);
            this.titleGap = (this.title - this.letter.height) / 2;
            this.rowGap = (this.row - this.letter.height) / 2;
            this.rowGapSmall = (this.row - this.letterSmall.height * 2) / 2;
            this.updateSize(rows, fromTime, toTime);
        }
        
        public void updateSize(int rows, int fromTime, int toTime) {
            if (rows == 0) {
                size = new Dimension(0, 0);
                this.rows = 0;
            } else {
                this.rows = rows;
                this.fromTime = fromTime;
                this.toTime = toTime;
                int height = 2 * border + rows * row + title;
                int width = 2 * border + description + (int) ((toTime - fromTime) * step);
                size = new Dimension(width, height);
            }
        }
    }
    
    private static final Color COLOR_1 = new Color(210, 210, 210);
    private static final Color COLOR_2 = new Color(230, 230, 230);
    private static final Color COLOR_LINE = new Color(170, 170, 170);

    private TrainDiagram diagram;
    private TrainsCycleType type;
    private Layout layout = new Layout();

    /** Creates new form CirculationView */
    public CirculationView() {
        initComponents();
    }

    @Override
    public void paintImage(Graphics g) {
        this.paint(g);
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (diagram != null && type != null && layout.rows > 0) {
            paintCirculations((Graphics2D) g, diagram.getCycles(type.getName()));
        }
    }

    public void setDiagram(TrainDiagram diagram) {
        this.type = null;
        this.diagram = diagram;
        this.repaintAndUpdateSize();
    }
    
    private void paintCirculations(Graphics2D g, Collection<TrainsCycle> circulations) {
        paintTimeTimeline(g);
        int row = 0;
        for (TrainsCycle circulation : circulations) {
            paintCirculation(g, circulation, row++);
        }
    }
    
    private void paintTimeTimeline(Graphics2D g) {
        int startX = layout.border + layout.description;
        int startY = layout.border;
        int end = layout.size.width - layout.border;
        int oldX = startX;
        int height = layout.size.height - 2 * layout.border;
        int seconds = 0;
        boolean odd = true;
        for (int h = 0; h <= 24; h++) {
            int x = startX + (int) ((seconds - layout.fromTime) * layout.step);
            g.setColor(odd ? COLOR_1 : COLOR_2);
            if (x > startX) {
                if (x > end) {
                    x = end;
                }
                g.fillRect(oldX, startY, x - oldX + 1, height);
                oldX = x;
            }
            odd = !odd;
            seconds += 3600;
        }
        seconds = 0;
        int titleTextPos = layout.border + layout.title - layout.titleGap - layout.textOffset;
        for (int i = 0; i <= 24; i++) {
            g.setColor(Color.BLACK);
            seconds = i * 3600;
            if (seconds >= layout.fromTime && seconds <= layout.toTime) {
                String hStr = Integer.toString(i);
                g.setColor(Color.BLACK);
                TextLayout tl = new TextLayout(hStr, g.getFont(), g.getFontRenderContext());
                Rectangle2D bounds = tl.getBounds();
                int pos = startX + (int) ((seconds - layout.fromTime) * layout.step);
                g.drawString(hStr, pos - (int)bounds.getWidth() / 2, titleTextPos);
                g.setColor(COLOR_LINE);
                g.drawLine(pos, layout.border + layout.title, pos, layout.size.height - layout.border);
            }
        }
        
        // row delimiters
        g.setColor(COLOR_LINE);
        for (int i = 0; i <= layout.rows; i++) {
            int p = layout.getRow(i);
            g.drawLine(layout.border, p, layout.size.width - layout.border, p);
        }
    }

    private void paintCirculation(Graphics2D g, TrainsCycle circulation, int row) {
        g.setColor(Color.BLACK);
        int textY = layout.getRow(row) + layout.row - layout.rowGap - layout.textOffset;
        int partY = layout.getRow(row) + layout.rowGapSmall + layout.letterSmall.height - layout.textOffsetSmall;
        g.drawString(circulation.getName(), layout.border, textY);
        // rectangle for each item
        int y = layout.getRow(row) + layout.row - layout.rowGapSmall - layout.letterSmall.height;
        int height = layout.letterSmall.height;
        Font backup = g.getFont();
        g.setFont(layout.smallFont);
        for (TrainsCycleItem item : circulation) {
            int x = layout.border + layout.description + (int) (layout.step * (item.getStartTime() - layout.fromTime));
            int width = (int) ((item.getEndTime() - item.getStartTime()) * layout.step);
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, height);
            g.drawString(item.getTrain().getName(), x, partY);
        }
        g.setFont(backup);
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
        if (newCount != layout.rows || newLowerLimit != layout.fromTime 
                || newUpperLimit != layout.toTime) {
            layout.updateSize(newCount, newLowerLimit, newUpperLimit);
            this.revalidate();
        }
        this.repaint();
    }
    
    public int getCount() {
        return layout.rows;
    }
    
    @Override
    public Dimension getPreferredSize() {
        if (!layout.init) {
            Graphics2D g = (Graphics2D) this.getGraphics();
            layout.updateValues(g);
        }
        return layout.size;
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
    
    public void setStepWidth(int size) {
        this.layout.stepWidth = size;
        this.layout.updateValues((Graphics2D) this.getGraphics());
        this.revalidate();
        this.repaint();
    }
}
