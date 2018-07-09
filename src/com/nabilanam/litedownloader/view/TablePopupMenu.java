package com.nabilanam.litedownloader.view;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.nabilanam.litedownloader.controller.TablePopupMenuController;
import com.nabilanam.litedownloader.model.DownloadStatus;
import com.nabilanam.litedownloader.model.MenuItem;

/**
 *
 * @author nabil
 */
@SuppressWarnings("serial")
public final class TablePopupMenu extends JPopupMenu {
	
	private final Table table;
	private final JMenuItem start;
	private final JMenuItem pause;
	private final JMenuItem stop;
	private final JMenuItem copyLink;
	private final JMenuItem removeLink;
	private final JMenuItem removeFile;
	private final JMenuItem launchFile;
	private final JMenuItem launchFolder;
	private final TablePopupMenuController popupController;

	public TablePopupMenu(Table table, TablePopupMenuController controller) {
		this.table = table;
		this.popupController = controller;

		start = new JMenuItem(MenuItem.START.getText());
		pause = new JMenuItem(MenuItem.PAUSE.getText());
		stop = new JMenuItem(MenuItem.STOP.getText());
		copyLink = new JMenuItem(MenuItem.COPY_LINK.getText());
		removeLink = new JMenuItem(MenuItem.REMOVE_LINK.getText());
		removeFile = new JMenuItem(MenuItem.REMOVE_FILE.getText());
		launchFile = new JMenuItem(MenuItem.LAUNCH_FILE.getText());
		launchFolder = new JMenuItem(MenuItem.LAUNCH_FOLDER.getText());

		setListeners();

		add(start);
		add(pause);
		add(stop);
		add(copyLink);
		add(removeLink);
		add(removeFile);
		add(launchFile);
		add(launchFolder);
	}

	public void show(int x, int y) {
		this.show(table, x, y);
	}

	private void setListeners() {
		start.addActionListener((ActionEvent e) -> {
			popupController.startDownload(table.getDid());
		});
		pause.addActionListener((ActionEvent e) -> {
			popupController.pauseDownload(table.getDid());
		});
		stop.addActionListener((ActionEvent e) -> {
			popupController.stopDownload(table.getDid());
		});
		copyLink.addActionListener((ActionEvent e) -> {
			String link = popupController.getDownloadURL(table.getDid());
			StringSelection selection = new StringSelection(link);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, null);
		});
		removeLink.addActionListener((ActionEvent e) -> {
			popupController.removeDownloadLink(table.getDid());
		});
		removeFile.addActionListener((ActionEvent e) -> {
			popupController.removeDownloadFile(table.getDid());
		});
		launchFile.addActionListener((ActionEvent e) -> {
			popupController.launchFile(table.getDid());
		});
		launchFolder.addActionListener((ActionEvent e) -> {
			popupController.launchFolder(table.getDid());
		});
	}

	public void updateItems(DownloadStatus status) {
		switch (status) {
		case Error:
			start.setText("Start");

			start.setEnabled(false);
			pause.setEnabled(false);
			stop.setEnabled(false);
			copyLink.setEnabled(true);
			removeLink.setEnabled(true);
			removeFile.setEnabled(false);
			launchFile.setEnabled(false);
			launchFolder.setEnabled(true);
			break;
		case Paused:
			start.setText("Resume");

			start.setEnabled(true);
			pause.setEnabled(false);
			stop.setEnabled(false);
			copyLink.setEnabled(true);
			removeLink.setEnabled(true);
			removeFile.setEnabled(true);
			launchFile.setEnabled(false);
			launchFolder.setEnabled(true);
			break;
		case Stopped:
			start.setText("Start");

			start.setEnabled(true);
			pause.setEnabled(false);
			stop.setEnabled(false);
			copyLink.setEnabled(true);
			removeLink.setEnabled(true);
			removeFile.setEnabled(true);
			launchFile.setEnabled(false);
			launchFolder.setEnabled(true);
			break;
		case Completed:
			start.setText("Resume");

			start.setEnabled(false);
			pause.setEnabled(false);
			stop.setEnabled(false);
			copyLink.setEnabled(true);
			removeLink.setEnabled(true);
			removeFile.setEnabled(true);
			launchFile.setEnabled(true);
			launchFolder.setEnabled(true);
			break;
		case Downloading:
			start.setText("Resume");

			start.setEnabled(false);
			pause.setEnabled(true);
			stop.setEnabled(true);
			copyLink.setEnabled(true);
			removeLink.setEnabled(false);
			removeFile.setEnabled(false);
			launchFile.setEnabled(false);
			launchFolder.setEnabled(true);
			break;
		case Merge:
			start.setText("Resume");

			start.setEnabled(true);
			pause.setEnabled(false);
			stop.setEnabled(false);
			copyLink.setEnabled(true);
			removeLink.setEnabled(false);
			removeFile.setEnabled(true);
			launchFile.setEnabled(false);
			launchFolder.setEnabled(true);
			break;
		default:
			break;
		}
	}
}
