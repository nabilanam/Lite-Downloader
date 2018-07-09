package com.nabilanam.litedownloader.controller;

import com.nabilanam.litedownloader.model.RemoveDownloadListener;

/**
 *
 * @author nabil
 */
public class TopbarController {

	private RemoveDownloadListener removeDownloadListener;
	
	public void addDownload(String link) {
		DownloadService.getInstance().addDownload(link, getFolderPath());
	}

	public void removeDownload() {
		removeDownloadListener.removeDownload();
	}

	public String getFolderPath() {
		return DownloadService.getInstance().getFileDirectory();
	}

	public void saveFolderPath(String directory) {
		DownloadService.getInstance().saveFileDirectory(directory);
	}

	public void setRemoveDownloadListener(RemoveDownloadListener listener) {
		this.removeDownloadListener = listener;
	}
}
