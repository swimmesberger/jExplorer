/*
 * www.fseek.org
 * ~Thedeath
 * 2010 - 2011
 */

package org.fseek.components;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.border.LineBorder;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public class CompEffects
{
    public ProgramColorizer colorizer;
    public CompEffects(ProgramColorizer colorizer)
    {
        this.colorizer = colorizer;
    }
    public void mouseOver(boolean bol, JComponent panel)
    {
        if(bol)
        {
            panel.setBackground(colorizer.getMouseOverBackgroundColor());
        }
        else
        {
            panel.setBackground(colorizer.getMainBackgroundColor());
        }
        panel.setOpaque(bol);
        select(bol, panel);
    }

   public void select(boolean bol, JComponent panel)
    {
        if(bol == true)
        {
            LineBorder lineBorder = new LineBorder(colorizer.getMouseOverBorderColor(), 1, true);
            panel.setBorder(lineBorder);
        }
        else
        {
            panel.setBorder(null);
        }
    }

   public void extendSelect(boolean bol, JComponent panel)
   {
       select(bol, panel);
       if(bol)
       {
           panel.setBackground(colorizer.getSelectBackgroundColor());
           LineBorder lineBorder = new LineBorder(colorizer.getSelectBorderColor(), 1, true);
           panel.setBorder(lineBorder);
       }
       else
       {
           panel.setBackground(colorizer.getMainBackgroundColor());
           panel.setBorder(null);
       }
       panel.setOpaque(bol);
   }
   
   public static Color getRecommendedFontColor(boolean light)
   {
       if(light)
       {
           return new Color(76, 96, 145);
       }
       return new Color(30,50,135);
   }
   
   public static Font getRecommendedFont()
   {
       return new java.awt.Font("Segoe UI", 0, 11);
   }

}
