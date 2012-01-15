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
package jexplorer.restarter;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JFrame;

import javax.swing.JOptionPane;
import jexplorer.MainFrame;


/**
 * This class is the mainclass of tinyupdate.jar. This tool is called by
 * jdataEditor.jar and moves fresh files from ./update/ to ./ after an update.
 * Since jdataEditor.jar is already closed when tinyupdate is running,
 * tinyupdate ca overwrite jdataEditor.jar <br>
 * TINYUPDATE.jar must run in JD_HOME
 * 
 * @author coalado
 * 
 */
public class Restarter
{
    /**
     * is set by -restart parameter to true, and is used to do a restart aftrer
     * moving files
     */
    private static boolean RESTART = false;

    /**
     * Returns the stacktrace of a Thorwable
     * 
     * @param thrown
     * @return
     */
    public static String getStackTrace(Throwable thrown)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        thrown.printStackTrace(pw);
        pw.close();
        return sw.toString();
    }

    public static void main(String[] args)
    {
        try
        {
            if(args.length >= 1)
            {
                String s = args[0];
                if(s.toUpperCase().equals("TRUE"))
                {
                    RESTART = true;
                }
                else
                {
                    RESTART = false;
                }
            }
            // waits while update.jar exixts and cannot be overwritten
            File file = new File( MainFrame.mainDir + File.separator + "update.jar");
            System.out.println(file.getAbsolutePath());
            if(!file.exists())
            {
                //updater not found....
                JOptionPane.showMessageDialog((JFrame)null, "Restarter not found");
                return;
            }
            while (!file.canWrite())
            {
                System.out.println("Wait for restarter terminating");
                try
                {
                    Thread.sleep(500);
                } catch (InterruptedException e)
                {
                    System.out.println(getStackTrace(e));
                }
            }
            //use javaw if available. this helps to use a bundelt jre and
            // ensures that jd uses the smae jre over restarts
            String javaPath = new File(new File(System.getProperty("sun.boot.library.path")), "java.exe").getAbsolutePath();

            Executer exec;

            if (new File(javaPath).exists())
            {
                exec = new Executer(javaPath);
            }
            else
            {
                exec = new Executer("java");
            }
            if(RESTART)
            {
                exec.addParameters(new String[]
                {
                    "-jar", MainFrame.mainDir + File.separator + "update.jar", " ", "true"
                });
            }
            else
            {
                exec.addParameters(new String[]
                {
                    "-jar", MainFrame.mainDir + File.separator + "update.jar", args[1], "false"
                });
            }
            exec.setRunin(new File(".").getAbsolutePath());
            exec.setWaitTimeout(0);
            exec.start();
            Thread.sleep(1000);
            System.exit(0);
        } catch (Throwable e)
        {
            System.out.println(getStackTrace(e));
        }
    }

}
