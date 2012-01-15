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
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.*;

/**
 *
 * @author simon
 */
public class Main
{
    public static final int port = 29170;
    public static File mainPath = getMainPath();
    public static long startTime;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        try
        {
            startTime = System.currentTimeMillis();
            UpdateServerThread.getActualVersion();
            ServerSocket socket = new ServerSocket(port);
            System.out.println("Server listening on " + "localhost" + ":" + socket.getLocalPort());
            while(true)
            {
                Socket s = socket.accept();
                UpdateServerThread tr = new UpdateServerThread(s);
                tr.start();
            }
        } catch (IOException ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    private static File getMainPath()
    {
        File mainFileT = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String absolutePath = null;
        try
        {
            absolutePath = mainFileT.getCanonicalPath();
            if (absolutePath.contains(".jar"))
            {
                int index = absolutePath.lastIndexOf(File.separator);
                absolutePath = absolutePath.substring(0, index);
            }
        } catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
        return new File(absolutePath);
    }

}
