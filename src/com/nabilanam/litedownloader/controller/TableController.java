package com.nabilanam.litedownloader.controller;

import com.nabilanam.litedownloader.model.Download;
import com.nabilanam.litedownloader.model.DownloadService;
import com.nabilanam.litedownloader.model.DownloadStatus;
import com.nabilanam.litedownloader.model.TableModel;

/**
 *
 * @author nabil
 */
public class TableController {
	
	private TableModel tableModel;

	public TableController(TableModel tableModel) {
		this.tableModel = tableModel;
	}
	
	public DownloadStatus getRowDownloadStatus(int did) {
		return DownloadService.getInstance().getDownloadStatus(did);
	}

	public void fireNewRowInserted(int id) {
		tableModel.fireTableRowsInserted(id, id);
		DownloadService.getInstance().saveDatabaseThreadSafe();
	}

	public void updateTableStatusFileMerging(Download download) {
		download.setDownloadStatus(DownloadStatus.Merge);
		tableModel.fireStatusCellUpdated(download.getDId());
		DownloadService.getInstance().saveDatabaseThreadSafe();
	}
	
	public void updateTableStatusDownloading(Download download) {
		download.setDownloadStatus(DownloadStatus.Downloading);
		tableModel.fireStatusCellUpdated(download.getDId());
		DownloadService.getInstance().saveDatabaseThreadSafe();
	}

	public void updateTableStatusStopped(Download download) {
		download.setDownloadStatus(DownloadStatus.Stopped);
		tableModel.fireStatusCellUpdated(download.getDId());
		DownloadService.getInstance().saveDatabaseThreadSafe();
	}

	public void updateTableStatusCompleted(Download download) {
		download.setDownloadStatus(DownloadStatus.Completed);
		tableModel.fireStatusCellUpdated(download.getDId());
		DownloadService.getInstance().saveDatabaseThreadSafe();
	}

	public void updateTableUndefinedDownload(Download download) {
		tableModel.fireSizeCellUpdated(download.getDId());
		tableModel.fireDoneCellUpdated(download.getDId());
		tableModel.fireDownloadedCellUpdated(download.getDId());
	}

	public void updateTableStatusError(Download download) {
		download.setDownloadStatus(DownloadStatus.Error);
		tableModel.fireStatusCellUpdated(download.getDId());
		DownloadService.getInstance().saveDatabaseThreadSafe();
	}

	public void updateTableProgressThreadSafe(Download download, int bytesRead) {
		download.addToDownloadedLengthThreadSafe(bytesRead);
		tableModel.fireDoneCellUpdated(download.getDId());
		tableModel.fireDownloadedCellUpdated(download.getDId());
	}
}
