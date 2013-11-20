package me.matt.irc.main;

import java.awt.TrayIcon.MessageType;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;

import me.matt.irc.main.gui.Chrome;
import me.matt.irc.main.gui.LoadScreen;
import me.matt.irc.main.gui.ServerBox;
import me.matt.irc.main.managers.CommandManager;
import me.matt.irc.main.managers.HandleManager;
import me.matt.irc.main.util.Methods;
import me.matt.irc.main.util.background.Tray;
import me.matt.irc.main.wrappers.IRCChannel;
import me.matt.irc.main.wrappers.IRCServer;

/**
 * This class represents the main plugin. All actions related to the IRC client are forwarded by this class.
 * 
 * @author matthewlanglois
 * 
 */
public class IRC {

    private HandleManager handles;
    private static CommandManager command;
    private IRCServer server;
    private ServerBox main;
    private Chrome chat;
    private final Set<IRCChannel> channels = new HashSet<IRCChannel>();
    private Tray tray;

    /**
     * The main program.
     * 
     * @param parameters
     *            Any prarmeters to execute the program with.
     */
    public IRC(final String parameters) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Exit the loadscreen
                    LoadScreen.quit();
                    // set up the server
                    main = new ServerBox();
                    main.setVisible(true);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Connect on a seprate thread to prevent the GUI from locking up while
    // connecting to the server.
    private final Runnable connect = new Runnable() {
        public void run() {
            // Check if we successfully connected.
            final boolean connect = handles.getIRCHandler().connect(
                    getConnectedServer());
            if (!connect) {
                // Tell the user why we were unable to connect
                Methods.sendHomeMessage("Connection timed out!");
                getTray().notify(server.getServer(), "Error connecting!",
                        MessageType.ERROR);
            } else {
                // Tell the user we are connected and good to go
                getTray().notify(server.getServer(), "Successfully connected!",
                        MessageType.INFO);
                Methods.sendHomeMessage("Successfully connected!");
            }
        }
    };

    /**
     * Set up the program once they enter their server details.
     * 
     * @param name
     *            The users nickname.
     * @param pass
     *            The server password.
     * @param ip
     *            The servers ip address or mask.
     * @param ident
     *            Will the user identify with the server.
     */
    public void setup(final String name, final String pass, final String ip,
            final boolean ident) {
        // set up the variables
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                chat = new Chrome();
                tray = new Tray();
                tray.update();
                command = new CommandManager();
                handles = new HandleManager();
                server = new IRCServer(ip, 6667, name, pass, ident);
                // set up a new thread to connect on
                final Thread t = new Thread(connect);
                t.setDaemon(true);
                t.setPriority(Thread.MAX_PRIORITY);
                t.start();
            }
        });
    }

    /**
     * Disables the program saftly.
     */
    public void disable() {
        tray.remove(); // remove the tray icon.
        if (handles != null) { // check for errors setting up to prevent memory
                               // leaks and unclosed programs.
            if (getHandleManager().getIRCHandler() != null) {
                if (getHandleManager().getIRCHandler().isConnected(
                        getConnectedServer())) {
                    for (final IRCChannel c : channels) {
                        getHandleManager().getIRCHandler().part(c);
                    }
                    getHandleManager().getIRCHandler().disconnect(
                            getConnectedServer());
                }
            }
        }
        System.exit(0);
    }

    /**
     * Handle commands executed within the client.
     * 
     * @param sender
     *            The person sending the command.
     * @param args
     *            The command arguments.
     * @param channel
     *            The channel the command was executed in.
     * @return True if the command executed successfully; otherwise false.
     */
    public static boolean onCommand(final String args, final IRCChannel channel) {
        return getCommandManager().onCommand(args, channel);
    }

    /**
     * The manager that holds the handlers.
     * 
     * @return The handlers.
     */
    public HandleManager getHandleManager() {
        return handles;
    }

    /**
     * The CommandManager that Assigns all the commands.
     * 
     * @return The plugins command manager.
     */
    public static CommandManager getCommandManager() {
        return command;
    }

    /**
     * Fetches the irc channels.
     * 
     * @return All of the IRCChannels
     */
    public Set<IRCChannel> getChannels() {
        return channels;
    }

    /**
     * Fetch the server to connect to.
     * 
     * @return The connected server.
     */
    public IRCServer getConnectedServer() {
        return server;
    }

    /**
     * Fetch the tray manager.
     * 
     * @return The instance of Tray.
     */
    public Tray getTray() {
        return tray;
    }

    /**
     * Fetches the main Chrome. (GUI)
     * 
     * @return The main GUI.
     */
    public Chrome getChrome() {
        return chat;
    }

}
