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
package jexplorer.ownComponents.fileTable;

import javax.swing.Icon;

public class FileIcon implements Comparable<FileIcon>
{
    private String fileName;
    private Icon icon;
    private boolean directory;

    public FileIcon(String fileName, Icon icon, boolean directory)
    {
        this.fileName = fileName;
        this.icon = icon;
        this.directory = directory;
    }

    /**
     * @return the fileName
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    /**
     * @return the icon
     */
    public Icon getIcon()
    {
        return icon;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon(Icon icon)
    {
        this.icon = icon;
    }

    /**
     * @return the directory
     */
    public boolean isDirectory()
    {
        return directory;
    }

    /**
     * @param directory the directory to set
     */
    public void setDirectory(boolean directory)
    {
        this.directory = directory;
    }

    public int compareTo(FileIcon o)
    {
        if(o.isDirectory() && this.isDirectory() == false)
        {
            return 1;
        }
        else if(o.isDirectory() == false && this.isDirectory())
        {
            return -1;
        }
        else
        {
            int compareTo = this.getFileName().compareToIgnoreCase(o.getFileName());
            return compareTo;
        }
    }

    @Override
    public String toString()
    {
        return fileName;
    }


}
