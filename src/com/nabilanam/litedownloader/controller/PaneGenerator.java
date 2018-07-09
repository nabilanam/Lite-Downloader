package com.nabilanam.litedownloader.controller;

import javax.swing.JFrame;

import com.nabilanam.litedownloader.view.Pane;

/**
 *
 * @author nabil
 */
public class PaneGenerator {
	
	private static Pane pane;
	
	public static void Init(JFrame parent) {
		pane = new Pane(parent, "Lite Downloader");
	}
	
	public static boolean showConfirmDialog(String message) {
		return pane.showConfirmDialog(message);
	}

	public static boolean showConfirmDialogInvokeLater(String message) {
		return pane.showConfirmDialogInvokeLater(message);
	}

	public static void showErrorMessage(String message) {
		pane.showErrorMessage(message);
	}

	public static void showErrorMessageInvokeLater(String message) {
		pane.showErrorMessageInvokeLater(message);
	}

	public static void showInformationMessage(String message) {
		pane.showInformationMessage(message);
	}

	public static String showInputDialog(String message) {
		return pane.showInputDialog(message);
	}
}
