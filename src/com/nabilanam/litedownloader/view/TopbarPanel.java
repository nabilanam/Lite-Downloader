package com.nabilanam.litedownloader.view;

import com.nabilanam.litedownloader.controller.TopbarController;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.*;

/**
 *
 * @author nabil
 */
@SuppressWarnings("serial")
public final class TopbarPanel extends JPanel
{
    private final JButton addLabel;
    private final JButton removeLabel;
    private final JButton optionLabel;
    private final OptionDialog optionDialog;
    private final JFrame parent;

    public TopbarPanel(JFrame parent, TopbarController tbc)
    {
        this.parent = parent;
        optionDialog = new OptionDialog(parent);
        optionDialog.setFolderPath(tbc.getFolderPath());

        addLabel = new JButton("Add");
        removeLabel = new JButton("Remove");
        optionLabel = new JButton("Options");

        setLayout(new FlowLayout(FlowLayout.CENTER));
        setBorder(BorderFactory.createEtchedBorder());
        setBackground(Color.white);

        add(addLabel);
        add(removeLabel);
        add(optionLabel);

        addLabel.addActionListener((ActionEvent e) ->
        {
            String link;
            if ((link = tbc.showInputDialog("Enter download url")) != null)
            {
                if (!link.trim().isEmpty())
                {
                    tbc.addDownload(link);
                }
                else
                {
                    tbc.showErrorMessage("Empty link");
                }
            }
        });
        optionLabel.addActionListener((ActionEvent e) ->
        {
            optionDialog.setVisible(true);
        });
        optionDialog.setActionListener((ActionEvent e) ->
        {
            String folderPath = null;
            try
            {
                folderPath = optionDialog.launch();
            }
            catch (IOException ex)
            {
                tbc.showErrorMessage("Dialog launch error");
            }
            if (folderPath != null)
            {
                optionDialog.setFolderPath(folderPath);
                tbc.saveFolderPath(folderPath);
            }
        });
    }
}
