package com.nabilanam.litedownloader.controller;

import com.nabilanam.litedownloader.model.Database;
import com.nabilanam.litedownloader.model.Download;
import com.nabilanam.litedownloader.model.DownloadInfoCallable;
import com.nabilanam.litedownloader.model.FileUtil;
import com.nabilanam.litedownloader.model.Messages;
import com.nabilanam.litedownloader.view.PaneGenerator;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.SwingUtilities;

/**
 *
 * @author nabil
 */
public class TopbarController
{
    private final ExecutorService es;
    private final Database database;
    private AddLinkListener addLinkListener;
    private PaneGenerator paneGenerator;
    private int TIMEOUT = 30;

    public TopbarController()
    {
        this.es = Executors.newCachedThreadPool();
        this.database = Database.getInstance();
    }

    public void setPaneGenerator(PaneGenerator paneGenerator)
    {
        this.paneGenerator = paneGenerator;
    }

    public void setAddLinkListener(AddLinkListener listener)
    {
        this.addLinkListener = listener;
    }

    public void addDownload(String link)
    {
        int id = database.getNewDId();
        Future<Download> future = es.submit(new DownloadInfoCallable(this, id, link, getFolderPath()));
        try
        {
            Download download;
            if ((download = future.get(TIMEOUT, TimeUnit.SECONDS)) != null)
            {
                database.add(download);
                addLinkListener.linkAdded(id);
            }
        }
        catch (InterruptedException ex)
        {
            showErrorMessage(Messages.ERROR_LINK_CHECK_INTERRUPTED);
        }
        catch (ExecutionException ex)
        {
            showErrorMessage(Messages.ERROR_LINK_CHECK_EXCEPTION);
        }
        catch (TimeoutException ex)
        {
            showErrorMessage(Messages.ERROR_LINK_CHECK_TIMEOUT);
        }
    }

    public String getFolderPath()
    {
        return database.getFileDirectory();
    }

    public void saveFolderPath(String folderPath)
    {
        try
        {
            database.setFileDirectory(folderPath);
            FileUtil.serialize(database);
        }
        catch (IOException ex)
        {
            showErrorMessage(Messages.ERROR_FOLDER_PATH_COULD_NOT_BE_SAVED);
        }
    }

    public String showInputDialog(String message)
    {
        if (paneGenerator != null)
        {
            return paneGenerator.showInputDialog(message);
        }
        return null;
    }

    public void showErrorMessage(String message)
    {
        if (paneGenerator != null)
        {
            paneGenerator.showErrorMessage(message);
        }
    }

    public void showErrorMessageThreadSafe(String message)
    {
        if (paneGenerator != null)
        {
            SwingUtilities.invokeLater(() ->
            {
                paneGenerator.showErrorMessage(message);
            });
        }
    }
}
