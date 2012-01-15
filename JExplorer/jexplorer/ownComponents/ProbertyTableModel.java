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
package jexplorer.ownComponents;

import java.util.ArrayList;
import java.util.Properties;
import javax.swing.table.DefaultTableModel;

public class ProbertyTableModel extends DefaultTableModel
{
    private ArrayList<Proberty> probs;
    private String[] colums = {"Name", "Wert"};

    public ProbertyTableModel()
    {
        probs = new ArrayList<Proberty>();
    }

    @Override
    public String getColumnName(int column)
    {
        return colums[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        return String.class;
    }

    public String[] getColums()
    {
        return colums;
    }

    @Override
    public int getColumnCount()
    {
        return colums.length;
    }

    @Override
    public int getRowCount()
    {
        if(probs == null)return 0;
        return probs.size();
    }

    @Override
    public Object getValueAt(int row, int column)
    {
        Proberty get = this.probs.get(row);
        switch(column)
        {
            case 0:
                return get.getKey();
            case 1:
                return get.getValue();
        }
        return null;
    }
    
    public void addProberty(Proberty prob)
    {
        this.probs.add(prob);
        fireTableRowsInserted(this.probs.size()-1, this.probs.size());
    }
    
}
