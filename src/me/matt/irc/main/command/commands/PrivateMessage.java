package me.matt.irc.main.command.commands;

import java.util.ArrayList;
import java.util.StringTokenizer;

import me.matt.irc.Application;
import me.matt.irc.main.command.IRCCommand;
import me.matt.irc.main.gui.PrivateMessageBox;
import me.matt.irc.main.gui.components.ChannelPanel;
import me.matt.irc.main.util.Methods;
import me.matt.irc.main.wrappers.IRCChannel;

public class PrivateMessage extends IRCCommand {

    @Override
    public boolean canExecute(final String msg, final IRCChannel channel) {
        return Application.getInstance().getHandleManager().getIRCHandler()
                .isConnected(Application.getInstance().getConnectedServer())
                && (msg.contains("/msg") || msg.contains("/pm"));
    }

    @Override
    public boolean execute(final String msg, final IRCChannel channel) {
        final ArrayList<String> messageParts = new ArrayList<String>();
        final StringTokenizer st = new StringTokenizer(msg);
        while (st.hasMoreElements()) {
            messageParts.add(st.nextToken());
        }
        final int size = messageParts.get(1).length()
                + messageParts.get(2).length() + 2;
        if (messageParts.size() > 2) {
            final PrivateMessageBox box = PrivateMessageBox.get(messageParts
                    .get(1));
            if (box != null) {
                box.appendMessage(Application.getInstance()
                        .getConnectedServer().getNick(), msg.substring(size));
            } else {
                PrivateMessageBox.showMessageBox(true, messageParts.get(1),
                        msg.substring(size));
            }
        } else {
            if (channel != null) {
                for (final ChannelPanel p : Application.getInstance()
                        .getChrome().getPanels()) {
                    if (p.getChannel().equalsIgnoreCase(channel.getChannel())) {
                        Methods.sendMessage(p, null, "Invalid command usage!");
                    }
                }
            } else {
                Methods.sendHomeMessage("Invalid command usage!");
            }
        }
        return false;
    }
}
