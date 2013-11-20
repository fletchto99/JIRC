package me.matt.irc.main.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import me.matt.irc.Application;
import me.matt.irc.main.Configuration;
import me.matt.irc.main.gui.components.ChannelPanel;
import me.matt.irc.main.gui.components.ChannelToolBar;
import me.matt.irc.main.gui.components.HomePanel;
import me.matt.irc.main.gui.components.SplashScreen;
import me.matt.irc.main.util.ImageUtil;
import me.matt.irc.main.wrappers.IRCChannel;

/**
 * The main gui.
 * 
 * @author Matt
 * 
 */
public class Chrome extends JFrame implements ActionListener {

    /**
     * Serial version.
     */
    private static final long serialVersionUID = 250622357338288316L;

    /**
     * Initlize the chrome.
     */
    public Chrome() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SplashScreen().display();
            }
        });
        init();
        pack();
        setVisible(true);
    }

    /**
     * Init the chrome.
     */
    private void init() {
        panels = new ArrayList<ChannelPanel>();
        channelBar = new ChannelToolBar(this);
        home = new HomePanel(this);

        setTitle("JIRC - A java irc client");
        setIconImage(ImageUtil.getImage(Configuration.Paths.Resources.ICON));
        setResizable(false);

        channelBar.setBounds(5, 0, 730, 22);
        home.setBounds(5, 25, 730, 545);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                Application.getInstance().disable();
            }
        });

        addComponentListener(new ComponentListener() {

            @Override
            public void componentHidden(final ComponentEvent arg0) {
            }

            @Override
            public void componentMoved(final ComponentEvent arg0) {
                if (LoadScreen.getDebugger().isVisible()) {
                    LoadScreen.getDebugger().setLocation(
                            new Point(Application.getInstance().getChrome()
                                    .getX()
                                    + Application.getInstance().getChrome()
                                            .getWidth(), Application
                                    .getInstance().getChrome().getY()));
                }
            }

            @Override
            public void componentResized(final ComponentEvent arg0) {
            }

            @Override
            public void componentShown(final ComponentEvent arg0) {
            }
        });

        add(channelBar, BorderLayout.NORTH);
        add(home, BorderLayout.CENTER);

        setPreferredSize(new Dimension(725, 570));
        setSize(725, 570);
        setLocationRelativeTo(getOwner());
    }

    private ChannelToolBar channelBar;
    private HomePanel home;
    private List<ChannelPanel> panels;

    /**
     * Handle actions.
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getActionCommand() == "add") {
            addChannel();
        } else if (e.getActionCommand().contains("remove")) {
            final int i = Integer.valueOf(e.getActionCommand().substring(7));
            removeChannel(i);
        }
    }

    /**
     * Set the specific panel visible.
     * 
     * @param panel
     *            The panel to set visible.
     */
    public void setVisible(final int panel) {
        for (final ChannelPanel c : panels) {
            c.setVisible(false);
        }
        home.setVisible(false);
        if (panel == 0) {
            home.setVisible(true);
            return;
        }
        getPanel(panel).setVisible(true);
    }

    /**
     * Fetch the home panel
     * 
     * @return The home panel.
     */
    public HomePanel getHome() {
        return home;
    }

    /**
     * Fetches all of the panels in a list
     * 
     * @return The panels in a list.
     */
    public List<ChannelPanel> getPanels() {
        return panels;
    }

    /**
     * Fetch a panel.
     * 
     * @param idx
     *            The index of the panel.
     * @return The panel.
     */
    public ChannelPanel getPanel(final int idx) {
        for (final ChannelPanel c : panels) {
            if (c.getIndex() == idx) {
                return c;
            }
        }
        return null;
    }

    /**
     * Adds a panel to the list.
     * 
     * @param p
     *            The panel to add.
     */
    public void addPanel(final ChannelPanel p) {
        panels.add(p);
    }

    /**
     * Removes a panel from the list.
     * 
     * @param p
     *            The panel to remove from the list.
     */
    public void removePanel(final ChannelPanel p) {
        if (p != null) {
            panels.remove(p);
            remove(p);
            validate();
            repaint();
        }
    }

    /**
     * Add a channel.
     */
    private void addChannel() {
        if (!Application.getInstance().getHandleManager().getIRCHandler()
                .isConnected(Application.getInstance().getConnectedServer())) {
            JOptionPane
                    .showMessageDialog(
                            this,
                            "You must be fully connected to the server before attempting to connect to a channel!");
            return;
        }
        new ChannelChoiceBox(channelBar);
    }

    /**
     * Remove a channel.
     * 
     * @param tab
     *            The tab of the channel to remove.
     */
    private void removeChannel(final int tab) {
        final int channelIndex = tab + 1;
        final ChannelPanel p = getPanel(channelIndex);
        for (final ChannelPanel cp : getPanels()) {
            if (cp.getIndex() > channelIndex) {
                cp.setIndex(cp.getIndex() - 1);
            }
        }
        for (final IRCChannel c : Application.getInstance().getChannels()) {
            if (c.getChannel().equalsIgnoreCase(p.getChannel())) {
                Application.getInstance().getHandleManager().getIRCHandler()
                        .part(c);
                Application.getInstance().getChannels().remove(c);
                break;
            }
        }
        removePanel(p);
        channelBar.removeTab(tab);
    }

    /**
     * Fetch the toolbar.
     * 
     * @return The channel toolbar.
     */
    public ChannelToolBar getToolbar() {
        return channelBar;
    }
}
