package net.parostroj.timetable.gui.views;

import java.awt.event.MouseEvent;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

class ToolTipHeader extends JTableHeader {

    private static final long serialVersionUID = 1L;

	public ToolTipHeader(TableColumnModel model) {
        super(model);
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        int col = this.getTable().columnAtPoint(e.getPoint());
        return col == -1 ? null : (String) this.getTable().getColumnModel().getColumn(col).getHeaderValue();
    }
}
