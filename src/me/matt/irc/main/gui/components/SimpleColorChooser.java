package me.matt.irc.main.gui.components;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import me.matt.irc.Application;
import me.matt.irc.main.Configuration;
import me.matt.irc.main.util.IRCModifier;
import me.matt.irc.main.util.ImageUtil;
import me.matt.irc.main.util.Methods;

/**
 * Creates a color chooser for IRC formats.
 *
 * @author matthewlanglois
 */
public class SimpleColorChooser extends JDialog implements MouseListener {

    private BufferedImage splash;

    private static final long serialVersionUID = 1L;

    private final JTextField parent;

    /**
     * Creates the color chooser.
     *
     * @param location
     *            The location to display the chooser.
     * @param parent
     *            The parent of the cooser.
     */
    public SimpleColorChooser(final Point location, final JTextField parent) {
        super(Application.getInstance().getChrome());
        this.parent = parent;
        try {
            splash = ImageUtil
                    .getBufferedImage(Configuration.Paths.Resources.COLORS);
        } catch (final IOException e) {
            Methods.debug(e);
            this.dispose();
        }
        this.setLocation(location.x, location.y - splash.getHeight());
        this.getRootPane().setOpaque(false);
        this.setUndecorated(true);
        this.setAlwaysOnTop(true);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle("ColorChoice");
        this.setModalityType(ModalityType.TOOLKIT_MODAL);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.setSize(splash.getWidth(), splash.getHeight());
        final JLabel label = new JLabel();
        label.setIcon(new ImageIcon(splash));
        this.add(label);
        this.pack();
        this.addMouseListener(this);
        this.setVisible(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }

    @Override
    public void mousePressed(final MouseEvent e) {
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        final Rectangle c1 = new Rectangle(6, 8, 17, 17);
        final Rectangle c2 = new Rectangle(28, 8, 17, 17);
        final Rectangle c3 = new Rectangle(48, 8, 17, 17);
        final Rectangle c4 = new Rectangle(70, 8, 17, 17);
        final Rectangle c5 = new Rectangle(92, 8, 17, 17);
        final Rectangle c6 = new Rectangle(112, 8, 17, 17);
        final Rectangle c7 = new Rectangle(132, 8, 17, 17);
        final Rectangle c8 = new Rectangle(154, 8, 17, 17);
        final Rectangle c9 = new Rectangle(6, 28, 17, 17);
        final Rectangle c10 = new Rectangle(28, 28, 17, 17);
        final Rectangle c11 = new Rectangle(48, 28, 17, 17);
        final Rectangle c12 = new Rectangle(70, 28, 17, 17);
        final Rectangle c13 = new Rectangle(92, 28, 17, 17);
        final Rectangle c14 = new Rectangle(112, 28, 17, 17);
        final Rectangle c15 = new Rectangle(132, 28, 17, 17);
        final Rectangle c16 = new Rectangle(154, 28, 17, 17);
        if (c1.contains(e.getPoint())) {
            parent.setText(parent.getText() + IRCModifier.WHITE.getModifier());
            this.dispose();
        } else if (c2.contains(e.getPoint())) {
            parent.setText(parent.getText() + IRCModifier.BLACK.getModifier());
            this.dispose();
        } else if (c3.contains(e.getPoint())) {
            parent.setText(parent.getText()
                    + IRCModifier.DARK_BLUE.getModifier());
            this.dispose();
        } else if (c4.contains(e.getPoint())) {
            parent.setText(parent.getText()
                    + IRCModifier.DARK_GREEN.getModifier());
            this.dispose();
        } else if (c5.contains(e.getPoint())) {
            parent.setText(parent.getText() + IRCModifier.RED.getModifier());
            this.dispose();
        } else if (c6.contains(e.getPoint())) {
            parent.setText(parent.getText() + IRCModifier.BROWN.getModifier());
            this.dispose();
        } else if (c7.contains(e.getPoint())) {
            parent.setText(parent.getText() + IRCModifier.PURPLE.getModifier());
            this.dispose();
        } else if (c8.contains(e.getPoint())) {
            parent.setText(parent.getText() + IRCModifier.OLIVE.getModifier());
            this.dispose();
        } else if (c9.contains(e.getPoint())) {
            parent.setText(parent.getText() + IRCModifier.YELLOW.getModifier());
            this.dispose();
        } else if (c10.contains(e.getPoint())) {
            parent.setText(parent.getText() + IRCModifier.GREEN.getModifier());
            this.dispose();
        } else if (c11.contains(e.getPoint())) {
            parent.setText(parent.getText() + IRCModifier.TEAL.getModifier());
            this.dispose();
        } else if (c12.contains(e.getPoint())) {
            parent.setText(parent.getText() + IRCModifier.CYAN.getModifier());
            this.dispose();
        } else if (c13.contains(e.getPoint())) {
            parent.setText(parent.getText() + IRCModifier.BLUE.getModifier());
            this.dispose();
        } else if (c14.contains(e.getPoint())) {
            parent.setText(parent.getText() + IRCModifier.MAGENTA.getModifier());
            this.dispose();
        } else if (c15.contains(e.getPoint())) {
            parent.setText(parent.getText()
                    + IRCModifier.DARK_GRAY.getModifier());
            this.dispose();
        } else if (c16.contains(e.getPoint())) {
            parent.setText(parent.getText()
                    + IRCModifier.LIGHT_GRAY.getModifier());
            this.dispose();
        }
    }
}
