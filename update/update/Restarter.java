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
package update;

import filesystem.LocalFile;
import filesystem.MyFile;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JFrame;

import javax.swing.JOptionPane;

import jd.nutils.Executer;


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
            if(args.length < 1)
            {
                JOptionPane.showMessageDialog((JFrame)null, "You need to specify the updated file !");
                System.out.println("You need to specify the updated file !");
                System.exit(0);
            }
            if(args.length >= 2)
            {
                String s = args[1];
                if(s.toUpperCase().equals("TRUE"))
                {
                    RESTART = true;
                    System.out.println("Restart true");
                }
                else
                {
                    RESTART = false;
                    System.out.println("Restart false");
                }
            }
            File newFile = null;
            if(RESTART != true)
            {
                newFile = new File(args[0]);
                if(!newFile.getName().equals("JExplorer.jar"))
                {
                    System.out.println("Wrong file !");
                    System.exit(0);
                }
                System.out.println(newFile.getAbsolutePath());
            }
            // waits while JExplorer.jar exixts and cannot be overwritten
            File file = new File(getMainPath() + File.separator + "JExplorer.jar");
            System.out.println(file.getAbsolutePath());
            if(file.exists())
            {
                while (!file.canWrite())
                {
                    System.out.println("Wait for JExplorer terminating");
                    try
                    {
                        Thread.sleep(500);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            boolean move = true;
            if(RESTART != true)
            {
                boolean delSuc = file.delete();
                move = move(file, newFile);
            }
            try
            {
                Thread.sleep(500);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            if(move == true)
            {
                
                //use javaw if available. this helps to use a bundelt jre and
                // ensures that jde uses the smae jre over restarts
                String javaPath = new File(new File(System.getProperty("sun.boot.library.path")), "java.exe").getAbsolutePath();
                Executer exec;
                System.out.println("Starting " + file.getAbsolutePath());
                if (new File(javaPath).exists())
                {
                    exec = new Executer(javaPath);
                }
                else
                {
                    exec = new Executer("java");
                }
                exec.addParameters(new String[]
                {
                    "-jar", file.getAbsolutePath()
                });
                String absolutePath = Restarter.getMainPath().getAbsolutePath();
                exec.setRunin(absolutePath);
                exec.setWaitTimeout(0);
                System.out.println(javaPath);
                System.out.println(absolutePath);
                exec.start();
                Thread.sleep(1000);
                System.exit(0);
            }

        } catch (Throwable e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    
    public static boolean move(File dest, File source)
    {
        MyFile f = new LocalFile(source);
        return f.move(new LocalFile(dest));
    }

    public static File getMainPath()
    {
        File mainFileT = new File(Restarter.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String absolutePath = mainFileT.getAbsolutePath();
        if (absolutePath.contains(".jar"))
        {
            int index = absolutePath.lastIndexOf(File.separator);
            absolutePath = absolutePath.substring(0, index);
        }
        return new File(absolutePath);
    }

}
