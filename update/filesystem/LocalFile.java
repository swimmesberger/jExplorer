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
package filesystem;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;

public class LocalFile implements MyFile,Serializable
{

    private File file;
    private ExtensionDatabase extData = new ExtensionDatabase();
    private long transfered = 0;
    private double transferRate = 0;

    public LocalFile(String pathName)
    {
        file = new File(pathName);
    }

    public LocalFile(File file)
    {
        this.file = file;
    }

    public String getFileName()
    {
        String name = file.getName();
        if (name == null || name.equals(""))
        {
            name = file.getAbsolutePath();
        }
        return name;
    }

    public long getFileSize()
    {
        return file.length();
    }

    public long getLastModified()
    {
        return file.lastModified();
    }

    public MyFile[] getFiles() throws IOException
    {
        if (!this.file.canRead())
        {
            throw new IOException("Zugriff nicht erlaubt !");
        }
        File[] listFiles = file.listFiles();
        if (listFiles == null)
        {
            throw new IOException("Zugriff nicht erlaubt !");
        }
        MyFile[] myFiles = new MyFile[listFiles.length];
        int count = 0;
        for (File f : listFiles)
        {
            myFiles[count] = new LocalFile(f);
            count++;
        }
        return myFiles;
    }

    public boolean isDirectory()
    {
        return file.isDirectory();
    }

    public boolean isHidden()
    {
        return file.isHidden();
    }

    public Icon getIcon(boolean large)
    {
        Icon systemIcon = MyFileSystem.getSystemIcon(file, large);
        return systemIcon;
    }

    public String getType()
    {
        return extData.getFullName(getExtension());
    }

    public String getExtension()
    {
        if (file.isDirectory())
        {
            return "$DIR$";
        }
        String name = this.file.getName();
        int indexOf = name.lastIndexOf('.');
        return name.substring(indexOf + 1, name.length());
    }

    public boolean move(MyFile to)
    {
//        to = appenFileNameIfNeeded(to);
        File fileTo = new File(to.getAbsolutePath());
        if(MyFileSystem.isSameDrive(file, fileTo))
        {
            return this.file.renameTo(fileTo);
        }
        else
        {
            return moveImpl(to);
        }
    }
    
    private MyFile appenFileNameIfNeeded(MyFile to)
    {
        String path = to.getAbsolutePath();
        int lastIndexOf = path.lastIndexOf(File.separator);
        path = path.substring(lastIndexOf + File.separator.length(), path.length());
        if(path == null || path.equals("") || path.equals(" "))
        {
            path = path + this.file.getName();
        }
        LocalFile f = new LocalFile(path);
        return f;
    }
    
    public boolean setFileName(String fileName)
    {
        if(fileName.equals(file.getName()))return true;
        String absolutePath = this.file.getAbsolutePath();
        int lastIndexOf = absolutePath.lastIndexOf(File.separator);
        absolutePath = absolutePath.substring(0, (lastIndexOf + File.separator.length()));
        File dest = new File(absolutePath + File.separator + fileName);
        boolean flag = this.file.renameTo(dest);
        if(flag)
        {
            this.file = dest;
        }
        return flag;
    }
    
    public boolean moveImpl(MyFile to)
    {
        boolean flag = false;
        flag = copy(to);
        flag = delete();
        return flag;
    }

    public void open() throws IOException
    {
        Desktop desktop = Desktop.getDesktop();
        try
        {
            desktop.open(file);
        } catch (IOException ex)
        {
            desktop.edit(file);
        }
    }

    public String getAbsolutePath()
    {
        return this.file.getAbsolutePath();
    }

    public boolean exists()
    {
        return this.file.exists();
    }

    public boolean copy(MyFile dest)
    {
        System.out.println("FROM: " + file.getAbsolutePath());
        System.out.println("TO: " + dest.getAbsolutePath());
        this.transfered = 0;
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try
        {
            fileInputStream = new FileInputStream(this.file);
            fileOutputStream = new FileOutputStream(new File(dest.getAbsolutePath()));

            FileChannel inputChannel = fileInputStream.getChannel();
            FileChannel outputChannel = fileOutputStream.getChannel();

            transfer(inputChannel, outputChannel, getFileSize(), 1024 * 1024 * 32 /* 32 MB */, true);

            fileInputStream.close();
            fileOutputStream.close();
            return true;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(fileInputStream != null)
            {
                try
                {
                    fileInputStream.close();
                } catch (IOException ex){}
            }
            if(fileOutputStream != null)
            {
                try
                {
                    fileOutputStream.close();
                } catch (IOException ex){}
            }
        }
        return false;
    }

