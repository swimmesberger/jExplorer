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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.fseek.components.CompEffects;

public class MyHeaderCellRenderer extends DefaultTableCellRenderer
{

    TableCellRenderer delegateRenderer;

    MyHeaderCellRenderer(TableCellRenderer delegateRenderer)
    {
        assert delegateRenderer instanceof JLabel : "Nur mit JLabels!";
        this.delegateRenderer = delegateRenderer;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
    boolean isSelected, boolean hasFocus, int row, int column)
    {
        JLabel label = (JLabel)delegateRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        // durchsichtig
        label.setOpaque(false);
        Font font = CompEffects.getRecommendedFont();
        if(font != null)
        {
            label.setFont(font);
        }
        label.setForeground(CompEffects.getRecommendedFontColor(true));
        return label;
    }
}
