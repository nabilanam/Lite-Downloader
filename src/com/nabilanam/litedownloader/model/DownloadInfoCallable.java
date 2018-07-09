package com.nabilanam.litedownloader.model;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import com.nabilanam.litedownloader.controller.DownloadService;
import com.nabilanam.litedownloader.controller.PaneGenerator;
import com.sun.xml.internal.ws.util.StringUtils;

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
			String fileName;
			if (contentDisposition != null && contentDisposition.contains("=")) {
				fileName = contentDisposition.split("=")[1].replaceAll("\"", "");
			} else {
				fileName = url.getPath().substring(url.getPath().lastIndexOf('/') + 1, url.getPath().length())
						.replaceAll("%20", " ");
			}
			String[] results = getValidatedFileNamePathRecursive(fileName, 0);
			fileName = results[0];
			String filePath = results[1];
			download = new Download(id, url.toString(), fileName, filePath, etag, lastModified, contentType,
					contentLength);
			con.disconnect();
		}
		return download;
	}

	private String[] getValidatedFileNamePathRecursive(String fileName, int i) {
		String filePath = Paths.get(folderPath, fileName).toString();
		if (!DownloadService.getInstance().downloadFileExists(filePath)) {
			return new String[] { fileName, filePath };
		} else {
			int pointIndex = fileName.lastIndexOf('.');
			int underscoreIndex = fileName.lastIndexOf('_');
			if (((underscoreIndex + 2) == pointIndex) && Character.isDigit(fileName.charAt(underscoreIndex + 1))) {
				i = Integer.parseInt(fileName.substring(underscoreIndex + 1, pointIndex)) + 1;
				fileName = fileName.substring(0, underscoreIndex + 1) + i + fileName.substring(pointIndex);
			} else {
				fileName = fileName.substring(0, pointIndex) + "_" + (++i) + fileName.substring(pointIndex);
			}
			return getValidatedFileNamePathRecursive(fileName, i);
		}
	}
}
