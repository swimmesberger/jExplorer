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
package jexplorer.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.jgoodies.forms.layout.Sizes;

public class TitledSeparator extends JPanel
{

    /**
     * The font for the title text.
     */
    private Font m_title_font;
    /**
     * The color of the title text.
     */
    private Color m_title_color;
    /**
     * The underlying JLabel component.
     */
    private JLabel m_label;

    /**
     * Creates a default TitledSeparator instance
     */
    public TitledSeparator()
    {
        this("Title", SwingConstants.LEFT);
    }

    /**
     * Creates and returns a labeled separator with the label in the left-hand
     * side. Useful to separate paragraphs in a panel; often a better choice
     * than a <code>TitledBorder</code>.
     * 
     * @param text
     *            the title's text
     * @return a title label with separator on the right side
     */
    public TitledSeparator(String text)
    {
        this(text, SwingConstants.LEFT);
    }

    /**
     * Creates and returns a labeled separator. Useful to separate paragraphs in
     * a panel, which is often a better choice than a <code>TitledBorder</code>.
     * 
     * @param text
     *            the title's text
     * @param alignment
     *            text alignment: left, center, right
     * @return a separator with title label
     */
    public TitledSeparator(String text, int alignment)
    {
        setLayout(new TitledSeparatorLayout());

        if (text == null)
        {
            m_label = new TitleLabel();
        }
        else
        {
            m_label = new TitleLabel(text);
        }

        m_label.setForeground(UIManager.getColor("TitledBorder.titleColor"));
        m_label.setFont(UIManager.getFont("TitledBorder.font"));
        m_label.setVerticalAlignment(SwingConstants.CENTER);
        m_label.setHorizontalAlignment(alignment);

        createSeparator(m_label);
    }

    /**
     * Creates and returns a labeled separator. Useful to separate paragraphs in
     * a panel, which is often a better choice than a <code>TitledBorder</code>.
     * <p>
     * 
     * The label's position is determined by the label's horizontal alignment.
     * 
     * @param label
     *            the title label component
     * @return a separator with title label
     * @throws NullPointerException
     *             if the label is <code>null</code>
     */
    private void createSeparator(JLabel label)
    {
        removeAll();
        setOpaque(false);

        add(label);
        add(new JSeparator());

        if (label != null
        && label.getHorizontalAlignment() == SwingConstants.CENTER)
        {
            add(new JSeparator());
        }
    }

    /**
     * Returns the alignment of the separator line relative to the label.
     * 
     * @return the separator alignment. SwingConstants.LEFT, CENTER, or RIGHT
     */
    public int getAlignment()
    {
        return m_label.getHorizontalAlignment();
    }

    /**
     * Returns the color of the title text
     * 
     * @return the color used for title text
     */
    public Color getTitleColor()
    {
        return m_title_color == null ? UIManager.getColor("TitledBorder.titleColor") : m_title_color;
    }

    /**
     * Returns the font of the title text.
     * 
     * @return the font used for title text.
     */
    public Font getTitleFont()
    {
        return m_title_font == null ? UIManager.getFont("TitledBorder.font") : m_title_font;
    }

    /**
     * Returns the title text.
     * 
     * @return the title text.
     */
    public String getText()
    {
        return m_label.getText();
    }

    /**
     * Sets the alignment of the separator line relative to the label.
     * 
     * @param alignment
     *            the alignment to set. SwingConstants.LEFT, CENTER, or RIGHT
     */
    public void setAlignment(int alignment)
    {
        if (m_label.getHorizontalAlignment() == alignment)
        {
            return;
        }

        m_label.setHorizontalAlignment(alignment);
        createSeparator(m_label);
    }

    /**
     * Sets the color of the title text
     * 
     * @param c
     *            the color used for title text
     */
    public void setTitleColor(Color c)
    {
        m_title_color = c;
        if (m_label != null)
        {
            m_label.setForeground(c);
        }
    }

    /**
     * Sets the font of the title text.
     * 
     * @param font
     *            the font used for title text.
     */
    public void setTitleFont(Font font)
    {
        m_title_font = font;
        if (m_label != null)
        {
            m_label.setFont(font);
        }
    }

    /**
     * Sets the title text.
     * 
     * @param title
     *            the title text to set.
     */
    public void setText(String title)
    {
        m_label.setText(title);
    }

    // A label that uses the TitleBorder font and color
    private class TitleLabel extends JLabel
    {

        private TitleLabel()
        {
            // Just invoke the super constructor.
        }

        private TitleLabel(String text)
        {
            super(text);
        }

