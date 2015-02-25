package me.matt.irc.main.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.matt.irc.Application;
import me.matt.irc.main.IRC;
import me.matt.irc.main.gui.components.ChannelPanel;
import me.matt.irc.main.gui.components.ColoredTextPane;
import me.matt.irc.main.wrappers.IRCChannel;
import me.matt.irc.main.wrappers.IRCServer;

/**
 * A class containing many methods used within the IRC client.
 *
 * @author Matt
 *
 */
public class Methods {

    /**
     * Sends a message to the MonsterIRC channel.
     *
     * @param channel
     *            The channel to send it to.
     * @param message
     *            The message to send.
     */
    public static void addUser(final IRCChannel channel, final String user) {
        for (final ChannelPanel p : Application.getInstance().getChrome()
                .getPanels()) {
            if (channel.getChannel().equalsIgnoreCase(p.getChannel())) {
                if (Application.getInstance().getChrome() != null) {
                    p.addUser(user);
                }
            }
        }
    }

    /**
     * Sends a message to the MonsterIRC channel.
     *
     * @param channel
     *            The channel to send it to.
     * @param message
     *            The message to send.
     * @param reason
     *            The ban reason.
     */
    public static void ban(final IRCServer server, final String channel,
            final String nick, final String reason) {
        Application.getInstance().getHandleManager().getIRCHandler()
                .ban(server, nick, channel, reason);
    }

    /**
     * Logs debugging messages to the console.
     *
     * @param error
     *            The message to print.
     */
    public static void debug(final Exception error) {
        Methods.logger.log(Level.SEVERE,
                "[MonsterIRC - Critical error detected!]");
        error.printStackTrace();
    }

    /**
     * Logs debugging messages to the console.
     *
     * @param error
     *            The message to print.
     */
    public static void debug(final Level level, final String error) {
        Methods.logger.log(level, "[IRC - Debug] " + error);
    }

    /**
     * Fetches the logger.
     *
     * @return The logger.
     */
    public static Logger getLogger() {
        return Methods.logger;
    }

    /**
     * Fetches the list of Admins in the current IRC channel.
     *
     * @return True if the user is admin; otherwise false.
     */
    public static boolean isAdmin(final IRCChannel channel, final String sender) {
        return channel.getOpList().contains(sender);
    }

    /**
     * Fetches the list of Operaters in the current IRC channel.
     *
     * @return The list of Operators.
     */
    public static boolean isHalfOP(final IRCChannel channel, final String sender) {
        return channel.getOpList().contains(sender);
    }

    /**
     * Fetches the list of Operaters in the current IRC channel.
     *
     * @return The list of Operators.
     */
    public static boolean isOp(final IRCChannel channel, final String sender) {
        return channel.getOpList().contains(sender);
    }

    /**
     * Fetches the list of Voices in the current IRC channel.
     *
     * @return The list of Voices.
     */
    public static boolean isVoice(final IRCChannel channel, final String sender) {
        return channel.getVoiceList().contains(sender);
    }

    /**
     * Checks if the user is higher than voice.
     *
     * @param channel
     *            The channel to check in.
     * @param sender
     *            The user to check.
     * @return True if higher than voice; otherwise false.
     */
    public static boolean isVoicePlus(final IRCChannel channel,
            final String sender) {
        if (Methods.isVoice(channel, sender)) {
            return true;
        }
        if (Methods.isHalfOP(channel, sender)) {
            return true;
        }
        if (Methods.isAdmin(channel, sender)) {
            return true;
        }
        if (Methods.isOp(channel, sender)) {
            return true;
        }
        return false;
    }

    /**
     * Sends a message to the MonsterIRC channel.
     *
     * @param channel
     *            The channel to send it to.
     * @param message
     *            The message to send.
     */
    public static void kick(final IRCServer server, final String channel,
            final String nick, final String reason) {
        Application.getInstance().getHandleManager().getIRCHandler()
                .kick(server, nick, channel, reason);
    }

    /**
     * Logs a message to the console.
     *
     * @param msg
     *            The message to print.
     */
    public static void log(final String msg) {
        Methods.logger.log(Level.INFO, msg);
    }

