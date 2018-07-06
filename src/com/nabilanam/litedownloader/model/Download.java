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
public class Download implements Serializable
{
    private static final long serialVersionUID = 6030894333031168823L;

    private int did;
    private final String url;
    private final String etag;
    private final String filePath;
    private final String fileName;
    private final String contentType;
    private final String lastModified;
    private final long contentLength;
    private long downloadedLength;
    private DownloadStatus downloadStatus;

    public Download(int did, String url, String fileName, String filePath, String etag, String lastModified, String contentType, long contentLength)
    {
        this.did = did;
        this.url = url;
        this.etag = etag;
        this.lastModified = lastModified;
        this.fileName = fileName;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.filePath = filePath;
        this.downloadedLength = 0;
        this.downloadStatus = DownloadStatus.Stopped;
    }
    
    public synchronized void addToDownloadedLengthThreadSafe(int length)
    {
        downloadedLength += length;
    }

    public int getDId()
    {
        return did;
    }

    public void setDId(int did)
    {
        this.did = did;
    }

    public URL getUrl()
    {
        try
        {
            return new URL(url);
        }
        catch (MalformedURLException ex)
        {
            Logger.getLogger(Download.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getFileName()
    {
        return fileName;
    }

    public long getContentLength()
    {
        return contentLength;
    }

    public Path getFilePath()
    {
        return Paths.get(filePath);
    }

    public long getDownloadedLength()
    {
        return downloadedLength;
    }

    public void setDownloadedLength(long length)
    {
        downloadedLength = length;
    }

    public DownloadStatus getDownloadStatus()
    {
        return downloadStatus;
    }

    public void setDownloadStatus(DownloadStatus downloadStatus)
    {
        this.downloadStatus = downloadStatus;
    }

    public String getContentType()
    {
        return contentType;
    }

    public float getPercantage()
    {
        return (float) downloadedLength / (float) contentLength;
    }

    public String getEtag()
    {
        return etag;
    }

    public String getLastModified()
    {
        return lastModified;
    }

    public String getLengthString()
    {
        return ByteRepresentation.Represent(contentLength);
    }

    public String getDownloadedLengthString()
    {
        return ByteRepresentation.Represent(downloadedLength);
    }
}
