package net.parostroj.timetable.gui.views;

import java.awt.event.MouseEvent;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

class ToolTipHeader extends JTableHeader {

    public ToolTipHeader(TableColumnModel model) {
        super(model);
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        int col = this.getTable().columnAtPoint(e.getPoint());
        return (String) this.getTable().getColumnModel().getColumn(col).getHeaderValue();
    }
}