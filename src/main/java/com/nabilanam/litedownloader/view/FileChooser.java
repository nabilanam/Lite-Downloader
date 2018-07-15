package com.nabilanam.litedownloader.view;

import java.awt.Window;
import java.io.IOException;

import javax.swing.JFileChooser;

/**
 *
 * @author nabil
 */
public class FileChooser {
	
	private final JFileChooser folderChooser;

	public FileChooser(int jFileChooserMode) {
		folderChooser = new JFileChooser();
		folderChooser.setFileSelectionMode(jFileChooserMode);
		folderChooser.setAcceptAllFileFilterUsed(false);
	}

	public String getPathFromUser(Window parent, String btnTxt) throws IOException {
		if (folderChooser.showDialog(parent, btnTxt) == JFileChooser.APPROVE_OPTION) {
			return folderChooser.getSelectedFile().getCanonicalPath();
		}
		return null;
	}
}
