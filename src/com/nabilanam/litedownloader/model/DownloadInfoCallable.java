package com.nabilanam.litedownloader.model;

import com.nabilanam.litedownloader.controller.TopbarController;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

/**
 *
 * @author nabil
 */
public class DownloadInfoCallable implements Callable<Download>
{
    private final int id;
    private final String link;
    private final String folderPath;
    private final TopbarController tableController;

    public DownloadInfoCallable(TopbarController tbc, int id, String link, String folderPath)
    {
        this.id = id;
        this.tableController = tbc;
        this.link = link;
        this.folderPath = folderPath;
    }

    @Override
    public Download call()
    {
        URL url;
        Download download = null;
        try
        {
            url = new URL(link);
        }
        catch (MalformedURLException ex)
        {
            tableController.showErrorMessageThreadSafe(Messages.ERROR_URL_MALFORMED);
            return download;
        }
        try
        {
            url.toURI();
        }
        catch (URISyntaxException ex)
        {
            tableController.showErrorMessageThreadSafe(Messages.ERROR_URL_SYNTAX);
            return download;
        }
        HttpURLConnection con;
        try
        {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
        }
        catch (ProtocolException ex)
        {
            tableController.showErrorMessageThreadSafe(Messages.ERROR_URL_HTTP_PROTOCOL_EXCEPTION);
            return download;
        }
        catch (IOException ex)
        {
            tableController.showErrorMessageThreadSafe(Messages.ERROR_IO_EXCEPTION);
            return download;
        }
        int responseCode;
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
        try
        {
            con.connect();
            responseCode = con.getResponseCode();
        }
        catch (IOException ex)
        {
            tableController.showErrorMessageThreadSafe(Messages.ERROR_IO_EXCEPTION);
            return download;
        }
        if (responseCode == HttpURLConnection.HTTP_OK)
        {
            String etag = con.getHeaderField("ETag");
            String lastModified = con.getHeaderField("Last-Modified");
            String contentType = con.getHeaderField("Content-Type");
            long contentLength = con.getContentLengthLong();
            String contentDisposition = con.getHeaderField("Content-Disposition");
            String fileName;
            if (contentDisposition != null && contentDisposition.contains("="))
            {
                fileName = contentDisposition.split("=")[1].replaceAll("\"", "");
            }
            else
            {
                fileName = url.getPath().substring(url.getPath().lastIndexOf('/') + 1,
                        url.getPath().length()).replaceAll("%20", " ");
            }
            Path filePath = Paths.get(folderPath, fileName);
            download = new Download(id, url.toString(), fileName, filePath.toString(), etag, lastModified, contentType, contentLength);
            con.disconnect();
        }
        return download;
    }
}
