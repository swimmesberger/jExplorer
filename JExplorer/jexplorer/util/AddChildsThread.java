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
package jexplorer.util;

import java.util.HashMap;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import jexplorer.ownComponents.folderTree.DefaultLinkTreeNode;

public class AddChildsThread extends Thread
{ 
    private static HashMap<String, AddChildsThread> threads = new HashMap<String, AddChildsThread>();
    
    private DefaultLinkTreeNode node;
    private boolean childToChild;
    private DefaultTreeModel model;
    
    private AddChildsThread(DefaultTreeModel model,DefaultLinkTreeNode node, boolean childToChild)
    {
        super("AddChildsThread - " + node.getUserObject().toString());
        this.node = node;
        this.childToChild = childToChild;
        this.model = model;
    }
    
    public static AddChildsThread create(DefaultTreeModel model,DefaultLinkTreeNode node, boolean childToChild)
    {
        String s = new TreePath(node).toString() + childToChild;
        if(!threads.containsKey(s))
        {
            AddChildsThread t = new AddChildsThread(model, node, childToChild);
            threads.put(s, t);
            return t;
        }
        return threads.get(s);
    }

    @Override
    public void run()
    {
        if(this.childToChild)
        {
            TreeUtil.addChildsToChilds(node, model);
        }
        else
        {
            TreeUtil.addChilds(node, model);
        }
    }
}