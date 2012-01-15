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
package jexplorer.ownComponents.fileTable;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import jexplorer.MainFrame;
import jexplorer.dragAndDrop.FileTransferHandler;
import jexplorer.dragAndDrop.FileTransferable;
import jexplorer.ownComponents.popupmenus.FileTablePopupMenu;
import org.fseek.plugin.interfaces.MyFile;

public class FileTable extends JTable implements Runnable, ClipboardOwner
{

    private static FileTable fileTable;
    private FileTableModel tableModel;
    private MyFile file;
    private MainFrame frame;
    private boolean mouseHold = false;
    private boolean strgHold = false;
    private boolean shiftHold = false;
    private ArrayList<Integer> selectedRows = new ArrayList<Integer>();
    private StartCellEditThread startCellEditThread;
    
    private Icon deleteIcon = null;

    private FileTable(MyFile file, MainFrame frame)
    {
        super();
        this.frame = frame;
        this.file = file;
        intMain();
    }
    
     private FileTable(MainFrame frame)
    {
        super();
        this.frame = frame;
        intMain();
    }

    public FileTable()
    {
        super();
    }

    public static FileTable create(MyFile file, MainFrame frame)
    {
        if (getFileTable() == null)
        {
            setFileTable(new FileTable(file, frame));
        }
        else if(getFileTable() != null && getFileTable().getFrame() != frame)
        {
            setFileTable(new FileTable(file, frame));
        }
        else
        {
            getFileTable().getRowSelector().clear();
            resetValues();
            StartCellEditThread temp = getFileTable().getStartCellEditThread();
            if (temp != null)
            {
                temp.interrupt();
            }
            getFileTable().getTableModel().setShowHiddenFiles(MainFrame.optionsHandler.isShowHiddenFiles());
            getFileTable().setFrame(frame);
            getFileTable().setFile(file);
        }
        return getFileTable();
    }
    
    public static FileTable create(MainFrame frame)
    {
        setFileTable(new FileTable(frame));
        return getFileTable();
    }
    
    private static void resetValues()
    {
        getFileTable().setMouseHold(false);
        getFileTable().setShiftHold(false);
        getFileTable().setStrgHold(false);
    }

    private void setColumEditor()
    {
        TableColumn column = this.getColumnModel().getColumn(0);
        column.setCellEditor(new FileTableCellEditor());
    }

    /**
     * @return the tableModel
     */
    public FileTableModel getTableModel()
    {
        return tableModel;
    }

    /**
     * @param tableModel the tableModel to set
     */
    public void setTableModel(FileTableModel tableModel)
    {
        this.tableModel = tableModel;
    }

