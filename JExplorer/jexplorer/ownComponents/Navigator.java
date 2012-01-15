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

import jexplorer.util.DefaultMainView;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import jexplorer.MainFrame;
import jexplorer.fileSystem.LocalFile;
import jexplorer.ownComponents.fileTable.FileTablePanel;
import org.fseek.plugin.interfaces.MainView;
import org.fseek.plugin.interfaces.MyFile;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public class Navigator extends javax.swing.JPanel
{
    private ImageIcon reloadIcon;
    private ImageIcon goToIcon;
    
    private boolean goTo;
    private String inputFieldBackup;
    
    private MainFrame mainFrame;
    
    private String old;
    
    private boolean fieldFoc;
    
    private HashMap<String, MainView> virtualLocations = new HashMap<String, MainView> ();
    
    
    /** Creates new form Navigator */
    public Navigator(MainFrame main)
    {
        this.mainFrame = main;
        initComponents();
        intDesign();
    }
    
    public Navigator()
    {
        initComponents();
        intDesign();
    }
    
    private void intDesign()
    {
        try
        {
            reloadIcon = new ImageIcon(MainFrame.selectedIconDirectory+File.separator+"navigatorReload.png");
            goToIcon = new ImageIcon(MainFrame.selectedIconDirectory+File.separator+"navigatorGoToButton.png");
            setIcon(new ImageIcon(MainFrame.selectedIconDirectory+File.separator+"computer.png"));
        //designer cant find MainFrame clas so I need to catch this
        }catch(java.lang.NoClassDefFoundError ex){}
        setGoTo(false);
        inputField.getDocument().addDocumentListener(new DocumentListener() 
        {
            public void insertUpdate(DocumentEvent e)
            {
                inputChanged();
            }

            public void removeUpdate(DocumentEvent e)
            {
                inputChanged();
            }

            public void changedUpdate(DocumentEvent e)
            {
                inputChanged();
            }
        });
        showButtons();
        virtualLocations.put("Computer", createComputerView());
    }
    
    private MainView createComputerView()
    {
        if(this.getMainFrame() == null)return null;
        ComputerPanel computerPanel = this.getMainFrame().getComputerPanel();
        if(computerPanel == null || this.goTo == false)
        {
            computerPanel = new ComputerPanel(this.getMainFrame().getSh(), getMainFrame());
            this.getMainFrame().setComputerPanel(computerPanel);
        }
        DefaultMainView computerView = null;
        try
        {
            computerView = new DefaultMainView(computerPanel, "Computer", new ImageIcon(MainFrame.selectedIconDirectory + File.separator + "computer.png"));
       //designer cant find MainFrame clas so I need to catch this
        }catch(java.lang.NoClassDefFoundError ex){}
        return computerView;
    }
    
    private void inputChanged()
    {
        if(this.inputField.getText().equals(this.inputFieldBackup))
        {
            setGoTo(false);
        }
        else
        {
            setGoTo(true);
        }
    }
    
    public void setGoTo(boolean bol)
    {
        if(bol)
        {
            reloadButton.setIcon(goToIcon);
            reloadButton.setToolTipText("Wechseln sie zu \""+inputField.getText()+"\"");

        }
        else
        {
           reloadButton.setIcon(reloadIcon);
           reloadButton.setToolTipText("\""+inputField.getText()+"\" aktualisieren");
        }
        this.setReloadAction(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e)
            {
                reload();
            }
        });
        goTo = bol;
    }
    
    private void reload()
    {
        navigateTo(inputField.getText());
    }
    
