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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class WindowsUtils
{

    private static final String REGQUERY_UTIL = "reg query ";
    private static final String REGSTR_TOKEN = "REG_SZ";
    private static final String DESKTOP_FOLDER_CMD = REGQUERY_UTIL
    + "\"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\"
    + "Explorer\\Shell Folders\" /v ";

    private WindowsUtils()
    {
    }

    public static String getCurrentUserPath(String search)
    {
        try
        {
            Process process = Runtime.getRuntime().exec(DESKTOP_FOLDER_CMD + "\"" + search + "\"");
            StreamReader reader = new StreamReader(process.getInputStream());

            reader.start();
            process.waitFor();
            reader.join();

            String result = reader.getResult();
            int p = result.indexOf(REGSTR_TOKEN);

            if (p == -1)
            {
                return null;
            }

            return result.substring(p + REGSTR_TOKEN.length()).trim();
        } catch (Exception e)
        {
            return null;
        }
    }


    static class StreamReader extends Thread
    {

        private InputStream is;
        private StringWriter sw;

        StreamReader(InputStream is)
        {
            this.is = is;
            sw = new StringWriter();
        }

        @Override
        public void run()
        {
            try
            {
                int c;
                while ((c = is.read()) != -1)
                {
                    sw.write(c);
                }
            } catch (IOException e)
            {
            }
        }

        String getResult()
        {
            return sw.toString();
        }
    }
}