package com.nabilanam.litedownloader.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.nabilanam.litedownloader.model.DownloadService;
import com.nabilanam.litedownloader.model.GlobalConstants;
import com.nabilanam.litedownloader.model.PaneMessages;
import com.nabilanam.litedownloader.model.TableModel;


/**
 *
 * @author nabil
 */
public class TablePopupMenuController {
	
	public TableModel tableModel;

	public TablePopupMenuController(TableModel tableModel) {
		this.tableModel = tableModel;
	}

	public void startDownload(int did) {
		DownloadService.getInstance().startDownload(did);
	}

	public void pauseDownload(int did) {
		DownloadService.getInstance().pauseDownload(did);
		tableModel.fireStatusCellUpdated(did);
	}

	public void stopDownload(int did) {
		DownloadService.getInstance().stopDownload(did);
		tableModel.fireStatusCellUpdated(did);
	}

	public void removeDownloadLink(int did) {
		DownloadService.getInstance().removeDownload(did);
		tableModel.fireTableRowsDeleted(did, did);
	}

	public void removeDownloadFile(int did) {
		try {
			Path path = DownloadService.getInstance().getDownloadFilePath(did);
			for (int a = 0; a < GlobalConstants.MAX_CONNECTION; a++) {
				Files.deleteIfExists(Paths.get(path.toString() + a));
			}
			Files.deleteIfExists(path);
		} catch (IOException ex) {
			PaneGenerator.showErrorMessage(PaneMessages.ERROR_FILE_CAN_NOT_BE_DELETED);
		}
		removeDownloadLink(did);
	}

	public void launchFile(int did) {
		try {
			File file = DownloadService.getInstance().getDownloadFilePath(did).toFile();
			if (file.exists()) {
				Desktop desktop = Desktop.getDesktop();
				desktop.open(file);
			} else {
				PaneGenerator.showErrorMessage(PaneMessages.ERROR_FILE_DOES_NOT_EXIST);
			}
		} catch (IOException ex) {
			PaneGenerator.showErrorMessage(PaneMessages.ERROR_PLATFORM_NOT_SUPPORTED);
		}
	}

	public void launchFolder(int did) {
		try {
			File file = DownloadService.getInstance().getDownloadFilePath(did).toFile().getParentFile();
			if (file.exists()) {
				Desktop desktop = Desktop.getDesktop();
				desktop.open(file);
			} else {
				PaneGenerator.showErrorMessage(PaneMessages.ERROR_FOLDER_DOES_NOT_EXIST);
			}
		} catch (IOException ex) {
			PaneGenerator.showErrorMessage(PaneMessages.ERROR_PLATFORM_NOT_SUPPORTED);
		}
	}

	public String getDownloadURL(int did) {
		return DownloadService.getInstance().getDownloadURL(did);
	}
}