package com.nabilanam.litedownloader.controller;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import com.nabilanam.litedownloader.model.DownloadService;
import com.nabilanam.litedownloader.model.RemoveDownloadListener;
import com.nabilanam.litedownloader.view.FileChooser;

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

	public synchronized String getNewFilePathFromUserThreadSafe() {
		String path = "";
		RunnableFuture<String> rf = new FutureTask<>(() -> {
			return new FileChooser(JFileChooser.FILES_ONLY).getPathFromUser(null, "Rename");
		});
		SwingUtilities.invokeLater(rf);
		try {
			path = rf.get();
		} catch (InterruptedException | ExecutionException ex) {
			Logger.getLogger(TableController.class.getName()).log(Level.SEVERE, null, ex);
		}
		return path;
	}
}
