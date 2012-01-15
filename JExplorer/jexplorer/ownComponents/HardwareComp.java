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

import jexplorer.util.DefaultMainView;
import java.awt.Color;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JProgressBar;
import jexplorer.MainFrame;
import jexplorer.fileSystem.LocalFile;
import jexplorer.ownComponents.fileTable.FileTablePanel;
import org.fseek.components.SelectHandler;
import org.fseek.components.ViewComponent;
import org.fseek.plugin.interfaces.MainView;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public class HardwareComp extends ViewComponent implements Runnable
{
    private long freeSpace = -1;
    private long totalSpace = -1;

    private File drive;

    private JProgressBar progressBar;

    
    private boolean mainDrive;
    
    private MainFrame frame;
    /** Creates new form HardwareComp */
    public HardwareComp(File drive, SelectHandler sh, MainFrame frame)
    {
        super(sh, frame.getCompEffects());
        this.mainDrive = false;
        this.frame = frame;
        this.drive = drive;
        progressBar = new JProgressBar();
        progressBar.setOpaque(false);
        progressBar.setForeground(new Color(5, 129, 157));
        super.progressBarPanel.add(progressBar);
        Thread t = new Thread(this);
        t.start();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        iconLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        progressBarPanel = new javax.swing.JPanel();
        spaceLabel = new javax.swing.JLabel();

        setOpaque(false);

        iconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jexplorer/ownComponents/images/harddriveIconWin_1.png"))); // NOI18N

        jPanel1.setOpaque(false);

        nameLabel.setFont(new java.awt.Font("Segoe UI", 0, 12));
        nameLabel.setText("Name");

        progressBarPanel.setOpaque(false);
        progressBarPanel.setLayout(new java.awt.GridLayout(1, 0));

        spaceLabel.setFont(new java.awt.Font("Segoe UI", 0, 12));
        spaceLabel.setText("undefined");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(nameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progressBarPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                    .addComponent(spaceLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(spaceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(iconLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(iconLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
     */
    
    
    @Override
    public void mouseClick(ImageIcon icon)
    {
        LocalFile locFile = new LocalFile(drive);
        MainView fileTableView = new DefaultMainView(new FileTablePanel(locFile, this.frame), this.getDriveName(), icon);
        frame.setMainView(fileTableView, false);
    }

    public void setDriveName(String name)
    {
        super.setCompName(name);
    }

    public String getDriveName()
    {
        return super.getCompName();
    }

    public long getTotalSpace()
    {
        return this.totalSpace;
    }

    public long getFreeSpace()
    {
        return this.freeSpace;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel iconLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JPanel progressBarPanel;
    private javax.swing.JLabel spaceLabel;
    // End of variables declaration//GEN-END:variables

    public void run()
    {
        this.totalSpace = this.getDrive().getTotalSpace();
        this.freeSpace = this.getDrive().getFreeSpace();
        String goodShowSize = getGoodShowSize(this.freeSpace);
        String goodShowSize1 = getGoodShowSize(this.totalSpace);
        if(goodShowSize == null || goodShowSize1 == null)
        {
            super.spaceLabel.setVisible(false);
            super.progressBarPanel.setVisible(false);
            fixLayout();
        }
        else
        {
            super.spaceLabel.setVisible(true);
            super.progressBarPanel.setVisible(true);
            super.spaceLabel.setText(goodShowSize + " frei von "  + goodShowSize1);
        }
        double temp = this.totalSpace / 100;
        double compVal = this.totalSpace - this.freeSpace;
        double percent = compVal / temp;
        this.progressBar.setValue((int) percent);
    }

    private String getGoodShowSize(long size)
    {
        String s = null;
        if(size == 0)
        {
            return null;
        }
        double i = size;
        /*
        if(size > 1000000000000)
        {
            i = (long) (size / 1000000000000);
            s = "TB";
        }*/
        if(size > 1000000000)
        {
            i = (size / 1000000000.0);
            s = "GB";
        }
        else if(size > 1000000)
        {
            i = (size / 1000000.0);
            s = "MB";
        }
        else if(size > 1000)
        {
            i = (size / 1000.0);
            s = "kB";
        }
        return (Math.round(i * 100) / 100.0)  + " " + s;
    }

    private void fixLayout()
    {
        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(super.jPanel1);
        super.jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    //.addGap(500)
                    .addComponent(super.nameLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30)
                .addComponent(super.nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

    }

    /**
     * @return the drive
     */
    public File getDrive()
    {
        return drive;
    }

    /**
     * @return the mainDrive
     */
    public boolean isMainDrive()
    {
        return mainDrive;
    }

    /**
     * @param mainDrive the mainDrive to set
     */
    public void setMainDrive(boolean mainDrive)
    {
        this.mainDrive = mainDrive;
    }
}