        /**
         * Override so we can update the title text and color based on the
         * current look and feel (if needed)
         */
        @Override
        public void updateUI()
        {
            super.updateUI();
            if (m_label != null)
            {
                m_label.setForeground(getTitleColor());
                m_label.setFont(getTitleFont());
            }
        }
    }

    // A layout for the title label and separator(s) in titled separators.
    private class TitledSeparatorLayout implements
    java.awt.LayoutManager
    {

        /**
         * Constructs a TitledSeparatorLayout that centers the separators
         * 
         */
        private TitledSeparatorLayout()
        {
        }

        /**
         * Does nothing. This layout manager looks up the components from the
         * layout container and used the component's index in the child array to
         * identify the label and separators.
         * 
         * @param name
         *            the string to be associated with the component
         * @param comp
         *            the component to be added
         */
        public void addLayoutComponent(String name, Component comp)
        {
            // Does nothing.
        }

        /**
         * Does nothing. This layout manager looks up the components from the
         * layout container and used the component's index in the child array to
         * identify the label and separators.
         * 
         * @param comp
         *            the component to be removed
         */
        public void removeLayoutComponent(Component comp)
        {
            // Does nothing.
        }

        /**
         * Computes and returns the minimum size dimensions for the specified
         * container. Forwards this request to <code>#preferredLayoutSize</code>.
         * 
         * @param parent
         *            the component to be laid out
         * @return the container's minimum size.
         * @see #preferredLayoutSize(Container)
         */
        public Dimension minimumLayoutSize(Container parent)
        {
            return preferredLayoutSize(parent);
        }

        /**
         * Computes and returns the preferred size dimensions for the specified
         * container. Returns the title label's preferred size.
         * 
         * @param parent
         *            the component to be laid out
         * @return the container's preferred size.
         * @see #minimumLayoutSize(Container)
         */
        public Dimension preferredLayoutSize(Container parent)
        {
            Component label = getLabel(parent);
            Dimension labelSize = label.getPreferredSize();
            Insets insets = parent.getInsets();
            int width = Math.max(1, labelSize.width + insets.left
            + insets.right);
            int height = Math.max(1, labelSize.height + insets.top
            + insets.bottom);
            return new Dimension(width, height);
        }

        /**
         * Lays out the specified container.
         * 
         * @param parent
         *            the container to be laid out
         */
        public void layoutContainer(Container parent)
        {
            synchronized (parent.getTreeLock())
            {
                // Look up the parent size and insets
                Dimension size = parent.getSize();
                Insets insets = parent.getInsets();
                int width = size.width - insets.left - insets.right;

                // Look up components and their sizes
                JLabel label = getLabel(parent);
                Dimension labelSize = label.getPreferredSize();
                int labelWidth = labelSize.width;
                int labelHeight = labelSize.height;

                Component separator1 = parent.getComponent(1);
                int separatorHeight = separator1.getPreferredSize().height;

                java.awt.FontMetrics metrics = label.getFontMetrics(label.getFont());
                int ascent = metrics.getMaxAscent();
                int hGapDlu = 3;
                int hGap = Sizes.dialogUnitXAsPixel(hGapDlu, label);
                int vOffset = 1 + (labelHeight - separatorHeight) / 2;

                int alignment = label.getHorizontalAlignment();
                int y = insets.top;
                if (alignment == JLabel.LEFT)
                {
                    int x = insets.left;
                    label.setBounds(x, y, labelWidth, labelHeight);
                    x += labelWidth;
                    x += hGap;
                    int separatorWidth = size.width - insets.right - x;
                    separator1.setBounds(x, y + vOffset,
                    separatorWidth, separatorHeight);
                }
                else if (alignment == JLabel.RIGHT)
                {
                    int x = insets.left + width - labelWidth;
                    label.setBounds(x, y, labelWidth, labelHeight);
                    x -= hGap;
                    x--;
                    int separatorWidth = x - insets.left;
                    separator1.setBounds(insets.left, y + vOffset,
                    separatorWidth, separatorHeight);
                }
                else
                {
                    int xOffset = (width - labelWidth - 2 * hGap) / 2;
                    int x = insets.left;
                    separator1.setBounds(x, y + vOffset, xOffset - 1,
                    separatorHeight);
                    x += xOffset;
                    x += hGap;
                    label.setBounds(x, y, labelWidth, labelHeight);
                    x += labelWidth;
                    x += hGap;
                    Component separator2 = parent.getComponent(2);
                    int separatorWidth = size.width - insets.right - x;
                    separator2.setBounds(x, y + vOffset,
                    separatorWidth, separatorHeight);
                }
            }
        }

        private JLabel getLabel(Container parent)
        {
            return m_label;
        }
    }
}
