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

import java.io.File;
import javax.swing.tree.DefaultTreeModel;
import jexplorer.ownComponents.folderTree.DefaultLinkTreeNode;
import jexplorer.MyFileSystem;
import jexplorer.fileSystem.LocalFile;
import org.fseek.plugin.interfaces.MyFile;

public class TreeUtil
{
    public static void addChilds(DefaultLinkTreeNode child, DefaultTreeModel model)
    {
        DefaultLinkTreeNode childNode = null;
        if(child != null)
        {
            MyFile tempMyFile = child.getLinkDir();
            File linkDir = null;
            if(tempMyFile instanceof LocalFile)
            {
                LocalFile lFile = (LocalFile)tempMyFile;
                linkDir = lFile.getFile();
            }
            if(linkDir == null)return;
            if(linkDir.canRead())
            {
                if(linkDir != null)
                {
                    File[] listFiles = linkDir.listFiles();
                    if(listFiles != null)
                    {
                        for(File f : listFiles)
                        {
                            if(f.isDirectory())
                            {
                                childNode = new DefaultLinkTreeNode(new LocalFile(f), MyFileSystem.getSystemIcon(f, false), f.getName());
                                int childCount = child.getChildCount();
                                model.insertNodeInto(childNode, child, childCount);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static void addChildsToChilds(DefaultLinkTreeNode node, DefaultTreeModel model)
    {
        for(int i = 0; i<node.getChildCount(); i++)
        {
            DefaultLinkTreeNode child = (DefaultLinkTreeNode)node.getChildAt(i);
            if(child.getChildCount() <= 0)
            {
                addChilds(child, model);
            }
        }
    }
}