    public void transfer(FileChannel inputChannel, ByteChannel outputChannel, long lengthInBytes, long chunckSizeInBytes, boolean verbose) throws IOException
    {
        long overallBytesTransfered = 0L;
        long otime = System.currentTimeMillis();
        long ltime = -System.currentTimeMillis();
        long bytesInSecond = 0;
        while (overallBytesTransfered < lengthInBytes)
        {
            long bytesToTransfer = Math.min(chunckSizeInBytes, lengthInBytes - overallBytesTransfered);
            long bytesTransfered = inputChannel.transferTo(overallBytesTransfered, bytesToTransfer, outputChannel);

            overallBytesTransfered += bytesTransfered;

            if (verbose)
            {
                long percentageOfOverallBytesTransfered = Math.round(overallBytesTransfered / ((double) lengthInBytes) * 100.0);
                System.out.printf("overall bytes transfered: %s progress %s%%\n", overallBytesTransfered, percentageOfOverallBytesTransfered); 
            }
            this.transfered += bytesTransfered;
            
            long time = System.currentTimeMillis();
            bytesInSecond += bytesTransfered;
            this.transferRate = bytesInSecond / 1024;
            if (time - otime > 1000)
            {
                if(verbose)
                {
                    System.out.println(transferRate + " kbytes/s");
                }
                otime = System.currentTimeMillis();
                bytesInSecond = 0;
            }
        }
        ltime += System.currentTimeMillis();

        if (verbose)
        {
            double kiloBytesPerSecond = (overallBytesTransfered / 1024.0) / (ltime / 1000.0);
            System.out.printf("Transfered: %s bytes in: %s s -> %s kbytes/s", overallBytesTransfered, ltime / 1000, kiloBytesPerSecond);
        }
    }

    public boolean delete()
    {
        boolean flag = false;
        if(this.isDirectory())
        {
            try
            {
                for(MyFile f : this.getFiles())
                {
                    flag = f.delete();
                    if(flag == false)
                    {
                        return false;
                    }
                }
                flag = this.file.delete();
                if(flag == false)
                {
                    return false;
                }
            } catch (IOException ex)
            {
                Logger.getLogger(LocalFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            flag = this.file.delete();
            if(flag == false)
            {
                return false;
            }
        }
        return flag;
    }

    public boolean cut(MyFile dest)
    {
        boolean copy = copy(dest);
        copy = delete();
        return copy;
    }

    public double getFileTransferRate()
    {
        return this.transferRate;
    }

    public long getLeftSize()
    {
        return this.transfered;
    }
    
    public File getFile()
    {
        return this.file;
    }
    
    public MyFile createDirectory(String name, boolean force) throws Exception
    {
        File f = new File(this.file.getAbsolutePath() + File.separator + name);
        if(f.exists() && force == false)
        {
            throw new FileExistsException("Ordner existiert bereits !");
        }
        boolean mkdir = f.mkdir();
        if(!mkdir && force == false)
        {
            throw new IOException("Ordner konnte nicht erstellt werden !");
        }
        return new LocalFile(f);
    }
    
    public MyFile createDirectory() throws Exception
    {
        boolean mkdir = this.file.mkdir();
        if(!mkdir)
        {
            throw new IOException("Ordner konnte nicht erstellt werden !");
        }
        return this;
    }
    
    public MyFile createFile(String name) throws Exception
    {
        File f = new File(this.file.getAbsolutePath() + File.separator + name);
        if(f.exists())
        {
            throw new FileExistsException("Datei existiert bereits !");
        }
        boolean crFile = f.createNewFile();
        if(!crFile)
        {
            throw new IOException("Datei konnte nicht erstellt werden !");
        }
        return new LocalFile(f);
    }
    
    public MyFile createFile() throws Exception
    {
        boolean crFile = this.file.createNewFile();
        if(!crFile)
        {
            throw new IOException("Datei konnte nicht erstellt werden !");
        }
        return this;
    }
    
    public MyFile getParentDirectory()
    {
        return new LocalFile(this.file.getParentFile());
    }
    
    public boolean containsFile(String fileName)
    {
        String absolutePath = file.getAbsolutePath();
        int index = absolutePath.lastIndexOf(File.separator);
        absolutePath = absolutePath.substring(0, index);
        MyFile f = new LocalFile(absolutePath + File.separator + fileName);
        return f.exists();
    }
}
