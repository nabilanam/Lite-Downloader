package com.nabilanam.litedownloader.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nabil
 */
public class Download{
	
	private int did;
	private final ArrayList<Path> tmpPaths;
	private final String tmpPathExt;
	private final String url;
	private final String etag;
	private final String fileName;
	private final String filePath;
	private final String contentType;
	private final String lastModified;
	private long contentLength;
	private long downloadedLength;
	private boolean isMultiConnection;
	private DownloadStatus downloadStatus;

	public Download(int did, String url, String fileName, String filePath, String etag, String lastModified,
			String contentType, long contentLength, boolean isMultiConnection) {
		this.did = did;
		this.url = url;
		this.etag = etag;
		this.lastModified = lastModified;
		this.fileName = fileName;
		this.filePath = filePath;
		this.contentType = contentType;
		this.contentLength = contentLength;
		this.isMultiConnection = isMultiConnection;
		this.tmpPathExt = "-ldr";
		this.tmpPaths = new ArrayList<>();
		this.downloadedLength = 0;
		this.downloadStatus = DownloadStatus.Stopped;
		generateTmpPaths(filePath + tmpPathExt, isMultiConnection);
	}

	private void generateTmpPaths(String fileLoc, boolean generate) {
		if (generate) {
			for (int i = 0; i < GlobalConstants.MAX_CONNECTION; i++) {
				tmpPaths.add(Paths.get(fileLoc + i));
			}
		}
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

	public synchronized float getPercantageThreadSafe() {
		return (float) downloadedLength / (float) contentLength;
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

	public boolean isMultiConnection() {
		return isMultiConnection;
	}

	public void setMultiConnection(boolean isMultiConnection) {
		this.isMultiConnection = isMultiConnection;
	}

	public void setDownloadStatus(DownloadStatus downloadStatus) {
		this.downloadStatus = downloadStatus;
	}

	public ArrayList<Path> getTmpPaths() {
		return tmpPaths;
	}

	@Override
	public String toString() {
		return "Download [did=" + did + ", tmpPaths=" + tmpPaths + ", tmpPathExt=" + tmpPathExt + ", url=" + url
				+ ", etag=" + etag + ", fileName=" + fileName + ", filePath=" + filePath + ", contentType="
				+ contentType + ", lastModified=" + lastModified + ", contentLength=" + contentLength
				+ ", downloadedLength=" + downloadedLength + ", isMultiConnection=" + isMultiConnection
				+ ", downloadStatus=" + downloadStatus + "]";
	}

}
