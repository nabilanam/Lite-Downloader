package com.nabilanam.litedownloader.view;

import com.nabilanam.litedownloader.controller.TableController;
import com.nabilanam.litedownloader.model.DownloadStatus;
import com.nabilanam.litedownloader.model.MenuItem;
import java.awt.event.ActionEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author nabil
 */
@SuppressWarnings("serial")
public final class TablePopupMenu extends JPopupMenu
{
    private final Table table;
    private final JMenuItem start;
    private final JMenuItem pause;
    private final JMenuItem stop;
    private final JMenuItem removeLink;
    private final JMenuItem removeFile;
    private final JMenuItem launchFile;
    private final JMenuItem launchFolder;
    private final TableController tableController;

    public TablePopupMenu(Table table, TableController controller)
    {
        this.table = table;
        this.tableController = controller;

        start = new JMenuItem(MenuItem.START.getText());
        pause = new JMenuItem(MenuItem.PAUSE.getText());
        stop = new JMenuItem(MenuItem.STOP.getText());
        removeLink = new JMenuItem(MenuItem.REMOVE_LINK.getText());
        removeFile = new JMenuItem(MenuItem.REMOVE_FILE.getText());
        launchFile = new JMenuItem(MenuItem.LAUNCH_FILE.getText());
        launchFolder = new JMenuItem(MenuItem.LAUNCH_FOLDER.getText());
        
        setListeners();
        
        add(start);
        add(pause);
        add(stop);
        add(removeLink);
        add(removeFile);
        add(launchFile);
        add(launchFolder);
    }

    void show(int x, int y)
    {
        this.show(table, x, y);
    }

    public void updateItems(DownloadStatus status)
    {
        switch (status)
        {
            case Error:
                start.setText("Start");
                
                start.setEnabled(false);
                pause.setEnabled(false);
                stop.setEnabled(false);
                removeLink.setEnabled(true);
                removeFile.setEnabled(false);
                launchFile.setEnabled(false);
                launchFolder.setEnabled(true);
                break;
            case Paused:
                start.setText("Resume");
                
                start.setEnabled(true);
                pause.setEnabled(false);
                stop.setEnabled(false);
                removeLink.setEnabled(true);
                removeFile.setEnabled(true);
                launchFile.setEnabled(false);
                launchFolder.setEnabled(true);
                break;
            case Stopped:
                start.setText("Start");
                
                start.setEnabled(true);
                pause.setEnabled(false);
                stop.setEnabled(false);
                removeLink.setEnabled(true);
                removeFile.setEnabled(true);
                launchFile.setEnabled(false);
                launchFolder.setEnabled(true);
                break;
            case Completed:
                start.setText("Resume");
                
                start.setEnabled(false);
                pause.setEnabled(false);
                stop.setEnabled(false);
                removeLink.setEnabled(true);
                removeFile.setEnabled(true);
                launchFile.setEnabled(true);
                launchFolder.setEnabled(true);
                break;
            case Downloading:
                start.setText("Resume");
                
                start.setEnabled(false);
                pause.setEnabled(true);
                stop.setEnabled(true);
                removeLink.setEnabled(false);
                removeFile.setEnabled(false);
                launchFile.setEnabled(false);
                launchFolder.setEnabled(true);
                break;
        }
    }

    private void setListeners()
    {
        start.addActionListener((ActionEvent e) ->
        {
            tableController.startDownload(table.getDid());
        });
        pause.addActionListener((ActionEvent e) ->
        {
            tableController.pauseDownload(table.getDid());
        });
        stop.addActionListener((ActionEvent e) ->
        {
            tableController.stopDownload(table.getDid());
        });
        removeLink.addActionListener((ActionEvent e) ->
        {
            tableController.removeDownloadLink(table.getDid());
        });
        removeFile.addActionListener((ActionEvent e) ->
        {
            tableController.removeDownloadFile(table.getDid());
        });
        launchFile.addActionListener((ActionEvent e) ->
        {
            tableController.launchFile(table.getDid());
        });
        launchFolder.addActionListener((ActionEvent e) ->
        {
            tableController.launchFolder(table.getDid());
        });
    }
}
