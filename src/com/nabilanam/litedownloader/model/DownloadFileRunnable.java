package com.nabilanam.litedownloader.model;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.nabilanam.litedownloader.controller.PaneGenerator;
import com.nabilanam.litedownloader.controller.TableController;

/**
 *
 * @author nabil
 */
public class DownloadFileRunnable implements Runnable {
	
	private final int MAX_THREAD = GlobalConstants.MAX_CONNECTION;
	private final ExecutorService es;
	public final Download download;
	private final TableController tableController;
	private final ArrayList<Path> tmpPaths;

	public DownloadFileRunnable(TableController tableController, Download download) {
		this.es = Executors.newFixedThreadPool(MAX_THREAD);
		this.download = download;
		this.tableController = tableController;
		this.tmpPaths = new ArrayList<>();
		populateTmpPaths();
	}

	private void populateTmpPaths() {
		int fileCount = download.getTmpFiles();
		for (int a = MAX_THREAD - fileCount; a < MAX_THREAD; a++) {
			tmpPaths.add(Paths.get(download.getFilePath().toString() + a));
		}
	}

	@Override
	public void run() {
		if (doesDownloadedFileExist()) {
			if (!getConfirmationFileDelete()) {
				tableController.updateTableStatusStopped(download);
				return;
			}
		}
		
		boolean isMultipart = isDownloadMultipart();
		if (isMultipart && getStatus() == HttpURLConnection.HTTP_NOT_MODIFIED) {
			if ((download.getDownloadStatus() == DownloadStatus.Paused)
					|| (download.getDownloadStatus() == DownloadStatus.Stopped)) {
				startMultipartDownload();
			}
			startMerge();
		} else if (isMultipart) {
			startMultipartDownload();
			startMerge();
		} else if (!isMultipart) {
			startSinglepartDownload();
		}
		es.shutdown();
	}

	private boolean doesDownloadedFileExist() {
		return download.getFilePath().toFile().exists()
				&& (download.getContentLength() == download.getFilePath().toFile().length());
	}

	private boolean getConfirmationFileDelete() {
		boolean confirm = PaneGenerator.showConfirmDialogInvokeLater(PaneMessages.CONFIRM_FILE_DELETE);
		if (confirm) {
			try {
				Files.deleteIfExists(download.getFilePath());
			} catch (IOException ex) {
				PaneGenerator.showErrorMessageInvokeLater(PaneMessages.ERROR_FILE_CAN_NOT_BE_DELETED
						+ System.lineSeparator() + download.getFilePath().toFile());
			}
		}
		return confirm;
	}

	private boolean isDownloadMultipart() {
		return supportRanges() && download.getContentLength() > MAX_THREAD;
	}

	private boolean supportRanges() {
		boolean result = false;
		HttpURLConnection rangeCon = null;
		try {
			rangeCon = (HttpURLConnection) download.getUrl().openConnection();
			rangeCon.setInstanceFollowRedirects(true);
			rangeCon.setRequestMethod("GET");
			rangeCon.setRequestProperty("Range", "bytes=0-10");
			rangeCon.setRequestProperty("User-Agent",
					"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
			if (rangeCon.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
				result = true;
			}
			rangeCon.disconnect();
		} catch (IOException ex) {
			if (rangeCon != null) {
				rangeCon.disconnect();
			}
			Logger.getLogger(DownloadFileRunnable.class.getName()).log(Level.SEVERE, null, ex);
		}
		return result;
	}

	private int getStatus() {
		int responseCode = 0;
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) download.getUrl().openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("If-None-Match", download.getEtag());
			con.setRequestProperty("If-Modified-Since", download.getLastModified());
			con.setRequestProperty("User-Agent",
					"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
			responseCode = con.getResponseCode();
			con.disconnect();
			return responseCode;
		} catch (IOException ex) {
			PaneGenerator.showErrorMessageInvokeLater(PaneMessages.ERROR_NETWORK_CONNECTION);
			if (con != null) {
				con.disconnect();
			}
			return responseCode;
		}
	}

	private void startMultipartDownload() {
		tableController.updateTableStatusDownloading(download);
		CountDownLatch cdl = new CountDownLatch(MAX_THREAD);
		long size = download.getContentLength() / MAX_THREAD;
		long end = -1;
		for (int a = 0; a < MAX_THREAD; a++) {
			end = addPartialDownload(cdl, size, end, a);
		}
		try {
			cdl.await();
		} catch (InterruptedException e) {
			gracefulShutdown();
			return;
		}
		tableController.updateTableStatusFileMerging(download);
	}

	private void startMerge() {
		if (download.getDownloadStatus() == DownloadStatus.Merge) {
			es.submit(new MultipartFileMergeRunnable(download, tmpPaths, tableController));
		}
	}

	private long addPartialDownload(CountDownLatch cdl, long size, long end, int a) {
		long start;
		Path path;
		path = tmpPaths.get(a);
		start = end + 1;
		end = (a == (MAX_THREAD - 1)) ? download.getContentLength() : (start + size);
		start += path.toFile().length();
		if (end - start > 0) {
			es.submit(new DownloadRunnable(cdl, path, start, end, download, tableController));
		} else {
			cdl.countDown();
		}
		return end;
	}

	private void gracefulShutdown() {
		es.shutdownNow();
		threadSleep();
	}

	private void threadSleep() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException ex) {
			Logger.getLogger(DownloadFileRunnable.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void startSinglepartDownload() {
		tableController.updateTableStatusDownloading(download);
		CountDownLatch cdl = new CountDownLatch(1);
		download.setSingleConnectionStatus();
		es.submit(new DownloadRunnable(cdl, download, tableController));
		try {
			cdl.await();
			if (download.getContentLength() < 0) {
				download.setContentLength(download.getDownloadedLength());
				tableController.updateTableUndefinedDownload(download);
			}
			tableController.updateTableStatusCompleted(download);
		} catch (InterruptedException e) {
			gracefulShutdown();
		}
	}
}
