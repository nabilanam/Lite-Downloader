package com.nabilanam.litedownloader.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.PatternSyntaxException;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

import com.nabilanam.litedownloader.controller.TableController;
import com.nabilanam.litedownloader.controller.TablePopupMenuController;
import com.nabilanam.litedownloader.model.Column;
import com.nabilanam.litedownloader.model.DownloadStatus;
import com.nabilanam.litedownloader.model.TableModel;

/**
 *
 * @author nabil
 */
@SuppressWarnings("serial")
public final class Table extends JTable {
	
	private final TableController tableController;
	private final TablePopupMenu popupMenu;
	private final TableRowSorter<TableModel> rowSorter;

	public Table(TableModel tableModel, TableController tableController, TablePopupMenuController popupMenuController) {
		super(tableModel);
		this.rowSorter = new TableRowSorter<TableModel>(tableModel);
		this.tableController = tableController;
		this.popupMenu = new TablePopupMenu(this, popupMenuController);
		setRowSorter(rowSorter);;
		setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		centerTableColumns();
		setPreferredColumnWidths();
		setMouseAdapter();
		getColumn(Column.DONE.getText()).setCellRenderer(new ProgressRenderer());
	}

	public int getDid() {
		int row = getSelectedRow();
		if (row == -1) {
			return row;
		}
		return (int) getValueAt(row, 0);
	}

	public void setMouseAdapter() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					int selectedRow = Table.this.rowAtPoint(e.getPoint());
					Table.this.getSelectionModel().addSelectionInterval(selectedRow, selectedRow);
					Table.this.showPopupMenu(e.getX(), e.getY());
				}
			}
		});
	}

	public void showPopupMenu(int x, int y) {
		if (popupMenu != null) {
			popupMenu.updateItems(tableController.getRowDownloadStatus(getDid()));
			popupMenu.show(x, y);
		}
	}

	private void centerTableColumns() {
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for (int x = 0; x < getColumnModel().getColumnCount(); x++) {
			getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
		}
	}

	private void setPreferredColumnWidths() {
		int[] widths = new int[] { 40, 80, 200, 50, 100, 100 };
		for (int x = 0; x < getColumnModel().getColumnCount() - 1; x++) {
			getColumnModel().getColumn(x).setPreferredWidth(widths[x]);
		}
	}

	public void filterByDownloadStatus(DownloadStatus status) {
		RowFilter<TableModel, Object> filter = null;
		if (status == null) {
			rowSorter.setRowFilter(filter);
			return;
		}
		try {
			filter = RowFilter.regexFilter(status.toString(), 6);
			rowSorter.setRowFilter(filter);
		} catch (PatternSyntaxException e) {
			return;
		}
	}
}
