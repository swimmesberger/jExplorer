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
package jexplorer.ownComponents.popupmenus;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import jexplorer.MainFrame;
import jexplorer.MyFileSystem;
import jexplorer.fileSystem.FileExistsException;
import org.fseek.plugin.interfaces.MyFile;
import jexplorer.ownComponents.fileTable.FileTable;
import jexplorer.ownComponents.fileTable.FileTableModel;

public class FileTablePopupMenu extends JPopupMenu
{

    private FileTable table;
    //icons
    private Icon folderIcon = MyFileSystem.getFolderIcon();
    private Icon textFileIcon = MyFileSystem.getTextFileIcon();
    
    private Icon deleteIcon;
    private Icon pasteIcon;
    private Icon copyIcon;
    private Icon cutIcon;
    private Icon deleteIcon_disabled;
    private Icon pasteIcon_disabled;
    private Icon copyIcon_disabled;
    private Icon cutIcon_disabled;
    
    
    public FileTablePopupMenu()
    {
        super();
    }

    public FileTablePopupMenu(FileTable table)
    {
        super();
        this.table = table;
        intMenu();
    }

    private void intMenu()
    {
        int[] selectedRows = table.getSelectedRows();
        FileTableModel m = (FileTableModel)table.getModel();
        if(selectedRows.length <= 0)
        {
            bringNoFileMenu(m.getParent().getFile());
        }
        else
        {
            bringFileMenu(m.getFile(selectedRows[0]), selectedRows);
        }
    }
    
    private void bringFileMenu(MyFile file, int[] rows)
    {
        this.add(createOpenMenu(rows[0]));
        this.add(createDeleteMenu(file));
        this.add(new javax.swing.JPopupMenu.Separator());
        this.add(createCutMenu());
        this.add(createCopyMenu());
        this.add(new javax.swing.JPopupMenu.Separator());
        this.add(createRenameMenu(file));
    }
    
    private void bringNoFileMenu(MyFile currentDir)
    {
        this.add(createRefreshMenu());
        this.add(new javax.swing.JPopupMenu.Separator());
        this.add(createPasteMenu(currentDir));
        this.add(new javax.swing.JPopupMenu.Separator());
        this.add(createNewMenu(currentDir));
    }
    
    private JMenuItem createRefreshMenu()
    {
        JMenuItem refreshItem = new JMenuItem("Aktualisieren");
        refreshItem.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e)
            {
                table.refresh();
            }
        });
        return refreshItem;
    }
    
    private JMenu createNewMenu(MyFile currentDir)
    {
        JMenu newMenu = new JMenu("Neu");
        JMenuItem newFolder = new JMenuItem("Ordner");
        newFolder.setIcon(folderIcon);
        newFolder.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e)
            {
                int count = 0;
                String name = "Neuer Ordner";
                while(true)
                {
                    try
                    {
                        MyFile createDir = table.createDir(name);
                        break;
                    } 
                    catch (FileExistsException ex)
                    {
                        count++;
                        name = name + " (" + count + ")";
                    }
                    catch (Exception ex)
                    {
                        JOptionPane.showMessageDialog(null, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        break;
                    }
                }
            }
        });
        JMenuItem newTextfile = new JMenuItem("Textdatei");
        newTextfile.setIcon(textFileIcon);
        newTextfile.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e)
            {
                int count = 0;
                String name = "Neue Datei";
                while(true)
                {
                    try
                    {
                        MyFile file = table.createFile(name, "txt");
                        break;
                    }
                    catch (FileExistsException ex)
                    {
                        count++;
                        name = name + " (" + count + ")";
                    }
                    catch (Exception ex)
                    {
                        JOptionPane.showMessageDialog(null, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        if(!currentDir.canWrite())
        {
            newFolder.setEnabled(false);
            newTextfile.setEnabled(false);
        }
        newMenu.add(newFolder);
        newMenu.add(newTextfile);
        return newMenu;
    }
    
    private JMenuItem createRenameMenu(final MyFile file)
    {
        JMenuItem renameItem = new JMenuItem("Umbenennen");
        renameItem.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e)
            {
                table.renameFile(file);
            }
        });
        if(!file.canWrite())
        {
            renameItem.setEnabled(false);
        }
        return renameItem;
    }
    
    private JMenuItem createDeleteMenu(MyFile file)
    {
        JMenuItem deleteItem = new JMenuItem("Löschen");
        setIcon(deleteIcon, deleteIcon_disabled, "deleteIconSmall.png", "deleteIconSmall_disabled.png", deleteItem);
        deleteItem.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e)
            {
                table.removeSelectedFiles();
            }
        });
        if(!file.canWrite())
        {
            deleteItem.setEnabled(false);
        }
        return deleteItem;
    }
    
    private void setIcon(Icon ic, Icon ic_disabled, String iconName, String iconNameDisabled, JMenuItem item)
    {
        if(ic == null)ic = new ImageIcon(MainFrame.selectedIconDirectory + File.separator + iconName);
        if(ic_disabled == null)ic_disabled = new ImageIcon(MainFrame.selectedIconDirectory + File.separator + iconNameDisabled);
        item.setIcon(ic);
        item.setDisabledIcon(ic_disabled);
    }

    private JMenuItem createOpenMenu(final int row)
    {
        JMenuItem openMenu = new JMenuItem("Öffnen");
        openMenu.setFont(new Font(openMenu.getFont().getName(), Font.BOLD, openMenu.getFont().getSize()));
        openMenu.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                table.browse(row);
            }
        });
        return openMenu;
    }
    
    private JMenuItem createCopyMenu()
    {
        JMenuItem createCopyPasteCutMenuImpl = createCopyPasteCutMenuImpl("Kopieren", "copy");
        setIcon(copyIcon, copyIcon_disabled, "copyIcon.png", "copyIcon_disabled.png", createCopyPasteCutMenuImpl);
        return createCopyPasteCutMenuImpl;
    }
    
    private JMenuItem createCutMenu()
    {
        JMenuItem createCopyPasteCutMenuImpl = createCopyPasteCutMenuImpl("Ausschneiden", "cut");
        setIcon(cutIcon, cutIcon_disabled, "cutIcon.png", "cutIcon_disabled.png", createCopyPasteCutMenuImpl);
        return createCopyPasteCutMenuImpl;
    }
    
    private JMenuItem createPasteMenu(MyFile currentDir)
    {
        JMenuItem pasteItem = createCopyPasteCutMenuImpl("Einfügen", "paste");
        setIcon(pasteIcon, pasteIcon_disabled, "pasteIcon.png", "pasteIcon_disabled.png", pasteItem);
        if(table.getClipboardContents() == null || !currentDir.canWrite())
        {
            pasteItem.setEnabled(false);
        }
        return pasteItem;
    }
    
    private JMenuItem createCopyPasteCutMenuImpl(String itemName, final String actionName)
    {
        JMenuItem item = new JMenuItem(itemName);
        item.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e)
            {
                jexplorer.dragAndDrop.CopyCutPaseAction action = new jexplorer.dragAndDrop.CopyCutPaseAction(table, actionName);
                action.actionPerformed(e);
            }
        });
        return item;
    }

    public class PopupListener extends MouseAdapter
    {

        public void mousePressed(MouseEvent e)
        {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e)
        {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e)
        {
            if (e.isPopupTrigger())
            {
                show(e.getComponent(),
                e.getX(), e.getY());
            }
        }
    }

}
