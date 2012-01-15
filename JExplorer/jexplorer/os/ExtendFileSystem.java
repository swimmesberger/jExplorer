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

public abstract class ExtendFileSystem
{
    //This method trys to find the desktop folder
    public abstract File getDesktop();
    
    // gets the folder where the last used files are located, works only on linux yet
    public abstract File getRecentFolder();

    // gets the downloads folder
    public abstract File getDownloadsFolder();

    // gets the images folder
    public abstract File getImageFolder();

    // gets the music folder
    public abstract File getMusicFolder();

    // gets the documents folder
    public abstract File getDocumentsFolder();

    // gets the videos folder
    public abstract File getVideosFolder();

    // gets the home folder
    public File getHomeFolder()
    {
        return new File(System.getProperty("user.home"));
    }
}
