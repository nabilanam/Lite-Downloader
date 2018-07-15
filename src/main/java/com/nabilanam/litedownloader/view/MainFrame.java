package com.nabilanam.litedownloader.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.nabilanam.litedownloader.controller.LeftbarController;
import com.nabilanam.litedownloader.controller.PaneGenerator;
import com.nabilanam.litedownloader.controller.TableController;
import com.nabilanam.litedownloader.controller.TablePopupMenuController;
import com.nabilanam.litedownloader.controller.TopbarController;
import com.nabilanam.litedownloader.model.DownloadService;
import com.nabilanam.litedownloader.model.GlobalConstants;
import com.nabilanam.litedownloader.model.TableModel;

/**
 *
 * @author nabil
 */
@SuppressWarnings("serial")
public final class MainFrame extends JFrame {

	public MainFrame() {
		super(GlobalConstants.APP_NAME);
		PaneGenerator.Init(this);
		TableModel tableModel = new TableModel();
		tableModel.setFileList(DownloadService.getInstance().getDownloads());
		
		TopbarController topbarController = new TopbarController();
		LeftbarController leftbarController = new LeftbarController();
		TableController tableController = new TableController(tableModel);
		TablePopupMenuController popupMenuController = new TablePopupMenuController(tableModel);
		
		Dimension size = new Dimension(800, 600);
		DownloadService.getInstance().setTableController(tableController);
		DownloadService.getInstance().setAddLinkListener((int did) -> {
			tableController.fireNewRowInserted(did);
		});
		leftbarController.setNodeSelectionListener((status) -> {
			tableModel.fireFilterChanged(status);
		});

		LeftbarPanel leftbarPanel = new LeftbarPanel(leftbarController);
		TopbarPanel topbarPanel = new TopbarPanel(this, topbarController);
		TablePanel tablePanel = new TablePanel(tableModel, tableController, popupMenuController);
		topbarController.setRemoveDownloadListener(() -> {
			int did = tablePanel.getSelectedDid();
			if (did != -1) {
				DownloadService.getInstance().removeDownload(did);
				tableModel.fireTableRowsDeleted(did, did);
			}
		});

		setLayout(new BorderLayout());
		add(topbarPanel, BorderLayout.NORTH);
		add(leftbarPanel, BorderLayout.WEST);
		add(tablePanel, BorderLayout.CENTER);

		setSize(size);
		setVisible(true);
		setMinimumSize(size);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				DownloadService.getInstance().dieNow();
				System.exit(0);
			}
		});
	}

}
