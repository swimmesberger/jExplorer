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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class UtilBox
{

    /**
     * Convenience method that returns a scaled instance of the
     * provided {@code BufferedImage}.
     *
     * @param img the original image to be scaled
     * @param targetWidth the desired width of the scaled instance,
     *    in pixels
     * @param targetHeight the desired height of the scaled instance,
     *    in pixels
     * @param hint one of the rendering hints that corresponds to
     *    {@code RenderingHints.KEY_INTERPOLATION} (e.g.
     *    {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
     *    {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
     *    {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
     * @param higherQuality if true, this method will use a multi-step
     *    scaling technique that provides higher quality than the usual
     *    one-step technique (only useful in downscaling cases, where
     *    {@code targetWidth} or {@code targetHeight} is
     *    smaller than the original dimensions, and generally only when
     *    the {@code BILINEAR} hint is specified)
     * @return a scaled version of the original {@code BufferedImage}
     */
    public static BufferedImage getScaledInstance(BufferedImage img,int targetWidth,int targetHeight,Object hint,boolean higherQuality)
    {
        int type = (img.getTransparency() == Transparency.OPAQUE)
        ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage) img;
        int w, h;
        if (higherQuality)
        {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        }
        else
        {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do
        {
            if (higherQuality && w > targetWidth)
            {
                w /= 2;
                if (w < targetWidth)
                {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight)
            {
                h /= 2;
                if (h < targetHeight)
                {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            if(hint == null)hint = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }
    
    public static BufferedImage imageToBufferedImage(Image img)
    {
        int width = img.getWidth(null); // es muss keinen ImageObserver geben
        int height = img.getHeight(null);
        BufferedImage bufImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bufImg.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return bufImg;
    }
    
    public static Icon rescaleIconIfNeeded(Icon systemIcon, int height, int width)
    {
        BufferedImage scaledInstance = null;
        if(systemIcon.getIconHeight() > height)
        {
            if(systemIcon instanceof ImageIcon)
            {
                ImageIcon tempImg = (ImageIcon)systemIcon;
                BufferedImage img = null;
                if(scaledInstance != null)
                {
                    img = scaledInstance;
                }
                else
                {
                    img = UtilBox.imageToBufferedImage(tempImg.getImage());
                }
                scaledInstance = UtilBox.getScaledInstance(img, img.getWidth(), height, null, false);
            }
        }
        if(systemIcon.getIconWidth() > width)
        {
            if(systemIcon instanceof ImageIcon)
            {
                ImageIcon tempImg = (ImageIcon)systemIcon;
                BufferedImage img = null;
                if(scaledInstance != null)
                {
                    img = scaledInstance;
                }
                else
                {
                    img = UtilBox.imageToBufferedImage(tempImg.getImage());
                }
                scaledInstance = UtilBox.getScaledInstance(img, width, img.getHeight(), null, false);
            }
        }
        if(scaledInstance == null)
        {
            return systemIcon;
        }
        return new ImageIcon(scaledInstance);
    }
    
    public static String fileSizeToString(long size)
    {
        String symbol = "B";
        double length = (Long)size;
        int round = 0;
        while(length > 1024)
        {
            switch(round)
            {
                case 0:
                    symbol = "KB";
                    break;
                case 1:
                    symbol = "MB";
                    break;
                case 2:
                    symbol = "GB";
                    break;
                case 3:
                    symbol = "TB";
                    break;
            }
            length = length/1024;
            round++;
        }
        return (int)length + " " + symbol;
    }
    
    public static String fileDateToFormatString(long d)
    {
        SimpleDateFormat form = new SimpleDateFormat();
        form.applyPattern("dd.MM.yyy HH:mm");
        String format = form.format(new Date(d));
        return format;
    }
}
