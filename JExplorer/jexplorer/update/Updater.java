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
package jexplorer.update;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import jexplorer.MainFrame;

public class Updater
{
    public static final int updateServerPort = 29170;
    public static final String updateServerIP = "127.0.0.1";
    
    private boolean connected = false;
    
    private boolean canceled = false;
    private File fileOut;
    
    private Socket updateServerSock;
    private BufferedReader br;
    private InputStream normalIn;
    private PrintWriter pw;
    
    public File readProgramm() throws java.net.ConnectException, java.io.FileNotFoundException
    {
        if(isConnected() == false)
        {
            connectToUpdateServer();
        }
        try
        {
            System.out.println("UPDATE PROGRESS: Started with getting actual Version from the update Server !");
            String[] fileInformation = getFileInformation();
            fileOut = createDownloadFile(fileInformation[1]);
            long size = Long.parseLong(fileInformation[2]);
            checkFile(fileOut);
            if(checkCancel() == true)return null;
            FileOutputStream fOut = new FileOutputStream(fileOut);
            readWriteProgram(size, fOut, null);
            if(checkCancel() == true)return null;
            readEnd();
            setConnected(false);
            return fileOut;
        } catch (IOException ex)
        {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            setConnected(false);
        }
        return null;
    }
    
    public File readProgrammGUI(JProgressBar bar) throws java.net.ConnectException, java.io.FileNotFoundException
    {
        bar.setValue(0);
        if(isConnected() == false)
        {
            connectToUpdateServer();
        }
        try
        {
            System.out.println("UPDATE PROGRESS: Started with getting actual Version from the update Server !");
            String[] fileInformation = getFileInformation();
            bar.setValue(10);
            fileOut = createDownloadFile(fileInformation[1]);
            bar.setValue(15);
            long size = Long.parseLong(fileInformation[2]);
            if(checkCancel() == true)return null;
            checkFile(fileOut);
            bar.setValue(20);
            FileOutputStream fOut = new FileOutputStream(fileOut);
            int i;
            readWriteProgram(size, fOut, bar);
            if(checkCancel() == true)return null;
            readEnd();
            bar.setValue(100);
            setConnected(false);
            return fileOut;
        } 
        catch (IOException ex)
        {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            setConnected(false);
        }
        return null;
    }
    
    private boolean checkCancel()
    {
        if(isCanceled() == true)
        {
            try
            {
                fileOut.delete();
                this.updateServerSock.close();
                return true;
            } catch (IOException ex)
            {
                Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
    
    private void readEnd() throws IOException
    {
        System.out.println("Wating for newLine");
        br.readLine();
        System.out.println("Wating for fileEnd");
        String fileEnd = br.readLine();
    }
    
    private void checkFile(File f)
    {
        if(f.canWrite() == false)
        {
            System.out.println("UPDATE PROGRESS: Not enought rights to download the programm !");
        }
        if(f.exists())
        {
            f.delete();
        }
    }
    
    private File createDownloadFile(String fileName)
    {
        System.out.println("Creating file");
        File downloadDir = new File(MainFrame.mainDir + File.separator + "downloads");
        if(!downloadDir.exists())
        {
            downloadDir.mkdir();
        }
        else
        {
            downloadDir.delete();
            downloadDir.mkdir();
        }
        File temp = new File(downloadDir.getAbsolutePath() + File.separator + fileName);
        return temp;
    }
    
    private String[] getFileInformation() throws IOException
    {
        System.out.println("Sending get command");
        pw.println("GET");
        pw.flush();
        System.out.println("Waiting for fileBegin");
        String fileBegin = br.readLine();
        System.out.println("Waiting for fileName");
        String fileName = br.readLine();
        System.out.println("Waiting for fileSize");
        String fileSize = br.readLine();
        String[] split = fileName.split("=");
        fileName = split[1];
        String[] split1 = fileSize.split("=");
        String size = split1[1];
        String[] infos = {fileBegin, fileName, size};
        return infos;
    }
    
    private void readWriteProgram(long size, FileOutputStream fOut, JProgressBar bar) throws IOException
    {
        int value = -1;
        if(bar != null)
        {
            value = bar.getValue();
        }
        int byteCount = 0;
        int onePercent = (int) (size / 80);
        int added = 0;
        System.out.println("Begin reading Programm");
        while(true)
        {
            if(isCanceled() == true)
            {
                fOut.close();
                return;
            }
            //System.out.println(byteCount);
            if(bar != null)
            {
                if(added > onePercent)
                {
                    int percent = (int) ((80 * byteCount) / size);
                    bar.setValue(value + percent);
                    added = 0;
                }
            }
            if(byteCount == size)
            {
                break;
            }
            int read = normalIn.read();
            if(read == -1)
            {
                break;
            }
            fOut.write(read);
            byteCount++;
            added++;
        }
        fOut.flush();
        fOut.close();
        System.out.println("Reading finished: " + byteCount + "//" + size);
    }
    
    public boolean checkUpdateNeeded(String version) throws ConnectException
    {
        try
        {
            System.out.println("UPDATE PROGRESS: Checking update needed !");
            System.out.println("Sending check command");
            pw.println("CHECK " + version);
            pw.flush();
            System.out.println("Waiting for answer");
            String readLine = br.readLine();
            boolean needed = false;
            if(readLine.toUpperCase().equals("TRUE"))
            {
                needed = true;
            }
            return needed;
        } catch (IOException ex)
        {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            setConnected(false);
        }
        return false;
    }
    
    public boolean connectToUpdateServer() throws java.net.ConnectException
    {
        try
        {
            this.updateServerSock = new Socket(updateServerIP, updateServerPort);
            updateServerSock.setSoTimeout(3000);
            this.normalIn = updateServerSock.getInputStream();
            this.br = new BufferedReader(new InputStreamReader(normalIn));
            this.pw = new PrintWriter(new OutputStreamWriter(this.updateServerSock.getOutputStream()));
            connected = true;
            String readLine = this.br.readLine();
            return true;
        } 
        catch (UnknownHostException ex)
        {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IOException ex)
        {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        }
        setConnected(false);
        return false;
    }
    
    public void disconnect()
    {
        try
        {
            this.br.close();
            this.normalIn.close();
            this.pw.flush();
            this.pw.close();
            this.updateServerSock.close();
        } 
        catch (IOException ex)
        {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (NullPointerException ex)
        {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the connected
     */
    public boolean isConnected()
    {
        return connected;
    }

    /**
     * @return the canceled
     */
    public boolean isCanceled()
    {
        return canceled;
    }

    /**
     * @param canceled the canceled to set
     */
    public void setCanceled(boolean canceled)
    {
        this.canceled = canceled;
    }

    /**
     * @param connected the connected to set
     */
    public void setConnected(boolean connected) throws ConnectException
    {
        if(connected == false)
        {
            disconnect();
        }
        else
        {
            connectToUpdateServer();
        }
        this.connected = connected;
    }
}
