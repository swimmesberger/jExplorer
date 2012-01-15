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
package updateserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class VersionReader
{   
    public static final File probFile = new File(Main.mainPath + File.separator + "server.conf");
    private static Properties globalConfig = new Properties();
    
    public static File getActualVersion() throws FileNotFoundException, IOException
    {
        String property = null;
        try
        {
            FileInputStream in = new FileInputStream(probFile);
            globalConfig = new Properties();
            globalConfig.load(in);
            property = globalConfig.getProperty("actualVersion");
            in.close();
        }catch(FileNotFoundException ex)
        {
            createProbFile();
        }
        if(property == null)
        {
            return null;
        }
        return new File(property);
    }
    
    public static String getActualVersionNumber() throws FileNotFoundException, IOException
    {
        String property = null;
        try
        {
            FileInputStream in = new FileInputStream(probFile);
            globalConfig = new Properties();
            globalConfig.load(in);
            property = globalConfig.getProperty("versionNumber");
            in.close();
        }catch(FileNotFoundException ex)
        {
            createProbFile();
        }
        if(property == null)
        {
            return null;
        }
        return property;
    }
    
    public static void createProbFile() throws IOException
    {
        FileOutputStream out = new FileOutputStream(probFile);
        setVal("actualVersion", "");
        setVal("versionNumber", "");
        globalConfig.store(out, null);
    }
    
    
    public static void setVal(String key, String val)
    {
        if(VersionReader.globalConfig.contains(val))
        {
            VersionReader.globalConfig.setProperty(key, val);
        }
        else
        {
            VersionReader.globalConfig.put(key, val);
        }
    }
}
