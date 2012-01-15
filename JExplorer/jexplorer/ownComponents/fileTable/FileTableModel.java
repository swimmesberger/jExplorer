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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.table.DefaultTableModel;
import org.fseek.plugin.interfaces.MyFile;

public class FileTableModel extends DefaultTableModel implements Serializable
{
    private String[] columNames = new String[]{"Name", "Änderungsdatum", "Typ", "Größe"};
    
    private ArrayList<MyFile> fileList;
    private ArrayList<MyFile> hiddenFiles;
    
    private boolean showHiddenFiles = true;
    
    private FileTable parent;
    
    
    public FileTableModel(boolean showHiddenFiles, FileTable parent)
    {
        this.parent = parent;
        this.showHiddenFiles = showHiddenFiles;
        fileList = new ArrayList<MyFile>();
        hiddenFiles = new ArrayList<MyFile>();
    }

    @Override
    public int getColumnCount()
    {
        return columNames.length;
    }

    @Override
    public int getRowCount()
    {
        if(fileList != null)
        {
            if(this.isShowHiddenFiles() == false)
            {
                return this.fileList.size();
            }
            return this.fileList.size() + this.hiddenFiles.size();
        }
        return 0;
    }

    @Override
    public String getColumnName(int column)
    {
        return columNames[column];
    }
    
    public void addRow( List aRowData ) 
    { 
      this.fileList.addAll(aRowData);
      this.fireTableRowsInserted( this.getRowCount() - 1, this.getRowCount()); 
    }

    @Override
    public Object getValueAt(int row, int column)
    {
        try
        {
            MyFile get = null;
            if(row > this.fileList.size()-1)
            {
                get = this.hiddenFiles.get(row-this.fileList.size());
            }
            else
            {
                get = this.fileList.get(row);
            }
            if(get.exists()==false)
            {
                this.removeRow(row);
                return null;
            }
            switch(column)
            {
                case 0:
                    Icon icon = get.getIcon(false);
                    return new FileIcon(get.getFileName(), icon, get.isDirectory());
                case 1:
                    return get.getLastModified();
                case 2:
                    return get.getType();
                case 3:
                    return get.getFileSize();
            }
        }catch(IndexOutOfBoundsException ex)
        {
            //ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int row, int column)
    {
        // To Do: update table
        MyFile get = fileList.get(row);
        if(!aValue.toString().equals(get.getFileName()))
        {
            switch(column)
            {
                case 0:
                    boolean setFileName = get.setFileName(aValue.toString());
            }
            try
            {
                fireTableRowsUpdated(row, row);
            }catch(java.lang.IndexOutOfBoundsException ex)
            {
                this.getParent().setRowSorter(null);
                fireTableRowsUpdated(row, row);
                this.getParent().intSorter();
            }
        }
    }
    

    @Override
    public void removeRow(int row)
    {
        if(row > this.fileList.size()-1)
        {
            this.hiddenFiles.remove(row-this.fileList.size());
        }
        else
        {
            this.fileList.remove(row);
        }
        try
        {
            fireTableRowsInserted(row, row);
        }catch(java.lang.IndexOutOfBoundsException ex)
        {
            this.getParent().setRowSorter(null);
            fireTableRowsInserted(row, row);
            this.getParent().intSorter();
        }
    }
    
    
    
    public void updateFiles()
    {
        parent = FileTable.create(getParent().getFile(), getParent().getFrame());
    }

    public String[] getColumNames()
    {
        return columNames;
    }
    
    
    
    @Override
    public boolean isCellEditable(int row, int column) 
    {
        if(column == 0)
        {
            return true;
        }
        return false;
    }
    
    
    
    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        switch(columnIndex)
        {
            case 0:
                return FileIcon.class;
            case 1:
                return Long.class;
            case 2:
                return String.class;
            case 3:
                return Long.class;
        }
        return Object.class;
    }
    
    
    
    public int addFile(MyFile file)
    {
        if(file.isHidden())
        {
            this.hiddenFiles.add(file);
        }
        else
        {
            this.fileList.add(file);
        }
        int rowCount = this.getRowCount();
        try
        {
            fireTableRowsInserted(rowCount-1, rowCount);
        }catch(java.lang.IndexOutOfBoundsException ex)
        {
            this.getParent().setRowSorter(null);
            fireTableRowsInserted(rowCount-1, rowCount);
            this.getParent().intSorter();
            return this.getParent().getRowSorter().convertRowIndexToView(rowCount-1);
        }
        return rowCount;
    }
    
    public MyFile getFile(int row)
    {
        MyFile get = null;
        if(row > this.fileList.size()-1)
        {
            get = this.hiddenFiles.get(row-this.fileList.size());
        }
        else
        {
            get = this.fileList.get(row);
        }
        return get;
    }
    
    public boolean removeFile(MyFile file)
    {
        boolean flag = false;
        flag = hiddenFiles.remove(file);
        if(flag == false)
        {
            flag = fileList.remove(file);
        }
        if(flag == false)
        {
            return false;
        }
        try
        {
            fireTableDataChanged();
        }catch(java.lang.IndexOutOfBoundsException ex)
        {
            this.getParent().setRowSorter(null);
            fireTableDataChanged();
            this.getParent().intSorter();
        }
        return flag;
    }
    
    public boolean removeFile(String path)
    {
        int fileRow = getFileRow(path);
        if(fileRow != -1)
        {
            removeRow(fileRow);
            return true;
        }
        return false;
    }
    
    public int getFileRow(MyFile file)
    {
        return getFileRow(file.getAbsolutePath());
    }
    
    public int getFileRow(String path)
    {
        for(int i = 0; i<this.getRowCount(); i++)
        {
            MyFile f = this.getFile(i);
            if(f.getAbsolutePath().equals(path))
            {
                return i;
            }
        }
        return -1;
    }
    
    
    public void clear()
    {
        int rowCount = this.getRowCount();
        this.fileList.clear();
        this.hiddenFiles.clear();
        fireTableDataChanged();
    }

    /**
     * @return the showHiddenFiles
     */
    public boolean isShowHiddenFiles()
    {
        return showHiddenFiles;
    }

    /**
     * @param showHiddenFiles the showHiddenFiles to set
     */
    public void setShowHiddenFiles(boolean showHiddenFiles)
    {
        this.showHiddenFiles = showHiddenFiles;
    }

    /**
     * @return the parent
     */
    public FileTable getParent()
    {
        return parent;
    }
}
