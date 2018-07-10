package com.nabilanam.litedownloader.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import com.nabilanam.litedownloader.controller.TableController;
import com.nabilanam.litedownloader.controller.TablePopupMenuController;
import com.nabilanam.litedownloader.model.Column;
import com.nabilanam.litedownloader.model.TableModel;

/**
 *
 * @author nabil
 */
@SuppressWarnings("serial")
public final class Table extends JTable {
	
	private final TableController tableController;
	private final TablePopupMenu popupMenu;

	public Table(TableModel tableModel, TableController tableController, TablePopupMenuController popupMenuController) {
		super(tableModel);
		this.setRowSorter(tableModel.getRowSorter());
		this.tableController = tableController;
		this.popupMenu = new TablePopupMenu(this, popupMenuController);
		setAutoCreateRowSorter(true);
		setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		centerTableColumns();
		setPreferredColumnWidths();
		setMouseAdapter();
		getColumn(Column.DONE.getText()).setCellRenderer(new ProgressRenderer());
	}

	public int getDid() {
		return (int) getValueAt(getSelectedRow(), 0);
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
}
