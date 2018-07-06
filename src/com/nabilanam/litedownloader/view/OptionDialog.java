package com.nabilanam.litedownloader.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author nabil
 */
@SuppressWarnings("serial")
public final class OptionDialog extends JDialog
{
    private final JLabel folderLabel;
    private final JButton folderChooseButton;
    private final JTextField folderTextField;
    private final FolderChooser folderChooser;

    public OptionDialog(JFrame parent)
    {
        Dimension dialogDimension = new Dimension(500, 100);
        Dimension buttonDimension = new Dimension(24, 21);

        folderChooser = new FolderChooser();
        folderTextField = new JTextField(20);
        folderChooseButton = new JButton("...");
        folderLabel = new JLabel("Download Location: ");

        folderTextField.setEditable(false);
        folderChooseButton.setSize(buttonDimension);
        folderChooseButton.setPreferredSize(buttonDimension);

        setTitle("Options");
        layoutComponents();
        setMinimumSize(dialogDimension);
        setPreferredSize(dialogDimension);
        setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
        setLocationRelativeTo(parent);
    }

    void setFolderPath(String path)
    {
        folderTextField.setText(path);
    }

    void setActionListener(ActionListener listener)
    {
        folderChooseButton.addActionListener(listener);
    }

    String getFolderPath()
    {
        return folderTextField.getText();
    }

    String launch() throws IOException
    {
        return folderChooser.showFolderChooser(OptionDialog.this, "Select Folder");
    }

    private void layoutComponents()
    {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        Insets labelInsets = new Insets(0, 0, 0, 5);
        Insets emptyInsets = new Insets(0, 0, 0, 0);

        //First row
        gc.gridy = 0;

        //nameLabel
        gc.gridx = 0;

        gc.weightx = 0.1;
        gc.weighty = 0.1;

        gc.insets = emptyInsets;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        add(folderLabel, gc);

        //nameTextField
        gc.gridx = 1;

        gc.weightx = 0.1;
        gc.weighty = 0.1;

        gc.insets = labelInsets;
        gc.anchor = GridBagConstraints.LINE_END;
        add(folderTextField, gc);

        //choose button
        gc.gridx = 2;

        gc.weightx = 0.1;
        gc.weighty = 0.1;

        gc.insets = emptyInsets;
        gc.anchor = GridBagConstraints.LINE_START;
        add(folderChooseButton, gc);
    }
}
