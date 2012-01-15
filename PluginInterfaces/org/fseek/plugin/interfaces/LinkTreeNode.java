package org.fseek.plugin.interfaces;

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


import java.awt.Color;
import java.io.File;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.MutableTreeNode;

public interface LinkTreeNode extends MutableTreeNode
{
    public Icon getIcon();

    /**
     * @return the linkDir
     */
    public MyFile getLinkDir();

    /**
     * @param linkDir the linkDir to set
     */
    public void setLinkDir(MyFile linkDir);

    /**
     * @param icon the icon to set
     */
    public void setIcon(ImageIcon icon);

    /**
     * @return the iconPath
     */
    public File getIconPath();

    /**
     * @param iconPath the iconPath to set
     */
    public void setIconPath(File iconPath);

    /**
     * @return the upperCase
     */
    public boolean isUpperCase();
    /**
     * @return the fontHeaderColor
     */
    public Color getFontHeaderColor();
}
