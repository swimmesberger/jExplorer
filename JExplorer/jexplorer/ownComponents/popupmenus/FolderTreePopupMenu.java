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
package jexplorer.ownComponents.popupmenus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import jexplorer.ownComponents.folderTree.DefaultLinkTreeNode;
import jexplorer.MainFrame;
import org.fseek.plugin.interfaces.MyFile;
import jexplorer.ownComponents.folderTree.FolderTree;
import org.fseek.plugin.interfaces.MainView;

public class FolderTreePopupMenu extends JPopupMenu
{
    private FolderTree tree;
    private MainFrame frame;
    private MouseEvent ev;
    public FolderTreePopupMenu()
    {
        super();
    }
    
    public FolderTreePopupMenu(FolderTree tree, MouseEvent e)
    {
        super();
        this.tree = tree;
        this.frame = tree.getFrame();
        this.ev = e;
        createFavoriteMenu();
    }
    
    private void createFavoriteMenu()
    {
        TreePath selectionPath = this.tree.getPathForLocation(ev.getX(), ev.getY());
        if(selectionPath == null)return;
        DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode)selectionPath.getLastPathComponent();
        if(lastPathComponent instanceof DefaultLinkTreeNode)
        {
            DefaultLinkTreeNode node = (DefaultLinkTreeNode)lastPathComponent;
            this.add(createOpenNewWindowMenu(node));
            if(node == this.tree.getFavoNode())
            {
                this.add(new Separator());
                this.add(createAddFavorite());
            }
            else
            {
                this.add(createRemoveFavorite(this.tree.getFavoFile(node.getUserObject().toString())));
            }
        }
    }
    
    private JMenuItem createOpenNewWindowMenu(final DefaultLinkTreeNode node)
    {
        JMenuItem newWindowItem = new JMenuItem("In neuem Fenster Öffnen");
        newWindowItem.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e)
            {        
                java.awt.EventQueue.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        MainFrame frame = new MainFrame(tree,node.getLinkDir());
                        frame.setVisible(true);
                    }
                });
            }
        });
        return newWindowItem;
    }
    
    private JMenuItem createAddFavorite()
    {
        JMenuItem addFavItem = new JMenuItem("Aktueller Ort zu den Favoriten hinzufügen");
        addFavItem.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e)
            {
                MyFile actualFile = frame.getActualFile();
                if(actualFile != null && actualFile.exists())
                {
                    MainView mainView = frame.getMainView();
                    tree.addFavorite(actualFile.getFileName(), actualFile, null);
                }
            }
        });
        return addFavItem;
    }
    
    private JMenuItem createRemoveFavorite(final MyFile f)
    {
        JMenuItem remFavItem = new JMenuItem("Favorit löschen");
        remFavItem.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e)
            {
                tree.removeFavorite(f);
            }
        });
        return remFavItem;
    }
    
    
}