    /**
     * Sends a message to the MonsterIRC channel.
     *
     * @param channel
     *            The channel to send it to.
     * @param message
     *            The message to send.
     */
    public static void mode(final IRCServer server, final String channel,
            final String nick, final String mode) {
        Application.getInstance().getHandleManager().getIRCHandler()
                .mode(server, nick, channel, mode);
    }

    /**
     * Sends a message to the MonsterIRC channel.
     *
     * @param channel
     *            The channel to send it to.
     * @param message
     *            The message to send.
     */
    public static void recieveMessage(final IRCChannel channel,
            final String name, final String message) {
        for (final ChannelPanel p : Application.getInstance().getChrome()
                .getPanels()) {
            if (channel.getChannel().equalsIgnoreCase(p.getChannel())) {
                final ColoredTextPane area = p.getMessageArea();
                if (Application.getInstance().getChrome() != null) {
                    if (area.getText().equalsIgnoreCase("")) {
                        area.append(name + ": " + message);
                    } else {
                        area.append("\n" + name + ": " + message);
                    }
                }
                return;
            }
        }
    }

    /**
     * Sends a message to the MonsterIRC channel.
     *
     * @param channel
     *            The channel to send it to.
     * @param message
     *            The message to send.
     */
    public static void removeUser(final IRCChannel channel, final String user) {
        for (final ChannelPanel p : Application.getInstance().getChrome()
                .getPanels()) {
            if (channel.getChannel().equalsIgnoreCase(p.getChannel())) {
                p.removeUser(user);
            }
        }
    }

    /**
     * Sends a message to the home pannel in the GUI.
     *
     * @param message
     *            The message to send.
     */
    public static void sendHomeMessage(final String message) {
        if (message.startsWith("/")) {
            IRC.getCommandManager().onCommand(message, null);
            return;
        }
        final ColoredTextPane area = Application.getInstance().getChrome()
                .getHome().getMessageArea();
        if (area.getText().equalsIgnoreCase("")) {
            area.append(message);
        } else {
            area.append("\n" + message);
        }
        Methods.log(message);
    }

    /**
     * Sends a message to the MonsterIRC channel.
     *
     * @param channel
     *            The channel to send it to.
     * @param message
     *            The message to send.
     */
    public static void sendMessage(final ChannelPanel panel,
            final IRCChannel channel, final String message) {
        if (message.startsWith("/")) {
            IRC.onCommand(message, channel);
            return;
        }
        final ColoredTextPane area = panel.getMessageArea();
        if (channel == null) {
            if (Application.getInstance().getChrome() != null) {
                if (area.getText().equalsIgnoreCase("")) {
                    area.append(message);
                } else {
                    area.append("\n" + message);
                }
            }
            return;
        }
        Application.getInstance().getHandleManager().getIRCHandler()
                .sendMessage(channel.getChannel(), message);
        if (Application.getInstance().getChrome() != null) {
            if (area.getText().equalsIgnoreCase("")) {
                area.append(Application.getInstance().getConnectedServer()
                        .getNick()
                        + ": " + message);
            } else {
                area.append("\n"
                        + Application.getInstance().getConnectedServer()
                                .getNick() + ": " + message);
            }
        }
    }

    /**
     * Sends a message to the MonsterIRC channel.
     *
     * @param to
     *            The person to send the message to.
     * @param message
     *            The message to send.
     */
    public static void sendMessage(final String to, final String message) {
        Application.getInstance().getHandleManager().getIRCHandler()
                .sendMessage(to, message);
    }

    /**
     * Sends a message to a user on the MonsterIRC server.
     *
     * @param to
     *            The user to send it to.
     * @param message
     *            The message to send.
     */
    public static void sendNotice(final String to, final String message) {
        Application.getInstance().getHandleManager().getIRCHandler()
                .sendNotice(to, message);
    }

    /**
     * Sends a raw message to the IRC server.
     *
     * @param RawMessage
     *            The message to send.
     */
    public static void sendRawMessage(final String RawMessage) {
        Application.getInstance().getHandleManager().getIRCHandler()
                .sendRaw(RawMessage);
    }

    private final static Logger logger = Logger.getLogger(IRC.class
            .getSimpleName());
}