/*
 * www.fseek.org
 * ~Thedeath
 * 2010 - 2011
 */
package org.fseek.components;

/**
 * This class saves all colors saved in the color.conf
 */

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public class ProgramColorizer
{
    private Color mainBackgroundColor;
    private Color treePanelColor;
    private Color navigationPanelColor;
    private Color seperatorColor;
    private Color treeFontColor;
    private boolean treeFontToUpperCase;
    
    private Color mouseOverBackgroundColor;
    private Color mouseOverBorderColor;
    
    private Color selectBorderColor;
    private Color selectBackgroundColor;
    
    private Color selectorBackgroundColor;
    private Color selectorBorderColor;
    
    
    private File configFile;
    private Properties config;
    
    public ProgramColorizer(Color mainBackgroundColor, Color mouseOverBackgroundColor, Color mouseOverBorderColor, Color selectBorderColor, Color selectBackgroundColor, Color selectorBackgroundColor, Color selectorBorderColor, Color treePanelColor, Color navigationPanelColor, Color seperatorColor, Color treeFontColor, boolean treeFontToUpperCase, String selectedIconDirectory) throws Exception
    {
        this.mainBackgroundColor = mainBackgroundColor;
        this.mouseOverBackgroundColor = mouseOverBackgroundColor;
        this.mouseOverBorderColor = mouseOverBorderColor;
        this.selectBorderColor = selectBorderColor;
        this.selectBackgroundColor = selectBackgroundColor;
        this.selectorBackgroundColor = selectorBackgroundColor;
        this.selectorBorderColor = selectorBorderColor;
        this.treePanelColor = treePanelColor;
        this.navigationPanelColor = navigationPanelColor;
        this.seperatorColor = seperatorColor;
        this.treeFontColor = treeFontColor;
        this.treeFontToUpperCase = treeFontToUpperCase;
        this.configFile = new File(selectedIconDirectory + File.separator + "color.conf");
        intConfig();
    }
    
    public ProgramColorizer(String selectedIconDirectory)
    {
        this.configFile = new File(selectedIconDirectory + File.separator + "color.conf");
        this.config = new Properties();
    }
    
    public void saveConfig()
    {
        OutputStream o = null;
        try
        {
            o = new FileOutputStream(configFile);
            config.store(o, "OTHER INFO/\n~AUTHOR\nColor Configuration File\n");
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if(o != null)
            {
                try
                {
                    o.close();
                } catch (IOException ex){}
            }
        }
    }
    
    public boolean loadConfig() throws Exception
    {
        InputStream s = null;
        this.config = new Properties();
        try
        {
            s = new FileInputStream(this.configFile);
            this.config.load(s);
            String temp = config.getProperty("MainBGColor");
            this.mainBackgroundColor = parseColor(temp);
            
            temp = config.getProperty("MouseOverBGColor"); 
            this.mouseOverBackgroundColor = parseColor(temp);
            temp = config.getProperty("MouseOverBorderColor");
            this.mouseOverBorderColor = parseColor(temp);
            
            temp = config.getProperty("SelectBorderColor");
            this.selectBorderColor = parseColor(temp);
            temp = config.getProperty("SelectBGColor");
            this.selectBackgroundColor = parseColor(temp);
            
            temp = config.getProperty("SelectorBGColor");
            this.selectorBackgroundColor = parseColor(temp);
            temp = config.getProperty("SelectorBorderColor");
            this.selectorBorderColor = parseColor(temp);
            temp = config.getProperty("TreePanelColor");
            if(temp != null)
            this.treePanelColor = parseColor(temp);
            temp = config.getProperty("NavigatePanelColor");
            if(temp != null)
            this.navigationPanelColor = parseColor(temp);
            temp = config.getProperty("SeperatorColor");
            if(temp!= null)
            this.setSeperatorColor(parseColor(temp));
            temp = config.getProperty("TreeFontColor");
            if(temp != null)
            this.setTreeFontColor(parseColor(temp));
            temp = config.getProperty("TreeFontHeaderToUpperCase");
            if(temp != null)
            {
                if(temp.equalsIgnoreCase("true"))
                {
                    setTreeFontToUpperCase(true);
                }
                else
                {
                    setTreeFontToUpperCase(false);
                }
            }
            return true;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if(s != null)
            {
                try
                {
                    s.close();
                } catch (IOException ex){}
            }
        }
        return false;
    }
    
    private Color parseColor(String s) throws NumberFormatException
    {
        String[] split = s.split(",");
        Color c = new Color(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        return c;
    }
    
    private String colorToFormat(Color c)
    {
        int red = c.getRed();
        int blue = c.getBlue();
        int green = c.getGreen();
        return red + "," + green + "," + blue;
    }
    
    private void intConfig() throws Exception
    {
        setMainBackgroundColor(mainBackgroundColor);
        setMouseOverBackgroundColor(mouseOverBackgroundColor);
        setMouseOverBorderColor(mouseOverBorderColor);
        setSelectBackgroundColor(selectBackgroundColor);
        setSelectBorderColor(selectBorderColor);
        setSelectorBackgroundColor(selectorBackgroundColor);
        setSelectorBorderColor(selectorBorderColor);
        setTreePanelColor(treePanelColor);
        setNavigationPanelColor(navigationPanelColor);
        saveConfig();
    }
    
    private void setValImpl(String key, String val)
    {
        if(this.config.contains(val))
        {
            this.config.setProperty(key, val);
        }
        else
        {
            this.config.put(key, val);
        }
    }

    /**
     * @return the mainBackgroundColor
     */
    public Color getMainBackgroundColor()
    {
        return mainBackgroundColor;
    }

    /**
     * @param mainBackgroundColor the mainBackgroundColor to set
     */
    public void setMainBackgroundColor(Color mainBackgroundColor)
    {
        setValImpl("MainBGColor", colorToFormat(mainBackgroundColor));
        this.mainBackgroundColor = mainBackgroundColor;
    }

    /**
     * @return the mouseOverBackgroundColor
     */
    public Color getMouseOverBackgroundColor()
    {
        return mouseOverBackgroundColor;
    }

    /**
     * @param mouseOverBackgroundColor the mouseOverBackgroundColor to set
     */
    public void setMouseOverBackgroundColor(Color mouseOverBackgroundColor)
    {
        setValImpl("MouseOverBGColor", colorToFormat(mouseOverBackgroundColor));
        this.mouseOverBackgroundColor = mouseOverBackgroundColor;
    }

    /**
     * @return the selectBorderColor
     */
    public Color getSelectBorderColor()
    {
        return selectBorderColor;
    }

    /**
     * @param selectBorderColor the selectBorderColor to set
     */
    public void setSelectBorderColor(Color selectBorderColor)
    {
        setValImpl("SelectBorderColor", colorToFormat(selectBorderColor));
        this.selectBorderColor = selectBorderColor;
    }

    /**
     * @return the selectBackgroundColor
     */
    public Color getSelectBackgroundColor()
    {
        return selectBackgroundColor;
    }

    /**
     * @param selectBackgroundColor the selectBackgroundColor to set
     */
    public void setSelectBackgroundColor(Color selectBackgroundColor)
    {
        setValImpl("SelectBGColor", colorToFormat(selectBackgroundColor));
        this.selectBackgroundColor = selectBackgroundColor;
    }

    /**
     * @return the selectorBackgroundColor
     */
    public Color getSelectorBackgroundColor()
    {
        return selectorBackgroundColor;
    }

    /**
     * @param selectorBackgroundColor the selectorBackgroundColor to set
     */
    public void setSelectorBackgroundColor(Color selectorBackgroundColor)
    {
        setValImpl("SelectorBGColor", colorToFormat(selectorBackgroundColor));
        this.selectorBackgroundColor = selectorBackgroundColor;
    }

    /**
     * @return the selectorBorderColor
     */
    public Color getSelectorBorderColor()
    {
        return selectorBorderColor;
    }

    /**
     * @param selectorBorderColor the selectorBorderColor to set
     */
    public void setSelectorBorderColor(Color selectorBorderColor)
    {
        setValImpl("SelectorBorderColor", colorToFormat(selectorBorderColor));
        this.selectorBorderColor = selectorBorderColor;
    }

    /**
     * @return the mouseOverBorderColor
     */
    public Color getMouseOverBorderColor()
    {
        return mouseOverBorderColor;
    }

    /**
     * @param mouseOverBorderColor the mouseOverBorderColor to set
     */
    public void setMouseOverBorderColor(Color mouseOverBorderColor)
    {
        setValImpl("MouseOverBorderColor", colorToFormat(mouseOverBorderColor));
        this.mouseOverBorderColor = mouseOverBorderColor;
    }

    /**
     * @return the treePanelColor
     */
    public Color getTreePanelColor()
    {
        return treePanelColor;
    }

    /**
     * @param treePanelColor the treePanelColor to set
     */
    public void setTreePanelColor(Color treePanelColor)
    {
        setValImpl("TreePanelColor", colorToFormat(treePanelColor));
        this.treePanelColor = treePanelColor;
    }

    /**
     * @return the navigationPanelColor
     */
    public Color getNavigationPanelColor()
    {
        return navigationPanelColor;
    }

    /**
     * @param navigationPanelColor the navigationPanelColor to set
     */
    public void setNavigationPanelColor(Color navigationPanelColor)
    {
        setValImpl("NavigatePanelColor", colorToFormat(navigationPanelColor));
        this.navigationPanelColor = navigationPanelColor;
    }

    /**
     * @return the seperatorColor
     */
    public Color getSeperatorColor()
    {
        return seperatorColor;
    }

    /**
     * @param seperatorColor the seperatorColor to set
     */
    public void setSeperatorColor(Color seperatorColor)
    {
        setValImpl("SeperatorColor", colorToFormat(seperatorColor));
        this.seperatorColor = seperatorColor;
    }

    /**
     * @return the treeFontColor
     */
    public Color getTreeFontColor()
    {
        return treeFontColor;
    }

    /**
     * @param treeFontColor the treeFontColor to set
     */
    public void setTreeFontColor(Color treeFontColor)
    {
        setValImpl("TreeFontColor", colorToFormat(seperatorColor));
        this.treeFontColor = treeFontColor;
    }

    /**
     * @return the treeFontToUpperCase
     */
    public boolean isTreeFontToUpperCase()
    {
        return treeFontToUpperCase;
    }

    /**
     * @param treeFontToUpperCase the treeFontToUpperCase to set
     */
    public void setTreeFontToUpperCase(boolean treeFontToUpperCase)
    {
        setValImpl("TreeFontHeaderToUpperCase", String.valueOf(treeFontToUpperCase));
        this.treeFontToUpperCase = treeFontToUpperCase;
    }
}
