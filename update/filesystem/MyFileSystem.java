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
package filesystem;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import sun.awt.shell.ShellFolder;

/**
 *
 * @author Thedeath<www.skyoix.com>
 */
public class MyFileSystem
{
    private static Icon textFileIcon;
    private static Icon folderIcon;
    
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
    //This method trys to find the desktop folder
    //TODO: mac support
    public static File getDesktop()
    {
        if (OSDetector.isWindows())
        {
            return new File(WindowsUtils.getCurrentUserPath("DESKTOP"));
        }
//        else if (OSDetector.isMac())
//        {
//            throw new UnsupportedOperationException("Mac isnt supported at the moment !");
//        }
        else// if (OSDetector.isLinux())
        {
            String trys[] =
            {
                "Desktop"
            };
            File file = null;
            for (String s : trys)
            {
                file = new File(getHomeFolder() + File.separator + s);
                if (file.exists())
                {
                    return file;
                }
            }
            return file;
        }
        //return null;
    }
    
    // gets the folder where the last used files are located, works only on linux yet
    //TODO: mac support, linux support
    public static File getRecentFolder()
    {
        if (OSDetector.isWindows())
        {
            return new File(WindowsUtils.getCurrentUserPath("RECENT"));
        }
        else if (OSDetector.isMac())
        {
            //throw new UnsupportedOperationException("Mac isnt supported at the moment !");
        }
        else if (OSDetector.isLinux())
        {
            //throw new UnsupportedOperationException("Linux isnt supported at the moment !");
        }
        return new File(".");
    }

    //TODO: mac support
    public static File getDownloadsFolder()
    {
        if (OSDetector.isWindows())
        {
            return new File(WindowsUtils.getCurrentUserPath("{374DE290-123F-4565-9164-39C4925E467B}"));
        }
//        else if (OSDetector.isMac())
//        {
//            throw new UnsupportedOperationException("Mac isnt supported at the moment !");
//        }
        else //if (OSDetector.isLinux())
        {
            // trys to find the downloads folder on linux, also for the different languages
            String trys[] =
            {
                "Downloads"
            };
            File file = null;
            for (String s : trys)
            {
                file = new File(getHomeFolder() + File.separator + s);
                if (file.exists())
                {
                    return file;
                }
            }
            return file;
        }
    }

    //TODO: mac support
    public static File getImageFolder()
    {
        if (OSDetector.isWindows())
        {
            return new File(WindowsUtils.getCurrentUserPath("My Pictures"));
        }
//        else if (OSDetector.isMac())
//        {
//            throw new UnsupportedOperationException("Mac isnt supported at the moment !");
//        }
        else// if (OSDetector.isLinux())
        {
            // trys to find the pictures folder on linux, also for the different languages
            String trys[] =
            {
                "Pictures", "Bilder"
            };
            File file = null;
            for (String s : trys)
            {
                file = new File(getHomeFolder() + File.separator + s);
                if (file.exists())
                {
                    return file;
                }
            }
            return file;
        }
    }

    //TODO: mac support
    public static File getMusicFolder()
    {
        if (OSDetector.isWindows())
        {
            return new File(WindowsUtils.getCurrentUserPath("My Music"));
        }
//        else if (OSDetector.isMac())
//        {
////            throw new UnsupportedOperationException("Mac isnt supported at the moment !");
//        }
        else // if (OSDetector.isLinux())
        {
            String trys[] =
            {
                "Music", "Musik"
            };
            File file = null;
            for (String s : trys)
            {
                file = new File(getHomeFolder() + File.separator + s);
                if (file.exists())
                {
                    return file;
                }
            }
            return file;
        }
    }

    //TODO: mac support
    public static File getDocumentsFolder()
    {
        if (OSDetector.isWindows())
        {
            return new File(WindowsUtils.getCurrentUserPath("Personal"));
        }
//        else if (OSDetector.isMac())
//        {
//            throw new UnsupportedOperationException("Mac isnt supported at the moment !");
//        }
        else //if (OSDetector.isLinux())
        {
            String trys[] =
            {
                "Documents", "Dokumente"
            };
            File file = null;
            for (String s : trys)
            {
                file = new File(getHomeFolder() + File.separator + s);
                if (file.exists())
                {
                    return file;
                }
            }
            return file;
        }
    }

    //TODO: mac support
    public static File getVideosFolder()
    {
        if (OSDetector.isWindows())
        {
            return new File(WindowsUtils.getCurrentUserPath("My Video"));
        }
//        else if (OSDetector.isMac())
//        {
//            throw new UnsupportedOperationException("Mac isnt supported at the moment !");
//        }
        else// if (OSDetector.isLinux())
        {
            String trys[] =
            {
                "Videos"
            };
            File file = null;
            for (String s : trys)
            {
                file = new File(getHomeFolder() + File.separator + s);
                if (file.exists())
                {
                    return file;
                }
            }
            return file;
        }
    }

    // gets the home directory
    public static File getHomeFolder()
    {
        return new File(System.getProperty("user.home"));
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
