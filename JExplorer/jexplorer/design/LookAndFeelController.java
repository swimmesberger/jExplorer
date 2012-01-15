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

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import jexplorer.MainFrame;
import jexplorer.OSDetector;
import jexplorer.config.OptionsHandler;
import org.fseek.components.ProgramColorizer;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public class LookAndFeelController
{

    private static OptionsHandler options;
    private static ProgramColorizer colorizer;

    public static void setUIManager()
    {
        try
        {
            if(options == null)
            {
                options = new OptionsHandler();
            }
            boolean loadConfig = false;
            try
            {
                loadConfig = options.loadConfig();
            }catch(Exception ex)
            {
                ex.printStackTrace();
            }
            MainFrame.optionsHandler = options;
            install();
            if(loadConfig == false)
            {
                intMainConfig();
            }
            File selectedIconDir = MainFrame.optionsHandler.getSelectedIconDir();
            if(selectedIconDir != null)
            {
                MainFrame.selectedIconDirectory = selectedIconDir;
            }
            else
            {
                MainFrame.selectedIconDirectory = new File(MainFrame.mainDir + File.separator + "icons" + File.separator + "windows");
            }
            if(colorizer == null)
            {
                colorizer = new ProgramColorizer(MainFrame.selectedIconDirectory.getAbsolutePath());
            }
            loadConfig = false;
            try
            {
                loadConfig = colorizer.loadConfig();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            if(loadConfig == false)
            {
                intColorConfig();
            }
            MainFrame.colorizer = colorizer;
            preSetup();
            if(options.getLookAndFeel().getClassName().toLowerCase().contains("synthetica"))
            {
                try
                {
                    Class<?> slaf = Class.forName("de.javasoft.plaf.synthetica.SyntheticaLookAndFeel");
                    Method method = slaf.getMethod("setLookAndFeel", new Class[] { String.class, boolean.class, boolean.class });
                    method.invoke(null, new Object[] { options.getLookAndFeel().getClassName(), true, true });
                }catch(InvocationTargetException ex)
                {
                    try
                    {
                        UIManager.setLookAndFeel(options.getLookAndFeel().getClassName());
                    }catch(Exception ex2)
                    {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    }
                }
            }
            else
            {
                UIManager.setLookAndFeel(options.getLookAndFeel().getClassName());
            }
            postSetup();
        } catch (Exception ex)
        {
            Logger.getLogger(LookAndFeelController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void intMainConfig() throws Exception
    {
        try
        {
            LookAndFeelWrapper defaultLAFM = getDefaultLAFM();
            String property = System.getProperty("user.language");
            if (property.startsWith("de"))
            {
                MainFrame.lang = "deDE";
            }
            else
            {
                MainFrame.lang = "enEN";
            }
            options.setLang(MainFrame.lang);
            options.setWindowDecEnabled(true);
            options.setFontSize(100);
            UIManager.LookAndFeelInfo defaultInfo = new UIManager.LookAndFeelInfo(defaultLAFM.getName(), defaultLAFM.getClassName());
            options.setLookAndFeel(defaultInfo);
            options.setSelectedIconDir(new File(MainFrame.mainDir + File.separator + "icons" + File.separator + "windows"));
            options.saveConfig();
        } catch (ParseException ex)
        {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void intColorConfig() throws Exception
    {
        try
        {
            colorizer = new ProgramColorizer(MainFrame.selectedIconDirectory.getAbsolutePath());
            colorizer.setMainBackgroundColor(Color.WHITE);

            colorizer.setMouseOverBackgroundColor(new Color(235,243,253));
            colorizer.setMouseOverBorderColor(new Color(184,214,251));

            colorizer.setSelectBackgroundColor(new Color(197,222,252));
            colorizer.setSelectBorderColor(new Color(125,162,206));

            colorizer.setSelectorBackgroundColor(new Color(51, 153, 255));
            colorizer.setSelectorBorderColor(new Color(51, 153, 255));
            colorizer.saveConfig();
        }
        catch (Exception ex)
        {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * INstalls all Look and feels founmd in libs/laf/
     */
    private static void install()
    {
        File[] lookAndFeelsFromLibDir = getLookAndFeelsFromLibDir();
        if(lookAndFeelsFromLibDir != null)
        {
            for (File file : lookAndFeelsFromLibDir)
            {
                try
                {
                    JarInputStream jarFile = new JarInputStream(new FileInputStream(file));
                    JarEntry e;
                    String cl;
                    ArrayList<String> names = new ArrayList<String>();
                    ArrayList<String> classes = new ArrayList<String>();
                    while ((e = jarFile.getNextJarEntry()) != null)
                    {
                        if (!e.getName().endsWith(".class") || e.getName().contains("$"))
                        {
                            continue;
                        }
                        cl = e.getName().replace("/", ".");
                        cl = cl.substring(0, cl.length() - 6);
                        if (!cl.toLowerCase().endsWith("lookandfeel"))
                        {
                            continue;
                        }
                        ClassLoader cload = new ClassLoader()
                        {
                        };
                        Class<?> clazz = cload.loadClass(cl);
                        try
                        {
                            if (LookAndFeel.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers()))
                            {
                                String name = clazz.getSimpleName().replace("LookAndFeel", "");
                                names.add(name);
                                classes.add(cl);
                            }
                        } catch (Throwable t)
                        {
                            t.printStackTrace();
                        }
                    }
                    // first collect all. Of the jar contaisn errors, an exception
                    // gets thrown and no laf is added
                    for (int i = 0; i < names.size(); i++)
                    {
                        UIManager.installLookAndFeel(names.get(i), classes.get(i));
                    }
                } catch (Throwable e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private static LookAndFeelWrapper getDefaultLAFM()
    {
        if (OSDetector.isMac() || OSDetector.isWindows())
        {
            return new LookAndFeelWrapper(UIManager.getSystemLookAndFeelClassName());
        }
        else
        {
            return new LookAndFeelWrapper("de.javasoft.plaf.synthetica.SyntheticaSimple2DLookAndFeel");
        }
    }

    private static File[] getLookAndFeelsFromLibDir()
    {
        FilenameFilter syntheticaFileNameFilter = new FilenameFilter()
        {

            public boolean accept(File dir, String name)
            {
                if (name.toLowerCase().contains("synthetica"))
                {
                    if (!name.toLowerCase().contains("addon"))
                    {
                        return true;
                    }
                }
                return false;
            }
        };
        File[] files = new File(MainFrame.mainDir, "lib/laf").listFiles(syntheticaFileNameFilter);
        if (files == null || files.length <= 0)
        {
            files = new File(MainFrame.mainDir, "lib").listFiles(syntheticaFileNameFilter);
        }
        return files;
    }
    
    //crypted normal synthetica key
    //a973327f5c044d191e7e7bd1240d4545f1e6e27c17577f71489a666e7abf2d1fa3baf3f426c148267bd52e0995118ca0
    //encrypted synthetica key
    //868AB3BF-22199E40-C6F72C24-532A3781-1542E57A
    //crypted addons synthetica key
    //501959263bd504cc2800214bfed4627839740377c755d20605cc97dd8c7ba99170ea4a6cdb2092aa5ad2a849a1524811
    //encrypted addons-synthetica key
    //4A88D92A-285E1988-EC63063B-D110CEC3-62794E51
    private static void preSetup()
    {
        boolean windowDeco = options.isWindowDecEnabled();
        UIManager.put("Synthetica.window.decoration", windowDeco);

        JFrame.setDefaultLookAndFeelDecorated(windowDeco);
        JDialog.setDefaultLookAndFeelDecorated(windowDeco);
//        String[] li =
//        {
//            "Licensee=Thedeath", "LicenseRegistrationNumber=NCSW110303", "Product=Synthetica", "LicenseType=Non Commercial", "ExpireDate=--.--.----", "MaxVersion=2.999.999"
//        };
//        String[] li2 =
//        {
//            "Licensee=Thedeath", "LicenseRegistrationNumber=NCSW110303", "Product=SyntheticaAddons", "LicenseType=Non Commercial", "ExpireDate=--.--.----", "MaxVersion=1.999.999"
//        };
//        UIManager.put("Synthetica.license.info", li);
//        UIManager.put("Synthetica.license.key", Crypt.decrypt(HexUtils.getByteArray("a973327f5c044d191e7e7bd1240d4545f1e6e27c17577f71489a666e7abf2d1fa3baf3f426c148267bd52e0995118ca0"), new byte[]
//        {
//            1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 11, 12, 13, 14, 15, 16
//        }));
//        UIManager.put("SyntheticaAddons.license.info", li2);
//        UIManager.put("SyntheticaAddons.license.key", Crypt.decrypt(HexUtils.getByteArray("501959263bd504cc2800214bfed4627839740377c755d20605cc97dd8c7ba99170ea4a6cdb2092aa5ad2a849a1524811"), new byte[]
//        {
//            1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 11, 12, 13, 14, 15, 16
//        }));
    }

    private static void postSetup()
    {
        int fontsize = options.getFontSize();
        Font fT = (Font) UIManager.get("Label.font");
        if (isSynthetica())
        {
            try
            {
                /*
                 * set default font to Dialog, so we can show japanese chars,
                 * note that java itself must have correct font mappings
                 */
                Class.forName("de.javasoft.plaf.synthetica.SyntheticaLookAndFeel").getMethod("setFont", new Class[]
                {
                    String.class, int.class
                }).invoke(null, new Object[]
                {
                    fT.getName(), 11
                });
            } catch (Throwable e)
            {
                e.printStackTrace();
            }
            try
            {
                /* dynamic fontsize */
                String font = "" + Class.forName("de.javasoft.plaf.synthetica.SyntheticaLookAndFeel").getMethod("getFontName", new Class[]
                {
                }).invoke(null, new Object[]
                {
                });
                int fonts = (Integer) Class.forName("de.javasoft.plaf.synthetica.SyntheticaLookAndFeel").getMethod("getFontSize", new Class[]
                {
                }).invoke(null, new Object[]
                {
                });
                Class.forName("de.javasoft.plaf.synthetica.SyntheticaLookAndFeel").getMethod("setFont", new Class[]
                {
                    String.class, int.class
                }).invoke(null, new Object[]
                {
                    font, (fonts * fontsize) / 100
                });
            } catch (Throwable e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            try
            {
                Font font = new Font(fT.getName(), fT.getStyle(), 11);
                Font font1 = Font.getFont("Segoe UI");
                if(font1 != null)
                {
                    font = font1;
                }
                for (Enumeration<Object> e = UIManager.getDefaults().keys(); e.hasMoreElements();)
                {
                    Object key = e.nextElement();
                    Object value = UIManager.get(key);

                    if (value instanceof Font)
                    {
                        Font f = null;
                        if (font != null)
                        {
                            f = font;
                        }
                        else
                        {
                            f = (Font) value;
                        }
                        UIManager.put(key, new FontUIResource(f.getName(), f.getStyle(), (f.getSize() * fontsize) / 100));
                    }
                }
            } catch (Throwable e)
            {
                e.printStackTrace();
            }
        }
    }

    public static boolean isSynthetica()
    {
        return UIManager.getLookAndFeel().getName().toLowerCase().contains("synthetica");
    }
}
