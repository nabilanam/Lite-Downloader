package com.nabilanam.litedownloader.view;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.nabilanam.litedownloader.controller.TableController;
import com.nabilanam.litedownloader.controller.TablePopupMenuController;
import com.nabilanam.litedownloader.model.DownloadStatus;
import com.nabilanam.litedownloader.model.TableModel;

/**
 *
 * @author nabil
 */
@SuppressWarnings("serial")
public final class TablePanel extends JPanel {
	
	private final Table table;
	
	public TablePanel(TableModel tableModel, TableController tableController, TablePopupMenuController popupMenuController) {
		super();
		setLayout(new BorderLayout());
		table = new Table(tableModel, tableController, popupMenuController);
		add(new JScrollPane(table), BorderLayout.CENTER);
	}

	public int getSelectedDid() {
		return table.getDid();
	}
	
	public void tableFilterChanged(DownloadStatus status) {
		table.filterByDownloadStatus(status);
	}
}
