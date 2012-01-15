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
package jexplorer.util;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JPanel;
import jexplorer.MainFrame;
import jexplorer.fileSystem.LocalFile;
import jexplorer.ownComponents.HardwareComp;
import jexplorer.ownComponents.fileTable.FileTable;
import jexplorer.ownComponents.fileTable.FileTableModel;
import org.fseek.plugin.interfaces.MyFile;

public class SearchThread extends Thread
{
    private MainFrame frame;
    private FileTable table;
    private FileTableModel model;
    private String searchString;
    public SearchThread(MainFrame frame, FileTable table, String searchString)
    {
        this.frame = frame;
        this.table = table;
        this.model = (FileTableModel)table.getModel();
        this.searchString = searchString;
    }
    
    @Override
    public void run()
    {
        search(searchString);
        table.intSorter();
    }
    
    private ArrayList<MyFile> search(String searchString)
    {
        MyFile actualFile1 = frame.getActualFile();
        ArrayList<MyFile> files = new ArrayList<MyFile>();
        if(actualFile1 == null)
        {
            JPanel contentPanel = frame.getComputerPanel().getHarddrives().getContentPanel();
            Component[] components = contentPanel.getComponents();
            for(Component comp : components)
            {
                if(isInterrupted())break;
                if(comp instanceof HardwareComp)
                {
                    HardwareComp c = (HardwareComp)comp;
                    File drive = c.getDrive();
                    searchImpl(searchString, new LocalFile(drive), files);
                }
            }
        }
        else
        {
            searchImpl(searchString, actualFile1, files);
        }
        return files;
    }
    
    private ArrayList<MyFile> searchImpl(String searchString, MyFile file, ArrayList<MyFile> files)
    {
        try
        {
            for(MyFile f : file.getFiles())
            {
                if(isInterrupted())break;
                String fileName = f.getFileName().toLowerCase();
                String searchStringTemp = searchString.toLowerCase();
                if(fileName.contains(searchStringTemp))
                {
                    files.add(f);
                    model.addFile(f);
                }
                if(f.isDirectory())
                {
                    searchImpl(searchString, f, files);
                }
            }
        } catch (Exception ex)
        {
            //Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(file.getAbsolutePath());
        }
        return files;
    }
}
