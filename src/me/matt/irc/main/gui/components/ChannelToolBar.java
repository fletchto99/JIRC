package me.matt.irc.main.gui.components;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import me.matt.irc.Application;
import me.matt.irc.main.Configuration;
import me.matt.irc.main.gui.Chrome;
import me.matt.irc.main.gui.Settings;
import me.matt.irc.main.util.ImageUtil;

/**
 * Creates a toolbar which will contain all of the channels.
 * 
 * @author matthewlanglois
 * 
 */
public class ChannelToolBar extends JToolBar {
    private static final long serialVersionUID = -1861866523519184211L;

    private static final ImageIcon ICON_HOME;
    private static final ImageIcon ICON_BOT;
    private static final ImageIcon ICON_SETTINGS;

    private static Image IMAGE_CLOSE;

    private static final Image IMAGE_CLOSE_OVER;

    private static final int TAB_INDEX = 1;
    private static int BUTTON_COUNT = 3;

    static {
        ICON_HOME = new ImageIcon(
                ImageUtil.getImage(Configuration.Paths.Resources.ICON_HOME));
        ICON_BOT = new ImageIcon(
                ImageUtil.getImage(Configuration.Paths.Resources.ICON_CHANNEL));
        IMAGE_CLOSE_OVER = ImageUtil
                .getImage(Configuration.Paths.Resources.ICON_CLOSE);
        ICON_SETTINGS = new ImageIcon(
                ImageUtil.getImage(Configuration.Paths.Resources.ICON_WRENCH));
    }

    private final AddButton addTabButton;

    public final Chrome parent;
    private int idx;

    /**
     * Setup the toolbar.
     * 
     * @param parent
     *            The chrome parent.
     */
    public ChannelToolBar(final Chrome parent) {
        try {
            IMAGE_CLOSE = getTransparentImage(
                    Configuration
                            .getResourceURL(Configuration.Paths.Resources.ICON_CLOSE),
                    0.5f);
        } catch (final MalformedURLException ignored) {
        }

        this.parent = parent;

        final HomeButton home = new HomeButton(ICON_HOME);

        final SettingsButton settings = new SettingsButton(ICON_SETTINGS);

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setFloatable(false);
        add(home);
        add(addTabButton = new AddButton(parent));
        add(Box.createHorizontalGlue());
        add(settings);
        updateSelection(false);
    }

    /**
     * Adds a tab to the toolbar.
     * 
     * @param name
     *            The name of the toolbar (channel)
     */
    public void addTab(final String name) {
        final int idx = getComponentCount() - BUTTON_COUNT - TAB_INDEX + 1;
        final ChannelButton cb = new ChannelButton(name, ICON_BOT, parent);
        add(cb, idx);
        final ChannelPanel c = new ChannelPanel(idx, name);
        parent.add(c, BorderLayout.CENTER);
        parent.addPanel(c);
        validate();
        setSelection(idx);
        Application.getInstance().getTray().update();
        parent.validate();
    }

