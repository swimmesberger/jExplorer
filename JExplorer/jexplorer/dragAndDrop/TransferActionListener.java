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
package jexplorer.dragAndDrop;

import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.JComponent;

public class TransferActionListener implements ActionListener,PropertyChangeListener
{

    private JComponent focusOwner = null;

    public TransferActionListener()
    {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addPropertyChangeListener("permanentFocusOwner", this);
    }

    public void propertyChange(PropertyChangeEvent e)
    {
        Object o = e.getNewValue();
        if (o instanceof JComponent)
        {
            focusOwner = (JComponent) o;
        }
        else
        {
            focusOwner = null;
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        if (focusOwner == null)
        {
            return;
        }
        String action = (String) e.getActionCommand();
        Action a = focusOwner.getActionMap().get(action);
        if (a != null)
        {
            a.actionPerformed(new ActionEvent(focusOwner,
            ActionEvent.ACTION_PERFORMED,
            null));
        }
    }
}