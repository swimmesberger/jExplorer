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
package jexplorer.design;

import javax.swing.UIManager.LookAndFeelInfo;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public class LookAndFeelWrapper
{

    private static final long serialVersionUID = 8010506524416796786L;
    private String className;
    private String name;

    public LookAndFeelWrapper(LookAndFeelInfo lafi)
    {
        this.className = lafi.getClassName();
        this.name = lafi.getName();
    }

    public LookAndFeelWrapper(String className)
    {
        this.className = className;
        name = className.substring(className.lastIndexOf(".") + 1);
    }

    /**
     * Sets a static name. just fort displaying
     *
     * @param string
     * @return
     */
    public LookAndFeelWrapper setName(String string)
    {
        this.name = string;
        return this;
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof LookAndFeelWrapper) && ((LookAndFeelWrapper) obj).getClassName() != null && ((LookAndFeelWrapper) obj).getClassName().equals(className);
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 97 * hash + (this.className != null ? this.className.hashCode() : 0);
        return hash;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public boolean isJTattoo()
    {
        return this.className.contains("jtattoo");
    }

    public boolean isSubstance()
    {
        return this.className.contains("substance");
    }

    public String getName()
    {
        return this.name;
    }
}