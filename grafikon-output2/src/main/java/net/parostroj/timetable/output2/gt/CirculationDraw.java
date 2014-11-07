package net.parostroj.timetable.output2.gt;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.gt.DrawUtils.FontInfo;

/**
 * View with circulations of certain type.
 *
 * @author jub
 */
public class CirculationDraw {

    private static class Layout {

        private static final double BORDER = 0.8d;
        private static final double TITLE = 1.2d;
        private static final double ROW = 2.2d;
        private static final int DESCRIPTION = 12;
        private static final float SMALL_FONT = 0.8f;
        private static final float TITLE_FONT = 2.0f;
        private static final double STEP_RATIO = 1.0d;
        private static final float FONT_SIZE = 11f;

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
        public FontInfo infoT;
        public int titleGap;
        public int rowGap;
        public int rowGapSmall;
        public int textOffset;
        public int textOffsetSmall;
        public int stepWidth;
        public int startY;
        public Font smallFont;
        public Font titleFont;
        public String titleText;
        public float zoom;

        public int getRow(int rowIndex) {
            return startY + rowIndex * this.row + this.title;
        }

        public void updateValues(Graphics2D g) {
            if (!this.init) {
                smallFont = g.getFont().deriveFont(g.getFont().getSize() * SMALL_FONT);
                titleFont = g.getFont().deriveFont(Font.BOLD, g.getFont().getSize() * TITLE_FONT);

                FontInfo infoN = DrawUtils.createFontInfo(g);
                FontInfo infoS = DrawUtils.createFontInfo(smallFont, g);
                infoT = DrawUtils.createFontInfo(titleFont, g);

                this.textOffset = infoN.descent;
                this.textOffsetSmall = infoS.descent;
                this.letter = new Dimension(DrawUtils.getStringWidth(g, "M"), infoN.height);
                this.letterSmall = new Dimension(DrawUtils.getStringWidth(g, smallFont, "M"), infoS.height);
                this.init = true;
            }
            this.border = (int) (this.letter.height * BORDER);
            this.row = (int) (this.letter.height * ROW);
            this.description = this.letter.width * (DESCRIPTION + 1);
            this.step = this.letter.width * stepWidth * STEP_RATIO / 3600d;
            this.title = (int) (this.letter.height * TITLE);
            this.titleGap = (this.title - this.letter.height) / 2;
            this.rowGap = (this.row - this.letter.height) / 2;
            this.rowGapSmall = (this.row - this.letterSmall.height * 2) / 2;
            // set size
            if (rows == 0) {
                size = new Dimension(0, 0);
            } else {
                int height = 2 * border + rows * row + title;
                startY = border;
                if (titleText != null) {
                    height += infoT.height;
                    startY += infoT.height;
                }
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
        this.layout.zoom = params.getZoom();
        this.layout.rows = circulations.size();
        this.update = true;
        this.layout.titleText = params.getTitle();
    }

    public void draw(Graphics2D g) {
        this.updateValues(g);
        if (layout.rows > 0) {
            if (layout.titleText != null) {
                paintTitle(g);
            }
            paintCirculations(g);
        }
    }

    public boolean updateValues(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(g.getFont().deriveFont(layout.zoom * Layout.FONT_SIZE));
        boolean updated = false;
        if (this.update) {
            this.layout.updateValues(g);
            this.update = false;
            updated = true;
        }
        return updated;
    }

    public Dimension getSize() {
        return layout.size;
    }

    public int getRows() {
        return layout.rows;
    }

    private void paintTitle(Graphics2D g) {
        Font backup = g.getFont();
        g.setFont(layout.titleFont);

        String text = DrawUtils.getStringForWidth(g, layout.titleText, layout.size.width - 2 * layout.border);
        int width = DrawUtils.getStringWidth(g, text);
        int offsetX = (layout.size.width - width) / 2;

        g.drawString(text, offsetX, layout.startY - (layout.infoT.descent * 2));

        g.setFont(backup);
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
        int end = layout.size.width - layout.border;
        int oldX = startX;
        int height = layout.row * layout.rows + layout.title;
        int seconds = 0;
        boolean odd = true;
        for (int h = 0; h <= 24; h++) {
            int x = startX + (int) ((seconds - layout.fromTime) * layout.step);
            g.setColor(odd ? COLOR_1 : COLOR_2);
            if (x > startX) {
                if (x > end) {
                    x = end;
                }
                g.fillRect(oldX, layout.startY, x - oldX + 1, height);
                oldX = x;
            }
            odd = !odd;
            seconds += 3600;
        }
        seconds = 0;
        int titleTextPos = layout.startY + layout.title - layout.titleGap - layout.textOffset;
        for (int i = 0; i <= 24; i++) {
            g.setColor(Color.BLACK);
            seconds = i * 3600;
            if (seconds >= layout.fromTime && seconds <= layout.toTime) {
                String hStr = Integer.toString(i);
                g.setColor(Color.BLACK);
                TextLayout tl = new TextLayout(hStr, g.getFont(), g.getFontRenderContext());
                Rectangle2D bounds = tl.getBounds();
                int pos = startX + (int) ((seconds - layout.fromTime) * layout.step);
                g.drawString(hStr, pos - (int) bounds.getWidth() / 2, titleTextPos);
                g.setColor(COLOR_LINE);
                g.drawLine(pos, layout.startY + layout.title, pos, layout.size.height - layout.border);
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
