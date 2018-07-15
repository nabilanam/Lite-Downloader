package com.nabilanam.litedownloader.model;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author nabil
 */
public class Database {
	
	private static Database instance;

	public static Database getInstance() {
		if (instance != null || (instance = FileUtil.deserialize()) != null) {
			return instance;
		}
		return (instance = new Database());
	}

	private final LinkedList<Download> downloads;
	private int did;
	private String directoryPath;

	public Database() {
		did = -1;
		downloads = new LinkedList<>();
		directoryPath = System.getProperty("user.home") + File.separator + "Downloads";
	}

	public int getNewDId() {
		return did + 1;
	}

	public void add(Download download) {
		did++;
		downloads.add(download);
	}

	public void remove(int did) {
		if (downloads.size() <= did) {
			return;
		}
		downloads.remove(did);
		this.did = -1;
		for (Download dFile : downloads) {
			dFile.setDId(++this.did);
		}
	}

	public List<Download> getDownloads() {
		for (Download download : downloads) {
			File file = download.getFilePath().toFile();
			DownloadStatus status = download.getDownloadStatus();
			if (status == DownloadStatus.Completed && !file.exists()) {
				resetDownload(download);
			}
			else if ((status == DownloadStatus.Paused 
					|| status == DownloadStatus.Stopped)
					&& download.getTmpPaths().size() > 0) {
				boolean exists = true;
				for (Path path : download.getTmpPaths()) {
					if (!path.toFile().exists()) {
						exists = false;
						break;
					}
				}
				if (!exists) {
					for (Path path : download.getTmpPaths()) {
						path.toFile().delete();
					}
					resetDownload(download);
				}
			}
		}
		return Collections.unmodifiableList(downloads);
	}

	private void resetDownload(Download download) {
		download.setDownloadedLength(0);
		download.setDownloadStatus(DownloadStatus.Stopped);
	}

	public Download get(int did) {
		return downloads.get(did);
	}

	public String getFileDirectory() {
		return directoryPath;
	}

	public void setFileDirectory(String directoryPath) {
		this.directoryPath = directoryPath;
	}

}
