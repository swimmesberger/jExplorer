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
package jexplorer.ownComponents.folderTree;

import unused.TreeMemorySaverThread;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import jexplorer.Favorites;
import jexplorer.MainFrame;
import jexplorer.MyFileSystem;
import jexplorer.design.CustomTreeCellRenderer;
import jexplorer.design.HideRowTreeUI;
import jexplorer.fileSystem.LocalFile;
import jexplorer.ownComponents.fileTable.FileTablePanel;
import jexplorer.ownComponents.popupmenus.FolderTreePopupMenu;
import jexplorer.util.AddChildsThread;
import jexplorer.util.DefaultMainView;
import org.fseek.plugin.interfaces.MyFile;

public class FolderTree extends JTree
{
    private MainFrame frame;
   
    private HashMap<String, Thread> threads = new HashMap<String, Thread>();
    
    private boolean finishedBuildTree = false;
    
    //important nodes
    private DefaultLinkTreeNode biblioNode;
    private DefaultLinkTreeNode favoNode;
    private DefaultLinkTreeNode computerNode;
    
    public FolderTree(MainFrame frame)
    {
        super();
        this.frame = frame;
        withoutModelCreation();
    }
    
    public FolderTree(MainFrame frame, DefaultTreeModel model, DefaultLinkTreeNode[] importantNodes)
    {
        super();
        this.frame = frame;
        if(model == null)
        {
            withoutModelCreation();
        }
        else
        {
            this.biblioNode = importantNodes[0];
            this.favoNode = importantNodes[1];
            this.computerNode = importantNodes[2];
            withModelCreation(model);
        }
    }
    
     public FolderTree()
    {
        super();
    }
    
