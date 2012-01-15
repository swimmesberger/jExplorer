/*
 * Copyright (C) 2011 Thedeath<www.fseek.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package unused;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import jexplorer.ownComponents.folderTree.DefaultLinkTreeNode;

public class TreeMemorySaverThread extends Thread
{
    private DefaultLinkTreeNode node;
    private DefaultTreeModel model;
    public TreeMemorySaverThread(DefaultLinkTreeNode node, DefaultTreeModel model)
    {
        super("TreeMemorySaverThread");
        this.node = node;
        this.model = model;
    }
    
    @Override
    public void run()
    {
        boolean flag = true;
        while(flag)
        {
            try
            {
                TreeMemorySaverThread.sleep(60000);
                for(int i = 0; i<node.getChildCount(); i++)
                {
                    DefaultMutableTreeNode childAt = (DefaultMutableTreeNode)node.getChildAt(i);
                    childAt.removeAllChildren();
                    model.nodeChanged(node);
                }
                flag = false;
                System.gc();
            } catch (InterruptedException ex)
            {
                flag = true;
            }
        }
    }
}
