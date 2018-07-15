package com.nabilanam.litedownloader.model;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.nabilanam.litedownloader.controller.PaneGenerator;

/**
 *
 * @author nabil
 */
public class DownloadInfoCallable implements Callable<Download> {

	private final int id;
	private final String link;
	private final String folderPath;

	public DownloadInfoCallable(int id, String link, String folderPath) {
		this.id = id;
		this.link = link;
		this.folderPath = folderPath;
	}

	@Override
	public Download call() {
		URL url;
		Download download = null;
		try {
			url = new URL(link);
		} catch (MalformedURLException ex) {
			PaneGenerator.showErrorMessageInvokeLater(PaneMessages.ERROR_URL_MALFORMED);
			return download;
		}
		try {
			url.toURI();
		} catch (URISyntaxException ex) {
			PaneGenerator.showErrorMessageInvokeLater(PaneMessages.ERROR_URL_SYNTAX);
			return download;
		}
		HttpURLConnection con;
		try {
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
		} catch (ProtocolException ex) {
			PaneGenerator.showErrorMessageInvokeLater(PaneMessages.ERROR_URL_HTTP_PROTOCOL_EXCEPTION);
			return download;
		} catch (IOException ex) {
			PaneGenerator.showErrorMessageInvokeLater(PaneMessages.ERROR_IO_EXCEPTION);
			return download;
		}
		int responseCode;
		con.setRequestProperty("User-Agent",
				"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
		try {
			con.connect();
			responseCode = con.getResponseCode();
		} catch (IOException ex) {
			PaneGenerator.showErrorMessageInvokeLater(PaneMessages.ERROR_IO_EXCEPTION);
			return download;
		}
		if (responseCode == HttpURLConnection.HTTP_OK) {
			String etag = con.getHeaderField("ETag");
			String lastModified = con.getHeaderField("Last-Modified");
			String contentType = con.getHeaderField("Content-Type");
			long contentLength = con.getContentLengthLong();
			String contentDisposition = con.getHeaderField("Content-Disposition");
			boolean isMultiConnection = isMultiConnection(url, contentLength);
			String fileName;
			if (contentDisposition != null && contentDisposition.contains("=")) {
				fileName = contentDisposition.split("=")[1].replaceAll("\"", "");
			} else {
				fileName = url.getPath().substring(url.getPath().lastIndexOf('/') + 1, url.getPath().length())
						.replaceAll("%20", " ");
			}
			String[] results = validateFileNameRecursive(fileName);
			fileName = results[0];
			String filePath = results[1];
			download = new Download(id, url.toString(), fileName, filePath, etag, lastModified, contentType,
					contentLength, isMultiConnection);
			con.disconnect();
		}
		return download;
	}

	private boolean isMultiConnection(URL url, long contentLength) {
		return supportRanges(url) && contentLength > GlobalConstants.MAX_CONNECTION;
	}

	private boolean supportRanges(URL url) {
		boolean result = false;
		HttpURLConnection rangeCon = null;
		try {
			rangeCon = (HttpURLConnection) url.openConnection();
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

	private String[] validateFileNameRecursive(String fileName) {
		String name = "";
		String ext = "";
		String filePath = Paths.get(folderPath, fileName).toString();
		String time = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		if (DownloadService.getInstance().downloadFileExists(filePath)) {
			int pointIndex = fileName.lastIndexOf('.');
			if (pointIndex > -1) {
				name = fileName.substring(0, pointIndex);
				ext = fileName.substring(pointIndex, fileName.length());
				fileName = name + "_" + time + ext;
				return validateFileNameRecursive(fileName);
			} else {
				return validateFileNameRecursive(fileName + "_" + time);
			}
		}
		return new String[] { fileName, filePath };
	}
}
