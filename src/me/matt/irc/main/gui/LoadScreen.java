package me.matt.irc.main.gui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import me.matt.irc.Application;
import me.matt.irc.main.Configuration;
import me.matt.irc.main.util.ImageUtil;
import me.matt.irc.main.util.Methods;
import me.matt.irc.main.util.background.SingleInstance;
import me.matt.irc.main.util.background.Updater;
import me.matt.irc.main.util.io.IOHelper;
import me.matt.irc.main.util.log.LabelLogHandler;
import me.matt.irc.main.util.log.TextPaneLogHandler;

/**
 * A class that creates a load screen.
 *
 * @author Matt
 *
 */
public class LoadScreen extends JDialog {

    private final class CheckInstances implements Callable<Boolean> {

        @Override
        public Boolean call() {
            if (!singleInstance) {
                Methods.log("Checking for multiple instances");
                if (this.check()) {
                    LoadScreen.instance.setVisible(false);
                    JOptionPane.showMessageDialog(null, "An instance of "
                            + Configuration.NAME + " is already running!");
                    System.exit(1);// exit when an instance is found
                }
            }
            return true;
        }

        private boolean check() {
            final SingleInstance si = new SingleInstance(Configuration.NAME);
            return si.isAppActive() ? true : false;
        }
    }

    private final class ExtractResources implements Callable<Boolean> {
        @Override
        public Boolean call() {
            Methods.log("Creating directories");
            Configuration.makeDirs();
            final String downloading = "Extracting resources";
            Methods.log(downloading);
            for (final Entry<String, String> item : Configuration.Paths
                    .getCachableResources().entrySet()) {
                Methods.log(downloading + " (" + item.getValue() + ")");
                LoadScreen.firstrun = IOHelper.extractFromJar(item.getKey(),
                        item.getValue());
            }
            if (LoadScreen.firstrun) {
                Methods.debug(Level.WARNING,
                        "Finished installing! Restart the program!");
                while (LoadScreen.firstrun) {
                    try {
                        Thread.sleep(2500);
                        LoadScreen.firstrun = false;
                    } catch (final InterruptedException e1) {
                    }
                }
                try {
                    Application.reboot();
                } catch (final IOException e1) {
                    e1.printStackTrace();
                }
                System.exit(0);
            }
            return true;
        }
    }

    private final class LoadSkin implements Callable<Boolean> {
        @Override
        public Boolean call() {
            if (Configuration.isSkinAvailable()) {
                Methods.log("Setting theme");
                SwingUtilities.invokeLater(() -> {
                    try {
                        UIManager.setLookAndFeel(Configuration.SKIN);
                        SwingUtilities
                                .updateComponentTreeUI(LoadScreen.instance);
                        JFrame.setDefaultLookAndFeelDecorated(true);
                        LoadScreen.debug = new Debugger();
                        Methods.getLogger().addHandler(
                                new TextPaneLogHandler(LoadScreen.debug
                                        .getDebugArea()));
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            return true;
        }
    }

    private final class LoadUpdates implements Callable<Boolean> {
        @Override
        public Boolean call() {
            Methods.log("Checking for updates");
            try {
                if (Updater.needsUpdate()
                        && Configuration.getRunningJarPath() != null) {
                    if (JOptionPane.showConfirmDialog(LoadScreen.this,
                            "Do you wish to update?") == JOptionPane.YES_OPTION) {
                        Methods.debug(Level.WARNING, "Updating!");
                        Updater.update();
                        return false;
                    }
                }
            } catch (final MalformedURLException e) {
            } catch (final IOException e) {
            } catch (final InterruptedException e) {
            }
            return true;
        }
    }

    /**
     * Fetch the debugger.
     *
     * @return The debugger GUI.
     */
    public static Debugger getDebugger() {
        return LoadScreen.debug;
    }

    /**
     * Close and dispose the loadscreen instance.
     */
    public static void quit() {
        if (LoadScreen.instance != null) {
            LoadScreen.instance.dispose();
        }
    }

    /**
     * Show the dialogue, run the loadscreen
     *
     * @param singleinstance
     *            Check for single instances.
     */
    public static void showDialog(final boolean singleinstance) {
        LoadScreen.instance = new LoadScreen(singleinstance);
    }

    private static final long serialVersionUID = 5520543482560560389L;

    private static LoadScreen instance = null;

    private static boolean firstrun = false;

    private static Debugger debug;

    private final boolean singleInstance;

    /**
     * Create and instance of the loadscreen and set up all of the defaults.
     *
     * @param singleinstance
     *            True if you wish to have only one instance of the program running; otherwise false.
     */
    private LoadScreen(final boolean singleinstance) {
        singleInstance = singleinstance;
        this.init();
        final List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
        tasks.add(new CheckInstances());
        tasks.add(new ExtractResources());
        tasks.add(new LoadUpdates());
        tasks.add(new LoadSkin());
        boolean error = false;
        for (final Callable<Boolean> task : tasks) {
            try {
                if (!task.call()) {
                    error = true;
                    break;
                }
            } catch (final Exception e) {
                Methods.debug(e);
                error = true;
                break;
            }
        }
        if (error) {
            System.exit(0);
        }

    }

    private void init() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        try {
            // Look and Feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception ignored) {
        }
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                System.exit(1); // close the program
            }
        });
        this.setTitle(Configuration.NAME);
        this.setIconImage(ImageUtil
                .getImage(Configuration.Paths.Resources.ICON));
        final JPanel panel = new JPanel(new GridLayout(2, 1));
        final int pad = 10;
        panel.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
        final JProgressBar progress = new JProgressBar();
        progress.setPreferredSize(new Dimension(350, progress
                .getPreferredSize().height));
        progress.setIndeterminate(true);
        panel.add(progress);
        final LabelLogHandler handler = new LabelLogHandler();
        Logger.getLogger("").addHandler(handler);
        handler.label.setBorder(BorderFactory.createEmptyBorder(pad, 0, 0, 0));
        final Font font = handler.label.getFont();
        handler.label.setFont(new Font(font.getFamily(), Font.BOLD, font
                .getSize()));
        handler.label.setPreferredSize(new Dimension(progress.getWidth(),
                handler.label.getPreferredSize().height + pad));
        panel.add(handler.label);
        Methods.log("Loading");
        this.add(panel);
        this.pack();
        this.setLocationRelativeTo(this.getOwner());
        this.setResizable(false);
        this.setVisible(true);
        this.setModal(true);
        this.setAlwaysOnTop(true);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
}
