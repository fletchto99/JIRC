package me.matt.irc.main.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import me.matt.irc.Application;
import me.matt.irc.main.Configuration;
import me.matt.irc.main.gui.components.ChannelToolBar;
import me.matt.irc.main.locale.Language;
import me.matt.irc.main.locale.Messages;

/**
 * This class contains all of the options to change in the program.
 *
 * @author matthewlanglois
 *
 */
public class Settings extends JPopupMenu {

    private static final long serialVersionUID = 1555391213470686686L;

    JMenuItem add = new JMenuItem(Messages.JOINCHANNEL);

    JMenuItem part = new JMenuItem(Messages.PARTCHANNEL);

    JMenuItem reload = new JMenuItem(Messages.RELOAD);

    JMenu language = new JMenu(Messages.LANGUAGE);

    JMenuItem eng = new JMenuItem(Messages.ENGLISH);

    JMenuItem fr = new JMenuItem(Messages.FRENCH);

    JMenuItem sp = new JMenuItem(Messages.SPANISH);

    JMenuItem debug = new JMenuItem(Messages.DEBUG);

    JMenuItem about = new JMenuItem(Messages.ABOUT);

    JMenuItem exit = new JMenuItem(Messages.EXIT);

    /**
     * Create an instance of the settings class.
     *
     * @param parent
     *            The parent to add the settings to.
     */
    public Settings(final ChannelToolBar parent) {
        this.init(parent);
    }

    /**
     * Create the settings button.
     *
     * @param parent
     *            The parent to add the settings to.
     */
    private void init(final ChannelToolBar parent) {
        exit.addActionListener(e -> Application.getInstance().disable());
        about.addActionListener(e -> JOptionPane.showMessageDialog(null,
                Messages.ABOUT_MESSAGE + "\n                      v: "
                        + Configuration.getVersion()));
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (parent.getButtonCount() < 10) {
                    parent.parent.actionPerformed(new ActionEvent(this, e
                            .getID(), "add"));
                } else {
                    JOptionPane.showMessageDialog(null,
                            "You have reached the maximum amount of channels!");
                }
            }
        });
        part.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                parent.parent.actionPerformed(new ActionEvent(this,
                        ActionEvent.ACTION_PERFORMED, "remove" + "."
                                + parent.getCurrentTab()));
            }
        });
        debug.addActionListener(e -> {
            if (!LoadScreen.getDebugger().isVisible()) {
                LoadScreen.getDebugger().setVisible(true);
            } else {
                LoadScreen.getDebugger().setVisible(true);
            }
        });
        try {
            add.setIcon(new ImageIcon(Configuration
                    .getResourceURL(Configuration.Paths.Resources.ICON_ADD)));
            part.setIcon(new ImageIcon(Configuration
                    .getResourceURL(Configuration.Paths.Resources.ICON_CLOSE)));
            exit.setIcon(new ImageIcon(Configuration
                    .getResourceURL(Configuration.Paths.Resources.ICON_EXIT)));
            about.setIcon(new ImageIcon(Configuration
                    .getResourceURL(Configuration.Paths.Resources.ICON_INFO)));
            reload.setIcon(new ImageIcon(Configuration
                    .getResourceURL(Configuration.Paths.Resources.ICON_REFRESH)));
            debug.setIcon(new ImageIcon(Configuration
                    .getResourceURL(Configuration.Paths.Resources.ICON_BUG)));
            language.setIcon(new ImageIcon(
                    Configuration
                            .getResourceURL(Configuration.Paths.Resources.ICON_LANGUAGE)));
        } catch (final MalformedURLException e) {
        }

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

        if (eng.getText().equalsIgnoreCase(Messages.CURR)) {
            eng.setEnabled(false);
        }

        if (fr.getText().equalsIgnoreCase(Messages.CURR)) {
            fr.setEnabled(false);
        }

        if (sp.getText().equalsIgnoreCase(Messages.CURR)) {
            sp.setEnabled(false);
        }

        if (parent.getCurrentTab() == -1) {
            part.setEnabled(false);
        }
        if (!parent.isAddVisible()) {
            add.setEnabled(false);
        }

        language.add(eng);
        language.add(fr);
        language.add(sp);

        // add all of the menu items
        this.add(add);
        this.add(part);
        this.add(reload);
        this.addSeparator();
        this.add(language);
        this.addSeparator();
        this.add(debug);
        this.add(about);
        this.addSeparator();
        this.add(exit);
        this.pack();
    }
}
