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

import javax.swing.Icon;

public interface MyFile
{
    boolean isDirectory();
    MyFile createDirectory(String name, boolean force) throws Exception;
    MyFile createDirectory() throws Exception;
    MyFile createFile(String name) throws Exception;
    MyFile createFile() throws Exception;
    String getFileName();
    MyFile getParentDirectory();
    boolean containsFile(String fileName);
    boolean move(MyFile to);
    boolean setFileName(String fileName);
    long getFileSize();
    long getLastModified();
    double getFileTransferRate();
    long getLeftSize();
    MyFile[] getFiles() throws Exception;
    boolean isHidden();
    boolean exists();
    Icon getIcon(boolean large);
    String getType();
    String getExtension();
    void open() throws Exception;
    String getAbsolutePath();
    boolean copy(MyFile dest);
    boolean delete();
    boolean cut(MyFile dest);
}