    /**
     * Removes a tab at the specified index.
     * 
     * @param idx
     *            The index of the tab to remove.
     */
    public void removeTab(int idx) {
        final int current = getCurrentTab() + TAB_INDEX;
        final int select = idx == current ? idx - TAB_INDEX : current;
        idx += TAB_INDEX;
        remove(idx);
        revalidate();
        repaint();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // update to a new selection
                setSelection(Math.max(0, select - 1));
            }
        });
        if (getButtonCount() == 9 && isAddVisible()) {
            setAdd(false);
        } else if (!isAddVisible()) {
            setAdd(true);
        }
        Application.getInstance().getTray().update();
    }

    /**
     * Fetch the currently selected tab.
     * 
     * @return The currently selected tab.
     */
    public int getCurrentTab() {
        if (idx > -1 && idx < getComponentCount() - 1) {
            return idx - TAB_INDEX;
        } else {
            return -1;
        }
    }

    /**
     * Set a new tab selection.
     * 
     * @param idx
     *            The tab to set as selected.
     */
    private void setSelection(final int idx) {
        updateSelection(true);
        this.idx = idx;
        updateSelection(false);
        parent.setVisible(idx);
    }

    /**
     * Sets the add button to visible when needed.
     * 
     * @param visible
     *            True to set the tab to visible; otherwise false.
     */
    public void setAdd(final boolean visible) {
        addTabButton.setVisible(visible);
    }

    /**
     * Check if the add button is visible
     * 
     * @return True if the tab is visible; otherwise false.
     */
    public boolean isAddVisible() {
        return addTabButton.isVisible();
    }

    /**
     * Updates the current tab.
     * 
     * @param enabled
     *            True to enable otherwise false.
     */
    private void updateSelection(final boolean enabled) {
        final int idx = getCurrentTab() + TAB_INDEX;
        if (idx >= 0) {
            getComponent(idx).setEnabled(enabled);
            getComponent(idx).repaint();
        }
    }

    /**
     * Fetches the button count.
     * 
     * @return The button count.
     */
    public int getButtonCount() {
        return getComponentCount();
    }

    /**
     * Fetches an image and makes it transparent.
     * 
     * @param url
     *            The image location to url.
     * @param transparency
     *            The transparency.
     * @return The transparent image.
     */
    private static Image getTransparentImage(final URL url,
            final float transparency) {
        BufferedImage parentImage = null;
        try {
            parentImage = ImageIO.read(url);
        } catch (final IOException ignored) {
        }
        try {
            final BufferedImage bufferedImage = new BufferedImage(
                    parentImage.getWidth(), parentImage.getHeight(),
                    Transparency.TRANSLUCENT);
            final Graphics2D graphics = bufferedImage.createGraphics();
            graphics.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, transparency));
            graphics.drawImage(parentImage, null, 0, 0);
            graphics.dispose();
            return bufferedImage;
        } catch (final NullPointerException ignored) {
            return null;
        }
    }

    /**
     * Home button to access the home panel.
     * 
     * @author matthewlanglois
     * 
     */
    private class HomeButton extends JPanel {
        private static final long serialVersionUID = 938456324328L;

        private final Image image;
        private boolean hovered;

        /**
         * Create the homebutton.
         * 
         * @param icon
         *            The icon to set it to.
         */
        public HomeButton(final ImageIcon icon) {
            super(new BorderLayout());
            image = icon.getImage();
            setBorder(new EmptyBorder(3, 6, 2, 3));
            setPreferredSize(new Dimension(24, 22));
            setMaximumSize(new Dimension(24, 22));
            setFocusable(false);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(final MouseEvent e) {
                    setSelection(getComponentIndex(HomeButton.this));
                }

                @Override
                public void mouseEntered(final MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        /**
         * Paint the component.
         */
        @Override
        public void paintComponent(final Graphics g) {
            super.paintComponent(g);
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            if (getComponentIndex(this) == idx) {
                g.setColor(new Color(255, 255, 255, 200));
                g.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 1, 4, 4);
                g.setColor(new Color(180, 180, 180, 200));
                g.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 1, 4, 4);
            } else if (hovered) {
                g.setColor(new Color(255, 255, 255, 150));
                g.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 1, 4, 4);
                g.setColor(new Color(180, 180, 180, 150));
                g.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 1, 4, 4);
            }
            g.drawImage(image, 3, 3, null);
        }

    }

    /**
     * Settings button to access the settings.
     * 
     * @author matthewlanglois
     * 
     */
    private class SettingsButton extends JPanel {
        private static final long serialVersionUID = 938456324328L;

        private final Image image;
        private boolean hovered;

        public SettingsButton(final ImageIcon icon) {
            super(new BorderLayout());
            image = icon.getImage();
            setBorder(new EmptyBorder(3, 6, 2, 3));
            setPreferredSize(new Dimension(24, 22));
            setMaximumSize(new Dimension(24, 22));
            setFocusable(false);

            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseReleased(final MouseEvent e) {
                    final Settings settingsMenu = new Settings(
                            ChannelToolBar.this);
                    settingsMenu.show(SettingsButton.this, 12, 10);
                }

                @Override
                public void mouseEntered(final MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        @Override
        public void paintComponent(final Graphics g) {
            super.paintComponent(g);
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            if (getComponentIndex(this) == idx) {
                g.setColor(new Color(255, 255, 255, 200));
                g.fillRoundRect(0, 0, getWidth(), getHeight() - 1, 4, 4);
                g.setColor(new Color(180, 180, 180, 200));
                g.drawRoundRect(0, 0, getWidth(), getHeight() - 1, 4, 4);
            } else if (hovered) {
                g.setColor(new Color(255, 255, 255, 150));
                g.fillRoundRect(0, 0, getWidth(), getHeight() - 1, 4, 4);
                g.setColor(new Color(180, 180, 180, 150));
                g.drawRoundRect(0, 0, getWidth(), getHeight() - 1, 4, 4);
            }
            g.drawImage(image, 3, 3, null);
        }

    }

    /**
     * Channel button to access the channel panel.
     * 
     * @author matthewlanglois
     * 
     */
    public class ChannelButton extends JPanel {
        private static final long serialVersionUID = 329845763420L;

        private final JLabel nameLabel;
        private boolean hovered;
        private boolean close;

        public ChannelButton(final String text, final Icon icon,
                final ActionListener listener) {
            super(new BorderLayout());
            setBorder(new EmptyBorder(3, 6, 2, 3));
            nameLabel = new JLabel(text);
            nameLabel.setIcon(icon);
            nameLabel.setPreferredSize(new Dimension(85, 22));
            nameLabel.setMaximumSize(new Dimension(85, 22));
            add(nameLabel, BorderLayout.WEST);

            setPreferredSize(new Dimension(110, 22));
            setMaximumSize(new Dimension(110, 22));
            setFocusable(false);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(final MouseEvent e) {
                    if (hovered && close) {
                        final int idx = getComponentIndex(ChannelButton.this)
                                - TAB_INDEX;
                        listener.actionPerformed(new ActionEvent(this,
                                ActionEvent.ACTION_PERFORMED, "remove" + "."
                                        + idx));
                    } else {
                        setSelection(getComponentIndex(ChannelButton.this));
                    }
                }

                @Override
                public void mouseEntered(final MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(final MouseEvent e) {
                    close = e.getX() > 95;
                    repaint();
                }
            });
        }

        public String getChannelLabel() {
            return nameLabel.getText();
        }

        @Override
        public void paintComponent(final Graphics g) {
            super.paintComponent(g);
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            final int RGB = Configuration.isSkinAvailable() ? getComponentIndex(this) == idx ? 100
                    : hovered ? 70 : 35
                    : getComponentIndex(this) == idx ? 255 : hovered ? 230
                            : 215;
            g.setColor(new Color(RGB, RGB, RGB, 200));
            g.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 1, 4, 4);
            g.setColor(new Color(180, 180, 180, 200));
            g.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 1, 4, 4);
            g.drawImage(hovered && close ? IMAGE_CLOSE_OVER : IMAGE_CLOSE, 90,
                    3, null);
        }
    }

    /**
     * Add button to access the home panel.
     * 
     * @author matthewlanglois
     * 
     */
    private class AddButton extends JComponent {
        private static final long serialVersionUID = 1L;

        private final Image ICON;
        private final Image ICON_OVER;
        private final Image ICON_DOWN;
        private boolean hovered = false;
        private boolean pressed = false;

        public AddButton(final ActionListener listener) {
            ICON_DOWN = ImageUtil
                    .getImage(Configuration.Paths.Resources.ICON_ADD);
            URL src = null;
            try {
                src = Configuration
                        .getResourceURL(Configuration.Paths.Resources.ICON_ADD);
            } catch (final MalformedURLException ignored) {
            }
            ICON = getTransparentImage(src, 0.3f);
            ICON_OVER = getTransparentImage(src, 0.7f);

            setPreferredSize(new Dimension(20, 20));
            setMaximumSize(new Dimension(20, 20));
            setFocusable(false);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(final MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                    hovered = false;
                    repaint();
                }

                @Override
                public void mousePressed(final MouseEvent e) {
                    pressed = true;
                    repaint();
                }

                @Override
                public void mouseReleased(final MouseEvent e) {
                    if (getButtonCount() < 10) {
                        pressed = false;
                        repaint();
                        listener.actionPerformed(new ActionEvent(this, e
                                .getID(), "add"));
                    } else {
                        JOptionPane
                                .showMessageDialog(null,
                                        "You have reached the maximum amount of channels!");
                    }
                }
            });
        }

        @Override
        public void paintComponent(final Graphics g) {
            super.paintComponent(g);
            if (pressed) {
                g.drawImage(ICON_DOWN, 2, 2, null);
            } else if (hovered) {
                g.drawImage(ICON_OVER, 2, 2, null);
            } else {
                g.drawImage(ICON, 2, 2, null);
            }
        }
    }
}
