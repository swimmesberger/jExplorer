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

import org.fseek.plugin.interfaces.MyFile;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public class FileExistsDialog extends javax.swing.JDialog
{
    private static FileExistsDialog dia;
    
    public static final int ANSWER_copyAndReplace = 1;
    public static final int ANSWER_noCopy = 2;
    public static final int ANSWER_letBoth = 3;
    public static final int ANSWER_cancel = -1;
    
    private MyFile fileFrom;
    private MyFile fileTo;
    
    private int answer = -1;
    
    private String freeFileName;
    /** Creates new form FileExistsDialog */
    private FileExistsDialog(java.awt.Frame parent, boolean modal, MyFile fileOne, MyFile fileTo)
    {
        super(parent, modal);
        this.fileFrom = fileOne;
        this.fileTo = fileTo;
        initComponents();
        this.setLocationRelativeTo(parent);
        this.setAlwaysOnTop(true);
        copyAndReplaceOption.setFile(fileFrom);
        noCopyOption.setFile(fileTo);
        freeFileName = checkFreeFile();
        copyBothOption.setSubTitle("Die zu kopierende Datei wird in \"" + freeFileName + "\" umbenannt.");
    }
    
    public FileExistsDialog(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();
    }
    
    public static int showFileExistsDialog(java.awt.Frame parent, boolean modal, MyFile fileOne, MyFile fileTo)
    {
        dia = new FileExistsDialog(parent, true, fileOne, fileTo);
        dia.setVisible(true);
        return dia.getAnswer();
    }
    
    public static FileExistsDialog getDia()
    {
        return dia;
    }
    
    private String checkFreeFile()
    {
        int count = 2;
        String fileName = fileFrom.getFileName();
        while(fileTo.containsFile(fileName))
        {
            fileName = formTempName(count);
            count++;
        }
        return fileName;
    }
    
    private String formTempName(int count)
    {
        String fileName = fileFrom.getFileName();
        int index = fileName.lastIndexOf(".");
        if(index != -1)
        {
            fileName = fileName.substring(0, index);
        }
        fileName = fileName + " (" + count + ")" + "." + fileFrom.getExtension();
        return fileName;
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
        mainTitle = new javax.swing.JLabel();
        subTitle = new javax.swing.JLabel();
        copyAndReplaceOption = new jexplorer.ownComponents.fileTransferDialog.CopyPanel();
        noCopyOption = new jexplorer.ownComponents.fileTransferDialog.CopyPanel();
        copyBothOption = new jexplorer.ownComponents.fileTransferDialog.CopyPanel();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Datei Kopieren");

        infoPanel.setBackground(new java.awt.Color(255, 255, 255));

        mainTitle.setFont(new java.awt.Font("Segoe UI", 0, 16));
        mainTitle.setForeground(new java.awt.Color(0, 51, 153));
        mainTitle.setText("<html>Es befindet sich bereits eine Datei desselben Namens an<br>diesem Ort.</html>");

        subTitle.setText("Klicken Sie auf die Datei, die Sie behalten möchten.");

        copyAndReplaceOption.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                copyAndReplaceOptionMouseClicked(evt);
            }
        });

        noCopyOption.setMainTitle("Nicht kopieren");
        noCopyOption.setSubTitle("<html>Es werden keine Dateien geändert. Die Folgende Datei wird im Zielordner<br>belassen:</html>");
        noCopyOption.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                noCopyOptionMouseClicked(evt);
            }
        });

        copyBothOption.setFileInfoEnabled(false);
        copyBothOption.setMainTitle("Kopieren, aber beide Dateien behalten");
        copyBothOption.setSubTitle("Die zu kopierende Datei wird in \"test (2).txt\" umbenannt.");
        copyBothOption.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                copyBothOptionMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout infoPanelLayout = new javax.swing.GroupLayout(infoPanel);
        infoPanel.setLayout(infoPanelLayout);
        infoPanelLayout.setHorizontalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(copyAndReplaceOption, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
            .addComponent(noCopyOption, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
            .addComponent(copyBothOption, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, infoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(subTitle, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
                    .addComponent(mainTitle, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE))
                .addContainerGap())
        );
        infoPanelLayout.setVerticalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(subTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(copyAndReplaceOption, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noCopyOption, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(copyBothOption, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        cancelButton.setText("Abbrechen");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(infoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap(333, Short.MAX_VALUE)
                .addComponent(cancelButton)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addComponent(infoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(cancelButton)
                .addContainerGap())
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

    private void copyAndReplaceOptionMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_copyAndReplaceOptionMouseClicked
    {//GEN-HEADEREND:event_copyAndReplaceOptionMouseClicked
        this.answer = FileExistsDialog.ANSWER_copyAndReplace;
        this.dispose();
    }//GEN-LAST:event_copyAndReplaceOptionMouseClicked

    private void noCopyOptionMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_noCopyOptionMouseClicked
    {//GEN-HEADEREND:event_noCopyOptionMouseClicked
        this.answer = FileExistsDialog.ANSWER_noCopy;
        this.dispose();
    }//GEN-LAST:event_noCopyOptionMouseClicked

    private void copyBothOptionMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_copyBothOptionMouseClicked
    {//GEN-HEADEREND:event_copyBothOptionMouseClicked
        this.answer = FileExistsDialog.ANSWER_letBoth;
        this.dispose();
    }//GEN-LAST:event_copyBothOptionMouseClicked

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelButtonActionPerformed
    {//GEN-HEADEREND:event_cancelButtonActionPerformed
        this.answer = FileExistsDialog.ANSWER_cancel;
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private jexplorer.ownComponents.fileTransferDialog.CopyPanel copyAndReplaceOption;
    private jexplorer.ownComponents.fileTransferDialog.CopyPanel copyBothOption;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel mainTitle;
    private jexplorer.ownComponents.fileTransferDialog.CopyPanel noCopyOption;
    private javax.swing.JLabel subTitle;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the freeFileName
     */
    public String getFreeFileName()
    {
        return freeFileName;
    }

    /**
     * @param freeFileName the freeFileName to set
     */
    public void setFreeFileName(String freeFileName)
    {
        this.freeFileName = freeFileName;
    }

    /**
     * @return the answer
     */
    public int getAnswer()
    {
        return answer;
    }
}