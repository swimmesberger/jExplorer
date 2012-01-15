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
package jexplorer;

import jexplorer.ownComponents.folderTree.DefaultLinkTreeNode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import jexplorer.fileSystem.LocalFile;
import jexplorer.ownComponents.ComputerPanel;
import org.fseek.plugin.interfaces.MyFile;

public class Favorites
{
    private static File favoDir;
    
    public static File createFavoritesDirectory()
    {
        setFavoDir(new File(MainFrame.mainDir + File.separator + "Favorites"));
        if (!favoDir.exists())
        {
            getFavoDir().mkdir();
            MyFile desktop = new LocalFile(MyFileSystem.getDesktop());
            if(desktop != null)
            createFavo("Desktop", desktop, MainFrame.selectedIconDirectory + File.separator + "desktop.png");
            File downloadsFolder = MyFileSystem.getDownloadsFolder();
            if(downloadsFolder != null)
            createFavo("Downloads", new LocalFile(downloadsFolder), MainFrame.selectedIconDirectory + File.separator + "downloads.png");
            File recentFolder = MyFileSystem.getRecentFolder();
            if(recentFolder != null)
            createFavo("Zuletzt verwendet", new LocalFile(recentFolder), MainFrame.selectedIconDirectory + File.separator + "recent.png");
        }
        return getFavoDir();
    }
    
    public static boolean createFavo(Object name, MyFile link, String iconPath)
    {
        ObjectOutputStream out = null;
        try
        {
            String toString = name.toString();
            File favo = new File(getFavoDir() + File.separator + toString + ".favo");
            out = new ObjectOutputStream(new FileOutputStream(favo));
            out.writeObject(toString);
            out.writeObject(link);
            if(iconPath != null)
            out.writeObject(iconPath);
            out.flush();
            return true;
        } catch (IOException ex)
        {
            Logger.getLogger(Favorites.class.getName()).log(Level.SEVERE, null, ex);
        } finally
        {
            try
            {
                out.close();
            } catch (IOException ex)
            {
                Logger.getLogger(Favorites.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
    
    public static boolean removeFavo(MyFile link)
    {
        return link.delete();
    }
    
    public static DefaultLinkTreeNode checkIconChange(DefaultLinkTreeNode favo)
    {
        if(favo == null)return favo;
        File iconPath = favo.getIconPath();
        if(iconPath == null)return favo;
        if(iconPath.getAbsolutePath().contains(MainFrame.selectedIconDirectory.getAbsolutePath()))
        {
        }
        else
        {
            favo.setIconPath(new File(MainFrame.selectedIconDirectory + File.separator  + iconPath.getName()));
            createFavo(favo.getUserObject(), favo.getLinkDir(), favo.getIconPath().getAbsolutePath());
        }
        return favo;
    }

    public static DefaultLinkTreeNode getFavo(MyFile f)
    {
        if(f == null)return null;
        DefaultLinkTreeNode node = null;
        ObjectInputStream out = null;
        try
        {
            out = new ObjectInputStream(new FileInputStream(f.getAbsolutePath()));
            Object name = (Object) out.readObject();
            MyFile link = (MyFile) out.readObject();
            String iconPath = null;
            try
            {
                iconPath = (String) out.readObject();
            }catch(Exception ex){}
            if(iconPath != null)
            {
                node = new DefaultLinkTreeNode(link, new File(iconPath), name);
            }
            else
            {
                LocalFile lf = null;
                if(link instanceof LocalFile)
                {
                    lf = (LocalFile)link;
                    Icon icon = null;
                    if(ComputerPanel.isDrive(lf.getFile()))
                    {
                        icon = ComputerPanel.getIconForFile(lf.getFile(), false);
                    }
                    else
                    {
                        icon = MyFileSystem.getSystemIcon(lf.getFile(), false);
                    }
                    node = new DefaultLinkTreeNode(link, icon, name);
                }
            }
        } 
        catch (Exception ex)
        {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            try
            {
                if(out != null)
                {
                    out.close();
                }
            } catch (IOException ex1)
            {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            MyFile favoDir1 = new LocalFile(getFavoDir());
            boolean delete = false;
            if(favoDir1 != null)
            {
                delete = favoDir1.delete();
            }
            if(delete == true)
            {
                File createFavoritesDirectory = createFavoritesDirectory();
                if(f.exists())
                {
                    DefaultLinkTreeNode favo = getFavo(f);
                    return favo;
                }
                else
                {
                    return null;
                }
            }
        } 
        finally
        {
            try
            {
                if(out != null)
                out.close();
            } catch (IOException ex)
            {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return node;
    }
    
    public static boolean isDrive(MyFile file)
    {
        int count = 0;
        String absolutePath = file.getAbsolutePath();
        while(absolutePath.contains(File.separator))
        {
            if(count == 1)return true;
            absolutePath = absolutePath.replace(File.separator, "");
            count++;
        }
        return false;
    }

    /**
     * @return the favoDir
     */
    public static File getFavoDir()
    {
        return favoDir;
    }

    /**
     * @param aFavoDir the favoDir to set
     */
    public static void setFavoDir(File aFavoDir)
    {
        favoDir = aFavoDir;
    }
}
