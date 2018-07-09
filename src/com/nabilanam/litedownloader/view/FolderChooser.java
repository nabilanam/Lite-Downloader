package com.nabilanam.litedownloader.view;

import java.awt.Window;
import java.io.IOException;

import javax.swing.JFileChooser;

/**
 *
 * @author nabil
 */
public class FolderChooser {
	
	private final JFileChooser folderChooser;

	public FolderChooser() {
		folderChooser = new JFileChooser();
		folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		folderChooser.setAcceptAllFileFilterUsed(false);
	}

	public String showFolderChooser(Window parent, String title) throws IOException {
		if (folderChooser.showDialog(parent, title) == JFileChooser.APPROVE_OPTION) {
			return folderChooser.getSelectedFile().getCanonicalPath();
		}
		return null;
	}
}
