package com.nabilanam.litedownloader.view;

import java.awt.Component;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author nabil
 */
@SuppressWarnings("serial")
public class ProgressRenderer extends JProgressBar implements TableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        int progress = Math.round((float) value * 100f);
        setValue(progress);
        return this;
    }
}
