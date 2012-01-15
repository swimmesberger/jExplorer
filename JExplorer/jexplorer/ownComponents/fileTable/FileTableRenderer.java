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

import javax.swing.JTable;
import javax.swing.JLabel;
import java.awt.Component;
import javax.swing.RowSorter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import jexplorer.util.UtilBox;

/**
 * JDIC API demo class.
 * <p>
 * A redefined TableCellRenderer class.
 */
public class FileTableRenderer extends DefaultTableCellRenderer
{

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
    boolean isSelected, boolean hasFocus, int row, int column)
    {
        Component component = super.getTableCellRendererComponent(table, value,
        isSelected, hasFocus, row, column);
        if(value == null)
        {
            return null;
        }
        if (value != null && value instanceof FileIcon)
        {
            // Set the Name column as left aligned.
            FileIcon fileIcon = (FileIcon) value;
            ((JLabel) component).setText(fileIcon.getFileName());
            ((JLabel) component).setHorizontalAlignment(JLabel.LEFT);
            ((JLabel) component).setIcon(fileIcon.getIcon());
            return component;
        }
        else
        {
            if(column == 1)
            {
                ((JLabel) component).setText(UtilBox.fileDateToFormatString((Long)value));
            }
            else if(column == 3)
            {
                RowSorter<? extends TableModel> rowSorter = table.getRowSorter();
                int rowTemp = row;
                if(rowSorter != null)
                {
                    rowTemp = rowSorter.convertRowIndexToModel(row);
                }
                FileIcon valueAt = (FileIcon)table.getModel().getValueAt(rowTemp, 0);
                if(valueAt.isDirectory())
                {
                    ((JLabel) component).setText("");
                }
                else
                {
                    ((JLabel) component).setText(UtilBox.fileSizeToString((Long)value));
                }
            }
            else
            {
                ((JLabel) component).setText(value.toString());
            }
            if(column == 3)
            {
                ((JLabel) component).setHorizontalAlignment(JLabel.RIGHT);
            }
            else
            {
                ((JLabel) component).setHorizontalAlignment(JLabel.LEFT);
            }
            ((JLabel) component).setIcon(null);
            return component;
        }
    }
}
