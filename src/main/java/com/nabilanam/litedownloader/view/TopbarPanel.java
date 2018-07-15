package com.nabilanam.litedownloader.view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.nabilanam.litedownloader.controller.PaneGenerator;
import com.nabilanam.litedownloader.controller.TopbarController;
import com.nabilanam.litedownloader.model.PaneMessages;

/**
 *
 * @author nabil
 */
@SuppressWarnings("serial")
public final class TopbarPanel extends JPanel {
	
	private final JButton addLabel;
	private final JButton removeLabel;
	private final JButton optionLabel;
	private final OptionDialog optionDialog;

	public TopbarPanel(JFrame parent, TopbarController topbarController) {
		optionDialog = new OptionDialog(parent);
		optionDialog.setFolderPath(topbarController.getFolderPath());

		addLabel = new JButton("Add");
		removeLabel = new JButton("Remove");
		optionLabel = new JButton("Options");

		setLayout(new FlowLayout(FlowLayout.CENTER));
		setBorder(BorderFactory.createEtchedBorder());
		setBackground(Color.white);

		add(addLabel);
		add(removeLabel);
		add(optionLabel);

		setListeners(topbarController);
	}

	private void setListeners(TopbarController topbarController) {
		addLabel.addActionListener((ActionEvent e) -> {
			String link;
			if ((link = PaneGenerator.showInputDialog(PaneMessages.INPUT_GUI_DOWNLOAD_URL)) != null) {
				if (!link.trim().isEmpty()) {
					topbarController.addDownload(link);
				} else {
					PaneGenerator.showErrorMessage(PaneMessages.ERROR_GUI_EMPTY_DOWNLOAD_LINK);
				}
			}
		});
		removeLabel.addActionListener((ActionEvent e) -> {
			topbarController.removeDownload();
		});
		optionLabel.addActionListener((ActionEvent e) -> {
			optionDialog.setVisible(true);
		});
		optionDialog.setActionListener((ActionEvent e) -> {
			String folderPath = null;
			try {
				folderPath = optionDialog.launch();
			} catch (IOException ex) {
				PaneGenerator.showErrorMessage(PaneMessages.ERROR_GUI_DIALOG_LAUNCH);
			}
			if (folderPath != null) {
				optionDialog.setFolderPath(folderPath);
				topbarController.saveFolderPath(folderPath);
			}
		});
	}
}
