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

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import jexplorer.MainFrame;
import jexplorer.restarter.Restarter;
import jexplorer.update.Updater;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public class UpdateDialog extends javax.swing.JDialog implements Runnable
{
    private Updater update;
    private JFrame parent;
    
    private Thread updateThread;
    /** Creates new form UpdateDialog */
    public UpdateDialog(java.awt.Frame parent, boolean modal, Updater update) 
    {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(parent);
        this.parent = (JFrame) parent;
        this.update = update;
        intDesign();
        updateThread = new Thread(this);
        updateThread.start();
    }
    
    public UpdateDialog(java.awt.Frame parent, boolean modal) 
    {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(parent);
        this.parent = (JFrame) parent;
        this.update = null;
        intDesign();
        updateThread = new Thread(this);
        updateThread.start();
    }
    
    private void intDesign()
    {
        mainPanel.setBackground(MainFrame.colorizer.getMainBackgroundColor());
    }
    
    private void startUpdate()
    {
        try
        {
            if(this.update == null)
            {
                this.update = new Updater();
                if(!this.update.isConnected())
                {
                        this.progressBar.setValue(0);
                        statusLabel.setText("Connecting to update Server...");
                        boolean connectToUpdateServer = this.update.connectToUpdateServer();
                        this.progressBar.setValue(100);
                        if(connectToUpdateServer == false)
                        {
                            statusLabel.setText("Keine Verbindung zum Update Server möglich.");
                            return;
                        }
                }
                statusLabel.setText("Check for Update...");
                boolean checkUpdateNeeded = this.update.checkUpdateNeeded(MainFrame.version);
                if(checkUpdateNeeded == true)
                {
                    boolean askUpdate = MainFrame.askUpdate(parent);
                    if(askUpdate == true)
                    {
                        downloadUpdate(checkUpdateNeeded);
                    }
                    else
                    {
                        this.dispose();
                    }
                }
                else
                {
                    downloadUpdate(checkUpdateNeeded);
                }
            }
            else
            {
                downloadUpdate(true);
            }
        } 
        catch (ConnectException ex)
        {
            Logger.getLogger(UpdateDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void downloadUpdate(boolean checkUpdateNeeded) throws ConnectException
    {
        if(checkUpdateNeeded)
        {
            try
            {
                statusLabel.setText("New version avaiable !");
                statusLabel.setText("Downloading update...");
                File readProgrammGUI = this.update.readProgrammGUI(progressBar);
                statusLabel.setText("Downloading update finished...");
                int showConfirmDialog = JOptionPane.showConfirmDialog(this, "You have to restart now to apply the changes !\r\nDo you want to restart now ?");
                switch(showConfirmDialog)
                {
                    case JOptionPane.YES_OPTION:
                        Restarter.main(new String[]{"false",readProgrammGUI.getAbsolutePath()});
                        break;
                    case JOptionPane.NO_OPTION:
                    case JOptionPane.CANCEL_OPTION:
                        break;
                }
                cancelButton.setText("Close");
            } catch (FileNotFoundException ex)
            {
                statusLabel.setText("Updating process ended unfinished !\r\n" + ex.getLocalizedMessage());
                return;
            }
        }
        else
        {
            statusLabel.setText("Your application is up to date !");
            this.progressBar.setVisible(false);
            this.progressBar.setValue(100);
            this.pack();
            this.cancelButton.setText("Close");
        }
        this.update.setConnected(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        statusLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Updater");
        setResizable(false);

        statusLabel.setText("Status");

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 226, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainPanelLayout.createSequentialGroup()
                    .addGap(5, 5, 5)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                                .addComponent(statusLabel))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                        .addComponent(cancelButton, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addGap(5, 5, 5)))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 74, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainPanelLayout.createSequentialGroup()
                    .addGap(5, 5, 5)
                    .addComponent(statusLabel)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cancelButton)
                    .addGap(6, 6, 6)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelButtonActionPerformed
    {//GEN-HEADEREND:event_cancelButtonActionPerformed
        this.update.setCanceled(true);
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusLabel;
    // End of variables declaration//GEN-END:variables

    public void run()
    {
        startUpdate();
    }
}
