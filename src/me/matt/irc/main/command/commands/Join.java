package me.matt.irc.main.command.commands;

import me.matt.irc.main.command.IRCCommand;
import me.matt.irc.main.wrappers.IRCChannel;

public class Join extends IRCCommand {

    @Override
    public boolean canExecute(final String msg, final IRCChannel channel) {
        return false;
    }

    @Override
    public boolean execute(final String msg, final IRCChannel channel) {
        return false;
    }
}
