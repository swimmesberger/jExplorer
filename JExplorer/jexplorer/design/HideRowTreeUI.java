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

import java.awt.Graphics;
import java.awt.Insets;
import java.util.HashSet;
import java.util.Set;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public class HideRowTreeUI extends BasicTreeUI
{

    private Set<Integer> hiddenRows = new HashSet<Integer>();

    public void hideRow(int row)
    {
        hiddenRows.add(row);
    }

    @Override
    protected void paintHorizontalPartOfLeg(Graphics g,java.awt.Rectangle clipBounds, Insets insets, java.awt.Rectangle bounds,TreePath path, int row, boolean isExpanded,boolean hasBeenExpanded, boolean isLeaf)
    {
        if (!hiddenRows.contains(row))
        {
            super.paintHorizontalPartOfLeg(g, clipBounds, insets, bounds,
            path, row, isExpanded, hasBeenExpanded, isLeaf);
        }
    }

    @Override
    protected void paintRow(Graphics g, java.awt.Rectangle clipBounds,Insets insets, java.awt.Rectangle bounds, TreePath path, int row,boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf)
    {
        if (!hiddenRows.contains(row))
        {
            super.paintRow(g, clipBounds, insets, bounds, path, row,
            isExpanded, hasBeenExpanded, isLeaf);
        }
    }

    @Override
    protected void paintExpandControl(Graphics g, java.awt.Rectangle clipBounds,Insets insets, java.awt.Rectangle bounds, TreePath path, int row,boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf)
    {
        if (!hiddenRows.contains(row))
        {
            super.paintExpandControl(g, clipBounds, insets, bounds,
            path, row, isExpanded, hasBeenExpanded, isLeaf);
        }
    }
}
