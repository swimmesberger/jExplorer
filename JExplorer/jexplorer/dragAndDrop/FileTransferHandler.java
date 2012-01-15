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
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.TransferHandler;
import javax.swing.table.TableModel;
import jexplorer.fileSystem.LocalFile;
import jexplorer.ownComponents.fileTable.FileTable;
import jexplorer.ownComponents.fileTable.FileTableModel;
import jexplorer.ownComponents.fileTransferDialog.FileTransferDialog;
import org.fseek.plugin.interfaces.MyFile;
import sun.awt.datatransfer.TransferableProxy;

public class FileTransferHandler extends TransferHandler
{
    private FileTable fileTable;
    public FileTransferHandler(FileTable fileTable)
    {
        this.fileTable = fileTable;
    }

    
    
    @Override
    public boolean canImport(TransferSupport support)
    {
        DataFlavor[] dataFlavors = support.getDataFlavors();
        DataFlavor fl = dataFlavors[0];
        String mimeType = fl.getMimeType();
        if(fl == DataFlavor.javaFileListFlavor || mimeType.contains("application/x-java-file-list"))
        {
            JTable.DropLocation loc = (JTable.DropLocation)support.getDropLocation();
            if(!loc.isInsertRow())
            {
                int row = loc.getRow();
                row = fileTable.getRowSorter().convertRowIndexToModel(row);
                FileTableModel model = (FileTableModel)fileTable.getModel();
                MyFile file = model.getFile(row);
                if(!file.isDirectory())
                {
                    return false;
                }
                try
                {
                    MyFile[] arr = getFiles(support.getTransferable());
                    for(MyFile f : arr)
                    {
                        if(f.getAbsolutePath().equals(file.getAbsolutePath()))
                        {
                            return false;
                        }
                    }
                } catch (UnsupportedFlavorException ex)
                {
                    Logger.getLogger(FileTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex)
                {
                    Logger.getLogger(FileTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return true;
        }
        else if(fl == DataFlavor.stringFlavor)
        {
            System.out.println("String flavor !");
        }
        return false;
    }

    // TODO DragAndDrop between 2 MainFrames
    @Override
    public boolean importData(TransferSupport support)
    {
        Transferable t = support.getTransferable();
        // didnt find other solution to get importData known about the action from copy/paste - clipboard
        if(t instanceof TransferableProxy)
        {
            try
            {
                TransferableProxy fileTrans = (TransferableProxy)t;
                Field privateStringField = TransferableProxy.class.getDeclaredField("transferable");
                privateStringField.setAccessible(true);
                FileTransferable fieldValue = (FileTransferable) privateStringField.get(fileTrans);
                return importData(support, fieldValue.getAction());
            } catch (IllegalArgumentException ex)
            {
                Logger.getLogger(FileTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex)
            {
                Logger.getLogger(FileTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchFieldException ex)
            {
                Logger.getLogger(FileTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex)
            {
                Logger.getLogger(FileTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        int row = -1;
        boolean insert = false;
        if(support.isDrop())
        {
            JTable.DropLocation loc = (JTable.DropLocation)support.getDropLocation();
            row = loc.getRow();
            try
            {
                RowSorter<? extends TableModel> rowSorter = fileTable.getRowSorter();
                row = rowSorter.convertRowIndexToModel(row);
            }catch(Exception ex)
            {
                
            }
            insert = loc.isInsertRow();
        }
        FileTableModel model = (FileTableModel)fileTable.getModel();
        MyFile file = null;
        if(insert || row == -1)
        {
            file = fileTable.getFile();
        }
        else
        { 
            file = model.getFile(row);
        }
        try
        {
            MyFile[] arr = getFiles(t);
            if(support.isDrop())
            {
                FileTransferDialog dia = null;
                if(support.getDropAction() == COPY)
                {
                    dia = new FileTransferDialog(this.fileTable.getFrame(), true, arr, file, FileTransferDialog.ACTION_COPY, fileTable.getTableModel());
                }
                else
                {
                    dia = new FileTransferDialog(this.fileTable.getFrame(), true, arr, file, FileTransferDialog.ACTION_MOVE, fileTable.getTableModel());
                }
                dia.setVisible(true);
            }
            return true;
        }
        catch(Exception ex)
        {
             Logger.getLogger(FileTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
 
    public boolean importData(TransferSupport support, int action)
    {
        Transferable t = support.getTransferable();
        MyFile file = fileTable.getFile();
        try
        {
            MyFile[] arr = getFiles(t);
            FileTransferDialog dia = null;
            if(action == COPY)
            {
                dia = new FileTransferDialog(this.fileTable.getFrame(), true, arr, file, FileTransferDialog.ACTION_COPY, fileTable.getTableModel());
            }
            else
            {
                dia = new FileTransferDialog(this.fileTable.getFrame(), true, arr, file, FileTransferDialog.ACTION_MOVE, fileTable.getTableModel());
            }
            dia.setVisible(true);
            return true;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public int getSourceActions(JComponent c)
    {
        return COPY_OR_MOVE;
    }
    
    public static MyFile[] getFiles(Transferable t) throws UnsupportedFlavorException, IOException, java.lang.ClassCastException
    {
        Object transferData = t.getTransferData(DataFlavor.javaFileListFlavor);
        if(transferData instanceof ArrayList)
        {
            ArrayList list = (ArrayList)transferData;
            MyFile[] arr = getFiles(list);
            return arr;
        }
        // need to do that for windows explorer support, they send me a Arrays$ArrayLidt didnt find an other solution
        else
        {
            ArrayList list = new ArrayList();
            list.addAll((Collection) transferData);
            MyFile[] arr = getFiles(list);
            return arr;
        }
    }
    

    
    public static MyFile[] getFiles(ArrayList list)
    {
        MyFile[] arr = new MyFile[list.size()];
        for(int i = 0; i<list.size(); i++)
        {
            arr[i] = new LocalFile((File)list.get(i));
        }
        return arr;
    }
}
