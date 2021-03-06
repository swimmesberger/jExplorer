/*
 * www.fseek.org
 * ~Thedeath
 * 2010 - 2011
 */

/*
 * CollapseSeperator.java
 *
 * Created on 14.04.2011, 15:10:37
 */

package org.fseek.components;

import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public class CollapseSeperator extends javax.swing.JPanel
{
    private boolean collapsed = true;

    private int compCount = 0;
    private int lines = 1;

    private ImageIcon uncollapsedIcon = null;
    private ImageIcon collapsedIcon = null;
    private String selectedIconDirectory;
    
    private CompEffects compEffects;
    
    public CollapseSeperator(String selectedIconDirectory, CompEffects compEffects)
    {
        this.selectedIconDirectory = selectedIconDirectory;
        this.compEffects = compEffects;
        initComponents();
        intIcons();
        intDesign();
        intFocusTraversal();
    }
    
    public CollapseSeperator()
    {
        this.selectedIconDirectory = null;
        this.compEffects = null;
        initComponents();
        intIcons();
        intDesign();
        intFocusTraversal();
    }
    
    private void intFocusTraversal()
    {
        contentPanel.setFocusCycleRoot(true);
        contentPanel.setFocusTraversalKeysEnabled(true);
        Set<AWTKeyStroke> backSet = new HashSet<AWTKeyStroke>(contentPanel.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        backSet.add(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0));
        Set<AWTKeyStroke> forwardSet = new HashSet<AWTKeyStroke>(contentPanel.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forwardSet.add(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));
        contentPanel.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backSet);
        contentPanel.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardSet);
        contentPanel.setFocusTraversalPolicy(new MyOwnFocusTraversalPolicy());
    }
    
    private void intDesign()
    {
        titleLabel.setForeground(CompEffects.getRecommendedFontColor(false));
        Font recommendedFont = CompEffects.getRecommendedFont();
        titleLabel.setFont(new java.awt.Font(recommendedFont.getName(), 0, 15));
        titleIcon.setIcon(uncollapsedIcon);
    }
    
    private void intIcons()
    {
        try
        {
            if(selectedIconDirectory != null)
            {
                uncollapsedIcon = new ImageIcon(selectedIconDirectory+File.separator+"uncollapsed.png");
                collapsedIcon = new ImageIcon(selectedIconDirectory+File.separator+"collapsed.png");
            }
        //designer cant find MainFrame clas so I need to catch this
        }catch(java.lang.NoClassDefFoundError ex){}
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titlePanel = new javax.swing.JPanel();
        titleIcon = new javax.swing.JLabel();
        titleLabel = new javax.swing.JLabel();
        titleSeperator = new javax.swing.JSeparator();
        contentPanel = new javax.swing.JPanel();

        setOpaque(false);

        titlePanel.setOpaque(false);
        titlePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                titlePanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                titlePanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                titlePanelMouseExited(evt);
            }
        });
        titlePanel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                titlePanelFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                titlePanelFocusLost(evt);
            }
        });

        titleLabel.setFont(new java.awt.Font("Segoe UI", 0, 15));
        titleLabel.setForeground(new java.awt.Color(30, 50, 135));
        titleLabel.setText("Festplatten");

        titleSeperator.setForeground(new java.awt.Color(218, 222, 227));

        javax.swing.GroupLayout titlePanelLayout = new javax.swing.GroupLayout(titlePanel);
        titlePanel.setLayout(titlePanelLayout);
        titlePanelLayout.setHorizontalGroup(
            titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(titlePanelLayout.createSequentialGroup()
                .addComponent(titleIcon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(titleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(titleSeperator, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE))
        );
        titlePanelLayout.setVerticalGroup(
            titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, titlePanelLayout.createSequentialGroup()
                .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(titlePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(titleSeperator, javax.swing.GroupLayout.DEFAULT_SIZE, 10, Short.MAX_VALUE))
                    .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(titleIcon, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(titleLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        contentPanel.setOpaque(false);
        contentPanel.setLayout(new java.awt.GridLayout(1, 0, 10, 10));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(titlePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(titlePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void titlePanelMouseEntered(java.awt.event.MouseEvent evt)//GEN-FIRST:event_titlePanelMouseEntered
    {//GEN-HEADEREND:event_titlePanelMouseEntered
        compEffects.mouseOver(true, this.titlePanel);
    }//GEN-LAST:event_titlePanelMouseEntered

    private void titlePanelMouseExited(java.awt.event.MouseEvent evt)//GEN-FIRST:event_titlePanelMouseExited
    {//GEN-HEADEREND:event_titlePanelMouseExited
        compEffects.mouseOver(false, this.titlePanel);
    }//GEN-LAST:event_titlePanelMouseExited

    private void titlePanelMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_titlePanelMouseClicked
    {//GEN-HEADEREND:event_titlePanelMouseClicked
        if(collapsed == true)
        {
            setCollapsed(false);
        }
        else
        {
            setCollapsed(true);
        }
        titlePanel.requestFocus();
    }//GEN-LAST:event_titlePanelMouseClicked

    private void titlePanelFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_titlePanelFocusGained
    {//GEN-HEADEREND:event_titlePanelFocusGained
        compEffects.select(true, this.titlePanel);
    }//GEN-LAST:event_titlePanelFocusGained

    private void titlePanelFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_titlePanelFocusLost
    {//GEN-HEADEREND:event_titlePanelFocusLost
        compEffects.select(false, this.titlePanel);
    }//GEN-LAST:event_titlePanelFocusLost

    public void setCollapsed(boolean bol)
    {
        if(bol)
        {
            titleIcon.setIcon(collapsedIcon);
        }
        else
        {
            titleIcon.setIcon(uncollapsedIcon);
            this.setSize(titlePanel.getSize());
        }
        collapsed = bol;
        contentPanel.setVisible(bol);
        JComponent comp = (JComponent)this.getParent();
        comp.revalidate();
        comp.repaint();
    }
    
    public void informInvisible()
    {
        compEffects.mouseOver(false, this.titlePanel);
        informChilds();
    }
    
    private void informChilds()
    {
        for(Component c : this.contentPanel.getComponents())
        {
            if(c instanceof ViewComponent)
            {
                ViewComponent comp = (ViewComponent)c;
                comp.informInvisible();
            }
        }
    }
    
    public void setTitle(String title)
    {
        titleLabel.setText(title);
    }

    public JPanel getContentPanel()
    {
        return contentPanel;
    }

    public void addComp(Component c)
    {
        compCount++;
        contentPanel.setLayout(new java.awt.GridLayout(lines, compCount, 10, 10));
        contentPanel.add(c);
    }

    public void setContentPanel(JPanel panel)
    {
        contentPanel = panel;
    }

    public void putOneDown()
    {
        lines++;
        contentPanel.setLayout(new java.awt.GridLayout(lines, compCount--, 10, 10));
    }

    public void putOneUp()
    {
        lines--;
        contentPanel.setLayout(new java.awt.GridLayout(lines, compCount++, 10, 10));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentPanel;
    private javax.swing.JLabel titleIcon;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JSeparator titleSeperator;
    // End of variables declaration//GEN-END:variables

    class MyOwnFocusTraversalPolicy extends FocusTraversalPolicy
    {

        @Override
        public Component getComponentAfter(Container aContainer, Component aComponent)
        {
            if(aContainer instanceof JPanel)
            {
                JPanel sep = (JPanel)aContainer;
                Component[] components = sep.getComponents();
                for(int i = 0; i<components.length; i++)
                {
                    if(components[i] == aComponent)
                    {
                        if(i+1 > components.length-1)
                        {
                            return getFirstComponent(aContainer);
                        }
                        else
                        {
                            return components[i+1];
                        }
                    }
                }
            }
            return null;
        }

        @Override
        public Component getComponentBefore(Container aContainer, Component aComponent)
        {
            if(aContainer instanceof JPanel)
            {
                JPanel sep = (JPanel)aContainer;
                Component[] components = sep.getComponents();
                for(int i = 0; i<components.length; i++)
                {
                    if(components[i] == aComponent)
                    {
                        if(i-1 < 0)
                        {
                            return getLastComponent(aContainer);
                        }
                        else
                        {
                            return components[i-1];
                        }
                    }
                }
            }
            return null;
        }

        @Override
        public Component getFirstComponent(Container aContainer)
        {
            if(aContainer instanceof JPanel)
            {
                JPanel sep = (JPanel)aContainer;
                Component[] components = sep.getComponents();
                if(components.length > 0)
                return components[0];
            }
            return null;
        }

        @Override
        public Component getLastComponent(Container aContainer)
        {
            if(aContainer instanceof JPanel)
            {
                JPanel sep = (JPanel)aContainer;
                Component[] components = sep.getComponents();
                if(components.length > 0)
                return components[components.length-1];
            }
            return null;
        }

        @Override
        public Component getDefaultComponent(Container aContainer)
        {
            if(aContainer instanceof JPanel)
            {
                JPanel sep = (JPanel)aContainer;
                Component[] components = sep.getComponents();
                if(components.length > 0)
                return components[0];
            }
            return null;
        }
        
    }
    
}
