package com.nabilanam.litedownloader.view;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author nabil
 */
public class PaneGenerator
{
    private final String APP_NAME = "Download Manager";
    private final JFrame parent;
    private final InputDialog inputDialog;

    public PaneGenerator(JFrame parent)
    {
        this.parent = parent;
        this.inputDialog = new InputDialog(parent);
    }

    public void showErrorMessage(String message)
    {
        JOptionPane.showMessageDialog(parent,
                message,
                APP_NAME,
                JOptionPane.ERROR_MESSAGE);
    }

    public void showInformationMessage(String message)
    {
        JOptionPane.showMessageDialog(parent,
                message,
                APP_NAME,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public String showInputDialog(String message)
    {
        return inputDialog.getInput();
    }

    public boolean showConfirmDialog(String message)
    {
        return JOptionPane.showConfirmDialog(parent, message, APP_NAME, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
