package com.nabilanam.litedownloader.view;

import com.nabilanam.litedownloader.controller.LeftbarController;
import com.nabilanam.litedownloader.model.DownloadStatus;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author nabil
 */
@SuppressWarnings("serial")
public final class LeftbarPanel extends JPanel
{
    private final JTree tree;

    public LeftbarPanel(LeftbarController lbController)
    {
        setLayout(new BorderLayout());
        setBackground(Color.white);
        setBorder(BorderFactory.createEtchedBorder());

        tree = new JTree(lbController.getTreeModel());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener((TreeSelectionEvent e) ->
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node == null)
            {
                return;
            }
            Object obj = node.getUserObject();
            DownloadStatus status = null;
            if (node.isLeaf())
            {
                status = (DownloadStatus) obj;
            }
            lbController.fireNodeSelected(status);
        });

        add(tree);
    }

}
