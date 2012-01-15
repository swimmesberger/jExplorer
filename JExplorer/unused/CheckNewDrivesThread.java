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
package unused;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.FileSystemView;
import jexplorer.ownComponents.ComputerPanel;

public class CheckNewDrivesThread extends Thread
{
    private ComputerPanel panel;
    public CheckNewDrivesThread(ComputerPanel panel)
    {
        super("CheckNewDrivesThread");
        this.panel = panel;
    }
    
    @Override
    public void run()
    {
        while(true)
        {
            if(isInterrupted())break;
            ArrayList<File> oldRoots = this.panel.getRoots();
            File[] newRoots = File.listRoots();
            if(newRoots.length != oldRoots.size())
            {
                int count = 0;
                for(File f : newRoots)
                {
                    File oldF = oldRoots.get(count);
                    if(!f.getAbsoluteFile().equals(oldF.getAbsoluteFile()))
                    {
                        this.panel.removeHarddrive(oldF);
                        this.panel.addHarddrive(f);
                    }
                    count++;
                }
            }
            try
            {
                CheckNewDrivesThread.sleep(2000);
            } catch (InterruptedException ex)
            {
                Logger.getLogger(CheckNewDrivesThread.class.getName()).log(Level.SEVERE, null, ex);
                interrupt();
            }
        }
    }
    
    public void setComputerPanel(ComputerPanel panel)
    {
        this.panel = panel;
    }
}
