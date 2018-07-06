package com.nabilanam.litedownloader.model;

import java.util.LinkedList;
import java.util.List;

import javax.swing.RowFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author nabil
 */
@SuppressWarnings("serial")
public final class TableModel extends AbstractTableModel
{
    private final TableRowSorter<TableModel> rowSorter;
    private List<Download> fileList;

    public TableModel()
    {
        fileList = new LinkedList<>();
        rowSorter = new TableRowSorter<>(this);
        rowSorter.setSortsOnUpdates(true);
    }
    
    public void changeFilter(DownloadStatus status)
    {
        if (status == null)
        {
            rowSorter.setRowFilter(null);
        }
        else
        {
            rowSorter.setRowFilter(new RowFilter() {
                @Override
                public boolean include(RowFilter.Entry entry)
                {
                    return status.name().equals(entry.getStringValue(6));
                }
            });
        }
    }

    @Override
    public String getColumnName(int column)
    {
        return Column.values()[column].getText();
    }

    @Override
    public int getRowCount()
    {
        return fileList.size();
    }

    @Override
    public int getColumnCount()
    {
        return Column.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        Download download = fileList.get(rowIndex);

        switch (columnIndex)
        {
            case 0:
                return download.getDId();
            case 1:
                return download.getContentType();
            case 2:
                return download.getFileName();
            case 3:
                return download.getLengthString();
            case 4:
                return download.getPercantage();
            case 5:
                return download.getDownloadedLengthString();
            case 6:
                return download.getDownloadStatus().name();
        }
        return null;
    }

    public void setFileList(List<Download> fileList)
    {
        this.fileList = fileList;
    }
    
    public TableRowSorter<TableModel> getRowSorter()
    {
        return rowSorter;
    }
}
