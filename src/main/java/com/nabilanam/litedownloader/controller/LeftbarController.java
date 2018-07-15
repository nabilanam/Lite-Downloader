package com.nabilanam.litedownloader.controller;

import com.nabilanam.litedownloader.model.DownloadStatus;
import com.nabilanam.litedownloader.model.NodeSelectionListener;
import com.nabilanam.litedownloader.model.TreeModel;

/**
 *
 * @author nabil
 */
public class LeftbarController {
	private NodeSelectionListener selectionListener;

	public void fireNodeSelected(DownloadStatus status) {
		if (selectionListener != null) {
			selectionListener.filterSelected(status);
		}
	}

	public TreeModel getTreeModel() {
		return new TreeModel();
	}

	public void setNodeSelectionListener(NodeSelectionListener listener) {
		this.selectionListener = listener;
	}
}
