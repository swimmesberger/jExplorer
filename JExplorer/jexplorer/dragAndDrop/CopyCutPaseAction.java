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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.UIManager;
import jexplorer.ownComponents.fileTable.FileTable;
import jexplorer.ownComponents.fileTable.FileTableModel;
import org.fseek.plugin.interfaces.MyFile;
import sun.swing.UIAction;

public class CopyCutPaseAction extends UIAction
{

    private FileTable fileTable;

    public CopyCutPaseAction(FileTable fileTable, String name)
    {
        super(name);
        this.fileTable = fileTable;
    }
    
    public CopyCutPaseAction(String name) 
    {
        super(name);
    }

    
    private MyFile[] getFiles()
    {
        int[] selectedRowsImpl = fileTable.getSelectedRowsForModel();
        if(selectedRowsImpl == null)return null;
        FileTableModel model = (FileTableModel) this.fileTable.getModel();
        MyFile[] files = new MyFile[selectedRowsImpl.length];
        int count = 0;
        for (int i : selectedRowsImpl)
        {
            files[count] = model.getFile(i);
            count++;
        }
        return files;
    }

    public void setClipboardContents(MyFile[] temp)
    {
        try
        {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new FileTransferable(temp, fileTable.getTableModel()), fileTable);
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public boolean isEnabled(Object sender)
    {
        if (sender instanceof JComponent
        && ((JComponent) sender).getTransferHandler() == null)
        {
            return false;
        }

        return true;
    }

    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        if (src instanceof JComponent)
        {
            JComponent c = (JComponent) src;
            TransferHandler th = fileTable.getTransferHandler();
            Clipboard clipboard = getClipboard(c);
            String name = (String) getValue(Action.NAME);
            Transferable trans = null;
            try
            {
                if (!"paste".equals(name))
                {
                    MyFile[] files = getFiles();
                    if(files == null)
                    {
                        return;
                    }
                    trans = new FileTransferable(files, fileTable.getTableModel());
                }
            } catch (IOException ex)
            {
                Logger.getLogger(CopyCutPaseAction.class.getName()).log(Level.SEVERE, null, ex);
            }

            // any of these calls may throw IllegalStateException
            try
            {
                if ((clipboard != null) && (th != null) && (name != null))
                {
                    if ("cut".equals(name))
                    {
                        FileTransferable fileTrans = (FileTransferable)trans;
                        fileTrans.setAction(TransferHandler.MOVE);
                        clipboard.setContents(trans, this.fileTable);
                        th.exportToClipboard(c, clipboard, TransferHandler.MOVE);
                        return;
                    }
                    else if ("copy".equals(name))
                    {
                        FileTransferable fileTrans = (FileTransferable)trans;
                        fileTrans.setAction(TransferHandler.COPY);
                        clipboard.setContents(trans, this.fileTable);
                        th.exportToClipboard(c, clipboard, TransferHandler.COPY);
                        return;
                    }
                    else if ("paste".equals(name))
                    {
                        trans = clipboard.getContents(null);
                    }
                }
            } catch (IllegalStateException ise)
            {
                // clipboard was unavailable
                UIManager.getLookAndFeel().provideErrorFeedback(c);
                return;
            }

            // this is a paste action, import data into the component
            if (trans != null)
            {
                if(trans instanceof FileTransferable && th instanceof FileTransferHandler)
                {
                    FileTransferable fileTrans = (FileTransferable)trans;
                    FileTransferHandler fileTh = (FileTransferHandler)th;
                    fileTh.importData(new TransferSupport(c, fileTrans), fileTrans.getAction());
                }
                else
                {
                    th.importData(new TransferSupport(c, trans));
                }
            }
        }
    }

    /**
     * Returns the clipboard to use for cut/copy/paste.
     */
    private Clipboard getClipboard(JComponent c)
    {
        return Toolkit.getDefaultToolkit().getSystemClipboard();
    }
}
