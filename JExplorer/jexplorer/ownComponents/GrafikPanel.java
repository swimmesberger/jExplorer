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

import jexplorer.design.Rectangle;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import jexplorer.MainFrame;

/**
 *
 * @author swimmesberger
 */
public class GrafikPanel extends javax.swing.JPanel
{
    private Color color = MainFrame.colorizer.getSelectorBackgroundColor();
    private Color borderColor = MainFrame.colorizer.getSelectorBorderColor();

    private int oX;
    private int oY;

    private int tempIndex;

    private LinkedList<Rectangle> rects = new LinkedList<Rectangle>();
    private Rectangle oldRect;


    
    /** Creates new form GrafikPanel */
    public GrafikPanel()
    {
        initComponents();
    }

    @Override
    public void paint(Graphics g)
    {
        Graphics2D dg = (Graphics2D)g;
        super.paint(dg);
        for(Rectangle r : rects)
        {
            r.draw(dg);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        
    }//GEN-LAST:event_formMouseClicked

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        update(evt, true);
    }//GEN-LAST:event_formMouseDragged

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        update(evt, false);
        this.requestFocus();
    }//GEN-LAST:event_formMousePressed

    private void formKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_formKeyPressed
    {//GEN-HEADEREND:event_formKeyPressed
        int keyCode = evt.getKeyCode();
        if(keyCode == KeyEvent.VK_SHIFT)
        {
            //setShiftPressed(true);
        }
    }//GEN-LAST:event_formKeyPressed

    private void formKeyReleased(java.awt.event.KeyEvent evt)//GEN-FIRST:event_formKeyReleased
    {//GEN-HEADEREND:event_formKeyReleased
        int keyCode = evt.getKeyCode();
        if(keyCode == KeyEvent.VK_SHIFT)
        {
            //setShiftPressed(false);
        }
    }//GEN-LAST:event_formKeyReleased

    private void formMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMouseReleased
    {//GEN-HEADEREND:event_formMouseReleased
        this.clearPainted();
    }//GEN-LAST:event_formMouseReleased

    private void update(MouseEvent evt, boolean dragg)
    {
        if(this.rects.size() > 0)
        {
            oldRect = this.rects.getLast();
        }
        int x = evt.getX();
        int y = evt.getY();
        Rectangle s = null;
        s = new Rectangle(x,y,0,0,this.color, true, 1.0f, borderColor, 0.5f);
        makeBigger(s, dragg, x, y);
        repaint();
    }

    private void makeBigger(Rectangle s, boolean dragg, int x, int y)
    {
        if(dragg == false)
        {
            this.rects.add(s);
            tempIndex = this.rects.size()-1;
            this.oX = s.getX();
            this.oY = s.getY();
        }
        else
        {
            s = this.rects.get(tempIndex);
            int xDiff = 0;
            int yDiff = 0;
            if(x < this.oX)
            {
                s.setX(x);
                xDiff = x - this.oX;
                xDiff = xDiff*(-1);
            }
            else
            {
                xDiff = x - s.getX();
            }
            if(y < this.oY)
            {
                s.setY(y);
                yDiff = y - this.oY;
                yDiff = yDiff*(-1);
            }
            else
            {
                yDiff = y - s.getY();
            }
            s.setWidth(xDiff);
            s.setHeight(yDiff);
        }
    }

    public void clearPainted()
    {
        this.rects.clear();
        repaint();
    }



    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * @return the oldRect
     */
    public Rectangle getOldRect()
    {
        return oldRect;
    }

    /**
     * @param oldRect the oldRect to set
     */
    public void setOldRect(Rectangle oldRect)
    {
        this.oldRect = oldRect;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
