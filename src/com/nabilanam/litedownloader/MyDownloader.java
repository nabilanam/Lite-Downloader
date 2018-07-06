package com.nabilanam.litedownloader;

import com.nabilanam.litedownloader.controller.LeftbarController;
import com.nabilanam.litedownloader.controller.TableController;
import com.nabilanam.litedownloader.controller.TopbarController;
import com.nabilanam.litedownloader.view.MainFrame;

/**
 *
 * @author nabil
 */
class MyDownloader
{
    public static void main(String[] args)
    {
        TopbarController topbarController = new TopbarController();
        LeftbarController leftbarController = new LeftbarController();
        TableController tableController = new TableController();
        MainFrame mainFrame = new MainFrame(topbarController,leftbarController,tableController);
        mainFrame.setVisible(true);
    }
} 
