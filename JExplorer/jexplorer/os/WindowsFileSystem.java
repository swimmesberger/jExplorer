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
import jexplorer.util.WindowsUtils;

public class WindowsFileSystem extends ExtendFileSystem
{
    public File getDesktop()
    {
        return new File(WindowsUtils.getCurrentUserPath("DESKTOP"));
    }
    
    public File getRecentFolder()
    {
        return new File(WindowsUtils.getCurrentUserPath("RECENT"));
    }

    public File getDownloadsFolder()
    {
        return new File(WindowsUtils.getCurrentUserPath("{374DE290-123F-4565-9164-39C4925E467B}"));
    }

    public File getImageFolder()
    {
        return new File(WindowsUtils.getCurrentUserPath("My Pictures"));
    }

    public File getMusicFolder()
    {
        return new File(WindowsUtils.getCurrentUserPath("My Music"));
    }

    public File getDocumentsFolder()
    {
        return new File(WindowsUtils.getCurrentUserPath("Personal"));
    }

    @Override
    public File getVideosFolder()
    {
        return new File(WindowsUtils.getCurrentUserPath("My Video"));
    }
}
