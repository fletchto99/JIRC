package me.matt.irc.main.command;

import me.matt.irc.main.wrappers.IRCChannel;

/**
 * This class is the Abstract Game command.
 *
 * @author matthewlanglois
 *
 */
public abstract class IRCCommand {

    /**
     * Checks if the command can be executed by the certian user.
     *
     * @param sender
     *            The command sender.
     * @param msg
     *            The command arguments.
     * @return True if the user is able to execute the command; otherwise false.
     */
    public abstract boolean canExecute(final String msg,
            final IRCChannel channel);

    /**
     * The action to perfrom when executing the command.
     *
     * @param sender
     *            The command sender.
     * @param msg
     *            The command arguments.
     * @return True if the command executed successfully; otherwise false.
     */
    public abstract boolean execute(final String msg, final IRCChannel channel);
}