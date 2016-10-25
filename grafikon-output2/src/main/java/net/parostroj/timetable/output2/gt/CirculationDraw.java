package net.parostroj.timetable.output2.gt;

import java.awt.*;
import java.awt.geom.AffineTransform;
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
        public CirculationDrawColors colors;

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
            this.step = this.letter.width * stepWidth * STEP_RATIO / SECONDS_IN_HOUR;
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

    private static final Color C_COLOR_COLUMN_1 = new Color(210, 210, 210);
    private static final Color C_COLOR_COLUMN_2 = new Color(230, 230, 230);
    private static final Color C_COLOR_LINE = new Color(170, 170, 170);

    public static final String COLOR_COLUMN_1 = "color_column_1";
    public static final String COLOR_COLUMN_2 = "color_column_2";
    public static final String COLOR_LINE = "color_line";
    public static final String COLOR_TEXT = "color_text";
    public static final String COLOR_FILL = "color_fill";
    public static final String COLOR_OUTLINE = "color_outline";

    private static final int SECONDS_IN_HOUR = 3600;
    private static final int HOURS_IN_DAY = 24;

    private final Collection<TrainsCycle> circulations;
    private final Layout layout;
    private boolean update;

    public CirculationDraw(CirculationDrawParams params) {
        this.circulations = params.getCirculations();
        this.layout = new Layout();
        this.layout.fromTime = params.getFrom();
        this.layout.toTime = params.getTo();
        this.layout.stepWidth = params.getWidthInChars();
        this.layout.zoom = params.getZoom();
        this.layout.rows = circulations.size();
        this.update = true;
        this.layout.titleText = params.getTitle();
        CirculationDrawColors colors = params.getColors();
        this.layout.colors = (location, item) -> {
            Color color = colors != null ? colors.getColor(location, item) : null;
            if (color == null) {
                switch (location) {
                    case COLOR_COLUMN_1: color = C_COLOR_COLUMN_1; break;
                    case COLOR_COLUMN_2: color = C_COLOR_COLUMN_2; break;
                    case COLOR_LINE: color = C_COLOR_LINE; break;
                    case COLOR_TEXT: color = Color.BLACK; break;
                    case COLOR_FILL: color = Color.RED; break;
                    case COLOR_OUTLINE: color = Color.BLACK; break;
                    default: color = Color.BLACK;
                }
            }
            return color;
        };
    }

    public void draw(Graphics2D g) {
        AffineTransform t = g.getTransform();
        t.scale(layout.zoom, layout.zoom);
        g.setTransform(t);
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
        g.setFont(g.getFont().deriveFont(Layout.FONT_SIZE));
        boolean updated = false;
        if (this.update) {
            this.layout.updateValues(g);
            this.update = false;
            updated = true;
        }
        return updated;
    }

    public Dimension getSize() {
        return new Dimension((int) (layout.size.width * layout.zoom),
                (int) (layout.size.height * layout.zoom));
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
        int height = layout.row * layout.rows + layout.title;
        boolean odd = true;
        int seconds = layout.fromTime - (layout.fromTime % SECONDS_IN_HOUR);
        int titleTextPos = layout.startY + layout.title - layout.titleGap - layout.textOffset;
        while (seconds <= layout.toTime) {
            Color rowColor = layout.colors.getColor(odd  ? COLOR_COLUMN_1 : COLOR_COLUMN_2);
            g.setColor(rowColor);
            int x1 = this.getX(Math.max(layout.fromTime, seconds));
            int x2 = this.getX(Math.min(layout.toTime, seconds + SECONDS_IN_HOUR));
            g.fillRect(x1, layout.startY, x2 - x1 + 1, height);
            if (seconds >= layout.fromTime) {
                String hStr = Integer.toString(seconds / SECONDS_IN_HOUR % HOURS_IN_DAY);
                drawHourTextWithLine(g, seconds, titleTextPos, hStr);
            }
            odd = !odd;
            seconds += SECONDS_IN_HOUR;
        }

        // row delimiters
        g.setColor(layout.colors.getColor(COLOR_LINE));
        for (int i = 0; i <= layout.rows; i++) {
            int p = layout.getRow(i);
            g.drawLine(layout.border, p, layout.size.width - layout.border, p);
        }
    }

    private void drawHourTextWithLine(Graphics2D g, int seconds, int titleTextPos, String hStr) {
        if (seconds >= layout.fromTime && seconds <= layout.toTime) {
            int pos = this.getX(seconds);
            g.setColor(layout.colors.getColor(COLOR_TEXT));
            g.drawString(hStr, pos - DrawUtils.getStringWidth(g, hStr) / 2, titleTextPos);
            g.setColor(layout.colors.getColor(COLOR_LINE));
            g.drawLine(pos, layout.startY + layout.title, pos, layout.size.height - layout.border);
        }
    }

    private void paintCirculation(Graphics2D g, TrainsCycle circulation, int row) {
        g.setColor(layout.colors.getColor(COLOR_TEXT));
        int textY = layout.getRow(row) + layout.row - layout.rowGap - layout.textOffset;
        int partY = layout.getRow(row) + layout.rowGapSmall + layout.letterSmall.height - layout.textOffsetSmall;
        g.drawString(circulation.getName(), layout.border, textY);
        // rectangle for each item
        int y = layout.getRow(row) + layout.row - layout.rowGapSmall - layout.letterSmall.height;
        int height = layout.letterSmall.height;
        Font backup = g.getFont();
        g.setFont(layout.smallFont);
        for (TrainsCycleItem item : circulation) {
            Interval interval = IntervalFactory.createInterval(item.getStartTime(), item.getEndTime());
            Interval normalized = interval.normalize();
            if (isVisible(normalized.getStart()) || isVisible(normalized.getEnd())) {
                drawItem(g, partY, y, height, item, normalized.getStart(), normalized.getEnd());
            }
            Interval overlap = normalized.getComplementatyIntervalOverThreshold(layout.fromTime);
            if (overlap != null && (isVisible(overlap.getStart()) || isVisible(overlap.getEnd()))) {
                drawItem(g, partY, y, height, item, overlap.getStart(), overlap.getEnd());
            }
        }
        g.setFont(backup);
    }

    private void drawItem(Graphics2D g, int partY, int y, int height, TrainsCycleItem item, int sTime, int eTime) {
        sTime = Math.max(sTime, layout.fromTime);
        eTime = Math.min(eTime, layout.toTime);
        int x = this.getX(sTime);
        int width = (int) ((eTime - sTime) * layout.step);
        g.setColor(layout.colors.getColor(COLOR_FILL, item));
        g.fillRect(x, y, width, height);
        g.setColor(layout.colors.getColor(COLOR_OUTLINE, item));
        g.drawRect(x, y, width, height);
        int w = DrawUtils.getStringWidth(g, item.getTrain().getDefaultName());
        if (x + w <= this.getX(layout.toTime)) {
            g.drawString(item.getTrain().getDefaultName(), x, partY);
        }
    }

    private boolean isVisible(int time) {
        return layout.fromTime <= time && time <= layout.toTime;
    }

    private int getStartX() {
        return layout.border + layout.description;
    }

    private int getX(int time) {
        int x = (int) (this.getStartX() + (time - layout.fromTime) * layout.step);
        return x;
    }
}
