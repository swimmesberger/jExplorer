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
package jexplorer.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import javax.swing.JTable;
import javax.swing.JViewport;
import jexplorer.MainFrame;
import jexplorer.design.Rectangle;

public class SelektorJViewport extends JViewport
{

    private Color color = MainFrame.colorizer.getSelectorBackgroundColor();
    private Color borderColor = MainFrame.colorizer.getSelectorBorderColor();
    private int oX;
    private int oY;
    private int tempIndex;
    private LinkedList<Rectangle> rects = new LinkedList<Rectangle>();
    private Rectangle oldRect;

    public SelektorJViewport()
    {
        addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                formMousePressed(evt);
            }

            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                formMouseReleased(evt);
            }

            @Override
            public void mouseClicked(MouseEvent e)
            {
                formMouseClicked(e);
            }
            
            
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter()
        {

            public void mouseDragged(java.awt.event.MouseEvent evt)
            {
                formMouseDragged(evt);
            }
        });
    }

    @Override
    public void paint(Graphics g)
    {
        Graphics2D dg = (Graphics2D) g;
        super.paint(dg);
        for (Rectangle r : rects)
        {
            r.draw(dg);
            System.out.println(r);
        }
    }
    
    private void formMouseClicked(java.awt.event.MouseEvent evt)
    {
        Component view = this.getView();
        if(view instanceof JTable)
        {
            JTable table = (JTable)view;
            table.removeRowSelectionInterval(0, table.getRowCount()-1);
        }
    }

    private void formMouseDragged(java.awt.event.MouseEvent evt)
    {
        update(evt, true);
        Component view = this.getView();
        if(view instanceof JTable)
        {
            try
            {
                JTable table = (JTable)view;
                Rectangle get = this.rects.get(0);
                int rowAtPoint = table.rowAtPoint(evt.getPoint());
                if(containsSelektor(table.getWidth(), evt.getY()))
                {
                    table.addRowSelectionInterval(rowAtPoint-1, rowAtPoint+1);
                }
                else
                {
                    table.removeRowSelectionInterval(rowAtPoint-1, rowAtPoint+1);
                }
            }catch(java.lang.IllegalArgumentException ex)
            {
                
            }
        }
    }
    
    private boolean containsSelektor(int x, int y)
    {
        Rectangle get = this.rects.get(0);
        int width = get.getWidth();
        int height = get.getHeight();
        int rX = get.getX();
        int rY = get.getY();
        System.out.println("RectY: " + rY);
        System.out.println("CompY: " + y);
        if(rY > y)
        {
            return false;
        }
        if(rX > x)
        {
            return false;
        }
        if((rY+height) >= y && (rX + width) >= x)
        {
            return true;
        }
        return false;
    }

    private void formMousePressed(java.awt.event.MouseEvent evt)
    {
        update(evt, false);
        this.requestFocus();
    }

    private void formMouseReleased(java.awt.event.MouseEvent evt)
    {
        this.clearPainted();
    }

    private void update(MouseEvent evt, boolean dragg)
    {
        if (this.rects.size() > 0)
        {
            oldRect = this.rects.getLast();
        }
        int x = evt.getX();
        int y = evt.getY();
        Rectangle s = null;
        s = new Rectangle(x, y, 0, 0, this.color, true, 1.0f, borderColor, 0.5f);
        makeBigger(s, dragg, x, y);
        repaint();
    }

    private void makeBigger(Rectangle s, boolean dragg, int x, int y)
    {
        if (dragg == false)
        {
            this.rects.add(s);
            tempIndex = this.rects.size() - 1;
            this.oX = s.getX();
            this.oY = s.getY();
        }
        else
        {
            s = this.rects.get(tempIndex);
            int xDiff = 0;
            int yDiff = 0;
            if (x < this.oX)
            {
                s.setX(x);
                xDiff = x - this.oX;
                xDiff = xDiff * (-1);
            }
            else
            {
                xDiff = x - s.getX();
            }
            if (y < this.oY)
            {
                s.setY(y);
                yDiff = y - this.oY;
                yDiff = yDiff * (-1);
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
    public Color getColor()
    {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Color color)
    {
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
}
