package com.nabilanam.litedownloader.view;

import com.nabilanam.litedownloader.controller.TableController;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author nabil
 */
@SuppressWarnings("serial")
public final class TablePanel extends JPanel
{
    public TablePanel(TableController tableController)
    {
        super();
        setLayout(new BorderLayout());
        add(new JScrollPane(new Table(tableController)), BorderLayout.CENTER);
    }
}
