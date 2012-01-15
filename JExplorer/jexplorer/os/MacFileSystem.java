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

public class MacFileSystem extends ExtendFileSystem
{

    @Override
    public File getDesktop()
    {
        File file = new File(getHomeFolder() + File.separator + "Desktop");
        return file;
    }

    @Override
    public File getRecentFolder()
    {
        return null;
    }

    @Override
    public File getDownloadsFolder()
    {
        File file = new File(getHomeFolder() + File.separator + "Downloads");
        return file;
    }

    @Override
    public File getImageFolder()
    {
        File file = new File(getHomeFolder() + File.separator + "Pictures");
        return file;
    }

    @Override
    public File getMusicFolder()
    {
        File file = new File(getHomeFolder() + File.separator + "Music");
        return file;
    }

    @Override
    public File getDocumentsFolder()
    {
        File file = new File(getHomeFolder() + File.separator + "Documents");
        return file;
    }

    @Override
    public File getVideosFolder()
    {
        File file = new File(getHomeFolder() + File.separator + "Movies");
        return file;
    }
    
}
