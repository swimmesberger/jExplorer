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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.FocusEvent;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author sWimmesberger
 */
public class FileTableCellEditor  extends AbstractCellEditor implements TableCellEditor
{
    private JComponent component = new JTextField();
    private Object object;
    private JTable table;
    private int row;
    private int colum;

    public Object getCellEditorValue()
    {
        if(object instanceof FileIcon)
        {
            FileIcon ic = (FileIcon)object;
            ic.setFileName(((JTextField)component).getText());
        }
        else
        {
            object = ((JTextField)component).getText();
        }
        return object;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
       if (object == null)
       {
           object = value;
       }
       if(object instanceof FileIcon)
       {
           FileIcon ic = (FileIcon)object;
           JPanel panel = new JPanel();
           panel.setOpaque(true);
           panel.setBorder(BorderFactory.createEmptyBorder());
           BorderLayout borderLayout = new BorderLayout(0,5);
           panel.setLayout(borderLayout);
           JLabel icLabel = new JLabel(ic.getIcon());
           panel.add(icLabel, BorderLayout.WEST);
           panel.add(component, BorderLayout.CENTER);
           ((JTextField)component).setText(value.toString());
           component.requestFocus();
           return panel;
       }
       component.requestFocus();
       return component;
    }

    private void focusLost(FocusEvent e)
    {
        Object cellEditorValue = getCellEditorValue();
        table.getModel().setValueAt(cellEditorValue, row, colum);
    }

}
