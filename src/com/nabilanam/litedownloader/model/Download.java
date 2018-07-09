package com.nabilanam.litedownloader.model;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nabil
 */
public class Download implements Serializable {
	private static final long serialVersionUID = 6030894333031168823L;

	private int did;
	private int tmpFiles;
	private final String url;
	private final String etag;
	private final String filePath;
	private final String fileName;
	private final String contentType;
	private final String lastModified;
	private long contentLength;
	private long downloadedLength;
	private boolean isSingleConnection;
	private DownloadStatus downloadStatus;

	public Download(int did, String url, String fileName, String filePath, String etag, String lastModified,
			String contentType, long contentLength) {
		this.did = did;
		this.url = url;
		this.etag = etag;
		this.tmpFiles = 4;
		this.lastModified = lastModified;
		this.fileName = fileName;
		this.contentType = contentType;
		this.contentLength = contentLength;
		this.filePath = filePath;
		this.downloadedLength = 0;
		this.downloadStatus = DownloadStatus.Stopped;
	}

	public synchronized void addToDownloadedLengthThreadSafe(int length) {
		downloadedLength += length;
	}

	public long getContentLength() {
		return contentLength;
	}

	public String getContentType() {
		return contentType;
	}

	public int getDId() {
		return did;
	}

	public long getDownloadedLength() {
		return downloadedLength;
	}

	public String getDownloadedLengthString() {
		return ByteRepresentation.Represent(downloadedLength);
	}

	public DownloadStatus getDownloadStatus() {
		return downloadStatus;
	}

	public String getEtag() {
		return etag;
	}

	public String getFileName() {
		return fileName;
	}

	public Path getFilePath() {
		return Paths.get(filePath);
	}

	public String getLastModified() {
		return lastModified;
	}

	public String getLengthString() {
		return ByteRepresentation.Represent(contentLength);
	}

	public float getPercantage() {
		return (float) downloadedLength / (float) contentLength;
	}

	public boolean getSingleConnectionStatus() {
		return isSingleConnection;
	}

	public URL getUrl() {
		try {
			return new URL(url);
		} catch (MalformedURLException ex) {
			Logger.getLogger(Download.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public void setContentLength(long length) {
		contentLength = length;
	}

	public void setDId(int did) {
		this.did = did;
	}

	public void setDownloadedLength(long length) {
		downloadedLength = length;
	}

	public void setDownloadStatus(DownloadStatus downloadStatus) {
		this.downloadStatus = downloadStatus;
	}

	public void setSingleConnectionStatus() {
		this.isSingleConnection = true;
	}

	public int getTmpFiles() {
		return tmpFiles;
	}

	public void setTmpFiles(int tmpFiles) {
		this.tmpFiles = tmpFiles;
	}

	@Override
	public String toString() {
		return "Download [did=" + did + ", tmpFiles=" + tmpFiles + ", url=" + url + ", etag=" + etag + ", filePath="
				+ filePath + ", fileName=" + fileName + ", contentType=" + contentType + ", lastModified="
				+ lastModified + ", contentLength=" + contentLength + ", downloadedLength=" + downloadedLength
				+ ", isSingleConnection=" + isSingleConnection + ", downloadStatus=" + downloadStatus + "]";
	}
	
}
