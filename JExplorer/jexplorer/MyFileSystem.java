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

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import jexplorer.os.ExtendFileSystem;
import jexplorer.os.OSFileSystemFactory;
import jexplorer.util.UtilBox;
import sun.awt.shell.ShellFolder;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public class MyFileSystem
{
    private static Icon textFileIcon;
    private static Icon folderIcon;
    
    private static ExtendFileSystem extFileSystem;
    
    static
    {
        extFileSystem = OSFileSystemFactory.createExtendFileSystem();
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="getSystemIcon(File, boolean)">
    // gets the system icon of a file, I made a custom implementation of the FileSystemView.getSystemIcon(File f) method because I also need the large icons
    public static Icon getSystemIcon(File f, boolean large)
    {
        if (f == null)
        {
            return null;
        }

        ShellFolder sf;

        try
        {
            sf = getShellFolder(f);
        } catch (FileNotFoundException e)
        {
            return null;
        }

        Image img = sf.getIcon(large);

        if (img != null)
        {
            return new ImageIcon(img, sf.getFolderType());
        }
        else
        {
            return UIManager.getIcon(f.isDirectory() ? "FileView.directoryIcon" : "FileView.fileIcon");
        }
    }

    // this method is needed to get the system icon, this method can also be found in the "FileSystemView" class, I just copied it from there
    private static ShellFolder getShellFolder(File f) throws FileNotFoundException
    {
        if (!(f instanceof ShellFolder) && !(f instanceof FileSystemRoot) && isFileSystemRoot(f))
        {
            f = createFileSystemRoot(f);
        }

        try
        {
            return ShellFolder.getShellFolder(f);
        } catch (InternalError e)
        {
            System.err.println("FileSystemView.getShellFolder: f=" + f);
            e.printStackTrace();
            return null;
        }
    }
    
    // this method is needed to get the system icon, this method can also be found in the "FileSystemView" class, I just copied it from there
    public static boolean isFileSystemRoot(File dir) 
    {
	return ShellFolder.isFileSystemRoot(dir);
    }
    
    // this method is needed to get the system icon, this method can also be found in the "FileSystemView" class, I just copied it from there
    private static File createFileSystemRoot(File f) 
    {
	return new FileSystemRoot(f) 
        {
	    public boolean exists() 
            {
		return true;
	    }
	};
    }
    
    // this class is needed for the getSystemIcon method
    static class FileSystemRoot extends File
    {

        public FileSystemRoot(File f)
        {
            super(f, "");
        }

        public FileSystemRoot(String s)
        {
            super(s);
        }

        public boolean isDirectory()
        {
            return true;
        }

        public String getName()
        {
            return getPath();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="getter methods for often used folders - impl for the different platforms">
    //TODO: mac support
    public static File getDesktop()
    {
        return extFileSystem.getDesktop();
    }
    
    // gets the folder where the last used files are located, works only on linux yet
    //TODO: mac support, linux support
    public static File getRecentFolder()
    {
        return extFileSystem.getRecentFolder();
    }

    //TODO: mac support
    public static File getDownloadsFolder()
    {
        return extFileSystem.getDownloadsFolder();
    }

    //TODO: mac support
    public static File getImageFolder()
    {
        return extFileSystem.getImageFolder();
    }

    //TODO: mac support
    public static File getMusicFolder()
    {
        return extFileSystem.getMusicFolder();
    }

    //TODO: mac support
    public static File getDocumentsFolder()
    {
        return extFileSystem.getDocumentsFolder();
    }

    //TODO: mac support
    public static File getVideosFolder()
    {
        return extFileSystem.getVideosFolder();
    }

    // gets the home directory
    public static File getHomeFolder()
    {
        return extFileSystem.getHomeFolder();
    }
    
    // </editor-fold>
    
    //checks if a file is on the same partition this is needed to check if the file should be copied and deleted or just just renamed
    public static boolean isSameDrive(File d, File f)
    {
        String dPath = d.getAbsolutePath();
        String fPath = f.getAbsolutePath();
        int index = dPath.indexOf(File.separator);
        int index1 = fPath.indexOf(File.separator);
        dPath = dPath.substring(0, index + File.separator.length());
        fPath = fPath.substring(0, index1 + File.separator.length());
        if(dPath.equals(fPath))
        {
            return true;
        }
        return false;
    }
    
    // <editor-fold defaultstate="collapsed" desc="getter methods for often used icons">
    // trys to get the icon for a text File if its impossible to get it uses the custom one
    public static Icon getTextFileIcon()
    {
        if(textFileIcon != null)
        {
            return textFileIcon;
        }
        FileSystemView view = FileSystemView.getFileSystemView();
        File homeDirectory = view.getHomeDirectory();
        int count = -1;
        File temp = new File(homeDirectory.getAbsolutePath() + File.separator + "temp" + count +".txt");
        while(temp.exists())
        {
            count++;
            temp = new File(homeDirectory.getAbsolutePath() + File.separator + "temp" + count +".txt");
        }
        try
        {
            temp.createNewFile();
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
        Icon systemIcon = view.getSystemIcon(temp);
        boolean delete = temp.delete();
        if(!delete)
        {
            temp.deleteOnExit();
        }
        systemIcon = UtilBox.rescaleIconIfNeeded(systemIcon, 16,16);
        if(systemIcon == null)
        {
            systemIcon = new ImageIcon(MainFrame.selectedIconDirectory + File.separator + "textFile.png");
        }
        textFileIcon = systemIcon;
        return systemIcon;
    }
    
    // trys to get the icon for a folder if its impossible to get it uses the custom one
    public static Icon getFolderIcon()
    {
        if(folderIcon != null)
        {
            return folderIcon;
        }
        FileSystemView view = FileSystemView.getFileSystemView();
        File homeDirectory = view.getHomeDirectory();
        int count = -1;
        File temp = new File(homeDirectory.getAbsolutePath() + File.separator + "temp" + count);
        while(temp.exists())
        {
            count++;
            temp = new File(homeDirectory.getAbsolutePath() + File.separator + "temp" + count);
        }
        temp.mkdir();
        Icon systemIcon = view.getSystemIcon(temp);
        boolean delete = temp.delete();
        if(!delete)
        {
            temp.deleteOnExit();
        }
        systemIcon = UtilBox.rescaleIconIfNeeded(systemIcon, 16,16);
        if(systemIcon == null)
        {
            systemIcon = new ImageIcon(MainFrame.selectedIconDirectory + File.separator + "folder.png");
        }
        folderIcon = systemIcon;
        return systemIcon;
    }
    

    // </editor-fold>
}

// <editor-fold defaultstate="collapsed" desc="WindowsUtils">
// this class is for windows pcs only, with this class its possible to read values from the registry
class WindowsUtils
{

    private static final String REGQUERY_UTIL = "reg query ";
    private static final String REGSTR_TOKEN = "REG_SZ";
    private static final String DESKTOP_FOLDER_CMD = REGQUERY_UTIL
    + "\"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\"
    + "Explorer\\Shell Folders\" /v ";

    private WindowsUtils()
    {
    }

    public static String getCurrentUserPath(String search)
    {
        try
        {
            Process process = Runtime.getRuntime().exec(DESKTOP_FOLDER_CMD + "\"" + search + "\"");
            StreamReader reader = new StreamReader(process.getInputStream());

            reader.start();
            process.waitFor();
            reader.join();

            String result = reader.getResult();
            int p = result.indexOf(REGSTR_TOKEN);

            if (p == -1)
            {
                return null;
            }

            return result.substring(p + REGSTR_TOKEN.length()).trim();
        } catch (Exception e)
        {
            return null;
        }
    }


    static class StreamReader extends Thread
    {

        private InputStream is;
        private StringWriter sw;

        StreamReader(InputStream is)
        {
            this.is = is;
            sw = new StringWriter();
        }

        @Override
        public void run()
        {
            try
            {
                int c;
                while ((c = is.read()) != -1)
                {
                    sw.write(c);
                }
            } catch (IOException e)
            {
            }
        }

        String getResult()
        {
            return sw.toString();
        }
    }
    // </editor-fold>
}
