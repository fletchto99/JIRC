package me.matt.irc.main.managers;

import java.util.LinkedList;

import me.matt.irc.Application;
import me.matt.irc.main.command.IRCCommand;
import me.matt.irc.main.command.commands.PrivateMessage;
import me.matt.irc.main.gui.components.ChannelPanel;
import me.matt.irc.main.util.Methods;
import me.matt.irc.main.wrappers.IRCChannel;

/**
 * This class manages all of the plugins commands.
 * 
 * @author matthewlanglois
 * 
 */
public class CommandManager {

    // Initialize an empty list that will contain the commands in order
    private final LinkedList<IRCCommand> commands = new LinkedList<IRCCommand>();

    /**
     * Creates an instance
     */
    public CommandManager() {
        commands.add(new PrivateMessage());
    }

    /**
     * Executes a command ran through IRC.
     * 
     * @param sender
     *            The user that sent the command.
     * @param msg
     *            The arguments in the command.
     * @param channel
     *            The channel the command was executed in, null if home panel.
     * @return True if the command executed successfully; Otherwise false.
     */
    public boolean onCommand(final String msg, final IRCChannel channel) {
        for (final IRCCommand c : commands) {// iterate through all of the commands
            if (c.canExecute(msg, channel)) {// check if we can execute
                try {
                    c.execute(msg, channel);
                } catch (final Exception ex) {
                    Methods.debug(ex);
                }
                return true;
            }
        }
        if (channel == null) {
            if (Application
                    .getInstance()
                    .getHandleManager()
                    .getIRCHandler()
                    .isConnected(Application.getInstance().getConnectedServer())) {
                Methods.sendHomeMessage("Invalid command!");
            } else {
                Methods.sendHomeMessage("You must be connected to run commands!");
            }
        } else {
            for (final ChannelPanel p : Application.getInstance().getChrome()
                    .getPanels()) {
                if (p.getChannel().equalsIgnoreCase(channel.getChannel())) {
                    if (Application
                            .getInstance()
                            .getHandleManager()
                            .getIRCHandler()
                            .isConnected(
                                    Application.getInstance()
                                            .getConnectedServer())) {
                        Methods.sendMessage(p, null, "Invalid command!");
                    } else {
                        Methods.sendMessage(p, null,
                                "You must be connected to run commands!");
                    }
                }
            }
        }
        return false;
    }
}