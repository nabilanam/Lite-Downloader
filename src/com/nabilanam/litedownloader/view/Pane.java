package com.nabilanam.litedownloader.view;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.nabilanam.litedownloader.controller.TableController;

/**
 *
 * @author nabil
 */
public class Pane {
	
	private String title;
	private JFrame parent;
	private InputDialog inputDialog;

	public Pane(JFrame parent, String title) {
		this.title = title;
		this.parent = parent;
		this.inputDialog = new InputDialog(parent, title);
	}

	public boolean showConfirmDialog(String message) {
		return JOptionPane.showConfirmDialog(parent, message, title,
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}

	public boolean showConfirmDialogInvokeLater(String message) {
		boolean result = false;
		RunnableFuture<Boolean> rf = new FutureTask<>(() -> {
			return showConfirmDialog(message);
		});
		SwingUtilities.invokeLater(rf);
		try {
			result = rf.get();
		} catch (InterruptedException | ExecutionException ex) {
			Logger.getLogger(TableController.class.getName()).log(Level.SEVERE, null, ex);
		}
		return result;
	}

	public void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
	}

	public void showErrorMessageInvokeLater(String message) {
		SwingUtilities.invokeLater(() -> {
			showErrorMessage(message);
		});
	}

	public void showInformationMessage(String message) {
		JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	public String showInputDialog(String message) {
		return inputDialog.getInput();
	}
}
