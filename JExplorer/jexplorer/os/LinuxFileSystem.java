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
package jexplorer.os;

import java.io.File;

public class LinuxFileSystem extends ExtendFileSystem
{

    public File getDesktop()
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
    
    // gets the folder where the last used files are located, works only on linux yet
    public File getRecentFolder()
    {
        return null;
    }

    public File getDownloadsFolder()
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

    //TODO: mac support
    public File getImageFolder()
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

    //TODO: mac support
    public File getMusicFolder()
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

    //TODO: mac support
    public File getDocumentsFolder()
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

    public File getVideosFolder()
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
