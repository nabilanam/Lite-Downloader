package com.nabilanam.litedownloader.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.nabilanam.litedownloader.controller.TableController;

/**
 *
 * @author nabil
 */
public class DownloadRunnable implements Runnable {
	
	public final int did;
	private final long start, end;
	private final Path path;
	public final Download download;
	private final CountDownLatch cdl;
	private final TableController tableController;

	public DownloadRunnable(CountDownLatch cdl, Download download, TableController tableController) {
		this(cdl, download.getFilePath(), 0, 0, download, tableController);
	}

	public DownloadRunnable(CountDownLatch cdl, Path path, long start, long end, Download download,
			TableController tableController) {
		this.cdl = cdl;
		this.end = end;
		this.path = path;
		this.start = start;
		this.download = download;
		this.did = download.getDId();
		this.tableController = tableController;
	}

	@Override
	public void run() {
		HttpURLConnection con = null;
		FileOutputStream outputStream;
		InputStream inputStream;
		try {
			con = (HttpURLConnection) download.getUrl().openConnection();
			con.setInstanceFollowRedirects(true);
			con.setRequestMethod("GET");
			if (start < end) {
				con.setRequestProperty("Range", "bytes=" + start + "-" + end);
			}
			con.setRequestProperty("User-Agent",
					"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
		} catch (IOException ex) {
			if (con != null) {
				con.disconnect();
			}
			return;
		}
		int responseCode;
		try {
			responseCode = con.getResponseCode();
			con.connect();
		} catch (IOException ex) {
			con.disconnect();
			return;
		}
		if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_PARTIAL) {
			try {
				inputStream = con.getInputStream();
			} catch (IOException ex) {
				con.disconnect();
				return;
			}
			try {
				outputStream = new FileOutputStream(path.toFile(), true);
			} catch (IOException ex) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException ex1) {
						Logger.getLogger(DownloadRunnable.class.getName()).log(Level.SEVERE, null, ex1);
					}
				}
				con.disconnect();
				return;
			}
			
			byte[] buffer = new byte[4096];
			int bytesRead;
			try {
				while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
					if (Thread.currentThread().isInterrupted()) {
						inputStream.close();
						outputStream.close();
						con.disconnect();
						return;
					}
					outputStream.write(buffer, 0, bytesRead);
					outputStream.flush();
					tableController.updateTableProgressThreadSafe(download, bytesRead);
				}
			} catch (IOException ex) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException ex1) {
						Logger.getLogger(DownloadRunnable.class.getName()).log(Level.SEVERE, null, ex1);
					}
				}
				try {
					outputStream.close();
				} catch (IOException ex1) {
					Logger.getLogger(DownloadRunnable.class.getName()).log(Level.SEVERE, null, ex1);
				}
				con.disconnect();
				return;
			}
			try {
				inputStream.close();
			} catch (IOException ex) {
				Logger.getLogger(DownloadRunnable.class.getName()).log(Level.SEVERE, null, ex);
			}
			try {
				outputStream.flush();
				outputStream.close();
			} catch (IOException ex) {
				Logger.getLogger(DownloadRunnable.class.getName()).log(Level.SEVERE, null, ex);
			}
			con.disconnect();
			cdl.countDown();
		}
	}
}
