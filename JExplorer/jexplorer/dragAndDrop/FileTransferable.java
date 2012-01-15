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
package jexplorer.dragAndDrop;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jexplorer.fileSystem.LocalFile;
import jexplorer.ownComponents.fileTable.FileTableModel;
import org.fseek.plugin.interfaces.MyFile;

public class FileTransferable implements Transferable
{
    public static DataFlavor modelFlavor = new DataFlavor(FileTableModel.class, "FileTableModel");
    private MyFile[] temp;
    private int action;
    private FileTableModel model;

    public FileTransferable(MyFile[] temp, FileTableModel model) throws IOException
    {
        this.temp = temp;
        this.model = model;   
    }
    
    
    

    public Object getTransferData(DataFlavor flavor)
    {
        List list = new ArrayList();
        for(MyFile f : temp)
        {
            LocalFile f1 = (LocalFile)f;
            list.add(f1.getFile());
        }
        return list;
    }
    

    public DataFlavor[] getTransferDataFlavors()
    {
        DataFlavor[] df = new DataFlavor[2];
        df[0] = DataFlavor.javaFileListFlavor;
        return df;
    }
    

    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
        if (flavor == DataFlavor.javaFileListFlavor)
        {
            return true;
        }
        return false;
    }



    /**
     * @return the action
     */
    public int getAction()
    {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(int action)
    {
        this.action = action;
    }
    
    
}