/*
 * www.fseek.org
 * ~Thedeath
 * 2010 - 2011
 */

package org.fseek.components;

/**
 * This class saves all the selections in the mainframe and adds effects if needed
 */

import java.util.ArrayList;
import javax.swing.JComponent;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public class SelectHandler
{
    private ArrayList<JComponent> selection = new ArrayList<JComponent>();
    private CompEffects compEffects;
    
    public SelectHandler(CompEffects compEffects)
    {
        this.compEffects = compEffects;

    }

    public void addSelection(JComponent s)
    {
        synchronized(selection)
        {
            if(!selection.contains(s))
            {
                compEffects.extendSelect(true, s);
                selection.add(s);
            }
        }
    }

    public synchronized JComponent removeSelection(JComponent s)
    {
        compEffects.extendSelect(false, s);
        selection.remove(s);
        return s;
    }

    public synchronized JComponent removeSelection(int i)
    {
        compEffects.extendSelect(false, selection.get(i));
        JComponent remove = selection.remove(i);
        return remove;
    }

    public void clearSelection()
    {
        synchronized(selection)
        {
            int size = this.selection.size();
            for(int i = 0; i<size; i++)
            {
                removeSelection(0);
            }
        }
    }

    public synchronized boolean contains(JComponent s)
    {
        return selection.contains(s);
    }
}