    /**
     * @return the file
     */
    public MyFile getFile()
    {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(MyFile file)
    {
        this.file = file;
        Thread t = new Thread(this);
        t.start();
    }

    public void run()
    {
        intModel();
        if(this.file != null)
        {
            intSorter();
        }
        setColumEditor();
//        fitColumnToContent(0, 50);
//        fitColumnToContent(1, 10);
//        fitColumnToContent(2, 10);
//        fitColumnToContent(3, 10);
    }

    public void browse(int rowIndex)
    {
        MyFile file1 = this.getTableModel().getFile(rowIndex);
        if (file1.isDirectory())
        {
            this.getFrame().setDirectory(file1);
        }
        else
        {
            try
            {
                file1.open();
            } catch (Exception ex)
            {
                JOptionPane.showMessageDialog(this, "Datei konnte nicht geöffnet werden !\n" + ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void intTable()
    {
        this.putClientProperty("JTable.autoStartsEdit", false);
        setTableModel(new FileTableModel(MainFrame.optionsHandler.isShowHiddenFiles(), this));
        this.setModel(getTableModel());
        FileTableRenderer ren = new FileTableRenderer();
        this.setDefaultRenderer(Long.class, ren);
        this.setDefaultRenderer(FileIcon.class, ren);
        this.setDefaultRenderer(String.class, ren);
    }

    public void intSorter()
    {
        TableRowSorter sorter = new TableRowSorter();
        setRowSorter(sorter);
        sorter.setModel(getTableModel());
        sorter.toggleSortOrder(0);
    }

    private void intModel()
    {
        if(this.getFile() == null)return;
        this.getTableModel().clear();
        this.setRowSorter(null);
        try
        {
            for (MyFile f : this.getFile().getFiles())
            {
                this.getTableModel().addFile(f);
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.frame.back(true);
        }
        this.setModel(this.getTableModel());
    }

    private void intDesign()
    {
        try
        {
            deleteIcon = new ImageIcon(MainFrame.selectedIconDirectory + File.separator + "deleteIcon.png");
        }catch(java.lang.NoClassDefFoundError ex){}
        //remove listener that ENTER key selects the next colum cell
        InputMap inputMap = this.getInputMap();
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        inputMap.put(enter, "selectNextColumnCell");
//        tableScrollPane.setViewportBorder(null);
//        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        setBorder(BorderFactory.createEmptyBorder());
        setShowGrid(false);
//        tableScrollPane.getViewport().setOpaque(false);
        this.setBackground(MainFrame.colorizer.getMainBackgroundColor());
    }

    private void intListener()
    {
        this.addMouseListener(new java.awt.event.MouseAdapter()
        {

            @Override
            public void mousePressed(MouseEvent e)
            {
                fileTableMousePressed(e);
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                fileTableMouseClicked(evt);
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                fileTableMouseReleased(e);
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter()
        {

            @Override
            public void mouseDragged(MouseEvent e)
            {
                fileTableMouseDragged(e);
            }
        });
        this.addKeyListener(new java.awt.event.KeyAdapter()
        {

            @Override
            public void keyReleased(java.awt.event.KeyEvent evt)
            {
                fileTableKeyReleased(evt);
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                fileTableKeyPressed(e);
            }
        });
        this.addFocusListener(new FocusAdapter() 
        {

            @Override
            public void focusLost(FocusEvent e)
            {
                resetValues();
            }
        });
    }

    private void intDragAndDrop()
    {
        this.setDragEnabled(true);
        this.setDropMode(DropMode.ON_OR_INSERT_ROWS);
        this.setTransferHandler(new FileTransferHandler(this));
        DragSource ds = DragSource.getDefaultDragSource();
        DragGestureRecognizer dgr = ds.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, new jexplorer.dragAndDrop.FileDragGestureListener(this));
    }

    private void intMain()
    {
        intDesign();
        intTable();
        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        startFillTable();
        intListener();
        intDragAndDrop();
        setMappings(this);
    }

    private void startFillTable()
    {
        Thread t = new Thread(this);
        t.setName("TableFillerThread");
        t.start();
    }

    /**
     * @return the frame
     */
    public MainFrame getFrame()
    {
        return frame;
    }

    /**
     * @param frame the frame to set
     */
    public void setFrame(MainFrame frame)
    {
        this.frame = frame;
    }

    //double click open folder/file
    private void fileTableMouseClicked(java.awt.event.MouseEvent evt)
    {
        if (evt.getClickCount() == 2)
        {
            fileTableOpenImpl();
        }
    }

    @Override
    public boolean editCellAt(int row, int column)
    {
        boolean editCellAt = super.editCellAt(row, column, null);
        Component editorComponent = this.getEditorComponent();
        checkEditorComponentFocus(editorComponent);
        return editCellAt;
    }

    // need to override this because I want to start edit with a delay
    @Override
    public boolean editCellAt(int row, int column, EventObject e)
    {
        if(row == -1)return false;
        if (e != null)
        {
            if (e instanceof MouseEvent)
            {
                MouseEvent me = (MouseEvent) e;
                if (me.getButton() == 1 && me.getClickCount() == 1 && column == 0)
                {
                    startCellEditThread = StartCellEditThread.startEdit(this, row, column);
                    return false;
                }
            }
        }
        else
        {
            startCellEditThread = StartCellEditThread.startEdit(this, row, column);
        }
        return false;
    }
    
    public boolean renameFile(MyFile file)
    {
        int fileRow = this.getTableModel().getFileRow(file);
        return editCellAt(fileRow, 0);
    }

    // save selected row
    private void fileTableMousePressed(java.awt.event.MouseEvent evt)
    {
        if (evt.getButton() == MouseEvent.BUTTON1)
        {
            int rowAtPoint = this.rowAtPoint(evt.getPoint());
            int selectedRow = -1;
            if (this.selectedRows.size() > 0)
            {
                selectedRow = this.selectedRows.get(0);
            }
            if (shiftHold && selectedRow != -1)
            {
                selectedRows.clear();
                if (selectedRow != rowAtPoint)
                {
                    selectedRows.add(selectedRow);
                    if (rowAtPoint > selectedRow)
                    {
                        for (int i = selectedRow; i < rowAtPoint; i++)
                        {
                            selectedRows.add(i);
                        }
                    }
                    else
                    {
                        for (int i = rowAtPoint; i < selectedRow; i++)
                        {
                            selectedRows.add(i);
                        }
                    }
                }
            }
            deselectIfNeeded(evt);
            if (shiftHold)
            {
                addOnlySelectedRow(evt);
            }
            setMouseHold(true);
        }
        else if(evt.getButton() == MouseEvent.BUTTON2)
        {
            int rowAtPoint = this.rowAtPoint(evt.getPoint());
            int selectedRow = -1;
            if (this.selectedRows.size() > 0)
            {
                selectedRow = this.selectedRows.get(0);
            }
            deselectIfNeeded(evt);
            setMouseHold(true);
        }
        maybeShowPopup(evt);
    }

    private void selectRowAtPoint(java.awt.event.MouseEvent evt)
    {
        int rowAtPoint = this.rowAtPoint(evt.getPoint());
        selectedRows.add(rowAtPoint);
    }

    private void deselectIfNeeded(java.awt.event.MouseEvent evt)
    {
        int rowAtPoint = this.rowAtPoint(evt.getPoint());
        if (rowAtPoint == -1)
        {
            return;
        }
        if (this.selectedRows.contains(rowAtPoint))
        {
            try
            {
                for (int i = 0; i < selectedRows.size(); i++)
                {
                    int val = selectedRows.get(i);
                    if (val == rowAtPoint)
                    {
                        selectedRows.remove(i);
                    }
                }
            } catch (java.lang.IndexOutOfBoundsException ex)
            {
                ex.printStackTrace();
            }
        }
        else
        {
            selectedRows.add(rowAtPoint);
        }
    }

    // I dont want to select rows when mouse dragged
    private void fileTableMouseDragged(java.awt.event.MouseEvent evt)
    {
        if (mouseHold)
        {
            if (this.selectedRows.size() < 2)
            {
                addOnlySelectedRow(evt);
            }
        }
    }

    // If mouse released (left mouse click) select the row the mousePressed event saved
    private void fileTableMouseReleased(java.awt.event.MouseEvent evt)
    {
        if (evt.getButton() == MouseEvent.BUTTON1)
        {
            if (!strgHold && !shiftHold)
            {
                selectedRows.clear();
                selectRowAtPoint(evt);
            }
            addOnlySelectedRow(evt);
            setMouseHold(false);
        }
        maybeShowPopup(evt);
    }

    private void addOnlySelectedRow(java.awt.event.MouseEvent evt)
    {
        JTable jtable = (JTable) evt.getSource();
        addOnlySelectedRowImpl(jtable);
    }
    
    private void addOnlySelectedRowImpl(JTable jtable)
    {
        if(jtable == null)
        {
            jtable = this;
        }
        jtable.clearSelection();
        for (int selection : this.selectedRows)
        {
            if (selection <= jtable.getRowCount() - 1 && selection >= 0)
            {
                jtable.addRowSelectionInterval(selection, selection);
            }
        }
    }

    // if enter key presesed open the selected folder/file
    // if delete key released delte the selected folder/file
    private void fileTableKeyReleased(java.awt.event.KeyEvent evt)
    {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            fileTableOpenImpl();
        }
        else
        {
            if (evt.getKeyCode() == KeyEvent.VK_DELETE)
            {
                removeSelectedFiles();
            }
        }
        if (evt.getKeyCode() == KeyEvent.VK_CONTROL)
        {
            this.setStrgHold(false);
        }
        if (evt.getKeyCode() == KeyEvent.VK_SHIFT)
        {
            this.setShiftHold(false);
        }
    }

    private void fileTableKeyPressed(java.awt.event.KeyEvent evt)
    {
        if (evt.getKeyCode() == KeyEvent.VK_CONTROL)
        {
            this.setStrgHold(true);
        }
        if (evt.getKeyCode() == KeyEvent.VK_SHIFT)
        {
            setShiftHold(true);
        }
    }

    private void fileTableOpenImpl()
    {
        for (int selRow : getSelectedRowsInModel())
        {
            browse(selRow);
        }
    }
    
    public void removeSelectedFiles()
    {
        fileTableRemoveImpl(getSelectedRowsInModel());
    }
    
    public void removeFiles(int[] row)
    {
        fileTableRemoveImpl(row);
    }

    private void fileTableRemoveImpl(int[] row)
    {
        int[] selectedRowsInModel = row;
        int option = -1;
        if(selectedRowsInModel.length <= 1)
        {
            MyFile f = this.getTableModel().getFile(selectedRowsInModel[0]);
            option = JOptionPane.showOptionDialog(this, "Möchtest du die Datei wirklich löschen ?", f.getFileName() + " löschen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, deleteIcon, null, null);
        }
        else
        {
             option = JOptionPane.showOptionDialog(this, "Möchtest du diese " + selectedRowsInModel.length + " Elemente wirklich löschen ?", "Mehrere Elemente löschen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, deleteIcon, null, null);
        }
        if(option != JOptionPane.YES_OPTION)return;
        for (int selRow : selectedRowsInModel)
        {
            
            
            // check if the user is editing the cell, because if I remove the row when the user is editing, the table still shows the row
            if (this.editCellAt(selRow, 0))
            {
                if (this.getCellEditor() != null)
                {
                    this.getCellEditor().cancelCellEditing();
                }
            }
            boolean delete = delete(selRow);
            if (delete)
            {
                this.getTableModel().removeRow(selRow);
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Datei konnte nicht gelöscht werden !", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean delete(int selRow)
    {
        MyFile file1 = this.getTableModel().getFile(selRow);
        return file1.delete();
    }

    private int[] getSelectedRowsInModel()
    {
        int[] selectedRow = this.getSelectedRows();
        for (int i = 0; i < selectedRow.length; i++)
        {
            selectedRow[i] = this.getRowSorter().convertRowIndexToModel(selectedRow[i]);
        }
        return selectedRow;
    }

    /**
     * @return the fileTable
     */
    private static FileTable getFileTable()
    {
        return fileTable;
    }

    /**
     * @param aFileTable the fileTable to set
     */
    private static void setFileTable(FileTable aFileTable)
    {
        fileTable = aFileTable;
    }

    public void refresh()
    {
        create(file, frame);
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents)
    {
        //clipboard.setContents(null, this);
    }

    public void setClipboardContents(MyFile[] temp)
    {
        try
        {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new FileTransferable(temp, this.getTableModel()), this);
        } catch (IOException ex)
        {
            Logger.getLogger(FileTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public MyFile[] getClipboardContents()
    {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //odd: the Object param of getContents is not currently used
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
        if (hasTransferableText)
        {
            try
            {
                MyFile[] files = FileTransferHandler.getFiles(contents);
                return files;
            } 
            catch (UnsupportedFlavorException ex)
            {
                Logger.getLogger(FileTable.class.getName()).log(Level.SEVERE, null, ex);
            } 
            catch (IOException ex)
            {
                Logger.getLogger(FileTable.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch(Exception ex)
            {
                Logger.getLogger(FileTable.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private void setMappings(JTable table)
    {
        ActionMap map = table.getActionMap();
        map.put(TransferHandler.getCutAction().getValue(Action.NAME), new jexplorer.dragAndDrop.CopyCutPaseAction(this, "cut"));
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME), new jexplorer.dragAndDrop.CopyCutPaseAction(this, "copy"));
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME), new jexplorer.dragAndDrop.CopyCutPaseAction(this, "paste"));
        InputMap imap = this.getInputMap();
        imap.put(KeyStroke.getKeyStroke("ctrl X"), TransferHandler.getCutAction().getValue(Action.NAME));
        imap.put(KeyStroke.getKeyStroke("ctrl C"), TransferHandler.getCopyAction().getValue(Action.NAME));
        imap.put(KeyStroke.getKeyStroke("ctrl V"), TransferHandler.getPasteAction().getValue(Action.NAME));
    }
    
    @Override
    public void selectAll()
    {
        for(int i = 0; i<getRowCount(); i++)
        {
            selectedRows.add(i);
            addOnlySelectedRowImpl(null);
        }
    }
    

    public int[] getSelectedRowsForModel()
    {
        if(this.selectedRows.size() <= 0)return null;
        int[] rows = new int[this.selectedRows.size()];
        int count = 0;
        for (int i = 0; i < selectedRows.size(); i++)
        {
            Integer get = selectedRows.get(i);
            if(get >= 0)
            {
                rows[i] = this.getRowSorter().convertRowIndexToModel(get);
                count++;
            }
        }
        if(count <= 0)
        {
            return null;
        }
        return rows;
    }

    public ArrayList getRowSelector()
    {
        return this.selectedRows;
    }

    public MyFile createDir(String name) throws Exception
    {
        MyFile createDirectory = this.file.createDirectory(name, false);
        FileTableModel tableModel1 = this.getTableModel();
        int addFile = tableModel1.addFile(createDirectory);
        editCellAt(addFile, 0);
        return createDirectory;
    }

    public MyFile createFile(String name, String ext) throws Exception
    {
        try
        {
            MyFile createFile = this.file.createFile(name + "." + ext);
            FileTableModel tableModel1 = this.getTableModel();
            int addFile = tableModel1.addFile(createFile);
            editCellAt(addFile, 0);
            return createFile;
        } 
        catch (IOException ex)
        {
            JOptionPane.showMessageDialog(this, "Datei konnte nicht erstellt werden !\n" + ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    /**
     * @param mouseHold the mouseHold to set
     */
    public void setMouseHold(boolean mouseHold)
    {
        this.mouseHold = mouseHold;
    }

    /**
     * @param strgHold the strgHold to set
     */
    public void setStrgHold(boolean strgHold)
    {
        this.strgHold = strgHold;
    }

    /**
     * @param shiftHold the shiftHold to set
     */
    public void setShiftHold(boolean shiftHold)
    {
        this.shiftHold = shiftHold;
    }

    /**
     * @return the startCellEditThread
     */
    public StartCellEditThread getStartCellEditThread()
    {
        return startCellEditThread;
    }

    /**
     * @param startCellEditThread the startCellEditThread to set
     */
    public void setStartCellEditThread(StartCellEditThread startCellEditThread)
    {
        this.startCellEditThread = startCellEditThread;
    }

    private void maybeShowPopup(MouseEvent e)
    {
        if (e.isPopupTrigger())
        {
            FileTablePopupMenu menu = new FileTablePopupMenu(this);
            menu.show(e.getComponent(),
            e.getX(), e.getY());
        }
    }

//    if the component comes from the jexplorer.ownComponents.fileTable.FileTableCellEditor it will be selected
    private Component checkEditorComponentFocus(Component comp)
    {
        JTextField field = null;
        if(comp instanceof JPanel)
        {
            JPanel panel = (JPanel)comp;
            Component[] components = panel.getComponents();
            if(components.length >= 2)
            {
                field = (JTextField)components[1];
            }
        }
        else if(comp instanceof JTextField)
        {
            field = (JTextField)comp;
        }
        if(field != null)
        {
            field.requestFocus();
            field.selectAll();
        }
        return comp;
    }
    
    
}
