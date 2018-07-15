package com.nabilanam.litedownloader.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 *
 * @author nabil
 */
public final class InputDialog {
	
	private final JDialog dialog;
	private final JTextField textField;
	private final TextPopupMenu popupMenu;
	private final JLabel label;
	private final JButton okButton;
	private final JButton cancelButton;
	private final JPanel panel;
	private final JPanel btnPanel;
	private String result;

	public InputDialog(JFrame parent, String title) {
		panel = new JPanel();
		btnPanel = new JPanel();
		okButton = new JButton("OK");
		textField = new JTextField(10);
		popupMenu = new TextPopupMenu();
		cancelButton = new JButton("Cancel");
		label = new JLabel("Add download link");
		dialog = new JDialog(parent, title, true);

		setListeners();
		layoutComponents();
	}

	public String getInput() {
		result = "";
		textField.setText(result);
		dialog.setVisible(true);
		return result;
	}

	private void setListeners() {
		textField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					popupMenu.show(textField, e.getX(), e.getY());
				}
			}
		});
		okButton.addActionListener((e) -> {
			result = textField.getText();
			dialog.setVisible(false);
		});
		cancelButton.addActionListener((e) -> {
			result = null;
			dialog.setVisible(false);
		});
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				result = null;
			}
		});
	}

	private void layoutComponents() {
		Border border1 = BorderFactory.createEtchedBorder();
		Border border2 = BorderFactory.createMatteBorder(5, 10, 5, 10, panel.getBackground());
		Border border = BorderFactory.createCompoundBorder(border2, border1);
		Font font = textField.getFont().deriveFont(Font.PLAIN, 14f);

		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		label.setPreferredSize(new Dimension(30, 30));

		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

		btnPanel.add(okButton);
		btnPanel.add(cancelButton);

		textField.setFont(font);
		textField.setBorder(border);

		panel.add(label);
		panel.add(textField);
		panel.add(btnPanel);
		panel.setPreferredSize(new Dimension(280, 105));

		dialog.setContentPane(panel);
		dialog.pack();
		dialog.setLocationRelativeTo(dialog.getParent());
	}
}
