package net.parostroj.timetable.output2.gt;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import net.parostroj.timetable.model.*;

/**
 * View with circulations of certain type.
 *
 * @author jub
 */
public class CirculationDraw {

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
        public int stepWidth;
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
            // set size
            if (rows == 0) {
                size = new Dimension(0, 0);
            } else {
                int height = 2 * border + rows * row + title;
                int width = 2 * border + description + (int) ((toTime - fromTime) * step);
                size = new Dimension(width, height);
            }
        }
    }

    private static final Color COLOR_1 = new Color(210, 210, 210);
    private static final Color COLOR_2 = new Color(230, 230, 230);
    private static final Color COLOR_LINE = new Color(170, 170, 170);

    private final Collection<TrainsCycle> circulations;
    private final Layout layout;
    private boolean update;

    public CirculationDraw(CirculationDrawParams params) {
        this.circulations = params.getCirculations();
        this.layout = new Layout();
        this.layout.fromTime = params.getFrom();
        this.layout.toTime = params.getTo();
        this.layout.stepWidth = params.getStep();
        this.layout.rows = circulations.size();
        this.update = true;
    }

    public void draw(Graphics2D g) {
        this.updateValues(g);
        if (layout.rows > 0) {
            paintCirculations(g);
        }
    }

    public boolean updateValues(Graphics2D g) {
        if (this.update) {
            this.layout.updateValues(g);
            this.update = false;
            return true;
        } else {
            return false;
        }
    }

    public Dimension getSize() {
        return layout.size;
    }

    public int getRows() {
        return layout.rows;
    }

    private void paintCirculations(Graphics2D g) {
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
}
