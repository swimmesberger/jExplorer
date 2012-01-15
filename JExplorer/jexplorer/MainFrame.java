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

import jexplorer.ownComponents.folderTree.DefaultLinkTreeNode;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Stack;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import jexplorer.config.ConfigDialog;
import jexplorer.config.OptionsHandler;
import jexplorer.design.LookAndFeelController;
import jexplorer.ownComponents.AboutDialog;
import jexplorer.ownComponents.ComputerPanel;
import jexplorer.ownComponents.folderTree.FolderTree;
import jexplorer.ownComponents.GrafikPanel;
import jexplorer.ownComponents.Navigator;
import jexplorer.util.DefaultMainView;
import jexplorer.ownComponents.UpdateDialog;
import jexplorer.ownComponents.arrows;
import jexplorer.ownComponents.fileTable.FileTable;
import jexplorer.ownComponents.fileTable.FileTablePanel;
import org.fseek.plugin.interfaces.LinkTreeNode;
import jexplorer.update.Updater;
import jexplorer.util.SearchThread;
import org.fseek.components.CompEffects;
import org.fseek.components.ProgramColorizer;
import org.fseek.components.SelectHandler;
import org.fseek.plugin.api.PluginAPI;
import org.fseek.plugin.interfaces.MainView;
import org.fseek.plugin.interfaces.MyFile;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public class MainFrame extends javax.swing.JFrame implements PluginAPI, Runnable
{

    // home directory
    public static File homeDir;
    // main directory where the .jar file is located
    public static File mainDir = getMainPath();

    // default language
    public static String lang = "deDE";
    public static OptionsHandler optionsHandler;
    public static ProgramColorizer colorizer;
    
    private SelectHandler sh;
    private CompEffects compEffects = new CompEffects(colorizer);
    // the selectedIconDirectory its global because the whole program have 1 unique icon directory
    public static File selectedIconDirectory = null;
    
    // the actual version of the program is needed for the update service
    public final static String version = "1.0";
    
    private ComputerPanel computerPanel;
    
    private ConfigDialog configDialog;
    
    private arrows arrows = new arrows();

    private Stack<MainView> backStack = new Stack<MainView>();
    private Stack<MainView> vorStack = new Stack<MainView>();
    private MainView actualMainView;
    private MyFile actualFile;
    
    // model for new MainFrame
    private DefaultTreeModel sModel;
    private DefaultLinkTreeNode[] importantNodes;
    private ComputerPanel sCompPanel;
    private MyFile sFile;
    
    private SearchThread searchThread;
    
    private static MemoryUsageThread tr;

    /** Creates new form MainFrame */
    public MainFrame(boolean firstStart)
    {
        if(firstStart)checkErrors();
        if(firstStart)Favorites.createFavoritesDirectory();
        initMainFrame();
        if(firstStart)new Thread(this).start();
    }
    
    public MainFrame(FolderTree tree, ComputerPanel compPanel)
    {
        this.sModel = (DefaultTreeModel)tree.getModel();
        this.importantNodes = new DefaultLinkTreeNode[]{tree.getBiblioNode(), tree.getFavoNode(), tree.getComputerNode()};
        this.sCompPanel = compPanel;
        initMainFrame();
    }
    
    public MainFrame(FolderTree tree, MyFile file)
    {
        this.sModel = (DefaultTreeModel)tree.getModel();
        this.importantNodes = new DefaultLinkTreeNode[]{tree.getBiblioNode(), tree.getFavoNode(), tree.getComputerNode()};
        initMainFrame();
        setDirectory(file);
    }
    
    private void initMainFrame()
    {
        sh = new SelectHandler(getCompEffects());
        initComponents();
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        intScrollPanel();
        intDesign();
        intColor();
        //fixLayout();
        this.arrowContainer.add(arrows);
        this.computerPanel.requestFocus();
        this.setTitle("JExplorer - Universal File Explorer");
        if(tr == null)
        {
            tr = new MemoryUsageThread(ramMenuItem);
            tr.start();
        }
        else
        {
            tr.addButton(ramMenuItem);
        }
        searchField.setFindAction(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                startSearchThread();
            }
        });
    }
    
    private void startSearchThread()
    {
        if(searchThread != null)
        {
            searchThread.interrupt();
        }
        FileTable createFileTable = createFileTable();
        searchThread = new SearchThread(this, createFileTable, searchField.getText());
        searchThread.start();
    }
    
    private FileTable createFileTable()
    {
        FileTablePanel panel = new FileTablePanel(this);
        DefaultMainView fileTableView = new DefaultMainView(panel, "Suchergebnisse" , new ImageIcon(MainFrame.selectedIconDirectory + File.separator + "search.png"));
        setMainView(fileTableView, false);
        return panel.getFileTable();
    }
    
    private void checkErrors()
    {
        if(!checkIconDir())
        {
            JOptionPane.showMessageDialog(this, "Icon Directory not found !\nDir:" + MainFrame.selectedIconDirectory.getAbsolutePath());
            System.exit(0);
        }
    }
    
    private boolean checkIconDir()
    {
        if(!selectedIconDirectory.exists())
        {
            return false;
        }
        return true;
    }
    

    
    private void doUpdate()
    {
        try
        {
            Updater up = new Updater();
            if(up.isConnected() == false)
            {
                boolean con = up.connectToUpdateServer();
                if(con == false)
                {
                    return;
                }
            }
            boolean checkUpdateNeeded = up.checkUpdateNeeded(MainFrame.version);
            if(checkUpdateNeeded == true)
            {
                boolean flag = askUpdate(this);
                if(flag == true)
                {
                    UpdateDialog upDialog = new UpdateDialog(this, true, up);
                    upDialog.setAlwaysOnTop(true);
                    upDialog.setVisible(true);
                }
            }
        } catch (java.net.ConnectException ex)
        {
            return;
        }
    }
    
    public static boolean askUpdate(JFrame frame)
    {
        int showConfirmDialog = JOptionPane.showConfirmDialog(frame, "An update is available!\r\nDo you want to install it ?");
        boolean flag = false;
        switch(showConfirmDialog)
        {
            case JOptionPane.YES_OPTION:
                flag = true;
                break;
            case JOptionPane.NO_OPTION:
            case JOptionPane.CANCEL_OPTION:
                flag = false;
                break;
        }
        return flag;
    }
    
    private void intColor()
    {
        Color mainBackgroundColor = MainFrame.colorizer.getMainBackgroundColor();
        if(mainBackgroundColor != null)
        {
            mainPanel.setOpaque(true);
            mainPanel.setBackground(mainBackgroundColor);
        }
        Color treePanelColor = MainFrame.colorizer.getTreePanelColor();
        if(treePanelColor != null)
        {
            treePanel.setOpaque(true);
            treePanel.setBackground(treePanelColor);
        }
        Color navigationPanelColor = MainFrame.colorizer.getNavigationPanelColor();
        if(navigationPanelColor != null)
        {
            navigatePanel.setOpaque(true);
            navigatePanel.setBackground(navigationPanelColor);
            searchField.setBackground(navigationPanelColor);
            searchField.setPromptBackround(Color.white);
        }
        Color seperatorColor = MainFrame.colorizer.getSeperatorColor();
        if(seperatorColor != null)
        {
//            infoPanelSeperator.setBackground(seperatorColor);
            treeSeperator.setBackground(seperatorColor);
            headSeperator.setBackground(seperatorColor);
            headSeperator.setForeground(seperatorColor);
            treeSeperator.setForeground(seperatorColor);
//            infoPanelSeperator.setForeground(seperatorColor);
        }
    }
    
    private void fixLayout()
    {
        
        headSeperator.setPreferredSize(new Dimension(1, 1));
        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(mainBackground);
        mainBackground.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//            .addComponent(infoPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//            .addComponent(infoPanelSeperator, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1068, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(treePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(treeSeperator, javax.swing.GroupLayout.PREFERRED_SIZE, 1, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(navigatePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(headSeperator, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1068, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(navigatePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(headSeperator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(treePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(treeSeperator, javax.swing.GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE))
//                .addComponent(infoPanelSeperator, javax.swing.GroupLayout.PREFERRED_SIZE,1, javax.swing.GroupLayout.PREFERRED_SIZE)
//                .addComponent(infoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        ));
        
        javax.swing.GroupLayout navigatePanelLayout = new javax.swing.GroupLayout(navigatePanel);
        navigatePanel.setLayout(navigatePanelLayout);
        navigatePanelLayout.setHorizontalGroup(
            navigatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(navigatePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(arrowContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(navigator, javax.swing.GroupLayout.DEFAULT_SIZE, 891, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        navigatePanelLayout.setVerticalGroup(
            navigatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(navigatePanelLayout.createSequentialGroup()
                .addGroup(navigatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(arrowContainer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                    .addGroup(navigatePanelLayout.createSequentialGroup()
                        .addGap(5)
                        .addGroup(navigatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(searchField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 10, Short.MAX_VALUE)
                            .addComponent(navigator, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 10, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)))
                .addContainerGap())
        );
        this.pack();
    }
    
    private void intDesign()
    {
        treeScrollPane.getViewport().setOpaque(false);
        navigator.setMainFrame(this);
        this.mainBackground.setBackground(colorizer.getMainBackgroundColor());
        this.setLocationRelativeTo(null);
        searchField.setPrompt("  Suchen");
        arrows.setEnabledLeftArrow(false);
        arrows.setEnabledRightArrow(false);
        vorMenuItem.setIcon(new ImageIcon(MainFrame.selectedIconDirectory + File.separator + "redoIcon.png"));
        backMenuItem.setIcon(new ImageIcon(MainFrame.selectedIconDirectory + File.separator + "undoIcon.png"));
        vorMenuItem.setDisabledIcon(new ImageIcon(MainFrame.selectedIconDirectory + File.separator + "redoIcon_disabled.png"));
        backMenuItem.setDisabledIcon(new ImageIcon(MainFrame.selectedIconDirectory + File.separator + "undoIcon_disabled.png"));
        getDirUpButton().setIcon(new ImageIcon(MainFrame.selectedIconDirectory + File.separator + "dirUp.png"));
        getDirUpButton().setEnabled(false);
        vorMenuItem.setEnabled(false);
        backMenuItem.setEnabled(false);
        //arrows.setEnabledRightArrow(false);
    }
    
    private void intScrollPanel()
    {
        mainScrollPanel = new javax.swing.JScrollPane();
        mainScrollPanel.setOpaque(false);
        mainScrollPanel.getViewport().setOpaque(false);
        mainScrollPanel.setBorder(null);
        mainScrollPanel.setViewportBorder(null);
        mainScrollPanel.setBorder(null);
        mainScrollPanel.setPreferredSize(new java.awt.Dimension(690, 381));
        if(sCompPanel == null)
        {
            setComputerPanel(new ComputerPanel(getSh(), this));
        }
        else
        {
            setComputerPanel(new ComputerPanel(getSh(), this));
        }
        DefaultMainView computerView = new DefaultMainView(getComputerPanel(), "Computer", new ImageIcon(MainFrame.selectedIconDirectory + File.separator + "computer.png"));
        setMainView(computerView, true);
        GridLayout mainPanelLayout = new GridLayout(1,1,0,0);
        mainPanel.setLayout(mainPanelLayout);
        mainPanel.add(mainScrollPanel);
//        mainPanelLayout.setHorizontalGroup(
//        mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(mainScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 697, Short.MAX_VALUE));
//        mainPanelLayout.setVerticalGroup(
//        mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(mainScrollPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE));
    }
    
    public void setDirectory(MyFile file)
    {
        DefaultMainView fileTableView = new DefaultMainView(new FileTablePanel(file, this), file.getFileName(), file.getIcon(false));
        setMainView(fileTableView, false);
    }
    
    public void setMainView(MainView view, boolean arrow)
    {
        if(actualMainView != null)
        {
            JPanel actualPanel = actualMainView.getPanel();
            if(actualPanel != null)
            {
                actualPanel.setVisible(false);
                if(actualPanel instanceof ComputerPanel)
                {
                    ComputerPanel p = (ComputerPanel)actualPanel;
                    p.informInvisible();
                }
            }
        }
        this.dirUpButton.setEnabled(false);
        addBack(actualMainView, arrow);
        if(view.getPanel() instanceof FileTablePanel)
        {
            FileTablePanel pan = (FileTablePanel)view.getPanel();
            FileTable table = (FileTable)pan.getFileTable();
            MyFile file = table.getFile();
            if(file != null)
            {
                setActualFile(file);
                view.setTitel(table.getFile().getAbsolutePath());
            }
            this.dirUpButton.setEnabled(true);
        }
        actualMainView = view;
        JPanel panel = view.getPanel();
        panel.setVisible(true);
        mainScrollPanel.setViewportView(panel);
        mainScrollPanel.invalidate();
        compEffects.mouseOver(false, dirUpButton);
        navigator.setText(view.getTitel());
        if(view.getIcon() != null)
        {
            navigator.setIcon((ImageIcon)view.getIcon());
        }
        navigator.setGoTo(false);
        panel.requestFocus();
        
    }
    
    public void addBack(MainView view, boolean arrow)
    {
        JButton leftArrow = this.arrows.getLeftArrow();
        addImpl(view, arrow, this.backStack, leftArrow, this.backMenuItem, false);
    }
    
    public void addVor(MainView view, boolean arrow)
    {
        JButton rightArrow = this.arrows.getRightArrow();
        addImpl(view, arrow, this.vorStack, rightArrow, this.vorMenuItem, true);
    }
    
    private void addImpl(MainView view, boolean arrow, final Stack<MainView> stack, final JButton button, final JMenuItem menuItem, final boolean vor)
    {
        if(arrow){return;}
        stack.add(view);
        button.setEnabled(true);
        menuItem.setEnabled(true);
        for(ActionListener a : button.getActionListeners())
        {
            button.removeActionListener(a);
        }
        for(ActionListener a : menuItem.getActionListeners())
        {
            menuItem.removeActionListener(a);
        }
        ActionListener actionListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                backAction(vor, stack, button, menuItem);
            }
        };
        button.addActionListener(actionListener);
        menuItem.addActionListener(actionListener);
    }
    
    /*
     * true = back
     * false = forward
     */
    public void back(boolean bol)
    {
        Stack<MainView> stack = null;
        JButton button = null;
        JMenuItem menuItem = null;
        if(!bol)
        {
            JButton rightArrow = this.arrows.getRightArrow();
            stack = this.vorStack;
            button = rightArrow;
            menuItem = this.backMenuItem;
        }
        else
        {
            JButton leftArrow = this.arrows.getLeftArrow();
            stack = this.backStack;
            button = leftArrow;
            menuItem = this.vorMenuItem;
        }
        backAction(bol, stack, button, menuItem);
    }
    
    private void backAction(boolean bol, Stack<MainView> stack, JButton button, JMenuItem menuItem)
    {
        if(stack.size() <= 0)
        {
            button.setEnabled(false);
            menuItem.setEnabled(false);
            return;
        }
        MainView pop = stack.pop();
        MainView actual = actualMainView;
        if(stack.size() <= 0)
        {
            button.setEnabled(false);
            menuItem.setEnabled(false);
        }
        if(actual.getTitel().equals(pop.getTitel()))return;
        String get = pop.getTitel();
        navigator.navigateTo(get, true);
        if(bol)
        {
            addBack(actual, false);
        }
        else
        {
            addVor(actual, false);
        }
    }
    
    public MainView getMainView()
    {
        return this.actualMainView;
    }
    

    private static File getMainPath()
    {
        String path = MainFrame.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        while(path.contains("%20"))
        {
            path = path.replace("%20", " ");
        }
        File mainFileT = new File(path);
        String absolutePath = null;
        try
        {
            absolutePath = mainFileT.getCanonicalPath();
            if (absolutePath.contains(".jar"))
            {
                int index = absolutePath.lastIndexOf(File.separator);
                absolutePath = absolutePath.substring(0, index);
            }
        } catch (IOException ex)
        {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            System.exit(1);
        }
        return new File(absolutePath);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainBackground = new javax.swing.JPanel();
        treePanel = new javax.swing.JPanel();
        treeScrollPane = new javax.swing.JScrollPane();
        folderTree = new FolderTree(this, this.sModel, importantNodes);
        treeSeperator = new javax.swing.JSeparator();
        mainPanel = new GrafikPanel();
        navigatePanel = new javax.swing.JPanel();
        navigator = new jexplorer.ownComponents.Navigator();
        searchField = new org.jdesktop.swingx.JXSearchField();
        arrowContainer = new javax.swing.JPanel();
        dirUpButton = new javax.swing.JButton();
        headSeperator = new javax.swing.JSeparator();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        vorMenuItem = new javax.swing.JMenuItem();
        backMenuItem = new javax.swing.JMenuItem();
        extrasMenu = new javax.swing.JMenu();
        optionsMenuItem = new javax.swing.JMenuItem();
        ramMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        checkUpdateMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        mainBackground.setBackground(new java.awt.Color(255, 255, 255));

        treePanel.setBackground(new java.awt.Color(255, 153, 153));
        treePanel.setOpaque(false);

        treeScrollPane.setBackground(new java.awt.Color(204, 0, 204));
        treeScrollPane.setBorder(null);
        treeScrollPane.setViewportBorder(null);
        treeScrollPane.setOpaque(false);

        folderTree.setBackground(new java.awt.Color(255, 0, 255));
        folderTree.setBorder(null);
        folderTree.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        folderTree.setOpaque(false);
        treeScrollPane.setViewportView(folderTree);

        javax.swing.GroupLayout treePanelLayout = new javax.swing.GroupLayout(treePanel);
        treePanel.setLayout(treePanelLayout);
        treePanelLayout.setHorizontalGroup(
            treePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(treePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(treeScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE))
        );
        treePanelLayout.setVerticalGroup(
            treePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(treeScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
        );

        treeSeperator.setForeground(new java.awt.Color(214, 229, 245));
        treeSeperator.setOrientation(javax.swing.SwingConstants.VERTICAL);
        treeSeperator.setPreferredSize(new java.awt.Dimension(1, 0));

        mainPanel.setOpaque(false);
        mainPanel.setLayout(new java.awt.GridLayout(1, 1));

        navigatePanel.setOpaque(false);

        navigator.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED, new java.awt.Color(225, 225, 225), new java.awt.Color(132, 132, 132), null, new java.awt.Color(216, 216, 216)));

        searchField.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED, new java.awt.Color(225, 225, 225), new java.awt.Color(132, 132, 132), null, new java.awt.Color(216, 216, 216))
        );
        searchField.setFocusBehavior(org.jdesktop.swingx.prompt.PromptSupport.FocusBehavior.SHOW_PROMPT);
        searchField.setSearchMode(org.jdesktop.swingx.JXSearchField.SearchMode.REGULAR);

        arrowContainer.setOpaque(false);
        arrowContainer.setLayout(new java.awt.GridLayout(1, 1));

        dirUpButton.setBorderPainted(false);
        dirUpButton.setContentAreaFilled(false);
        dirUpButton.setFocusPainted(false);
        dirUpButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dirUpButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dirUpButtonMouseExited(evt);
            }
        });
        dirUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dirUpButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout navigatePanelLayout = new javax.swing.GroupLayout(navigatePanel);
        navigatePanel.setLayout(navigatePanelLayout);
        navigatePanelLayout.setHorizontalGroup(
            navigatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(navigatePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(arrowContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dirUpButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(navigator, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        navigatePanelLayout.setVerticalGroup(
            navigatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(navigatePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(navigatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(arrowContainer, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dirUpButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(navigator, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(searchField, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout mainBackgroundLayout = new javax.swing.GroupLayout(mainBackground);
        mainBackground.setLayout(mainBackgroundLayout);
        mainBackgroundLayout.setHorizontalGroup(
            mainBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainBackgroundLayout.createSequentialGroup()
                .addComponent(treePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(treeSeperator, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 820, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(headSeperator, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
            .addComponent(navigatePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainBackgroundLayout.setVerticalGroup(
            mainBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainBackgroundLayout.createSequentialGroup()
                .addComponent(navigatePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(headSeperator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addGroup(mainBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(treeSeperator, javax.swing.GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
                    .addComponent(treePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)))
        );

        fileMenu.setText("File");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("New");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem1);

        menuBar.add(fileMenu);

        editMenu.setText("Edit");

        vorMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_RIGHT, java.awt.event.InputEvent.ALT_MASK));
        vorMenuItem.setText("Vorwärts");
        editMenu.add(vorMenuItem);

        backMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_LEFT, java.awt.event.InputEvent.ALT_MASK));
        backMenuItem.setText("Zurück");
        editMenu.add(backMenuItem);

        menuBar.add(editMenu);

        extrasMenu.setText("Extras");

        optionsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        optionsMenuItem.setText("Optionen");
        optionsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionsMenuItemActionPerformed(evt);
            }
        });
        extrasMenu.add(optionsMenuItem);

        ramMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        ramMenuItem.setText("RAM 0 von 0 - cleanup");
        ramMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ramMenuItemActionPerformed(evt);
            }
        });
        extrasMenu.add(ramMenuItem);

        menuBar.add(extrasMenu);

        helpMenu.setText("Help");

        checkUpdateMenuItem.setText("Auf Update überprüfen");
        checkUpdateMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkUpdateMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(checkUpdateMenuItem);

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainBackground, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainBackground, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void optionsMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_optionsMenuItemActionPerformed
    {//GEN-HEADEREND:event_optionsMenuItemActionPerformed
        if(configDialog == null)
        {
            configDialog = new ConfigDialog(this, true, optionsHandler);
        }
        configDialog.setVisible(true);
    }//GEN-LAST:event_optionsMenuItemActionPerformed

    private void checkUpdateMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_checkUpdateMenuItemActionPerformed
    {//GEN-HEADEREND:event_checkUpdateMenuItemActionPerformed
        UpdateDialog upDia = new UpdateDialog(this, true);
        upDia.setVisible(true);
    }//GEN-LAST:event_checkUpdateMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_aboutMenuItemActionPerformed
    {//GEN-HEADEREND:event_aboutMenuItemActionPerformed
        AboutDialog dia = new AboutDialog(this, true);
        dia.setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void ramMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ramMenuItemActionPerformed
    {//GEN-HEADEREND:event_ramMenuItemActionPerformed
        Util.cleanMemory();
    }//GEN-LAST:event_ramMenuItemActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                MainFrame frame = new MainFrame(getFolderTree(),getComputerPanel());
                frame.setVisible(true);
            }
        });
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
        this.tr.interrupt();
        this.computerPanel.interrupt();
    }//GEN-LAST:event_formWindowClosing

    private void dirUpButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_dirUpButtonActionPerformed
    {//GEN-HEADEREND:event_dirUpButtonActionPerformed
        if(this.getActualFile() != null)
        {
            MyFile parentDirectory = this.getActualFile().getParentDirectory();
            if(parentDirectory == null)
            {
                navigator.navigateTo("Computer", false);
            }
            else
            {
                setDirectory(parentDirectory);
            }
        }
    }//GEN-LAST:event_dirUpButtonActionPerformed

    private void dirUpButtonMouseEntered(java.awt.event.MouseEvent evt)//GEN-FIRST:event_dirUpButtonMouseEntered
    {//GEN-HEADEREND:event_dirUpButtonMouseEntered
        JComponent comp = (JComponent)(evt.getSource());
        if(comp.isEnabled())
        compEffects.mouseOver(true, comp);
    }//GEN-LAST:event_dirUpButtonMouseEntered

    private void dirUpButtonMouseExited(java.awt.event.MouseEvent evt)//GEN-FIRST:event_dirUpButtonMouseExited
    {//GEN-HEADEREND:event_dirUpButtonMouseExited
        JComponent comp = (JComponent)(evt.getSource());
        if(comp.isEnabled())
        compEffects.mouseOver(false, comp);
    }//GEN-LAST:event_dirUpButtonMouseExited

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {

            public void run()
            {
                intLookAndFeel();
                new MainFrame(true).setVisible(true);
            }
        });
    }

    public static void intLookAndFeel()
    {
        LookAndFeelController.setUIManager();
        UIManager.put("Tree.hash", MainFrame.colorizer.getTreePanelColor());
    }
    private javax.swing.JScrollPane mainScrollPanel;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JPanel arrowContainer;
    private javax.swing.JMenuItem backMenuItem;
    private javax.swing.JMenuItem checkUpdateMenuItem;
    private javax.swing.JButton dirUpButton;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenu extrasMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JTree folderTree;
    private javax.swing.JSeparator headSeperator;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel mainBackground;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JPanel navigatePanel;
    private jexplorer.ownComponents.Navigator navigator;
    private javax.swing.JMenuItem optionsMenuItem;
    private javax.swing.JMenuItem ramMenuItem;
    private org.jdesktop.swingx.JXSearchField searchField;
    private javax.swing.JPanel treePanel;
    private javax.swing.JScrollPane treeScrollPane;
    private javax.swing.JSeparator treeSeperator;
    private javax.swing.JMenuItem vorMenuItem;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the sh
     */
    public SelectHandler getSh()
    {
        return sh;
    }

    /**
     * @return the computerPanel
     */
    public ComputerPanel getComputerPanel()
    {
        return computerPanel;
    }

    /**
     * @param computerPanel the computerPanel to set
     */
    public void setComputerPanel(ComputerPanel computerPanel)
    {
        this.computerPanel = computerPanel;
    }

    /**
     * @return the actualFile
     */
    public MyFile getActualFile()
    {
        return actualFile;
    }

    /**
     * @param actualFile the actualFile to set
     */
    public void setActualFile(MyFile actualFile)
    {
        this.actualFile = actualFile;
    }
    
    public Navigator getNavigator()
    {
        return this.navigator;
    }

    /**
     * @return the folderTree
     */
    public FolderTree getFolderTree()
    {
        return (FolderTree)folderTree;
    }

    public LinkTreeNode addNodeToTree(LinkTreeNode node)
    {
        DefaultTreeModel model = (DefaultTreeModel)this.getFolderTree().getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
        model.insertNodeInto(node, root, root.getChildCount());
        return node;
    }
    
    public void addFavorite(Object node, MyFile sFile, String iconPath)
    {
        this.getFolderTree().addFavorite(node, sFile, iconPath);
    }
    
    public DefaultLinkTreeNode getBiblioNode()
    {
        return this.getFolderTree().getBiblioNode();
    }
    
    public DefaultLinkTreeNode getComputerNode()
    {
        return this.getFolderTree().getComputerNode();
    }

    public boolean addVirtualLocation(String locName, MainView view)
    {
        return this.navigator.addVirtualLocation(locName, view);
    }

    /**
     * @return the compEffects
     */
    public CompEffects getCompEffects()
    {
        return compEffects;
    }

    public void run()
    {
        doUpdate();
    }

    /**
     * @return the dirUpButton
     */
    public javax.swing.JButton getDirUpButton()
    {
        return dirUpButton;
    }
}
