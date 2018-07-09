package com.nabilanam.litedownloader.view;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.DefaultEditorKit;

/**
 *
 * @author nabil
 */
@SuppressWarnings("serial")
public final class TextPopupMenu extends JPopupMenu {
	
	private final JMenuItem paste;
	private final JMenuItem copy;
	private final JMenuItem cut;

	public TextPopupMenu() {
		cut = new JMenuItem(new DefaultEditorKit.CutAction());
		copy = new JMenuItem(new DefaultEditorKit.CopyAction());
		paste = new JMenuItem(new DefaultEditorKit.PasteAction());

		cut.setText("Cut");
		copy.setText("Copy");
		paste.setText("Paste");

		add(paste);
		addSeparator();
		add(cut);
		add(copy);
	}
}
