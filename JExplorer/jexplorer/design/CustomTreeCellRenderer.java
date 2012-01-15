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
package jexplorer.design;


import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import jexplorer.ownComponents.folderTree.DefaultLinkTreeNode;
import jexplorer.MainFrame;

public class CustomTreeCellRenderer extends DefaultTreeCellRenderer
{

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        Font font = getFont();
        if(font == null)
        {
            font = (Font) UIManager.get("Label.font");
        }
        if ((value instanceof DefaultLinkTreeNode) && (value != null))
        {
            DefaultLinkTreeNode node = (DefaultLinkTreeNode)value;
            setIcon(node.getIcon());
            setForeground(node.getFontHeaderColor());
            if(node.isUpperCase())
            {
                String s = value.toString();
                s = s.toUpperCase();
                value = s;

                Font newFont = new Font(font.getName(), Font.BOLD, font.getSize());
                setFont(newFont);
            }
            else
            {
                Font newFont = new Font(font.getName(), Font.PLAIN, font.getSize());
                setFont(newFont);
            }
        }
        setBackground(MainFrame.colorizer.getTreePanelColor());
        //we can not call super.getTreeCellRendererComponent method, since it overrides our setIcon call and cause rendering of labels to '...' when node expansion is done
        //so, we copy (and modify logic little bit) from super class method:
        String stringValue = tree.convertValueToText(value, sel,
        expanded, leaf, row, hasFocus);

        this.hasFocus = hasFocus;
        setText(stringValue);
//        if (sel)
//        {
//            //setForeground(getTextSelectionColor());
//            CompEffects.select(true, this);
//        }
//        else
//        {
//            //setForeground(getTextNonSelectionColor());
//            CompEffects.select(false, this);
//        }
        if (!tree.isEnabled())
        {
            setEnabled(false);
        }
        else
        {
            setEnabled(true);
        }
        
        if(sel)
        {
            setBackground(new Color(140,191,242));
        }
        setComponentOrientation(tree.getComponentOrientation());
        selected = sel;
        setOpaque(true);
        return this;
    }
}
