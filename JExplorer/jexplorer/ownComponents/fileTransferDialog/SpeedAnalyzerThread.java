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
package jexplorer.ownComponents.fileTransferDialog;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import org.fseek.plugin.interfaces.MyFile;

public class SpeedAnalyzerThread extends Thread
{
    private MyFile file;
    private JLabel speedLabel;
    private JProgressBar bar;
    public SpeedAnalyzerThread(MyFile file, JLabel speedLabel, JProgressBar bar)
    {
        super("SpeedAnalyzerThread");
        this.file = file;
        this.speedLabel = speedLabel;
        this.bar = bar;
    }
    
    @Override
    public void run()
    {
        while(true)
        {
            if(isInterrupted())break;
            speedLabel.setText(file.getFileTransferRate()/1024 + " MB/Sekunde");
            long percentageOfOverallBytesTransfered = Math.round(file.getLeftSize() / ((double) file.getFileSize()) * 100.0);
            bar.setValue((int)percentageOfOverallBytesTransfered);
            bar.repaint();
        }
    }
}
