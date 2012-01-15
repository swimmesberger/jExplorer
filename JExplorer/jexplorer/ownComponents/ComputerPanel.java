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
package jexplorer.ownComponents;

/**
 * This class shows all harddrives in a panel, this class also adds the harddrives to the tree.
 */

import jexplorer.ownComponents.folderTree.DefaultLinkTreeNode;
import jexplorer.design.Rectangle;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultTreeModel;
import jexplorer.MainFrame;
import jexplorer.MyFileSystem;
import jexplorer.OSDetector;
import jexplorer.fileSystem.LocalFile;
import jexplorer.util.AddChildsThread;
import org.fseek.components.CollapseSeperator;
import org.fseek.components.SelectHandler;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public class ComputerPanel extends GrafikPanel implements Runnable
{

//    public static CheckNewDrivesThread checkDriveThread;

    private static String mainDrive;

    private SelectHandler sh;
    
    public static FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    
    private static ArrayList<File> roots;
    
    public static HashMap<String,String> driveNames = new HashMap<String, String>();
    
    private MainFrame frame;
    
    private Thread drivesThread;
    /** Creates new form ComputerPanel */
    public ComputerPanel(SelectHandler sh, MainFrame frame)
    {
        this.sh = sh;
        this.frame = frame;
        initComputerPanel();
    }
    
//    public ComputerPanel(SelectHandler sh, MainFrame frame, ArrayList<File> roots)
//    {
//        this.sh = sh;
//        this.frame = frame;
//        initComputerPanel();
//    }
    
    private void initComputerPanel()
    {
        initComponents();
        harddrives.setTitle("Benutzbare Geräte");
        devices.setTitle("Andere Geräte");
        drivesThread = new Thread(this);
        drivesThread.setName("HarddrivesCheckerThread");
        drivesThread.start();
    }

    private synchronized void addHarddrives()
    { 
        if(ComputerPanel.roots == null)
        {
            ComputerPanel.roots = new ArrayList<File>();
            for(File f : File.listRoots())
            {
                HardwareComp comp = addHarddrive(f);
                getRoots().add(f);
                addHarddriveToTree(comp);
            }
        }
        else
        {
            for(int i = 0; i<ComputerPanel.roots.size(); i++)
            {
                addHarddrive(ComputerPanel.roots.get(i));
            }
        }
        this.frame.getFolderTree().updateComputerNode();
        this.frame.getFolderTree().repaint();
        this.frame.getFolderTree().treeDidChange();
    }
    
    public HardwareComp addHarddrive(File f)
    {
        if(MainFrame.homeDir == null)
        {
            MainFrame.homeDir = getFileSystemView().getHomeDirectory();
        }
        String systemDisplayName = getFileSystemView().getSystemDisplayName(f);
        HardwareComp comp = new HardwareComp(f,sh,frame);
        if(systemDisplayName == null || systemDisplayName.isEmpty())
        {
            systemDisplayName = f.getPath();
        }
        if(getFileSystemView().isFloppyDrive(f))
        {
            comp.setDriveName("Diskettenlaufwerk ("+systemDisplayName+")");
            comp.setIcon(getIconForFile(f, true));
            comp.setSmallIcon(getIconForFile(f, false));
            getDevices().addComp(comp);
        }
        else if(f.canRead() == false)
        {
            if(isMainDrive(f))
            {
                if(MainFrame.homeDir.canRead())
                {
                    comp = new HardwareComp(MainFrame.homeDir, sh, frame);
                    systemDisplayName = MainFrame.homeDir.getName();
                    f = MainFrame.homeDir;
                }
            }
            comp.setDriveName(systemDisplayName);
            comp.setIcon(getIconForFile(f, true));
            comp.setSmallIcon(getIconForFile(f, false));
            getDevices().addComp(comp);
        }
        else
        {
            if(isMainDrive(f))
            {
                comp.setIcon(getIconForFile(f, true));
                comp.setSmallIcon(getIconForFile(f, false));
                comp.setMainDrive(true);
            }
            else
            {
                comp.setIcon(getIconForFile(f, true));
                comp.setSmallIcon(getIconForFile(f, false));
            }
            if(systemDisplayName == null)
            {
                systemDisplayName = f.getAbsolutePath();
            }
            comp.setDriveName(systemDisplayName);
            driveNames.put(systemDisplayName, f.getAbsolutePath());
            if(comp.getSmallIcon() == null)
            {
                comp.setSmallIcon(MyFileSystem.getSystemIcon(f, false));
            }
            if(comp.getIcon() == null)
            {
                comp.setIcon(MyFileSystem.getSystemIcon(f, true));
            }
            getHarddrives().addComp(comp);
            return comp;
        }
        return null;
    }
    
    public static Icon getIconForFile(File f, boolean large)
    {
        Icon tIcon = null;
        if(getFileSystemView().isFloppyDrive(f))
        {
            tIcon = new ImageIcon(MainFrame.selectedIconDirectory + File.separator + "floppyIcon.png");
        }
        else if(f.canRead() == false)
        {
            tIcon = MyFileSystem.getSystemIcon(f,large);
        }
        else
        {
            if(isMainDrive(f))
            {
                if(OSDetector.isWindows())
                {
                    if(large)
                    {
                        tIcon = new javax.swing.ImageIcon(MainFrame.selectedIconDirectory + File.separator + "primaryHarddriveIconWin.png");
                    }
                    else
                    {
                        tIcon = new javax.swing.ImageIcon(MainFrame.selectedIconDirectory + File.separator + "primaryHarddriveIconWinSmall.png");
                    }
                }
                else if(OSDetector.isMac())
                {
                    if(large)
                    {
                        tIcon = new javax.swing.ImageIcon(MainFrame.selectedIconDirectory + File.separator + "primaryHarddriveIconMac.png");
                    }
                    else
                    {
                        tIcon = new javax.swing.ImageIcon(MainFrame.selectedIconDirectory + File.separator + "primaryHarddriveIconMacSmall.png");
                    }                    
                }
                else
                {
                    if(large)
                    {
                        tIcon = new javax.swing.ImageIcon(MainFrame.selectedIconDirectory + File.separator + "primaryHarddriveIconLinux.png");
                    }
                    else
                    {
                        tIcon = new javax.swing.ImageIcon(MainFrame.selectedIconDirectory + File.separator + "primaryHarddriveIconLinuxSmall.png");
                    }     
                }
            }
            else
            {
                if(large)
                {
                    tIcon = new javax.swing.ImageIcon(MainFrame.selectedIconDirectory + File.separator + "harddriveIcon.png");
                }
                else
                {
                    tIcon = new javax.swing.ImageIcon(MainFrame.selectedIconDirectory + File.separator + "harddriveIconSmall.png");
                }   
            }
        }
        if(tIcon == null)
        {
            tIcon = MyFileSystem.getSystemIcon(f, large);
        }
        return tIcon;
    }
    
    public void removeHarddrive(File f)
    {
        CollapseSeperator harddrives1 = getHarddrives();
        CollapseSeperator devices1 = getDevices();
        removeHarddriveImpl(harddrives1,f);
        removeHarddriveImpl(devices1,f);
    }
    
    public void removeHarddrive(int index)
    {
        CollapseSeperator harddrives1 = getHarddrives();
        harddrives1.remove(index);
    }
    
    public void removeDevice(int index)
    {
        CollapseSeperator devices1 = getDevices();
        devices1.remove(index);
    }
    
    public boolean isDevice(File f)
    {
        if(f.canRead() == false)
        {
            return true;
        }
        if(getFileSystemView().isFloppyDrive(f))
        {
            return true;
        }
        return false;
    }
    
    private void removeHarddriveImpl(CollapseSeperator sep, File f)
    {
        for(Component c : sep.getComponents())
        {
            if(c instanceof HardwareComp)
            {
                HardwareComp hardC = (HardwareComp)c;
                if(hardC.getDrive().getAbsolutePath().equals(f.getAbsolutePath()))
                {
                    sep.remove(c);
                }
            }
        }
    }
    
    public void addHarddriveToTree(HardwareComp comp)
    {
        if(comp == null)return;
        //wait until tree build finished
        while(this.frame.getFolderTree().isFinishedBuildTree() == false){}
        try 
        {
            Thread.sleep(100);
        } catch (InterruptedException ex) 
        {
            
        }
        DefaultLinkTreeNode computerNode = this.frame.getFolderTree().getComputerNode();
        if(comp.isMainDrive())
        {
            try
            {
                File drive = comp.getDrive();
                Icon smallIcon = comp.getSmallIcon();
                String driveName = comp.getDriveName();
                DefaultLinkTreeNode linkTreeNode = new DefaultLinkTreeNode(new LocalFile(drive), smallIcon, driveName);
                DefaultTreeModel model = (DefaultTreeModel)this.frame.getFolderTree().getModel();
                model.insertNodeInto(linkTreeNode, computerNode, computerNode.getChildCount());
                AddChildsThread t = AddChildsThread.create((DefaultTreeModel)this.frame.getFolderTree().getModel(),linkTreeNode, false);
                try
                {
                    t.start();
                }catch(Exception ex){};
            }catch(NullPointerException ex)
            {
                ex.printStackTrace();
            }
        }
        else
        {
            try
            {
                DefaultLinkTreeNode linkTreeNode = new DefaultLinkTreeNode(new LocalFile(comp.getDrive()), MyFileSystem.getSystemIcon(comp.getDrive(), false), comp.getDriveName());
                DefaultTreeModel model = (DefaultTreeModel)this.frame.getFolderTree().getModel();
                model.insertNodeInto(linkTreeNode, computerNode, computerNode.getChildCount());
                AddChildsThread t = AddChildsThread.create((DefaultTreeModel)this.frame.getFolderTree().getModel(),linkTreeNode, false);
                try
                {
                    t.start();
                }catch(Exception ex){};
            }catch(NullPointerException ex)
            {
                ex.printStackTrace();
                System.out.println(comp.getDrive());
                System.out.println(comp.getDriveName());
            }
        }
    }
    
    public static boolean isMainDrive(File f)
    {
        if(mainDrive == null)
        {
            if(MainFrame.homeDir == null)
            {
                MainFrame.homeDir = fileSystemView.getHomeDirectory();
            }
            ComputerPanel.mainDrive = getDriveBegin(MainFrame.homeDir);
        }
        String s = getDriveBegin(f);
        if(s.equals(ComputerPanel.mainDrive))
        {
            return true;
        }
        return false;
    }
    
    public static boolean isDrive(File f)
    {
        String absolutePath = f.getAbsolutePath();
        int count = 0;
        while(absolutePath.contains(File.separator))
        {
            if(count > 1)
            {
                return false;
            }
            count++;
            absolutePath = absolutePath.replace(File.separator, "");
        }
        return true;
    }
    

    private static String getDriveBegin(File f)
    {
        try
        {
            String canonicalPath = f.getCanonicalPath();
            int index = canonicalPath.indexOf(File.separator);
            String s = canonicalPath.substring(0, index);
            return s;
        } catch (IOException ex)
        {
            System.out.println(ex);
        }
        return null;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        placePanel = new javax.swing.JPanel();
        devices = new org.fseek.components.CollapseSeperator(MainFrame.selectedIconDirectory.getAbsolutePath(), frame.getCompEffects());
        harddrives = new org.fseek.components.CollapseSeperator(MainFrame.selectedIconDirectory.getAbsolutePath(), frame.getCompEffects());

        setOpaque(false);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });

        placePanel.setOpaque(false);

        javax.swing.GroupLayout placePanelLayout = new javax.swing.GroupLayout(placePanel);
        placePanel.setLayout(placePanelLayout);
        placePanelLayout.setHorizontalGroup(
            placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(harddrives, javax.swing.GroupLayout.DEFAULT_SIZE, 628, Short.MAX_VALUE)
            .addComponent(devices, javax.swing.GroupLayout.DEFAULT_SIZE, 628, Short.MAX_VALUE)
        );
        placePanelLayout.setVerticalGroup(
            placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(placePanelLayout.createSequentialGroup()
                .addComponent(harddrives, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(devices, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(placePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(placePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseDragged(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMouseDragged
    {//GEN-HEADEREND:event_formMouseDragged
        JPanel hardrivesContent = getHarddrives().getContentPanel();
        Rectangle oldRect = super.getOldRect();
        for(Component c : hardrivesContent.getComponents())
        {
            if(isInner(c, oldRect))
            {
                sh.addSelection((JComponent) c);
            }
            else
            {
                sh.removeSelection((JComponent) c);
            }
        }
        JPanel devicesContent = getDevices().getContentPanel();
        for(Component c : devicesContent.getComponents())
        {
            if(isInner(c, oldRect))
            {
                sh.addSelection((JComponent) c);
            }
            else
            {
                sh.removeSelection((JComponent) c);
            }
        }
    }//GEN-LAST:event_formMouseDragged

    private void formMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMousePressed
    {//GEN-HEADEREND:event_formMousePressed
        sh.clearSelection();
    }//GEN-LAST:event_formMousePressed

    private void informChilds()
    {
        for(Component c : this.getComponents())
        {
            if(c instanceof CollapseSeperator)
            {
                CollapseSeperator sep = (CollapseSeperator)c;
                sep.informInvisible();
            }
        }
    }
    
    public void informInvisible()
    {
        informChilds();
    }
    
    private boolean isInner(Component c, Rectangle r)
    {
        Point location = c.getLocation();
        Dimension size = c.getSize();
        Point location2 = new Point((int)(location.x+size.getWidth()) , (int)(location.y+size.getHeight()));
        int x = r.getX();
        int y = r.getY();
        int x2 = x + r.getWidth();
        int y2 = y+ r.getHeight();
        if(x <= location.x && y <= location.y)
        {
            if(x2 >= location2.x && y2 >= location2.y)
            {
                return true;
            }
        }
        return false;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.fseek.components.CollapseSeperator devices;
    private org.fseek.components.CollapseSeperator harddrives;
    private javax.swing.JPanel placePanel;
    // End of variables declaration//GEN-END:variables

    public void run()
    {
        addHarddrives();
        getHarddrives().setCollapsed(true);
        getDevices().setCollapsed(true);
        this.revalidate();
    }
    

    /**
     * @return the devices
     */
    public CollapseSeperator getDevices()
    {
        return devices;
    }

    /**
     * @return the harddrives
     */
    public CollapseSeperator getHarddrives()
    {
        return harddrives;
    }

    /**
     * @return the fileSystemView
     */
    public static FileSystemView getFileSystemView()
    {
        return fileSystemView;
    }
    
    public ArrayList<File> getRoots()
    {
        return roots;
    }
    
    public void interrupt()
    {
        if(drivesThread != null)
        drivesThread.interrupt();
    }
}