//    private void nagivateTo(File f)
//    {
//        this.mainFrame.setMainView(this, inputFieldBackup, goToIcon);
//    }
    
    private boolean navigateToVirtualLocation(String locName, boolean bol)
    {
        MainView get = this.virtualLocations.get(locName);
        if(get != null)
        {
            this.getMainFrame().setMainView(get, bol);
            removeButtonsTo(get.getTitel());
            this.inputField.setText("Computer");
            return true;
        }
        return false;
    }
    
    public boolean addVirtualLocation(String locName, MainView view)
    {
        if(this.virtualLocations.containsKey(locName))
        {
            return false;
        }
        this.virtualLocations.put(locName, view);
        return true;
    }
    
    private void navigateToImpl(String s, boolean bol)
    {
        if(s.equals("Computer"))
        {
            virtualLocations.put("Computer", createComputerView());
        }
        if(navigateToVirtualLocation(s, bol))
        {}
        else
        {
            String text = s;
            MyFile file = new LocalFile(text);
            if(file.exists())
            {
                DefaultMainView fileTableView = new DefaultMainView(new FileTablePanel(file, this.mainFrame), file.getFileName(), file.getIcon(false));
                mainFrame.setMainView(fileTableView, bol);
                removeButtonsTo(fileTableView.getTitel());
            }
            else
            {
                if(text.contains("\\"))
                {
                    JOptionPane.showMessageDialog(this, "Die Datei \""+text+"\" konnte nicht gefunden werden.\nÜberprüfen Sie die Schreibweise, und wiederholen Sie den Vorgang !", "Error", JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    Desktop desktop = Desktop.getDesktop();
                    try
                    {
                        if(!text.startsWith("http://"))
                        {
                            text = "http://"+text;
                        }
                        desktop.browse(new URI(text));
                    } 
                    catch (URISyntaxException ex)
                    {
                        Logger.getLogger(Navigator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(Navigator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        inputFieldBackup = s;
    }
    
    public void navigateTo(String s)
    {
        navigateToImpl(s, false);
    }
    
    public void navigateTo(String s, boolean bol)
    {
        goTo = true;
        navigateToImpl(s, bol);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        icon = new javax.swing.JLabel();
        pathPanel = new javax.swing.JPanel();
        inputField = new javax.swing.JTextField();
        reloadButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED, null, new java.awt.Color(132, 132, 132), null, null));
        setPreferredSize(new java.awt.Dimension(422, 21));

        icon.setBackground(new java.awt.Color(255, 255, 255));
        icon.setOpaque(true);

        pathPanel.setOpaque(false);
        pathPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pathPanelMouseClicked(evt);
            }
        });
        pathPanel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                pathPanelFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                pathPanelFocusLost(evt);
            }
        });
        pathPanel.setLayout(new javax.swing.BoxLayout(pathPanel, javax.swing.BoxLayout.X_AXIS));

        inputField.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        inputField.setBorder(null);
        inputField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                inputFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                inputFieldFocusLost(evt);
            }
        });
        inputField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inputFieldKeyReleased(evt);
            }
        });
        pathPanel.add(inputField);

        reloadButton.setBackground(new java.awt.Color(255, 255, 255));
        reloadButton.setToolTipText("\"Computer\" aktualisieren");
        reloadButton.setBorder(null);
        reloadButton.setBorderPainted(false);
        reloadButton.setContentAreaFilled(false);
        reloadButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        reloadButton.setFocusPainted(false);
        reloadButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                reloadButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                reloadButtonMouseExited(evt);
            }
        });
        reloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(icon, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 379, Short.MAX_VALUE)
                .addComponent(reloadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(23, 23, 23)
                    .addComponent(pathPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                    .addGap(16, 16, 16)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(reloadButton, javax.swing.GroupLayout.DEFAULT_SIZE, 16, Short.MAX_VALUE)
            .addComponent(icon, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 16, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pathPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void reloadButtonMouseEntered(java.awt.event.MouseEvent evt)//GEN-FIRST:event_reloadButtonMouseEntered
    {//GEN-HEADEREND:event_reloadButtonMouseEntered
        reloadButton.setBackground(new Color(206, 235, 251));
        reloadButton.setOpaque(true);
}//GEN-LAST:event_reloadButtonMouseEntered

    private void reloadButtonMouseExited(java.awt.event.MouseEvent evt)//GEN-FIRST:event_reloadButtonMouseExited
    {//GEN-HEADEREND:event_reloadButtonMouseExited
        reloadButton.setBackground(Color.WHITE);
        reloadButton.setOpaque(false);
}//GEN-LAST:event_reloadButtonMouseExited

    private void inputFieldFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_inputFieldFocusGained
    {//GEN-HEADEREND:event_inputFieldFocusGained
        setFieldText();
    }//GEN-LAST:event_inputFieldFocusGained

    private void inputFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_inputFieldFocusLost
    {//GEN-HEADEREND:event_inputFieldFocusLost
        if(evt.getOppositeComponent() == this.reloadButton)
        {
            return;
        }
        inputField.setText(inputFieldBackup);
        showButtons();
        fieldFoc = false;
    }//GEN-LAST:event_inputFieldFocusLost

    private void reloadButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_reloadButtonActionPerformed
    {//GEN-HEADEREND:event_reloadButtonActionPerformed
        navigateTo(this.getText());
    }//GEN-LAST:event_reloadButtonActionPerformed

    private void inputFieldKeyReleased(java.awt.event.KeyEvent evt)//GEN-FIRST:event_inputFieldKeyReleased
    {//GEN-HEADEREND:event_inputFieldKeyReleased
        int keyCode = evt.getKeyCode();
        if(keyCode == KeyEvent.VK_ENTER)
        {
            reloadButtonActionPerformed(null);
        }
    }//GEN-LAST:event_inputFieldKeyReleased

    private void pathPanelFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_pathPanelFocusGained
    {//GEN-HEADEREND:event_pathPanelFocusGained
        showField();
    }//GEN-LAST:event_pathPanelFocusGained

    private void pathPanelFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_pathPanelFocusLost
    {//GEN-HEADEREND:event_pathPanelFocusLost
        if(fieldFoc == false)
        {
            showButtons();
        }
    }//GEN-LAST:event_pathPanelFocusLost

    private void pathPanelMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_pathPanelMouseClicked
    {//GEN-HEADEREND:event_pathPanelMouseClicked
        pathPanel.requestFocus();
    }//GEN-LAST:event_pathPanelMouseClicked

    private void showField()
    {
        pathPanel.removeAll();
        pathPanel.add(this.inputField);
        this.inputField.requestFocus();
        fieldFoc = true;
        this.pathPanel.repaint();
    }
    
    private String[] customSplit(String text)
    {
        String[] split = null;
        if(text.equals(File.separator))
        {
            split = new String[]{File.separator};
        }
        else
        {
            split = text.split("\\" + File.separator);
        }
        if(split.length > 0 && split[0].equals(""))
        {
            split[0] = File.separator;
        }
        return split;
    }
    
    private void showButtons()
    {
        pathPanel.removeAll();
        setFieldText();
        String text = this.inputField.getText();
        String[] split = customSplit(text);
        int count = 0;
        JButton jButton = new JButton("Computer");
        jButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e)
            {
                goTo = true;
                navigateTo("Computer");
            }
        });
        pathPanel.add(jButton);
        if(split.length == 1)
        {
            if(split[0].equals("Computer"))
            {
                return;
            }
        }
        addSeperator(pathPanel);
        for(String s : split)
        {
            if(s != null && !s.equals(""))
            {
                JButton but = new JButton(s);
                but.addActionListener(new ActionListener() 
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        JButton source = (JButton)e.getSource();
                        String to = "";
                        MyFile actualFile = mainFrame.getActualFile();
                        if(actualFile != null)
                        {
                            String text = actualFile.getAbsolutePath();
                            String[] split = customSplit(text);
                            int count = 0;
                            for(String st : split)
                            {
                                if(count > 0)
                                {
                                    to = to +  File.separator + st;
                                }
                                else
                                {
                                    to = st;
                                }
                                String get = ComputerPanel.driveNames.get(source.getText());
                                if(get == null)
                                {
                                    get = source.getText();
                                }
                                String expr = st;
                                if(get.equals(expr))
                                {
                                    break;
                                }
                                count++;
                            }
                            navigateTo(to + File.separator);
                        }
                    }
                });
                but.setText(s);
                pathPanel.add(but);
                if(split.length != count+1)
                {
                    addSeperator(pathPanel);
                }
                count++;
            }
        }
        this.pathPanel.repaint();
    }
    
    private void addSeperator(JPanel panel)
    {
        JLabel label = new JLabel();
        try
        {
            label.setIcon(new ImageIcon(MainFrame.selectedIconDirectory + File.separator + "rightArrow_navi.png"));
        //designer cant find MainFrame clas so I need to catch this
        }catch(java.lang.NoClassDefFoundError ex){}
        label.setHorizontalAlignment(JLabel.CENTER);
        panel.add(label);
    }
    
    private void setFieldText()
    {
        this.inputFieldBackup = inputField.getText();
        if(this.mainFrame != null)
        {
            if(this.inputFieldBackup.equals("Computer"))
            {

            }
            else
            {
                MyFile actualFile = this.mainFrame.getActualFile();
                if(actualFile != null)
                {
                    this.inputField.setText(actualFile.getAbsolutePath());
                }
                else
                {
                    inputField.setText(this.mainFrame.getMainView().getTitel());
                }
            }
            this.inputField.selectAll();
        }
    }
    
    private void removeButtonsTo(String s)
    {
        ArrayList<Component> buttons = new ArrayList<Component>();
        for(Component c : this.pathPanel.getComponents())
        {
            if(c instanceof JButton)
            {
                JButton but = (JButton)c;
                String text = but.getText();
                buttons.add(but);
                if(text.equals(s))
                {
                    break;
                }
            }
            if(c instanceof JLabel)
            {
                buttons.add(c);
            }
        }
        this.pathPanel.removeAll();
        for(Component but: buttons)
        {
            this.pathPanel.add(but);
        }
        this.pathPanel.repaint();
    }
    
    public void setReloadAction(ActionListener lis)
    {
        for(ActionListener li : reloadButton.getActionListeners())
        {
            reloadButton.removeActionListener(li);
        }
        reloadButton.addActionListener(lis);
    }

    public void setIcon(Icon icon)
    {
        try
        {
            if(icon.getIconHeight() > 19 || icon.getIconWidth() > 19)
            {
                ImageIcon imgIc = (ImageIcon)icon;
                ImageIcon scaledImage = new ImageIcon(imgIc.getImage().getScaledInstance(19, 19, Image.SCALE_SMOOTH));
                this.icon.setIcon(scaledImage);
                return;
            }
        }catch(ClassCastException ex)
        {
            this.icon.setIcon(icon);
            return;
        }
        this.icon.setIcon(icon);
    }

    public String getText()
    {
        return this.inputField.getText();
    }

    public void setText(String s)
    {
        this.inputField.setText(s);
        setGoTo(this.goTo);
        showButtons();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel icon;
    private javax.swing.JTextField inputField;
    private javax.swing.JPanel pathPanel;
    private javax.swing.JButton reloadButton;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the mainFrame
     */
    public MainFrame getMainFrame()
    {
        return mainFrame;
    }

    /**
     * @param mainFrame the mainFrame to set
     */
    public void setMainFrame(MainFrame mainFrame)
    {
        this.mainFrame = mainFrame;
    }
}
