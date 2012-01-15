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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import jexplorer.MainFrame;
import jexplorer.fileSystem.FileExistsException;
import jexplorer.fileSystem.LocalFile;
import org.fseek.plugin.interfaces.MyFile;
import jexplorer.ownComponents.GradientPanel;
import jexplorer.ownComponents.fileTable.FileTableModel;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public class FileTransferDialog extends javax.swing.JDialog implements Runnable
{
    public static final int ACTION_MOVE = 1;
    public static final int ACTION_COPY = 2;
    
    public static final int MODEL_ACTION_REMOVE = 1;
    public static final int MODEL_ACTION_ADD = 2;
    public static final int MODEL_ACTION_ADD_REM_DEPEND_MODEL = 3;
    
    // preload all icons for faster clicking
    private ImageIcon lessIcon = new ImageIcon(MainFrame.selectedIconDirectory + File.separator + "lessDetail_off.png");
    private ImageIcon moreIcon = new ImageIcon(MainFrame.selectedIconDirectory + File.separator + "moreDetail_off.png");
    private ImageIcon lessIcon_focus = new ImageIcon(MainFrame.selectedIconDirectory + File.separator + "lessDetail_on.png");
    private ImageIcon moreIcon_focus = new ImageIcon(MainFrame.selectedIconDirectory + File.separator + "moreDetail_on.png");
    
    // this flag controls if the transferDialog shows detailed information or not
    private boolean details = false;
    
    // in "less information view" I only need 1 label thats that label
    private JLabel lessInfoLabel;
    
    private MyFile[] from;
    private MyFile to;
    
    private int action = 1;
    private FileTableModel fileTable;
    private FileTableModel fileTableFrom;
    
    private Thread copyThread;
    private ArrayList<Thread> runningThreads = new ArrayList<Thread>();

    // parent file - to FileTableModel
    private MyFile modelWFile = null;
    private MyFile modelFromFile = null;
    

    
    
    /** Creates new form FileTransferDialog */
    public FileTransferDialog(JFrame parent, boolean modal, MyFile[] from, MyFile to, int action)
    {
        super(parent, modal);
        initComponents();
        this.from = from;
        this.to = to;
        this.action = action;
        this.setLocationRelativeTo(parent);
        intMain();
    }
    
    /** Creates new form FileTransferDialog with to FileTable */
    public FileTransferDialog(JFrame parent, boolean modal, MyFile[] from, MyFile to, int action, FileTableModel table)
    {
        super(parent, modal);
        initComponents();
        this.from = from;
        this.to = to;
        this.action = action;
        this.fileTable = table;
        this.setLocationRelativeTo(parent);
        intMain();
    }
    
    /** Creates new form FileTransferDialog with to and from FileTable*/
    public FileTransferDialog(JFrame parent, boolean modal, MyFile[] from, MyFile to, int action, FileTableModel tableTo, FileTableModel tableFrom)
    {
        super(parent, modal);
        initComponents();
        this.from = from;
        this.to = to;
        this.action = action;
        this.fileTable = tableTo;
        this.fileTableFrom = tableFrom;
        this.setLocationRelativeTo(parent);
        intMain();
    }
    
    private void intMain()
    {
        if(lessInfoLabel == null)
        {
            lessInfoLabel = new JLabel();
        }
        lessInfoLabel.setFont(new java.awt.Font("Segoe UI", 0, 15));
        this.setTitle(headerLabel.getText());
        progress.setSize(progress.getWidth(), 30);
        addFocusListener();
        addActionListener();
        setLessDetails();
        if(copyThread == null)
        {
            copyThread = new Thread(this);
            copyThread.setName("CopyThread");
            copyThread.start();
            runningThreads.add(copyThread);
        }
    }
    
    private void actionFiles()
    {
        String fr = from[0].getAbsolutePath();
        int lastIndexOf = fr.lastIndexOf(File.separator);
        if(lastIndexOf != -1)
        {
            fr = fr.substring(0, lastIndexOf+File.separator.length());
        }
        this.fromLabelDesc.setText(fr);
        this.toLabelDesc.setText(to.getAbsolutePath());
        int leftElements = from.length;
        int leftSize = 0;
        for(MyFile f : from)
        {
            leftSize += f.getFileSize();
        }
        this.elementsLabelDesc.setText(leftElements + " (" + leftSize + ")");
        FileTableModel model = (FileTableModel)fileTable;
        if(fileTableFrom != null)
        {
            FileTableModel modelFrom = (FileTableModel)fileTableFrom;
            this.modelFromFile = modelFrom.getParent().getFile();
        }
        this.modelWFile = model.getParent().getFile();
        int count = 0;
        for(MyFile f : from)
        {
            SpeedAnalyzerThread speeder = new SpeedAnalyzerThread(f, this.speedLabelDesc, this.progress);
            speeder.start();
            runningThreads.add(speeder);
            this.nameLabelDesc.setText(f.getFileName());
            boolean copyDirectory = copyDirectory(f, to);
            leftElements--;
            leftSize -= f.getFileSize();
            this.elementsLabelDesc.setText(leftElements + " (" + leftSize + ")");
            speeder.interrupt();
            runningThreads.remove(speeder);
            count++;
        }
    }
    
    private void updateModelTo(MyFile file, int modelAction)
    {
        FileTableModel model = (FileTableModel)fileTable;
        updateModelSt(file, modelAction, model, modelWFile);
    }
    
    public static void updateModelSt(MyFile file, int modelAction, FileTableModel model, MyFile modelWFile)
    {
        String absolutePath = file.getParentDirectory().getAbsolutePath();
        String absolutePath1 = modelWFile.getAbsolutePath();
        if(absolutePath.equals(absolutePath1))
        {
            if(modelAction == MODEL_ACTION_REMOVE)
            {
                model.removeFile(file.getAbsolutePath());
            }
            else if(modelAction == MODEL_ACTION_ADD)
            {
                model.addFile(file);
            }
            else if(modelAction == MODEL_ACTION_ADD_REM_DEPEND_MODEL)
            {
                model.addFile(file);
            }
        }
        else if(modelAction == MODEL_ACTION_ADD_REM_DEPEND_MODEL)
        {
            model.removeFile(file.getAbsolutePath());
        }
    }
    
    boolean force = false;
    private boolean copyDirectory(MyFile from, MyFile to)
    {
        if(from.getAbsolutePath().equals(to.getAbsolutePath()))
        {
            return true;
        }
        if(to.exists() && !to.isDirectory())
        {
            return false;
        }
        else
        {
            if(!to.exists())
            {
                try
                {
                    to.createDirectory();
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
        if(from.isDirectory())
        {
            try
            {
                MyFile createDirectory = null;
                try
                {
                    createDirectory = to.createDirectory(from.getFileName(), force);
                    if(createDirectory!=null)updateModelTo(createDirectory, MODEL_ACTION_ADD);
                }catch(FileExistsException ex)
                {
                    int showFileIntegrateDialog = FileIntegrateDialog.showFileIntegrateDialog(null, from, to);
                    if(showFileIntegrateDialog == JOptionPane.YES_OPTION)
                    {
                        force = true;
                        createDirectory = to.createDirectory(from.getFileName(), force);
                        if(createDirectory!=null)updateModelTo(createDirectory, MODEL_ACTION_ADD);
                    }
                    else
                    {
                        return false;
                    }
                }
                for(MyFile f : from.getFiles())
                {
                    boolean copyDirectory = copyDirectory(f, createDirectory);
                    if(copyDirectory == false)
                    {
                        return false;
                    }
                }
                if(this.action == FileTransferDialog.ACTION_MOVE)
                {
                    from.delete();
//                    if(from!=null)updateModelFrom(from, MODEL_ACTION_REMOVE);
                }
                return true;
            } catch (Exception ex)
            {
                Logger.getLogger(FileTransferDialog.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        else
        {
            boolean flag = false;
            MyFile toFile = new LocalFile(to.getAbsolutePath() + File.separator + from.getFileName());
            boolean replace = false;
            if(toFile.exists())
            {
                int showFileExistsDialog = FileExistsDialog.showFileExistsDialog(null, true, from, toFile);
                if(showFileExistsDialog == FileExistsDialog.ANSWER_noCopy || showFileExistsDialog == FileExistsDialog.ANSWER_cancel)
                {
                    return false;
                }
                else if(showFileExistsDialog == FileExistsDialog.ANSWER_letBoth)
                {
                    toFile = new LocalFile(to.getAbsolutePath() + File.separator + FileExistsDialog.getDia().getFreeFileName());
                }
                else if(showFileExistsDialog == FileExistsDialog.ANSWER_copyAndReplace)
                {
                    replace = true;
                    boolean delete = toFile.delete();
                    updateModelTo(toFile, MODEL_ACTION_REMOVE);
                }
            }
            if(this.action == FileTransferDialog.ACTION_MOVE)
            {
                flag = from.move(toFile);
                if(flag == true)
                {
                    updateModelTo(toFile, MODEL_ACTION_ADD);
//                    updateModelFrom(from, MODEL_ACTION_REMOVE);
                }
            }
            else if(this.action == FileTransferDialog.ACTION_COPY)
            {
                flag = from.copy(toFile);
                updateModelTo(toFile, MODEL_ACTION_ADD);
            }
            return flag;
        }
    }
    
    private void addActionListener()
    {
        detailsButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e)
            {
                if(details)
                {
                    setLessDetails();
                }
                else
                {
                    setMoreDetails();
                }
            }
        });
    }
    
    private void addFocusListener()
    {
        detailsButton.addFocusListener(new FocusListener() 
        {
            public void focusGained(FocusEvent e)
            {
                if(details)
                {
                    detailsButton.setIcon(lessIcon_focus);
                }
                else
                {
                    detailsButton.setIcon(moreIcon_focus);
                }
            }

            public void focusLost(FocusEvent e)
            {
                if(details)
                {
                    detailsButton.setIcon(lessIcon);
                }
                else
                {
                    detailsButton.setIcon(moreIcon);
                }
            }
        });
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
        infoPanel = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        fromLabel = new javax.swing.JLabel();
        toLabel = new javax.swing.JLabel();
        restLabel = new javax.swing.JLabel();
        elementsLabel = new javax.swing.JLabel();
        speedLabel = new javax.swing.JLabel();
        elementsLabelDesc = new javax.swing.JLabel();
        restLabelDesc = new javax.swing.JLabel();
        toLabelDesc = new javax.swing.JLabel();
        speedLabelDesc = new javax.swing.JLabel();
        fromLabelDesc = new javax.swing.JLabel();
        nameLabelDesc = new javax.swing.JLabel();
        progress = new javax.swing.JProgressBar();
        seperator = new javax.swing.JSeparator();
        footerPanel = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();
        detailsButton = new javax.swing.JButton();
        headerPanel = new GradientPanel(new Color(220,229,244), new Color(1,45,86));
        headerLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(459, 225));

        mainPanel.setPreferredSize(new java.awt.Dimension(401, 225));
        mainPanel.setRequestFocusEnabled(false);

        infoPanel.setOpaque(false);

        nameLabel.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        nameLabel.setText("Name:");

        fromLabel.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        fromLabel.setText("Von:");

        toLabel.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        toLabel.setText("Nach:");

        restLabel.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        restLabel.setText("Restdauer:");

        elementsLabel.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        elementsLabel.setText("Elemente verbleibend:");

        speedLabel.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        speedLabel.setText("Geschwindigkeit:");

        elementsLabelDesc.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        elementsLabelDesc.setText("1 (117 MB)");

        restLabelDesc.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        restLabelDesc.setText("Berechnung...");

        toLabelDesc.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        toLabelDesc.setText("E:");

        speedLabelDesc.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        speedLabelDesc.setText("97,0 MB/Sekunde");

        fromLabelDesc.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        fromLabelDesc.setText("I:\\Files\\Programme");

        nameLabelDesc.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        nameLabelDesc.setText("test.iso");

        javax.swing.GroupLayout infoPanelLayout = new javax.swing.GroupLayout(infoPanel);
        infoPanel.setLayout(infoPanelLayout);
        infoPanelLayout.setHorizontalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(elementsLabel)
                    .addComponent(restLabel)
                    .addComponent(toLabel)
                    .addComponent(speedLabel)
                    .addComponent(fromLabel)
                    .addComponent(nameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(toLabelDesc, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                    .addComponent(elementsLabelDesc, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                    .addComponent(restLabelDesc, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                    .addComponent(speedLabelDesc, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                    .addComponent(nameLabelDesc, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                    .addComponent(fromLabelDesc, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
        );
        infoPanelLayout.setVerticalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameLabelDesc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fromLabel)
                    .addComponent(fromLabelDesc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(toLabel)
                    .addComponent(toLabelDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(restLabel)
                    .addComponent(restLabelDesc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(elementsLabel)
                    .addComponent(elementsLabelDesc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(speedLabel)
                    .addComponent(speedLabelDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE))
        );

        seperator.setForeground(new java.awt.Color(223, 223, 223));

        footerPanel.setOpaque(false);

        cancelButton.setText("Abbrechen");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        detailsButton.setText("Weniger Details");
        detailsButton.setBorderPainted(false);
        detailsButton.setContentAreaFilled(false);
        detailsButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        detailsButton.setFocusPainted(false);
        detailsButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        detailsButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout footerPanelLayout = new javax.swing.GroupLayout(footerPanel);
        footerPanel.setLayout(footerPanelLayout);
        footerPanelLayout.setHorizontalGroup(
            footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, footerPanelLayout.createSequentialGroup()
                .addComponent(detailsButton, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addContainerGap())
        );
        footerPanelLayout.setVerticalGroup(
            footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(cancelButton)
                .addComponent(detailsButton))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(seperator, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
            .addComponent(footerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(infoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addComponent(infoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seperator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(footerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        headerPanel.setPreferredSize(new java.awt.Dimension(400, 40));

        headerLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        headerLabel.setText("Kopieren von 1 Element (699 MB)");

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(headerLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
                .addContainerGap())
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(headerLabel)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelButtonActionPerformed
    {//GEN-HEADEREND:event_cancelButtonActionPerformed
        for(Thread t : this.runningThreads)
        {
            t.stop();
        }
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    public void setMoreDetails()
    {
        detailsButton.setText("Weniger Details");
        detailsButton.setIcon(lessIcon_focus);
        restoreInfoPanel();
        details = true;
    }
    
    public void setLessDetails()
    {
        detailsButton.setText("Weitere Details");
        detailsButton.setIcon(moreIcon_focus);
        lessInfoPanel();
        details = false;
    }
    
    private void lessInfoPanel()
    {
        if(lessInfoLabel == null)
        {
            lessInfoLabel = new JLabel();
        }
        this.infoPanel.removeAll();
        BorderLayout borderLayout = new BorderLayout(10, 10);
        this.infoPanel.setLayout(borderLayout);
        
        this.infoPanel.add(lessInfoLabel, BorderLayout.CENTER);
        this.infoPanel.add(progress, BorderLayout.SOUTH);
        this.lessInfoLabel.setText(fromLabel.getText() + " " + fromLabelDesc.getText() + " " + toLabel.getText().toLowerCase() + " " + toLabelDesc.getText());
        this.infoPanel.repaint();
        this.pack();
        this.setSize(0, 0);
    }
    
    private void restoreInfoPanel()
    {
        this.infoPanel.removeAll();
        javax.swing.GroupLayout infoPanelLayout = new javax.swing.GroupLayout(infoPanel);
        infoPanel.setLayout(infoPanelLayout);
        infoPanelLayout.setHorizontalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(elementsLabel)
                    .addComponent(restLabel)
                    .addComponent(toLabel)
                    .addComponent(speedLabel)
                    .addComponent(fromLabel)
                    .addComponent(nameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(toLabelDesc, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                    .addComponent(elementsLabelDesc, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                    .addComponent(restLabelDesc, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                    .addComponent(speedLabelDesc, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                    .addComponent(nameLabelDesc, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                    .addComponent(fromLabelDesc, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
        );
        infoPanelLayout.setVerticalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameLabelDesc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fromLabel)
                    .addComponent(fromLabelDesc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(toLabel)
                    .addComponent(toLabelDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(restLabel)
                    .addComponent(restLabelDesc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(elementsLabel)
                    .addComponent(elementsLabelDesc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(speedLabel)
                    .addComponent(speedLabelDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE))
        );
        this.infoPanel.repaint();
        this.pack();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton detailsButton;
    private javax.swing.JLabel elementsLabel;
    private javax.swing.JLabel elementsLabelDesc;
    private javax.swing.JPanel footerPanel;
    private javax.swing.JLabel fromLabel;
    private javax.swing.JLabel fromLabelDesc;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel nameLabelDesc;
    private javax.swing.JProgressBar progress;
    private javax.swing.JLabel restLabel;
    private javax.swing.JLabel restLabelDesc;
    private javax.swing.JSeparator seperator;
    private javax.swing.JLabel speedLabel;
    private javax.swing.JLabel speedLabelDesc;
    private javax.swing.JLabel toLabel;
    private javax.swing.JLabel toLabelDesc;
    // End of variables declaration//GEN-END:variables

    public void run()
    {
        actionFiles();
        this.dispose();
    }
}
