package com.nabilanam.litedownloader.controller;

import com.nabilanam.litedownloader.model.Column;
import com.nabilanam.litedownloader.model.Database;
import com.nabilanam.litedownloader.model.Download;
import com.nabilanam.litedownloader.model.DownloadFileRunnable;
import com.nabilanam.litedownloader.model.DownloadStatus;
import com.nabilanam.litedownloader.model.FileUtil;
import com.nabilanam.litedownloader.model.Messages;
import com.nabilanam.litedownloader.model.TableModel;
import com.nabilanam.litedownloader.view.PaneGenerator;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author nabil
 */
public class TableController
{
    private final ExecutorService es;
    private final Database database;
    private final TableModel tableModel;
    private final HashMap<Integer, Future> hashMap;
    private PaneGenerator paneGenerator;

    public TableController()
    {
        hashMap = new HashMap<>();
        tableModel = new TableModel();
        this.database = Database.getInstance();
        this.es = Executors.newCachedThreadPool();
        tableModel.setFileList(database.getDownloads());
    }

    public void startDownload(int did)
    {
        hashMap.remove(did);
        Download download = database.get(did);
        hashMap.put(did, es.submit(new DownloadFileRunnable(this, download)));
        download.setDownloadStatus(DownloadStatus.Downloading);
        fireStatusColumnUpdated(did);
    }

    public void pauseDownload(int did)
    {
        cancelDownload(hashMap.remove(did));
        database.get(did)
                .setDownloadStatus(DownloadStatus.Paused);
        fireStatusColumnUpdated(did);
    }

    public void pauseDownload(int did, Future future)
    {
        cancelDownload(future);
        database.get(did)
                .setDownloadStatus(DownloadStatus.Paused);
        fireStatusColumnUpdated(did);
    }

    public void stopDownload(int did)
    {
        cancelDownload(hashMap.remove(did));
        database.get(did)
                .setDownloadStatus(DownloadStatus.Stopped);
        fireStatusColumnUpdated(did);
    }

    public void removeDownloadLink(int did)
    {
        hashMap.remove(did);
        database.remove(did);
        saveDatabase();
        tableModel.fireTableRowsDeleted(did, did);
    }

    public void removeDownloadFile(int did)
    {
        try
        {
            Download download = database.get(did);
            
            for (int a = 0; a < 4; a++)
            {
                Files.deleteIfExists(Paths.get(download.getFilePath().toString() + a));
            }
            Files.deleteIfExists(database.get(did).getFilePath());
        }
        catch (IOException ex)
        {
            showErrorMessage(Messages.ERROR_FILE_CAN_NOT_BE_DELETED);
        }
        removeDownloadLink(did);
    }

    public void launchFile(int row)
    {
        try
        {
        	File file = database.get(row).getFilePath().toFile();
        	if (file.exists()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(file);
			}
        	else {
        		showErrorMessage(Messages.ERROR_FILE_DOES_NOT_EXIST);
        	}
        }
        catch (IOException ex)
        {
            showErrorMessage(Messages.ERROR_PLATFORM_NOT_SUPPORTED);
        }
    }

    public void launchFolder(int row)
    {
        try
        {
        	File file = database.get(row).getFilePath().toFile().getParentFile();
            if (file.exists()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(file);
			}
        	else {
        		showErrorMessage(Messages.ERROR_FOLDER_DOES_NOT_EXIST);
        	}
        }
        catch (IOException ex)
        {
            showErrorMessage(Messages.ERROR_PLATFORM_NOT_SUPPORTED);
        }
    }

    private void cancelDownload(Future future)
    {
        if (future != null)
        {
            future.cancel(true);
        }
        try
        {
            Thread.sleep(50);
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(TableController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void saveDatabase()
    {
        try
        {
            FileUtil.serialize(database);
        }
        catch (IOException ex)
        {
            showErrorMessage(Messages.ERROR_DB_CHANGE_COULD_NOT_BE_SAVED);
        }
    }

    public void fireFilterChanged(DownloadStatus status)
    {
        tableModel.changeFilter(status);
    }

    public void fireNewRowInserted(int id)
    {
        tableModel.fireTableRowsInserted(id, id);
        saveDatabase();
    }

    public synchronized void fireDoneColumnUpdatedThreadSafe(int row)
    {
        tableModel.fireTableCellUpdated(row, Column.DONE.getId());
    }

    public synchronized void fireDownloadedColumnUpdatedThreadSafe(int row)
    {
        tableModel.fireTableCellUpdated(row, Column.DOWNLOADED.getId());
    }

    public void fireStatusColumnUpdated(int row)
    {
        tableModel.fireTableCellUpdated(row, Column.STATUS.getId());
        saveDatabase();
    }

    public synchronized void fireStatusColumnUpdatedThreadSafe(int row)
    {
        tableModel.fireTableCellUpdated(row, Column.STATUS.getId());
        saveDatabase();
    }

    public TableModel getTableModel()
    {
        return tableModel;
    }

    public DownloadStatus getDownloadStatus(int did)
    {
        return database.get(did).getDownloadStatus();
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

    public boolean showConfirmDialogThreadSafe(String message)
    {
        boolean result = false;
        RunnableFuture<Boolean> rf = new FutureTask<>(() ->
        {
            return paneGenerator.showConfirmDialog(message);
        });
        if (paneGenerator != null)
        {
            SwingUtilities.invokeLater(rf);
            try
            {
                result = rf.get();
            }
            catch (InterruptedException | ExecutionException ex)
            {
                Logger.getLogger(TableController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    public void setPaneGenerator(PaneGenerator paneGenerator)
    {
        this.paneGenerator = paneGenerator;
    }

    public void dieNow()
    {
        Set<Map.Entry<Integer, Future>> entrySet = hashMap.entrySet();
        entrySet.forEach((entry) ->
        {
            pauseDownload(entry.getKey(), entry.getValue());
        });
    }
}
