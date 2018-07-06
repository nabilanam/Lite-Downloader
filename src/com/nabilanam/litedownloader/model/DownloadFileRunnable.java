package com.nabilanam.litedownloader.model;

import com.nabilanam.litedownloader.controller.TableController;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

/**
 *
 * @author nabil
 */
public class DownloadFileRunnable implements Runnable
{
    private final int MAX_THREAD = 4;
    private final ExecutorService es;
    private final Download download;
    private final TableController tableController;
    private final ArrayList<Path> tmpPaths;

    public DownloadFileRunnable(TableController tableController, Download download)
    {
        this.es = Executors.newFixedThreadPool(MAX_THREAD);
        this.download = download;
        this.tableController = tableController;
        this.tmpPaths = new ArrayList<>();
        populateTmpPaths();
    }

    @Override
    public void run()
    {
        if (download.getFilePath().toFile().exists())
        {
            if (!getConfirmationFileDelete())
            {
                download.setDownloadStatus(DownloadStatus.Stopped);
                tableController.fireStatusColumnUpdated(download.getDId());
                return;
            }
        }
        if (getStatus() == HttpURLConnection.HTTP_NOT_MODIFIED)
        {
            boolean supportRanges = supportRanges();
            if (supportRanges && download.getContentLength() > MAX_THREAD)
            {
                CountDownLatch cdl = new CountDownLatch(MAX_THREAD);
                long size = download.getContentLength() / MAX_THREAD;
                long start, end = -1;
                Path path;
                System.out.println("");
                for (int a = 0; a < MAX_THREAD; a++)
                {
                    path = tmpPaths.get(a);
                    start = end + 1;
                    end = (a == (MAX_THREAD - 1)) ? download.getContentLength() : (start + size);
                    start += path.toFile().length();
                    if (end - start > 0)
                    {
                        es.submit(new DownloadRunnable(cdl, path, start, end, download, tableController));
                    }
                    else
                    {
                        cdl.countDown();
                    }
                }
                try
                {
                    cdl.await();
                }
                catch (InterruptedException e)
                {
                    es.shutdownNow();
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException ex)
                    {
                        Logger.getLogger(DownloadFileRunnable.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return;
                }
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException ex)
                {
                    Logger.getLogger(DownloadFileRunnable.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (download.getContentLength() == download.getDownloadedLength())
                {
                    mergeFiles();
                }
                else
                {
                    for (Path tmpPath : tmpPaths)
                    {
                        try
                        {
                            Files.deleteIfExists(tmpPath);
                        }
                        catch (IOException ex)
                        {
                            Logger.getLogger(DownloadFileRunnable.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    tableController.showErrorMessageThreadSafe("File size mismatch!");
                    download.setDownloadStatus(DownloadStatus.Error);
                    tableController.fireStatusColumnUpdatedThreadSafe(download.getDId());
                }
            }
            else
            {
                System.out.println("single");
                CountDownLatch cdl = new CountDownLatch(1);
                es.submit(new DownloadRunnable(cdl, download, tableController));
                try
                {
                    cdl.await();
                }
                catch (InterruptedException e)
                {
                    es.shutdownNow();
                }
            }
            es.shutdown();
        }
    }

    private boolean getConfirmationFileDelete()
    {
        boolean confirm = tableController.showConfirmDialogThreadSafe("File already exists.\nDelete file and proceed?");
        if (confirm)
        {
            try
            {
                Files.deleteIfExists(download.getFilePath());
            }
            catch (IOException ex)
            {
                tableController.showErrorMessageThreadSafe("Unable to delete " + download.getFilePath().toFile());
            }
        }
        return confirm;
    }

    private boolean supportRanges()
    {
        boolean result = false;
        HttpURLConnection rangeCon = null;
        try
        {
            rangeCon = (HttpURLConnection) download.getUrl().openConnection();
            rangeCon.setInstanceFollowRedirects(true);
            rangeCon.setRequestMethod("GET");
            rangeCon.setRequestProperty("Range", "bytes=0-10");
            rangeCon.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
            if (rangeCon.getResponseCode() == HttpURLConnection.HTTP_PARTIAL)
            {
                result = true;
            }
            rangeCon.disconnect();
        }
        catch (IOException ex)
        {
            if (rangeCon != null)
            {
                rangeCon.disconnect();
            }
            Logger.getLogger(DownloadFileRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    private int getStatus()
    {
        int responseCode = 0;
        HttpURLConnection con = null;
        try
        {
            con = (HttpURLConnection) download.getUrl().openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("If-None-Match", download.getEtag());
            con.setRequestProperty("If-Modified-Since", download.getLastModified());
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
            responseCode = con.getResponseCode();
            con.disconnect();
            return responseCode;
        }
        catch (IOException ex)
        {
            tableController.showErrorMessageThreadSafe("Connection problem!");
            if (con != null)
            {
                con.disconnect();
            }
            return responseCode;
        }
    }

    private void mergeFiles()
    {
        if (download.getFilePath().toFile().exists())
        {
            try
            {
                Files.deleteIfExists(download.getFilePath());
            }
            catch (IOException ex)
            {
                tableController.showErrorMessageThreadSafe("Can't delete existing file!");
            }
        }
        try (FileOutputStream fos = new FileOutputStream(download.getFilePath().toFile()))
        {
            for (Path tmpPath : tmpPaths)
            {
                try (FileInputStream fis = new FileInputStream(tmpPath.toFile()))
                {
                    int bytesRead;
                    byte[] buffer = new byte[4096];
                    while ((bytesRead = fis.read(buffer)) > -1)
                    {
                        if (Thread.interrupted())
                        {
                            tableController.showErrorMessageThreadSafe("File merging process interrupted");
                        }
                        fos.write(buffer, 0, bytesRead);
                        fos.flush();
                    }
                }
            }
        }
        catch (FileNotFoundException ex)
        {
            tableController.showErrorMessageThreadSafe("File merging error!\nFile Not Found Exception");
            return;
        }
        catch (IOException ex)
        {
            tableController.showErrorMessageThreadSafe("File merging error!\nI/O Exception");
            return;
        }
        for (Path tmpPath : tmpPaths)
        {
            try
            {
                Files.deleteIfExists(tmpPath);
            }
            catch (IOException ex)
            {
                Logger.getLogger(DownloadFileRunnable.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        download.setDownloadStatus(DownloadStatus.Completed);
        tableController.fireStatusColumnUpdatedThreadSafe(download.getDId());
    }

    private void populateTmpPaths()
    {
        for (int a = 0; a < MAX_THREAD; a++)
        {
            tmpPaths.add(Paths.get(download.getFilePath().toString() + a));
        }
    }
}