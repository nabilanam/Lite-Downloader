package com.nabilanam.litedownloader.model;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

/**
 *
 * @author nabil
 */
@SuppressWarnings("serial")
public final class TreeModel extends DefaultTreeModel {
	
	public TreeModel() {
		super(new DefaultMutableTreeNode("All Downloads"));

		DefaultMutableTreeNode downloadingNode = new DefaultMutableTreeNode(DownloadStatus.Downloading);
		DefaultMutableTreeNode completedNode = new DefaultMutableTreeNode(DownloadStatus.Completed);
		DefaultMutableTreeNode pausedNode = new DefaultMutableTreeNode(DownloadStatus.Paused);
		DefaultMutableTreeNode stoppedNode = new DefaultMutableTreeNode(DownloadStatus.Stopped);
		DefaultMutableTreeNode errorNode = new DefaultMutableTreeNode(DownloadStatus.Error);

		insertNodeInto(downloadingNode, (MutableTreeNode) this.root, 0);
		insertNodeInto(completedNode, (MutableTreeNode) this.root, 1);
		insertNodeInto(pausedNode, (MutableTreeNode) this.root, 0);
		insertNodeInto(stoppedNode, (MutableTreeNode) this.root, 2);
		insertNodeInto(errorNode, (MutableTreeNode) this.root, 3);
	}
}
