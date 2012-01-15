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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Simon
 */
public class UpdateServerThread extends Thread
{
    private Socket s;
    private BufferedReader br;
    private PrintWriter pw;
    private ObjectOutputStream oOut;
    private OutputStream nOut;
    
    private static File actualVersion;
    private static String actualVersionName;
    private static String versionNumber;

    public static ArrayList<Socket> connected = new ArrayList<Socket>();
    public UpdateServerThread(Socket s)
    {
        super("UpdateServerThread");
        this.s = s;
    }

    @Override
    public void run()
    {
        try
        {
            System.out.println("Client connected: " + s.getInetAddress().toString() + ":" + s.getPort());
            synchronized(connected)
            {
                connected.add(s);
            }
            pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            oOut = new ObjectOutputStream(s.getOutputStream());
            nOut = s.getOutputStream();
            pw.println("Welcome mesg !");
            pw.flush();
            while(true)
            {
                oOut.flush();
                String readLine = br.readLine();
                if(readLine == null)
                {
                    break;
                }
                if(readLine.toUpperCase().startsWith("GET"))
                {
                    File f = null;
                    try
                    {
                        System.out.println("Client: GET Command");
                        int index = readLine.indexOf(' ');
                        if(index == -1)
                        {
                            f = UpdateServerThread.actualVersion;
                            sendFile(f);
                        }
                        else
                        {
                            String fileName = readLine.substring(index+1, readLine.length());
                            f = new File(Main.mainPath + File.separator + fileName);
                            sendFile(f);
                        }
                    }catch(FileNotFoundException ex)
                    {
                        if(f != null)
                            System.out.println(f.getAbsolutePath() + " not found !");
                        else
                            System.out.println("File null !");
                    }
                }
                else if(readLine.toUpperCase().startsWith("CHECK"))
                {
                    System.out.println("Client: CHECK Command");
                    String[] split = readLine.split(" ");
                    if(split.length > 1)
                    {
                        String version = split[1];
                        System.out.println("Version sent: "+version);
                        if(version.equals(UpdateServerThread.versionNumber))
                        {
                            pw.println(false);
                            System.out.println("No update needed !");
                        }
                        else
                        {
                            pw.println(true);
                            System.out.println("Update needed !");
                        }
                    }
                    else
                    {
                        pw.println("Invalid Syntax !");
                        System.out.println("Invalid Syntax !");
                    }
                    pw.flush();
                }
                else
                {
                    System.out.println("Invalid command sent !");
                }
            }
        } catch (IOException ex)
        {
            ex.printStackTrace();
        } finally
        {
            System.out.println(s.getInetAddress() + " disconnected !");
            if(pw != null)
            {
                pw.close();
            }
            if(br != null)
            {
                try
                {
                    br.close();
                } catch (IOException ex){}
            }
            if(oOut != null)
            {
                try
                {
                    oOut.close();
                } catch (IOException ex){}
            }
            try
            {
                boolean remove = UpdateServerThread.connected.remove(s);
                s.close();
            } catch (IOException ex){}
        }
    }
    
    public static void getActualVersion()
    {
        try
        {
            File actualVersion1 = VersionReader.getActualVersion();
            UpdateServerThread.actualVersion = new File(Main.mainPath + File.separator + actualVersion1);
            UpdateServerThread.versionNumber = VersionReader.getActualVersionNumber();
            if(actualVersion1 == null || actualVersion1.getPath().equals("") || UpdateServerThread.versionNumber.equals(""))
            {
                System.out.println("Modify server.conf first !");
                System.exit(1);
            }
            UpdateServerThread.actualVersionName = UpdateServerThread.actualVersion.getName();
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(UpdateServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(UpdateServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void sendFile(File f) throws FileNotFoundException
    {
        System.out.println("Sending File: " + f.getAbsolutePath());
        try
        {
            long length = f.length();
            this.pw.println("<file>");
            this.pw.println("fileName=" + f.getName());
            this.pw.println("fileSize=" + length);
            this.pw.flush();
            FileInputStream fin = new FileInputStream(f);
            int i;
            int byteCount = 0;
            while((i = fin.read()) != -1)
            {
                nOut.write(i);
                byteCount++;
            }
            fin.close();
            nOut.flush();
            this.pw.println();
            this.pw.println("</file>");
            this.pw.flush();
            System.out.println("Sending File: " + f.getAbsolutePath() + " finished !");
            System.out.println("Transfered " + byteCount + "//" + length);
        } catch (IOException ex)
        {
            Logger.getLogger(UpdateServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
