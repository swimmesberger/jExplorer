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

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import org.fseek.plugin.interfaces.LinkTreeNode;
import org.fseek.plugin.interfaces.MyFile;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public class DefaultLinkTreeNode extends DefaultMutableTreeNode implements Serializable, LinkTreeNode
{
    private MyFile linkDir;
    private Icon icon;
    private File iconPath;
    private boolean mouseOver = false;
    private boolean selected = false;
    private boolean upperCase;
    private Color fontHeaderColor;

    public DefaultLinkTreeNode(MyFile file, File iconPath, Object userObject, Color fontHeaderColor, boolean upperCase)
    {
        super(userObject);
        this.icon = new ImageIcon(iconPath.getAbsolutePath());
        this.iconPath = iconPath;
        ImageIcon imgIcon = (ImageIcon)icon;
        if(imgIcon.getIconHeight() != 17 || imgIcon.getIconWidth() != 17)
        {
            icon = new ImageIcon(imgIcon.getImage().getScaledInstance(17, 17, Image.SCALE_SMOOTH));
        }
        this.linkDir = file;
        this.upperCase = upperCase;
        this.fontHeaderColor = fontHeaderColor;
    }
    
    public DefaultLinkTreeNode(MyFile file, File iconPath, Object userObject)
    {
        super(userObject);
        this.icon = new ImageIcon(iconPath.getAbsolutePath());
        this.iconPath = iconPath;
        ImageIcon imgIcon = (ImageIcon)icon;
        if(imgIcon.getIconHeight() != 17 || imgIcon.getIconWidth() != 17)
        {
            icon = new ImageIcon(imgIcon.getImage().getScaledInstance(17, 17, Image.SCALE_SMOOTH));
        }
        this.linkDir = file;
        this.upperCase = false;
        this.fontHeaderColor = Color.BLACK;
    }
    
    public DefaultLinkTreeNode(MyFile file, Icon icon, Object userObject)
    {
        super(userObject);
        this.icon = icon;
        this.iconPath = null;
        this.linkDir = file;
        this.upperCase = false;
        this.fontHeaderColor = Color.BLACK;
    }
    
    public DefaultLinkTreeNode(File iconPath, Object userObject)
    {
        super(userObject);
        this.icon = new ImageIcon(iconPath.getAbsolutePath());
        this.iconPath = iconPath;
        ImageIcon imgIcon = (ImageIcon)icon;
        if(imgIcon.getIconHeight() != 17 || imgIcon.getIconWidth() != 17)
        {
            icon = new ImageIcon(imgIcon.getImage().getScaledInstance(17, 17, Image.SCALE_SMOOTH));
        }
        this.upperCase = false;
        this.fontHeaderColor = Color.BLACK;
    }
    
    public DefaultLinkTreeNode(File iconPath, Object userObject, Color fontHeaderColor, boolean upperCase)
    {
        super(userObject);
        this.icon = new ImageIcon(iconPath.getAbsolutePath());
        this.iconPath = iconPath;
        ImageIcon imgIcon = (ImageIcon)icon;
        if(imgIcon.getIconHeight() != 17 || imgIcon.getIconWidth() != 17)
        {
            icon = new ImageIcon(imgIcon.getImage().getScaledInstance(17, 17, Image.SCALE_SMOOTH));
        }
        this.upperCase = upperCase;
        this.fontHeaderColor = fontHeaderColor;
    }

    public Icon getIcon()
    {
        return icon;
    }

    /**
     * @return the linkDir
     */
    public MyFile getLinkDir()
    {
        return linkDir;
    }

    /**
     * @param linkDir the linkDir to set
     */
    public void setLinkDir(MyFile linkDir)
    {
        this.linkDir = linkDir;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon(ImageIcon icon)
    {
        if(icon.getIconHeight() != 16 || icon.getIconWidth() != 16)
        {
            icon = new ImageIcon(icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        }
        this.icon = icon;
    }

    /**
     * @return the iconPath
     */
    public File getIconPath()
    {
        return iconPath;
    }

    /**
     * @param iconPath the iconPath to set
     */
    public void setIconPath(File iconPath)
    {
        this.iconPath = iconPath;
        setIcon(new ImageIcon(this.iconPath.getAbsolutePath()));
    }

    /**
     * @return the upperCase
     */
    public boolean isUpperCase()
    {
        return upperCase;
    }

    /**
     * @return the fontHeaderColor
     */
    public Color getFontHeaderColor()
    {
        return fontHeaderColor;
    }


}
