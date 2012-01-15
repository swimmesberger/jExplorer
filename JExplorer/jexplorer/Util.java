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

public class Util
{
    public static long getAvaiableMemory()
    {
        return Runtime.getRuntime().totalMemory();
        
    }
    
    public static String getAviableMemoryAsString()
    {
        long avaiableMemory = getAvaiableMemory();
        double d = avaiableMemory/1048576;
        return (int)d + "MB";
    }
    
    public static long freeMemory()
    {
        return Runtime.getRuntime().freeMemory();
    }
    
    public static long usedMemory()
    {
        return getAvaiableMemory() - freeMemory();
    }
    
    public static String usedMemoryAsString()
    {
        long avaiableMemory = usedMemory();
        double d = avaiableMemory/1048576;
        return d + "MB";
    }
    
    
    public static long cleanMemory()
    {
        Runtime.getRuntime().gc();
        return freeMemory();
    }
}
