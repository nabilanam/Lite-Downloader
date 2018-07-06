package com.nabilanam.litedownloader.view;

import com.nabilanam.litedownloader.controller.LeftbarController;
import com.nabilanam.litedownloader.controller.TableController;
import com.nabilanam.litedownloader.controller.TopbarController;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;

/**
 *
 * @author nabil
 */
@SuppressWarnings("serial")
public final class MainFrame extends JFrame
{
    private final TableController tableController;
    
    public MainFrame(TopbarController topBarController, LeftbarController leftbarController, TableController tableController)
    {
        super();
        this.tableController = tableController;
        Dimension size = new Dimension(800, 600);
        PaneGenerator paneGenerator = new PaneGenerator(this);
        topBarController.setPaneGenerator(paneGenerator);
        tableController.setPaneGenerator(paneGenerator);
        topBarController.setAddLinkListener((int did) ->
        {
            tableController.fireNewRowInserted(did);
        });
        leftbarController.setNodeSelectionListener((status) ->
        {
            tableController.fireFilterChanged(status);
        });
        
        LeftbarPanel leftbarPanel = new LeftbarPanel(leftbarController);
        TopbarPanel topbarPanel = new TopbarPanel(this, topBarController);
        TablePanel tablePanel = new TablePanel(tableController);

        setLayout(new BorderLayout());
        add(topbarPanel, BorderLayout.NORTH);
        add(leftbarPanel, BorderLayout.WEST);
        add(tablePanel, BorderLayout.CENTER);

        setSize(size);
        setVisible(true);
        setMinimumSize(size);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public synchronized void addWindowListener(WindowListener l)
    {
        super.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                setVisible(false);
                tableController.dieNow();
                System.exit(0);
            }
        });
    }

}
