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

import java.io.Serializable;
import java.util.HashMap;


public class ExtensionDatabase implements Serializable
{
    private String[] fullNames = new String[]{"Andwendung","Executable Jar File", "Dateiordner", "Textdokument", "XML-Dokument", "Adobe Acrobat Document", "Remotedesktopverbindung", "Textdokument", "Datenbank Container", "Konfigurationsdatei", "Waveform Audio Datei", "Komprimierte Audio Datei", "Windows Verknüpfung", "Photoshop Datei", "Portable Netzwerk Graphic","JPG-Bild"};
    private String[] ext = new String[]{"exe","jar","$dir$","txt", "xml", "pdf", "rdp", "log", "dbc", "ini", "wav", "mp3", "lnk", "psd", "png", "jpg", "db"};
    
    private HashMap<String,String> map = new HashMap<String, String>();
    
    public ExtensionDatabase()
    {
        intDatabase();
    }
    
    public String getFullName(String ext)
    {
        ext = ext.toLowerCase();
        if(map.containsKey(ext))
        {
            return map.get(ext);
        }
        return ext;
    }
    
    private void intDatabase()
    {
        for(int i = 0; i<fullNames.length; i++)
        {
            map.put(ext[i], fullNames[i]);
        }
    }
}
