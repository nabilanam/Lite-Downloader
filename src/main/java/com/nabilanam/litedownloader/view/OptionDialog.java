package com.nabilanam.litedownloader.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author nabil
 */
@SuppressWarnings("serial")
public final class OptionDialog extends JDialog {
	
	private final JLabel directoryLabel;
	private final JButton directoryChooseButton;
	private final JTextField directoryTextField;
	private final FileChooser fileChooser;

	public OptionDialog(JFrame parent) {
		Dimension dialogDimension = new Dimension(500, 100);
		Dimension buttonDimension = new Dimension(24, 21);

		fileChooser = new FileChooser(JFileChooser.DIRECTORIES_ONLY);
		directoryTextField = new JTextField(20);
		directoryChooseButton = new JButton("...");
		directoryLabel = new JLabel("Download Location: ");

		directoryTextField.setEditable(false);
		directoryChooseButton.setSize(buttonDimension);
		directoryChooseButton.setPreferredSize(buttonDimension);

		setTitle("Options");
		layoutComponents();
		setMinimumSize(dialogDimension);
		setPreferredSize(dialogDimension);
		setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
		setLocationRelativeTo(parent);
	}

	String getFolderPath() {
		return directoryTextField.getText();
	}

	String launch() throws IOException {
		return fileChooser.getPathFromUser(OptionDialog.this, "Select Folder");
	}

	void setActionListener(ActionListener listener) {
		directoryChooseButton.addActionListener(listener);
	}

	void setFolderPath(String path) {
		directoryTextField.setText(path);
	}

	private void layoutComponents() {
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		Insets labelInsets = new Insets(0, 0, 0, 5);
		Insets emptyInsets = new Insets(0, 0, 0, 0);

		// First row
		gc.gridy = 0;

		// nameLabel
		gc.gridx = 0;

		gc.weightx = 0.1;
		gc.weighty = 0.1;

		gc.insets = emptyInsets;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.LINE_END;
		add(directoryLabel, gc);

		// nameTextField
		gc.gridx = 1;

		gc.weightx = 0.1;
		gc.weighty = 0.1;

		gc.insets = labelInsets;
		gc.anchor = GridBagConstraints.LINE_END;
		add(directoryTextField, gc);

		// choose button
		gc.gridx = 2;

		gc.weightx = 0.1;
		gc.weighty = 0.1;

		gc.insets = emptyInsets;
		gc.anchor = GridBagConstraints.LINE_START;
		add(directoryChooseButton, gc);
	}
}
