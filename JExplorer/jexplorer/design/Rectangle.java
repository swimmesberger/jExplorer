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
package jexplorer.design;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 *
 * @author swimmesberger
 */
public class Rectangle
{
    private int width;
    private int height;
    private boolean filled;
    private Color color;
    private Color borderColor;
    private int x;
    private int y;
    private float stroke;
    private float transp;

    public Rectangle(int x, int y, int width, int height, Color c, boolean filled, float stroke, Color borderColor, float transp)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = c;
        this.stroke = stroke;
        this.filled = filled;
        this.borderColor = borderColor;
        this.transp = transp;
    }
    
    public void draw(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;
        Color oldCol = g2d.getColor();
        Stroke oldStroke = g2d.getStroke();
        Composite normal = g2d.getComposite();
        if(transp < 1)
        {
            Composite trans = AlphaComposite.getInstance(
                                  AlphaComposite.SRC_OVER, transp);
            g2d.setComposite(trans);
        }
        g2d.setColor(color);
        
        g2d.setStroke(new BasicStroke(stroke));
        if(isFilled())
        {
            g2d.fillRect(getX(), getY(), getWidth(), getHeight());
            g2d.setComposite(normal);
            g2d.setColor(borderColor);
        }
        else
        {
            g2d.drawRect(getX(), getY(), getWidth(), getHeight());
            g2d.setComposite(normal);
            g2d.setColor(borderColor);
        }
        g2d.drawLine(getX(), getY(), getX()+getWidth(), getY());
        g2d.drawLine(getX(), getY()+getHeight(), getX()+getWidth(), getY()+getHeight());
        g2d.drawLine(getX(), getY(), getX(), getY()+getHeight());
        g2d.drawLine(getX()+getWidth(), getY(), getX()+getWidth(), getY()+getHeight());
        g2d.setColor(oldCol);
        g2d.setStroke(oldStroke);
    }

    /**
     * @return the width
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width)
    {
        this.width = width;
    }

    /**
     * @return the height
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height)
    {
        this.height = height;
    }

    /**
     * @return the filled
     */
    public boolean isFilled()
    {
        return filled;
    }

    /**
     * @param filled the filled to set
     */
    public void setFilled(boolean filled)
    {
        this.filled = filled;
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
     * @return the x
     */
    public int getX()
    {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x)
    {
        this.x = x;
    }

    /**
     * @return the y
     */
    public int getY()
    {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y)
    {
        this.y = y;
    }

    /**
     * @return the stroke
     */
    public float getStroke()
    {
        return stroke;
    }

    /**
     * @param stroke the stroke to set
     */
    public void setStroke(float stroke)
    {
        this.stroke = stroke;
    }

    @Override
    public String toString()
    {
        return "color=" + this.color.toString() + " height=" + this.height + " width=" + this.width + " x=" + this.x + " y=" + this.y + " filled=" + this.filled + " stroke=" + this.stroke;
    }


}
