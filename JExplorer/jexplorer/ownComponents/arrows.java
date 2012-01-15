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

import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import jexplorer.MainFrame;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public class arrows extends javax.swing.JPanel
{

    /** Creates new form arrows */
    public arrows()
    {
        initComponents();
        doImages();
        correctLayout();
    }
    
    private void doImages()
    {
        leftArrow.setIcon(new ImageIcon(MainFrame.selectedIconDirectory + File.separator + "leftArrow.png"));
        rightArrow.setIcon(new ImageIcon(MainFrame.selectedIconDirectory + File.separator + "rightArrow.png"));
        leftArrow.setDisabledIcon(new ImageIcon(MainFrame.selectedIconDirectory + File.separator + "disabledArrowLeft.png"));
        rightArrow.setDisabledIcon(new ImageIcon(MainFrame.selectedIconDirectory + File.separator + "disabledArrowRight.png"));
    }
    
    private void correctLayout()
    {
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(leftArrow)
                .addComponent(rightArrow))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(leftArrow, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
            .addComponent(rightArrow, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
        );
    }

    public JButton getLeftArrow()
    {
        return this.leftArrow;
    }

    public JButton getRightArrow()
    {
        return this.rightArrow;
    }

    public void addLeftArrowActionListener(ActionListener lis)
    {
        this.leftArrow.addActionListener(lis);
    }

    public void addRightArrowActionListener(ActionListener lis)
    {
        this.rightArrow.addActionListener(lis);
    }

    public void setEnabledLeftArrow(boolean bol)
    {
        this.leftArrow.setEnabled(bol);
    }

    public void setEnabledRightArrow(boolean bol)
    {
        this.rightArrow.setEnabled(bol);
    }

    public boolean isEnabledRightArrow()
    {
        return this.rightArrow.isEnabled();
    }

    public boolean isEnabledLeftArrow()
    {
        return this.leftArrow.isEnabled();
    }
    
    private void initComponentsB() 
    {

        leftArrow = new javax.swing.JButton();
        rightArrow = new javax.swing.JButton();

        setOpaque(false);

        leftArrow.setToolTipText("Zurück");
        leftArrow.setBorder(null);
        leftArrow.setBorderPainted(false);
        leftArrow.setContentAreaFilled(false);
        leftArrow.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        leftArrow.setFocusPainted(false);

        rightArrow.setToolTipText("Vorwärts");
        rightArrow.setBorder(null);
        rightArrow.setBorderPainted(false);
        rightArrow.setContentAreaFilled(false);
        rightArrow.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rightArrow.setFocusPainted(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(leftArrow)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rightArrow))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(leftArrow, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
            .addComponent(rightArrow, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
        );
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        leftArrow = new javax.swing.JButton();
        rightArrow = new javax.swing.JButton();

        setOpaque(false);

        leftArrow.setToolTipText("Zurück");
        leftArrow.setBorder(null);
        leftArrow.setBorderPainted(false);
        leftArrow.setContentAreaFilled(false);
        leftArrow.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        leftArrow.setFocusPainted(false);

        rightArrow.setToolTipText("Vorwärts");
        rightArrow.setBorder(null);
        rightArrow.setBorderPainted(false);
        rightArrow.setContentAreaFilled(false);
        rightArrow.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rightArrow.setDefaultCapable(false);
        rightArrow.setFocusPainted(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(leftArrow)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rightArrow))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(leftArrow, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
            .addComponent(rightArrow, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton leftArrow;
    private javax.swing.JButton rightArrow;
    // End of variables declaration//GEN-END:variables
}