    private void withoutModelCreation()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        intTree(root,new DefaultTreeModel(root), true);
    }
    
    private void withModelCreation(DefaultTreeModel model)
    {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
        intTree(root,model, false);
    }
    

    
    public void updateComputerNode()
    {
        DefaultTreeModel model = (DefaultTreeModel)this.getModel();
        model.nodeChanged(getComputerNode());
        this.expandPath(new TreePath(getComputerNode().getPath()));
    }
    
    private void folderTreeValueChanged(javax.swing.event.TreeSelectionEvent evt)
    {
        DefaultLinkTreeNode node = (DefaultLinkTreeNode)this.getLastSelectedPathComponent();
        if(node != null)
        {
            MyFile linkDir = node.getLinkDir();
            if(linkDir == null)
            {
                String userObject = (String)node.getUserObject();
                if(userObject.equals("Computer"))
                {
                    getFrame().getNavigator().setGoTo(true);
                    getFrame().getNavigator().navigateTo("Computer");
                }
            }
            else
            {
                DefaultMainView view = new DefaultMainView(new FileTablePanel(linkDir, getFrame()), linkDir.getFileName(), node.getIcon());
                getFrame().setMainView(view, false);
            }
        }
    }
    
    private void folderTreeExpanded(TreeExpansionEvent event)
    {
        TreePath path = event.getPath();
        DefaultLinkTreeNode node = (DefaultLinkTreeNode)path.getLastPathComponent();
        AddChildsThread t = AddChildsThread.create((DefaultTreeModel)this.getModel(), node, true);
        try
        {
            t.start();
        }catch(Exception ex){};
        if(threads.containsKey(path.toString()))
        {
            TreeMemorySaverThread get = (TreeMemorySaverThread)threads.get(path.toString());
            get.interrupt();
        }
    }
    
    
    private void folderTreeCollapsed(TreeExpansionEvent event)
    {
        TreePath path = event.getPath();
        DefaultLinkTreeNode node = (DefaultLinkTreeNode)path.getLastPathComponent();
        TreeMemorySaverThread t = new TreeMemorySaverThread(node, (DefaultTreeModel)this.getModel());
        t.start();
        if(threads.containsKey(path.toString()))
        {
            TreeMemorySaverThread get = (TreeMemorySaverThread)threads.get(path.toString());
            get.stop();
            threads.remove(path.toString());
        }
        threads.put(path.toString(), t);
    }
    
    
    private void maybeShowPopup(MouseEvent e)
    {
        if (e.isPopupTrigger())
        {
            FolderTreePopupMenu menu = new FolderTreePopupMenu(this,e);
            menu.show(e.getComponent(),
            e.getX(), e.getY());
        }
    }
    
    
    private void intTree(DefaultMutableTreeNode root, DefaultTreeModel model, boolean intModel)
    {
        intTreeActions();
        if(intModel == true)
        {
            intTreeModel(root, model);
        }
        else
        {
            this.setModel(model);
        }
        finishedBuildTree = true;
        if (MainFrame.homeDir == null)
        {
            MainFrame.homeDir = new File(System.getProperty("user.home"));
        }
        setRootVisible(false);
        intTreeUI();
        finishTree();
    }
    
    private void finishTree()
    {
        expandPath(new TreePath(getBiblioNode().getPath()));
        expandPath(new TreePath(getFavoNode().getPath()));
    }
    
    private void intTreeActions()
    {
        addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() 
        {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) 
            {
                folderTreeValueChanged(evt);
            }
        });
        addTreeExpansionListener(new TreeExpansionListener()
        {
            public void treeExpanded(TreeExpansionEvent event)
            {
                folderTreeExpanded(event);
            }

            public void treeCollapsed(TreeExpansionEvent event)
            {
//                folderTreeCollapsed(event);
            }
        });
        addMouseListener(new MouseAdapter() 
        {

            @Override
            public void mousePressed(MouseEvent e)
            {
                maybeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                maybeShowPopup(e);
            }
            
            
            
        });
    }
    
    private void intTreeModel(DefaultMutableTreeNode root, DefaultTreeModel model)
    {
        // creating Favorite node
        favoNode = new DefaultLinkTreeNode(new LocalFile(Favorites.getFavoDir()), new File(MainFrame.selectedIconDirectory + File.separator + "star.png"), "Favoriten", MainFrame.colorizer.getTreeFontColor(), MainFrame.colorizer.isTreeFontToUpperCase());
        root.add(getFavoNode());
        if (getFavoNode().getLinkDir().exists())
        {
            try
            {
                for (MyFile f : getFavoNode().getLinkDir().getFiles())
                {
                    addFavoImpl(f);
                }
            } catch (Exception ex)
            {
                Logger.getLogger(FolderTree.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // end creating favorite node
        
        // creating Bibliothek
        biblioNode = new DefaultLinkTreeNode(new LocalFile(MyFileSystem.getHomeFolder()), new File(MainFrame.selectedIconDirectory + File.separator + "bibliothek.png"), "Home", MainFrame.colorizer.getTreeFontColor(), MainFrame.colorizer.isTreeFontToUpperCase());
        model.insertNodeInto(getBiblioNode(), root, root.getChildCount());
        DefaultLinkTreeNode pictureNode = new DefaultLinkTreeNode(new LocalFile(MyFileSystem.getImageFolder()), new File(MainFrame.selectedIconDirectory + File.separator + "pictureSmall.png"), "Bilder");
        if(pictureNode.getLinkDir().exists())
        {
            model.insertNodeInto(pictureNode, getBiblioNode(), getBiblioNode().getChildCount());
        }
        DefaultLinkTreeNode documentNode = new DefaultLinkTreeNode(new LocalFile(MyFileSystem.getDocumentsFolder()), new File(MainFrame.selectedIconDirectory + File.separator + "documentsSmall.png"), "Dokumente");
        if(documentNode.getLinkDir().exists())
        {
            model.insertNodeInto(documentNode, getBiblioNode(), getBiblioNode().getChildCount());
        }
        DefaultLinkTreeNode musicNode = new DefaultLinkTreeNode(new LocalFile(MyFileSystem.getMusicFolder()), new File(MainFrame.selectedIconDirectory + File.separator + "musicSmall.png"), "Musik");
        if(musicNode.getLinkDir().exists())
        {
            model.insertNodeInto(musicNode, getBiblioNode(), getBiblioNode().getChildCount());
        }
        DefaultLinkTreeNode videosNode = new DefaultLinkTreeNode(new LocalFile(MyFileSystem.getVideosFolder()), new File(MainFrame.selectedIconDirectory + File.separator + "videosSmall.png"), "Videos");
        if(videosNode.getLinkDir().exists())
        {
            model.insertNodeInto(videosNode, getBiblioNode(), getBiblioNode().getChildCount());
        }
        // end creating Bibliothek
       
        // creating Computer node
        setComputerNode(new DefaultLinkTreeNode(new File(MainFrame.selectedIconDirectory + File.separator + "computer.png"), "Computer", MainFrame.colorizer.getTreeFontColor(), MainFrame.colorizer.isTreeFontToUpperCase()));
        model.insertNodeInto(getComputerNode(), root, root.getChildCount());
        synchronized(this.getComputerNode())
        {
            getComputerNode().notifyAll();
        }
        //end creating computer node
        AddChildsThread biblioThread = AddChildsThread.create((DefaultTreeModel)this.getModel(), getBiblioNode(), true);
        AddChildsThread computerNodeThread = AddChildsThread.create((DefaultTreeModel)this.getModel(), getComputerNode(), true);
        try
        {
            biblioThread.start();
            computerNodeThread.start();
        }catch(Exception ex){};
        setModel(model);
    }
    
    public MyFile getFavoFile(String name)
    {
        try
        {
            for (MyFile f : getFavoNode().getLinkDir().getFiles())
            {
                String fileName = f.getFileName();
                int lastIndexOf = fileName.lastIndexOf(".");
                fileName = fileName.substring(0, lastIndexOf);
                if(fileName.equals(name))
                {
                    return f;
                }
            }
        } catch (Exception ex)
        {
            Logger.getLogger(FolderTree.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the computerNode
     */
    public DefaultLinkTreeNode getComputerNode()
    {
        return computerNode;
    }

    /**
     * @param computerNode the computerNode to set
     */
    public void setComputerNode(DefaultLinkTreeNode computerNode)
    {
        this.computerNode = computerNode;
    }
    
    private void intTreeUI()
    {
        HideRowTreeUI ownUi = new HideRowTreeUI();
        setUI(ownUi);
        ownUi.setCollapsedIcon(new ImageIcon(MainFrame.selectedIconDirectory + File.separator + "uncollapsed.png"));
        ownUi.setExpandedIcon(new ImageIcon(MainFrame.selectedIconDirectory + File.separator + "collapsed.png"));
        DefaultTreeCellRenderer renderer = new CustomTreeCellRenderer();
        setCellRenderer(renderer);
        setShowsRootHandles(true);
        putClientProperty("JTree.lineStyle", "None");
        setRowHeight(20);
        repaint();
    }

    /**
     * @return the finishedBuildTree
     */
    public boolean isFinishedBuildTree()
    {
        return finishedBuildTree;
    }

    /**
     * @return the biblioNode
     */
    public DefaultLinkTreeNode getBiblioNode()
    {
        return biblioNode;
    }

    /**
     * @return the favoNode
     */
    public DefaultLinkTreeNode getFavoNode()
    {
        return favoNode;
    }

    /**
     * @return the frame
     */
    public MainFrame getFrame()
    {
        return frame;
    }
    
    public void addFavorite(Object name, MyFile link, String iconPath)
    {
        String toString = name.toString();
        while(toString.contains(File.separator))
        {
            toString = toString.replace(File.separator, "");
        }
        while(toString.contains(":"))
        {
            toString = toString.replace(":", "");
        }
        boolean createFavo = Favorites.createFavo(toString, link, iconPath);
        addFavoImpl(getFavoFile(toString.toString()));
    }
    
    public void removeFavorite(MyFile f)
    {
        DefaultLinkTreeNode favo = Favorites.getFavo(f);
        boolean removeFavo = Favorites.removeFavo(f);
        if(removeFavo)
        {
            DefaultLinkTreeNode favoNode1 = getFavoNode();
            for(int i = 0; i<favoNode1.getChildCount(); i++)
            {
                DefaultMutableTreeNode childAt = (DefaultMutableTreeNode)favoNode1.getChildAt(i);
                if(childAt.getUserObject().equals(favo.getUserObject()))
                {
                    DefaultTreeModel model = (DefaultTreeModel)this.getModel();
                    model.removeNodeFromParent(childAt);
                }
            }
        }
    }
    
    private void addFavoImpl(MyFile f)
    {
        DefaultLinkTreeNode favo = Favorites.getFavo(f);
        Favorites.checkIconChange(favo);
        DefaultLinkTreeNode favoNode1 = getFavoNode();
        DefaultTreeModel model = (DefaultTreeModel)this.getModel();
        model.insertNodeInto(favo, favoNode1, favoNode1.getChildCount());
        AddChildsThread nodeThread = AddChildsThread.create((DefaultTreeModel)this.getModel(), favo, false);
        nodeThread.start();
    }
}