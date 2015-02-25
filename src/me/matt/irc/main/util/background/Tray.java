package me.matt.irc.main.util.background;

import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import me.matt.irc.Application;
import me.matt.irc.main.Configuration;
import me.matt.irc.main.gui.LoadScreen;
import me.matt.irc.main.locale.Language;
import me.matt.irc.main.locale.Messages;
import me.matt.irc.main.util.ImageUtil;

/**
 * A class which creates the tray icon in the system tray.
 *
 * @author matthewlanglois
 */
public class Tray {

    /**
     * The icon to be displayed in the system tray.
     */
    private final static TrayIcon sysTray = new TrayIcon(ImageUtil.getImage(
            Configuration.Paths.Resources.ICON).getScaledInstance(
            SystemTray.getSystemTray().getTrayIconSize().width,
            SystemTray.getSystemTray().getTrayIconSize().height, 0), "IRC");

    /**
     * The system tray.
     */
    private final SystemTray tray = SystemTray.getSystemTray();

    /**
     * The popup menu
     */
    private PopupMenu menu;

    /**
     * Sets up the system tray.
     */
    public Tray() {
        this.add(Tray.sysTray);
    }

    /**
     * Adds an icon to the system tray.
     *
     * @param icon
     *            The icon to add.
     */
    public void add(final TrayIcon icon) {
        if (SystemTray.isSupported()) {
            try {
                tray.add(icon);
            } catch (final Exception e) {
            }
        }
    }

    /**
     * Displays a notification for the user to see.
     *
     * @param caption
     *            The title of the message.
     * @param text
     *            The text to display.
     * @param type
     *            The message type.
     */
    public void notify(final String caption, final String text,
            final MessageType type) {
        Tray.sysTray.displayMessage(caption, text, type);
    }

    /**
     * Removes the icon from the system tray.
     */
    public void remove() {
        if (SystemTray.isSupported()) {
            try {
                tray.remove(Tray.sysTray);
            } catch (final Exception e) {
            }
        }
    }

    /**
     * Used to update the menu when an option changes.
     */
    public void update() {
        menu = new PopupMenu();

        final MenuItem add = new MenuItem(Messages.JOINCHANNEL);

        final MenuItem part = new MenuItem(Messages.PARTCHANNEL);

        final MenuItem reload = new MenuItem(Messages.RELOAD);

        final Menu language = new Menu(Messages.LANGUAGE);

        final MenuItem eng = new MenuItem(Messages.ENGLISH);

        final MenuItem fr = new MenuItem(Messages.FRENCH);

        final MenuItem sp = new MenuItem(Messages.SPANISH);

        final MenuItem debug = new MenuItem(Messages.DEBUG);

        final MenuItem about = new MenuItem(Messages.ABOUT);

        final MenuItem exit = new MenuItem(Messages.EXIT);
        exit.addActionListener(e -> Application.getInstance().disable());
        about.addActionListener(e -> JOptionPane.showMessageDialog(null,
                Messages.ABOUT_MESSAGE));
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (Application.getInstance().getChrome().getToolbar()
                        .getButtonCount() < 10) {
                    Application.getInstance().getChrome().getToolbar().parent
                            .actionPerformed(new ActionEvent(this, e.getID(),
                                    "add"));
                } else {
                    JOptionPane.showMessageDialog(null,
                            "You have reached the maximum amount of channels!");
                }
            }
        });
        part.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Application.getInstance().getChrome().getToolbar().parent
                        .actionPerformed(new ActionEvent(this,
                                ActionEvent.ACTION_PERFORMED, "remove"
                                        + "."
                                        + Application.getInstance().getChrome()
                                                .getToolbar().getCurrentTab()));
            }
        });
        debug.addActionListener(e -> {
            if (!LoadScreen.getDebugger().isVisible()) {
                LoadScreen.getDebugger().setVisible(true);
            } else {
                LoadScreen.getDebugger().setVisible(true);
            }
        });

        eng.addActionListener(arg0 -> {
            Messages.setLanguage(Language.ENGLISH);
            Application.getInstance().getTray().update();
        });

        fr.addActionListener(arg0 -> {
            Messages.setLanguage(Language.FRENCH);
            Application.getInstance().getTray().update();
        });

        sp.addActionListener(arg0 -> {
            Messages.setLanguage(Language.SPANISH);
            Application.getInstance().getTray().update();
        });

        if (eng.getLabel().equalsIgnoreCase(Messages.CURR)) {
            eng.setEnabled(false);
        }

        if (fr.getLabel().equalsIgnoreCase(Messages.CURR)) {
            fr.setEnabled(false);
        }

        if (sp.getLabel().equalsIgnoreCase(Messages.CURR)) {
            sp.setEnabled(false);
        }

        if (Application.getInstance().getChrome().getToolbar().getCurrentTab() == -1) {
            part.setEnabled(false);
        }
        if (!Application.getInstance().getChrome().getToolbar().isAddVisible()) {
            add.setEnabled(false);
        }

        language.add(eng);
        language.add(fr);
        language.add(sp);

        menu.add(add);
        menu.add(part);
        menu.add(reload);
        menu.addSeparator();
        menu.add(language);
        menu.addSeparator();
        menu.add(debug);
        menu.add(about);
        menu.addSeparator();
        menu.add(exit);
        Tray.sysTray.setPopupMenu(menu);
    }
}