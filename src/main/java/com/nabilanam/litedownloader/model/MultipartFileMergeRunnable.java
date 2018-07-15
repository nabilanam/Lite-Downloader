package com.nabilanam.litedownloader.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.nabilanam.litedownloader.controller.PaneGenerator;
import com.nabilanam.litedownloader.controller.TableController;

/**
 *
 * @author nabil
 */
public class MultipartFileMergeRunnable implements Runnable {

	private Download download;
	private final int MAX_THREAD = GlobalConstants.MAX_CONNECTION;
	private ArrayList<Path> tmpPaths;
	private TableController tableController;

	public MultipartFileMergeRunnable(Download download, TableController tableController) {
		this.download = download;
		this.tmpPaths = download.getTmpPaths();
		this.tableController = tableController;
	}

	@Override
	public void run() {
		if (isDownloadComplete()) {
			if (isFileMergeComplete()) {
				tableController.updateTableStatusCompleted(download);
			}
		} else {
			deleteTempFiles();
			PaneGenerator.showErrorMessageInvokeLater(PaneMessages.ERROR_FILE_SIZE_MISMATCH);
			tableController.updateTableStatusError(download);
		}
	}

	private boolean isDownloadComplete() {
		return download.getContentLength() == download.getDownloadedLength();
	}

	private boolean isFileMergeComplete() {
		FileOutputStream fos = null;
		FileInputStream fis = null;
		try {
			fos = new FileOutputStream(download.getFilePath().toFile());
			for (int i = MAX_THREAD - tmpPaths.size(); i < tmpPaths.size(); i++) {
				Path tmpPath = tmpPaths.get(i);
				fis = new FileInputStream(tmpPath.toFile());
				int bytesRead;
				byte[] buffer = new byte[4096];
				while ((bytesRead = fis.read(buffer)) > -1) {
					fos.write(buffer, 0, bytesRead);
					fos.flush();
					if (Thread.interrupted()) {
						fos.close();
						fis.close();
						threadSleep();
						deleteTempFilesRecursive(i);
						tableController.updateTableStatusFileMerging(download);
						return false;
					}
				}
				fis.close();
			}
			fos.close();
		} catch (FileNotFoundException ex) {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			PaneGenerator.showErrorMessageInvokeLater(
					PaneMessages.ERROR_FILE_MERGE + System.lineSeparator() + PaneMessages.ERROR_FILE_DOES_NOT_EXIST);
			return false;
		} catch (IOException ex) {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			PaneGenerator.showErrorMessageInvokeLater(
					PaneMessages.ERROR_FILE_MERGE + System.lineSeparator() + PaneMessages.ERROR_IO_EXCEPTION);
			return false;
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		threadSleep();
		deleteTempFiles();
		return true;
	}

	private void deleteTempFilesRecursive(int i) {
		if (--i > -1) {
			try {
				Files.deleteIfExists(tmpPaths.get(i));
				tmpPaths.remove(i);
				deleteTempFilesRecursive(i);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void threadSleep() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException ex) {
			Logger.getLogger(DownloadFileRunnable.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void deleteTempFiles() {
		for (Path tmpPath : tmpPaths) {
			try {
				Files.deleteIfExists(tmpPath);
			} catch (IOException ex) {
				Logger.getLogger(DownloadFileRunnable.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

}
