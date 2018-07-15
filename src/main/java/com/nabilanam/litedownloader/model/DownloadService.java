package com.nabilanam.litedownloader.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.nabilanam.litedownloader.controller.PaneGenerator;
import com.nabilanam.litedownloader.controller.TableController;

/**
 *
 * @author nabil
 */
public class DownloadService {

	private static DownloadService instance;

	public static DownloadService getInstance() {
		return (instance == null) ? (instance = new DownloadService()) : instance;
	}

	private int TIMEOUT = 20;
	private ExecutorService es;
	private Database database;
	private TableController tableController;
	private ConcurrentHashMap<Integer, Future<?>> hashMap;
	private AddLinkListener addLinkListener;

	private DownloadService() {
		es = Executors.newCachedThreadPool();
		database = Database.getInstance();
		hashMap = new ConcurrentHashMap<>();
	}

	public void addDownload(String link, String folderPath) {
		int id = database.getNewDId();
		Future<Download> future = es.submit(new DownloadInfoCallable(id, link, folderPath));
		try {
			Download download;
			if ((download = future.get(TIMEOUT, TimeUnit.SECONDS)) != null) {
				database.add(download);
				addLinkListener.linkAdded(id);
			}
		} catch (InterruptedException ex) {
			PaneGenerator.showErrorMessage(PaneMessages.ERROR_LINK_CHECK_INTERRUPTED);
		} catch (ExecutionException ex) {
			PaneGenerator.showErrorMessage(PaneMessages.ERROR_LINK_CHECK_EXCEPTION);
		} catch (TimeoutException ex) {
			PaneGenerator.showErrorMessage(PaneMessages.ERROR_LINK_CHECK_TIMEOUT);
		}
	}

	public void removeDownload(int did) {
		hashMap.remove(did);
		int i = -1;
		for (Integer key : hashMap.keySet()) {
			hashMap.put(++i, hashMap.remove(key));
		}
		database.remove(did);
		saveDatabaseThreadSafe();
	}

	public void startDownload(int did) {
		hashMap.remove(did);
		Download download = database.get(did);
		hashMap.put(did, es.submit(new DownloadFileRunnable(tableController, download)));
	}

	public void pauseDownload(int did) {
		cancelDownload(hashMap.remove(did));
		database.get(did).setDownloadStatus(DownloadStatus.Paused);
	}

	public void stopDownload(int did) {
		cancelDownload(hashMap.remove(did));
		database.get(did).setDownloadStatus(DownloadStatus.Stopped);
	}

	private void cancelDownload(Future<?> future) {
		if (future != null) {
			future.cancel(true);
		}
		try {
			Thread.sleep(50);
		} catch (InterruptedException ex) {
			Logger.getLogger(TableController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void dieNow() {
		Set<Map.Entry<Integer, Future<?>>> entrySet = hashMap.entrySet();
		entrySet.forEach((entry) -> {
			pauseDownloadGracefully(entry.getKey(), entry.getValue());
		});
		saveDatabaseThreadSafe();
	}

	private void pauseDownloadGracefully(int did, Future<?> future) {
		cancelDownload(future);
		try {
			Download download = database.get(did);
			if ((download.getDownloadStatus() != DownloadStatus.Completed)
					&& (download.getDownloadStatus() != DownloadStatus.Merge)) {
				download.setDownloadStatus(DownloadStatus.Paused);
			}
		} catch (Exception ex) {
			Logger.getLogger(TableController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public synchronized void saveDatabaseThreadSafe() {
		try {
			FileUtil.serialize(database);
		} catch (IOException ex) {
			PaneGenerator.showErrorMessage(PaneMessages.ERROR_DB_CHANGE_COULD_NOT_BE_SAVED);
		}
	}

	public Path getDownloadFilePath(int did) {
		return database.get(did).getFilePath();
	}

	public String getDownloadURL(int did) {
		return database.get(did).getUrl().toString();
	}

	public List<Download> getDownloads() {
		return database.getDownloads();
	}

	public DownloadStatus getDownloadStatus(int did) {
		return database.get(did).getDownloadStatus();
	}

	public String getFileDirectory() {
		return database.getFileDirectory();
	}

	public void saveFileDirectory(String directory) {
		database.setFileDirectory(directory);
		saveDatabaseThreadSafe();
	}

	public boolean downloadFileExists(String filePath) {
		if (new File(filePath).exists()) {
			return true;
		}
		for (Download download : getDownloads()) {
			if (download.getFilePath().toString().equals(filePath)) {
				return true;
			}
		}
		return false;
	}

	public void setTableController(TableController tableController) {
		this.tableController = tableController;
	}

	public void setAddLinkListener(AddLinkListener addLinkListener) {
		this.addLinkListener = addLinkListener;
	}
}
