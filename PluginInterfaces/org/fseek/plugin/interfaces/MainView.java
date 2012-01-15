package org.fseek.plugin.interfaces;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import javax.swing.Icon;
import javax.swing.JPanel;

/**
 *
 * @author sWimmesberger
 */
public interface MainView
{

    /**
     * @return the panel
     */
    public JPanel getPanel();
    /**
     * @param panel the panel to set
     */
    public void setPanel(JPanel panel);

    /**
     * @return the titel
     */
    public String getTitel();

    /**
     * @param titel the titel to set
     */
    public void setTitel(String titel);

    /**
     * @return the icon
     */
    public Icon getIcon();

    /**
     * @param icon the icon to set
     */
    public void setIcon(Icon icon);
}
