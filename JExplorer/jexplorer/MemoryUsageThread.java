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
package jexplorer;

import java.util.ArrayList;
import javax.swing.AbstractButton;

public class MemoryUsageThread extends Thread
{
    private static ArrayList<AbstractButton> buttons = new ArrayList<AbstractButton>();
    
    public MemoryUsageThread(AbstractButton but)
    {
        super("MemoryUsageThread");
        buttons.add(but);
    }
    
    @Override
    public void run()
    {
        while(true)
        {
            if(isInterrupted())break;
            try
            {
                for(int i = 0; i<buttons.size();i++)
                {
                    AbstractButton but = null;
                    synchronized(buttons)
                    {
                        but = buttons.get(i);
                    }
                    but.setText("RAM "+Util.usedMemoryAsString()+" von "+Util.getAviableMemoryAsString()+" - cleanup");
                }
                MemoryUsageThread.sleep(1000);
            } catch (InterruptedException ex)
            {
                interrupt();
            }
        }
    }
    
    public synchronized void addButton(AbstractButton but)
    {
        buttons.add(but);
    }
}
