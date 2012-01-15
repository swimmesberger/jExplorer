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

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author sWimmesberger
 */
public class StartCellEditThread extends Thread
{
    public static StartCellEditThread thread;

    private FileTable table;
    private int row;
    private int colum;

    private boolean edit = false;

    private StartCellEditThread(FileTable table, int row, int colum)
    {
        super("StartCellEditThread");
        this.table = table;
        this.row = row;
        this.colum = colum;
    }

    @Override
    public void run()
    {
        try
        {
            StartCellEditThread.sleep(1000);
            boolean editCellAt = table.editCellAt(row, colum);
            edit = true;
        }catch(InterruptedException ex)
        {

        }
    }



    public static StartCellEditThread startEdit(FileTable table, int row, int colum)
    {
        if(thread == null || !thread.isAlive())
        {
            if(thread != null)thread.interrupt();
            thread = new StartCellEditThread(table, row, colum);
            thread.start();
        }
        return thread;
    }

}
