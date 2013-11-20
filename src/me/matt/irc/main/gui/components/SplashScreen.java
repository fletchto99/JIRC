package me.matt.irc.main.gui.components;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

import me.matt.irc.main.Configuration;
import me.matt.irc.main.util.ImageUtil;
import me.matt.irc.main.util.Methods;
import me.matt.irc.main.util.io.HttpClient;

/**
 * A class that creates a splash sceen.
 * 
 * @author matthewlanglois
 */
public class SplashScreen extends JDialog implements MouseListener {

    private BufferedImage splash;

    private static final long serialVersionUID = 1L;

    private final int display = 2000;

    /**
     * Create the splash screen.
     */
    public SplashScreen() {
        super();
        try {
            splash = ImageUtil
                    .getBufferedImage(Configuration.Paths.Resources.LOGO);
        } catch (final IOException e) {
            Methods.debug(e);
            dispose();
        }
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int x = (screen.width / 2) - (splash.getWidth() / 2);
        final int y = (screen.height / 2) - (splash.getHeight() / 2);

        this.getRootPane().setOpaque(false);
        this.setLocation(x, y);
        this.setUndecorated(true);
        this.setAlwaysOnTop(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle("Logo");
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.setSize(splash.getWidth(), splash.getHeight());
        final JLabel label = new JLabel();
        label.setIcon(new ImageIcon(splash));
        add(label);
        pack();
        addMouseListener(this);
        setVisible(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
    }

    /**
     * Display the splash screen on a timer.
     */
    public void display() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                dispose();
            }
        }, display);
    }

    public void mouseClicked(final MouseEvent e) {
    }

    public void mousePressed(final MouseEvent e) {
    }

    public void mouseReleased(final MouseEvent e) {
        HttpClient.openURL(Configuration.Paths.URLs.WEBSITE);
    }

    public void mouseEntered(final MouseEvent e) {
    }

    public void mouseExited(final MouseEvent e) {
    }
}
