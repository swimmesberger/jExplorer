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
package jexplorer.config;

/**
 * This class saves all options selected with the ConfigDialog
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager.LookAndFeelInfo;
import jexplorer.MainFrame;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public class OptionsHandler
{
    private LookAndFeelInfo lookAndFeel;
    private String lang;
    private boolean windowDecEnabled;
    private boolean showHiddenFiles;
    private int fontSize;
    private File selectedIconDir;
    
    private File configFile;
    private Properties globalConfig;
    

    public OptionsHandler(LookAndFeelInfo lookAndFeel, String lang, boolean windowDecEnabled, int fontSize, File selectedIconDir, boolean showHiddenFiles) throws Exception
    {
        this.lookAndFeel = lookAndFeel;
        this.lang = lang;
        this.windowDecEnabled = windowDecEnabled;
        this.fontSize = fontSize;
        this.selectedIconDir = selectedIconDir;
        this.showHiddenFiles = showHiddenFiles;
        this.globalConfig = new Properties();
        this.configFile = new File(MainFrame.mainDir + File.separator + "main.conf");
        intConfig();
    }

    public OptionsHandler()
    {
        this.configFile = new File(MainFrame.mainDir + File.separator + "main.conf");
        this.globalConfig = new Properties();
    }

    private void intConfig() throws Exception
    {
        setLookAndFeel(lookAndFeel);
        setLang(lang);
        setWindowDecEnabled(windowDecEnabled);
        setFontSize(fontSize);
        setSelectedIconDir(selectedIconDir);
        setShowHiddenFiles(showHiddenFiles);
        saveConfig();
    }

    public void saveConfig()
    {
        OutputStream o = null;
        try
        {
            o = new FileOutputStream(configFile);
            globalConfig.store(o, "http://www.fseek.org/\n~Thedeath\nGlobal Configuration File\n");
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
        this.globalConfig = new Properties();
        try
        {
            s = new FileInputStream(this.configFile);
            this.globalConfig.load(s);
            LookAndFeelInfo info = new LookAndFeelInfo(this.globalConfig.getProperty("LookAndFeelName"), this.globalConfig.getProperty("LookAndFeelClass"));
            this.lookAndFeel = info;
            this.lang = this.globalConfig.getProperty("Language");
            this.fontSize = Integer.parseInt(this.globalConfig.getProperty("FontSize"));
            this.windowDecEnabled = Boolean.parseBoolean(this.globalConfig.getProperty("WindowDecoration"));
            this.selectedIconDir = new File(this.globalConfig.getProperty("IconDirectory"));
            this.showHiddenFiles = Boolean.parseBoolean(this.globalConfig.getProperty("ShowHiddenFiles"));
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

    /**
     * @return the lookAndFeel
     */
    public LookAndFeelInfo getLookAndFeel()
    {
        return lookAndFeel;
    }

    /**
     * @param lookAndFeel the lookAndFeel to set
     */
    public void setLookAndFeel(LookAndFeelInfo lookAndFeel) throws Exception
    {
        setValImpl("LookAndFeelClass", lookAndFeel.getClassName());
        setValImpl("LookAndFeelName", lookAndFeel.getName());
        this.lookAndFeel = lookAndFeel;
    }

    private void setValImpl(String key, String val)
    {
        if(this.globalConfig.contains(val))
        {
            this.globalConfig.setProperty(key, val);
        }
        else
        {
            this.globalConfig.put(key, val);
        }
    }

    /**
     * @return the lang
     */
    public String getLang()
    {
        return lang;
    }

    /**
     * @param lang the lang to set
     */
    public void setLang(String lang)
    {
        setValImpl("Language", lang);
        this.lang = lang;
    }

    /**
     * @return the enableWindowDec
     */
    public boolean isWindowDecEnabled()
    {
        return windowDecEnabled;
    }

    /**
     * @param enableWindowDec the enableWindowDec to set
     */
    public void setWindowDecEnabled(boolean enableWindowDec)
    {
        setValImpl("WindowDecoration", String.valueOf(enableWindowDec));
        this.windowDecEnabled = enableWindowDec;
    }

    /**
     * @return the fontSize
     */
    public int getFontSize()
    {
        return fontSize;
    }

    /**
     * @param fontSize the fontSize to set
     */
    public void setFontSize(int fontSize)
    {
        setValImpl("FontSize", String.valueOf(fontSize));
        this.fontSize = fontSize;
    }

    /**
     * @return the selectedIconDir
     */
    public File getSelectedIconDir()
    {
        return this.selectedIconDir;
    }

    /**
     * @param selectedIconDir the selectedIconDir to set
     */
    public void setSelectedIconDir(File selectedIconDir)
    {
        try
        {
            if(selectedIconDir != null)
            {
                setValImpl("IconDirectory", selectedIconDir.getCanonicalPath());
            }
            else
            {
                setValImpl("IconDirectory", "");
            }
            this.selectedIconDir = selectedIconDir;
        } catch (IOException ex)
        {
            Logger.getLogger(OptionsHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the enableWindowDec
     */
    public boolean isShowHiddenFiles()
    {
        return showHiddenFiles;
    }

    /**
     * @param enableWindowDec the enableWindowDec to set
     */
    public void setShowHiddenFiles(boolean showHiddenFiles)
    {
        setValImpl("ShowHiddenFiles", String.valueOf(showHiddenFiles));
        this.showHiddenFiles = showHiddenFiles;
    }
    
}
