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
package jexplorer;

/**
 *
 * @author Thedeath<www.fseek.org>
 */

import java.awt.Desktop ;
import java.io.File ;
import java.io.IOException ;
import java.net.URI ;
import java.net.URL ;

public class OSDetector
{
        public static final byte OS_LINUX_OTHER = 6;
        public static final byte OS_MAC_OTHER = 5;
        public static final byte OS_WINDOWS_OTHER = 4;
        public static final byte OS_WINDOWS_NT = 3;
        public static final byte OS_WINDOWS_2000 = 2;
        public static final byte OS_WINDOWS_XP = 0;
        public static final byte OS_WINDOWS_2003 = 7;
        public static final byte OS_WINDOWS_VISTA = 1;
        public static final byte OS_WINDOWS_7 = 8;
        private static final byte OS_ID;

        static
        {
            String OS = System.getProperty("os.name").toLowerCase();
            if (OS.indexOf("windows 7") > -1)
            {
                OS_ID = 8;
            }
            else if (OS.indexOf("windows xp") > -1)
            {
                OS_ID = 0;
            }
            else if (OS.indexOf("windows vista") > -1)
            {
                OS_ID = 1;
            }
            else if (OS.indexOf("windows 2000") > -1)
            {
                OS_ID = 2;
            }
            else if (OS.indexOf("windows 2003") > -1)
            {
                OS_ID = 7;
            }
            else if (OS.indexOf("nt") > -1)
            {
                OS_ID = 3;
            }
            else if (OS.indexOf("windows") > -1)
            {
                OS_ID = 4;
            }
            else if (OS.indexOf("mac") > -1)
            {
                OS_ID = 5;
            }
            else
            {
                OS_ID = 6;
            }
        }

        public static void openURL(URL url)
        {
            if (!Desktop.isDesktopSupported())
            {
                System.out.println("Desktop is not supported (fatal)");
            }

            Desktop desktop = Desktop.getDesktop();

            if (!desktop.isSupported(Desktop.Action.BROWSE))
            {
                System.out.println("Desktop doesn't support the browse action (fatal)");
            }
            try
            {
                desktop.browse(url.toURI());
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public static void openFile(File file)
        {
            if ((isWindows()) && (file.isFile()))
            {
                try
                {
                    Runtime.getRuntime().exec("cmd /c \"" + file.getAbsolutePath() + "\"");
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                return;
            }

            if (!Desktop.isDesktopSupported())
            {
                System.out.println("Desktop is not supported (fatal)");
            }

            Desktop desktop = Desktop.getDesktop();

            if (!desktop.isSupported(Desktop.Action.OPEN))
            {
                System.out.println("Desktop doesn't support the OPEN action (fatal)");
            }
            try
            {
                URI uri = file.getCanonicalFile().toURI();
                desktop.open(new File(uri));
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        public static boolean isLinux()
        {
            return OS_ID == 6;
        }

        public static boolean isMac()
        {
            return OS_ID == 5;
        }

        public static boolean isWindows()
        {
            switch (OS_ID)
            {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 7:
                case 8:
                    return true;
                case 5:
                case 6:
            }
            return false;
        }

}
