package me.matt.irc.main.managers.handlers;

import java.awt.TrayIcon.MessageType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.logging.Level;

import me.matt.irc.Application;
import me.matt.irc.main.Configuration;
import me.matt.irc.main.gui.PrivateMessageBox;
import me.matt.irc.main.util.Methods;
import me.matt.irc.main.wrappers.IRCChannel;
import me.matt.irc.main.wrappers.IRCServer;

/**
 * This handles all of the IRC related stuff.
 * 
 * @author matthewlangloiss
 * 
 */
public class IRCHandler {

    private BufferedWriter writer = null;
    private Socket connection = null;
    private BufferedReader reader = null;
    private Thread watch = null;
    private Thread print = null;
    private final LinkedList<String> messageQueue = new LinkedList<String>();
    private boolean connected = false;

    /**
     * Connects to an IRC server then a channel.
     * 
     * @param server
     *            The server to connect to.
     * @return True if connected successfully; otherwise false.
     */
    public boolean connect(final IRCServer server) {
        Methods.sendHomeMessage("Attempting to connect");
        if (connection != null) {
            if (connection.isConnected()) {
                // check for a current connection
                Methods.sendHomeMessage("Attempting to disconnect before re-connecting!");
                disconnect(server);
            }
        }
        connection = null;
        connection = new Socket(); // init the socket
        InetAddress addr;
        try {
            addr = InetAddress.getByName(server.getServer());
            final SocketAddress sockaddr = new InetSocketAddress(addr,
                    server.getPort());// connect to the socket on the port
            connection.connect(sockaddr);
        } catch (final UnknownHostException error) {
            Methods.debug(error);
        } catch (final ConnectException e) {
            Methods.sendHomeMessage("The irc server may be down or you network provider blocks connections on this port/ip.");
        } catch (final IOException e) {
            Methods.debug(e);
        }
        String line = null;
        if (connection.isConnected()) {
            try {
                writer = new BufferedWriter(new OutputStreamWriter(
                        connection.getOutputStream()));
                reader = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                Methods.sendHomeMessage("Attempting to connect to chat.");
                writer.write("NICK " + server.getNick() + "\r\n");
                writer.flush();
                writer.write("USER " + server.getNick() + " 8 * :"
                        + Configuration.Paths.Resources.VERSION + "\r\n");
                writer.flush();
                Methods.sendHomeMessage("Processing connection....");
                while ((line = reader.readLine()) != null) {
                    Methods.debug(Level.WARNING, line);
                    if (line.contains("004") || line.contains("376")) {
                        // The line the server sends once connected successfully
                        break;
                    } else if (line.contains("433")) {
                        // send the server our nickname and begin the 30 second
                        // ident timer (if identifing)
                        if (!server.isIdentifing()) {
                            Methods.sendHomeMessage("Your nickname is already in use, please switch it");
                            Methods.sendHomeMessage("using \"nick [NAME]\" and try to connect again.");
                            disconnect(server);
                            return false;
                        } else {
                            // send the ghost/kill command if the nick is in use
                            // and you own the account
                            Methods.log("Sending ghost command....");
                            writer.write("NICKSERV GHOST " + server.getNick()
                                    + " " + server.getNickservPassword()
                                    + "\r\n");
                            writer.flush();
                            continue;
                        }
                        // check to see if the server pings us within the time
                        // of connecting
                    } else if (line.toLowerCase().startsWith("ping ")) {
                        writer.write("PONG " + line.substring(5) + "\r\n");
                        writer.flush();
                        continue;
                    }
                }
                if (server.isIdentifing()) {
                    Methods.sendHomeMessage("Identifying with Nickserv....");
                    writer.write("NICKSERV IDENTIFY "
                            + server.getNickservPassword() + "\r\n");
                    writer.flush();
                }
                // set up the continous threads
                // they will exit when the jvm exits or when they are manually
                // killed within the program
                watch = new Thread(KEEP_ALIVE);
                watch.setDaemon(true);
                watch.setPriority(Thread.MAX_PRIORITY);
                watch.start();
                print = new Thread(DISPATCH);
                print.setDaemon(true);
                print.setPriority(Thread.MAX_PRIORITY);
                print.start();
                connected = true;
            } catch (final Exception e) {
                Methods.sendHomeMessage("Failed to connect to IRC! Try again in about 1 minute!");
                disconnect(server);
            }
        } else {
            Methods.sendHomeMessage("There was an error when trying to connect to the IRC server");
            return false;
        }
        return isConnected(server);
    }

    /**
     * Disconnects a user from the IRC server.
     * 
     * @return True if we disconnect successfully; otherwise false.
     */
    public boolean disconnect(final IRCServer server) {
        if (isConnected(server)) {
            try {
                if (watch != null) {
                    watch.interrupt();
                    watch = null;
                }
                if (print != null) {
                    print.interrupt();
                    print = null;
                }
                if (!connection.isClosed()) { // properly close the connection.
                    Methods.sendHomeMessage("Closing connection.");
                    connection.shutdownInput();
                    connection.shutdownOutput();
                    if (writer != null) {
                        writer.flush();
                        writer.close();
                        writer = null;
                    }
                    connection.close();
                    connection = null;
                }
                messageQueue.clear(); // clear the message queue, no need to
                                      // keep old messages!
                Methods.sendHomeMessage("Successfully disconnected from IRC.");
            } catch (final Exception e) {
                connection = null;
                messageQueue.clear();// still clear the queue
                Methods.sendHomeMessage("Successfully disconnected from IRC.");
            }
        }
        connected = false;
        return !isConnected(server);
    }

    /**
     * Checks if the user is connected to an IRC server.
     * 
     * @return True if conencted to an IRC server; othewise false.
     */
    public boolean isConnected(final IRCServer server) {
        if (connection != null) {
            return connection.isConnected() && connected
                    && !connection.isClosed();
        }
        return false;
    }

    /**
     * Joins an IRC channel on that server.
     * 
     * @param channel
     *            The channel to join.
     */
    public void join(final IRCChannel channel) {
        if (channel.getPassword() != null && channel.getPassword() != "") {
            final String pass = "JOIN " + channel.getChannel() + " "
                    + channel.getPassword();
            messageQueue.add(pass);
        } else {
            final String nopass = "JOIN " + channel.getChannel();
            messageQueue.add(nopass);
        }
    }

    /**
     * Quits a channel in the IRC
     * 
     * @param channel
     *            The channel to leave.
     * @throws IOException
     */
    public void part(final IRCChannel channel) {
        try {
            // part from 'x' channel
            if (isConnected(Application.getInstance().getConnectedServer())) {
                writer.write("PART " + channel.getChannel() + "\r\n");
                writer.flush();
            }
        } catch (final IOException e) {
            Methods.debug(e);
        }
    }

    /**
     * Keep the connection alive and scan for messages. TODO: clean this up
     */
    private final Runnable KEEP_ALIVE = new Runnable() {
        public void run() {
            try {
                if (isConnected(Application.getInstance().getConnectedServer())
                        && reader != null) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Methods.debug(Level.WARNING, line);// debug all lines
                        // check for pings
                        if (line.toLowerCase().startsWith("ping")) {
                            writer.write("PONG " + line.substring(5) + "\r\n");
                            writer.flush();
                            Methods.debug(Level.INFO,
                                    "PONG " + line.substring(5));
                            continue;
                            // check for Client to Client protocall messages
                        } else if (isCTCP(line)) {
                            final String _name = line.substring(1,
                                    line.indexOf("!"));
                            final String ctcpMsg = getCTCPMessage(line)
                                    .toUpperCase();
                            if (ctcpMsg.equals("VERSION")) {
                                writer.write("NOTICE " + _name + " :"
                                        + (char) ctcpControl + "VERSION "
                                        + "JIRC written by Fletch_to_99"
                                        + (char) ctcpControl + "\r\n");
                                writer.flush();
                            } else if (ctcpMsg.equals("TIME")) {
                                final SimpleDateFormat sdf = new SimpleDateFormat(
                                        "dd MMM yyyy hh:mm:ss zzz");
                                writer.write("NOTICE " + _name + " :"
                                        + (char) ctcpControl + " TIME "
                                        + sdf.format(new Date())
                                        + (char) ctcpControl + "\r\n");
                                writer.flush();
                            } else if (ctcpMsg.equals("PING")) {
                                writer.write("NOTICE "
                                        + _name
                                        + " :"
                                        + (char) ctcpControl
                                        + " PING "
                                        + "JIRC by fletch to 99 is to fast to ping."
                                        + (char) ctcpControl + "\r\n");
                                writer.flush();
                            } else if (ctcpMsg.equals("FINGER")) {
                                writer.write("NOTICE "
                                        + _name
                                        + " :"
                                        + (char) ctcpControl
                                        + " FINGER "
                                        + "JIRC written by matthew langlois slaps "
                                        + _name + " across the face."
                                        + (char) ctcpControl + "\r\n");
                                writer.flush();
                            }
                            continue;
                        }
                        String name = null;
                        String msg = null;
                        String subline = null;
                        if (line.indexOf(" :") != -1) {
                            subline = line.substring(0, line.indexOf(" :"));
                        }
                        for (final IRCChannel c : Application.getInstance()
                                .getChannels()) {
                            try {
                                if (subline != null) {
                                    if (subline.toLowerCase().contains(
                                            "PRIVMSG ".toLowerCase()
                                                    + c.getChannel()
                                                            .toLowerCase())) {
                                        name = line.substring(1,
                                                line.indexOf("!"));
                                        msg = line
                                                .substring(line.indexOf(" :") + 2);
                                        if (line.contains("ACTION")) {
                                            msg = "[Action] * "
                                                    + line.substring(
                                                            line.indexOf(" :") + 2)
                                                            .replaceFirst(
                                                                    "ACTION",
                                                                    name);
                                        }
                                    } else if (subline.toLowerCase().contains(
                                            "QUIT".toLowerCase())) {
                                        name = line.substring(1,
                                                line.indexOf("!"));
                                        msg = name + " has left "
                                                + c.getChannel();
                                        Methods.removeUser(c, name);
                                    }
                                } else {
                                    if (line.toLowerCase().contains(
                                            "MODE ".toLowerCase()
                                                    + c.getChannel()
                                                            .toLowerCase())) {
                                        if (line.indexOf("!") != -1) {
                                            if (line.substring(1,
                                                    line.indexOf("!")) != null) {
                                                name = line.substring(1,
                                                        line.indexOf("!"));
                                            }
                                        }
                                        final String mode = line.substring(
                                                line.toLowerCase().indexOf(
                                                        c.getChannel()
                                                                .toLowerCase())
                                                        + 1
                                                        + c.getChannel()
                                                                .length(),
                                                line.toLowerCase().indexOf(
                                                        c.getChannel()
                                                                .toLowerCase())
                                                        + 3
                                                        + c.getChannel()
                                                                .length());
                                        final String _name = line
                                                .substring(line
                                                        .toLowerCase()
                                                        .indexOf(
                                                                c.getChannel()
                                                                        .toLowerCase())
                                                        + c.getChannel()
                                                                .length() + 4);
                                        msg = "[Mode] " + name + " gave mode "
                                                + mode + " to " + _name + ".";
                                        if (mode.contains("+v")) {
                                            c.getVoiceList().add(_name);
                                        } else if (mode.contains("-v")) {
                                            c.getVoiceList().remove(_name);
                                        } else if (mode.contains("+o")) {
                                            c.getOpList().add(_name);
                                        } else if (mode.contains("-o")) {
                                            c.getOpList().remove(_name);
                                        } else if (mode.contains("+h")) {
                                            c.getHOpList().add(_name);
                                        } else if (mode.contains("-h")) {
                                            c.getHOpList().remove(_name);
                                        } else if (mode.contains("+a")) {
                                            c.getAdminList().add(_name);
                                        } else if (mode.contains("-a")) {
                                            c.getAdminList().remove(_name);
                                        } else if (mode.contains("+q")) {
                                            c.getOpList().add(_name);
                                        } else if (mode.contains("-q")) {
                                            c.getOpList().remove(_name);
                                        }
                                    } else if (line.toLowerCase().contains(
                                            "PART ".toLowerCase()
                                                    + c.getChannel()
                                                            .toLowerCase())) {
                                        name = line.substring(1,
                                                line.indexOf("!"));
                                        Methods.removeUser(c, name);
                                        msg = " has left " + c.getChannel();
                                    } else if (line.toLowerCase().contains(
                                            "KICK ".toLowerCase()
                                                    + c.getChannel()
                                                            .toLowerCase())) {
                                        name = line.substring(1,
                                                line.indexOf("!"));
                                        final String _name = line.substring(
                                                line.toLowerCase().indexOf(
                                                        c.getChannel()
                                                                .toLowerCase())
                                                        + c.getChannel()
                                                                .length() + 1,
                                                line.indexOf(" :") - 1);
                                        msg = _name
                                                + " has been kicked from "
                                                + c.getChannel()
                                                + ". ("
                                                + line.substring(line
                                                        .indexOf(" :") + 1)
                                                + ")";
                                    } else if (line.toLowerCase().contains(
                                            "JOIN ".toLowerCase()
                                                    + c.getChannel()
                                                            .toLowerCase()
                                                            .toLowerCase())) {
                                        name = line.substring(1,
                                                line.indexOf("!"));
                                        msg = " has joined " + c.getChannel();
                                        if (name.equalsIgnoreCase(Application
                                                .getInstance()
                                                .getConnectedServer().getNick())) {
                                            continue;
                                        }
                                        Methods.addUser(c, name);
                                    }
                                }

                                if (msg != null && name != null
                                        && c.getChannel() != null) {
                                    Methods.recieveMessage(c, name, msg);
                                    break;
                                }
                            } catch (final Exception e) {
                                Methods.debug(e);
                            }
                        }

                        if (line.toLowerCase().contains(
                                "PRIVMSG ".toLowerCase()
                                        + Application.getInstance()
                                                .getConnectedServer().getNick()
                                                .toLowerCase())) {
                            name = line.substring(1, line.indexOf("!"));
                            msg = line.substring(line.indexOf(" :") + 2);
                            Application.getInstance().getTray()
                                    .notify(name, msg, MessageType.WARNING);
                            final PrivateMessageBox box = PrivateMessageBox
                                    .get(name);
                            if (box != null) {
                                box.appendMessage(name, msg);
                            } else {
                                PrivateMessageBox.showMessageBox(name, msg);
                            }
                        } else if (line.toLowerCase().contains("353")) {
                            if (msg == null) {
                                IRCChannel chan = null;
                                final String split = line.substring(line
                                        .indexOf(" :") + 2);
                                final String channel = line.substring(
                                        line.indexOf("#"), line.indexOf(" :"));
                                final StringTokenizer st = new StringTokenizer(
                                        split);
                                final ArrayList<String> list = new ArrayList<String>();
                                while (st.hasMoreTokens()) {
                                    list.add(st.nextToken());
                                }
                                for (final IRCChannel c : Application
                                        .getInstance().getChannels()) {
                                    if (c.getChannel().toLowerCase()
                                            .contains(channel.toLowerCase())) {
                                        chan = c;
                                        break;
                                    }
                                }
                                if (chan != null) {
                                    for (final String s : list) {
                                        Methods.addUser(chan, s);
                                        if (s.contains("@")) {
                                            chan.getOpList()
                                                    .add(s.substring(s
                                                            .indexOf("@") + 1));
                                            Methods.log(s.substring(s
                                                    .indexOf("@") + 1)
                                                    + " is an OP in "
                                                    + chan.getChannel());
                                        } else if (s.contains("+")) {
                                            chan.getVoiceList()
                                                    .add(s.substring(s
                                                            .indexOf("+") + 1));
                                            Methods.log(s.substring(s
                                                    .indexOf("+") + 1)
                                                    + " is voice in "
                                                    + chan.getChannel());
                                        } else if (s.contains("~")) {
                                            chan.getOpList()
                                                    .add(s.substring(s
                                                            .indexOf("~") + 1));
                                            Methods.log(s.substring(s
                                                    .indexOf("~") + 1)
                                                    + " is an OP in "
                                                    + chan.getChannel());
                                        } else if (s.contains("%")) {
                                            chan.getHOpList()
                                                    .add(s.substring(s
                                                            .indexOf("%") + 1));
                                            Methods.log(s.substring(s
                                                    .indexOf("%") + 1)
                                                    + " is half op in "
                                                    + chan.getChannel());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (final Exception e) {
                Thread.currentThread().interrupt();// umm how did we get this?
            } finally {
                try {
                    reader.close();// close the current connnection
                    Thread.currentThread().interrupt();// stop the thread
                } catch (final IOException e) {
                    Thread.currentThread().interrupt();// how did we get this?
                }
            }
        }

        private final byte ctcpControl = 1;

        /**
         * Check for Client to Client messages within the line,
         * 
         * @param input
         *            The line recieved.
         * @return True if the message is CTCP; otherwise false.
         */
        private boolean isCTCP(final String input) {
            if (input.length() != 0) {
                final String message = input
                        .substring(input.indexOf(":", 1) + 1);
                if (message.length() != 0) {
                    final char[] messageArray = message.toCharArray();
                    return ((byte) messageArray[0]) == 1
                            && ((byte) messageArray[messageArray.length - 1]) == 1;
                }
            }
            return false;
        }

        /**
         * Fetch the CTCP message
         * 
         * @param input
         *            The line to scan.
         * @return The ctcp message stripped.
         */
        private String getCTCPMessage(final String input) {
            if (input.length() != 0) {
                final String message = input
                        .substring(input.indexOf(":", 1) + 1);
                return message.substring(
                        message.indexOf((char) ctcpControl) + 1,
                        message.indexOf((char) ctcpControl, 1));
            }
            return null;
        }
    };

    /**
     * Send all messages on a seprate thread.
     */
    private final Runnable DISPATCH = new Runnable() {
        public void run() {
            while (true) {
                try {
                    int i = 0;
                    if (isConnected(Application.getInstance()
                            .getConnectedServer())) {
                        while (!messageQueue.isEmpty()) {// scan the message
                                                         // queue
                            final String message = messageQueue.remove();// the first
                            // element
                            // in the
                            // queue
                            writer.write(message + "\r\n");
                            writer.flush();
                            i++;
                            if (i >= 3) {// exit when more than three messages
                                         // were sent
                                break;
                            }
                            if (messageQueue.isEmpty()) {
                                break;
                            }
                        }
                        Thread.sleep(1000);
                    }
                } catch (final Exception ex) {
                    Thread.currentThread().interrupt();// how did we get this???
                    break;
                }
            }
        }
    };

    /**
     * Sends a message to the specified channel.
     * 
     * @param Message
     *            The message to send.
     * @param channel
     *            The channel to send the message to.
     */
    public void sendMessage(final String channel, final String Message) {
        final String prefix = "PRIVMSG " + channel + " :";
        final int length = 500 - prefix.length();
        final String parts[] = Message.toString().split(
                "(?<=\\G.{" + length + "})");
        for (final String part : parts) {
            final String msg = prefix + part;
            messageQueue.add(msg);// append the message to the queue
        }
    }

    /**
     * Sends a message to the specified channel.
     * 
     * @param Message
     *            The message to send.
     * @param channel
     *            The channel to send the message to.
     */
    public void sendRaw(final String RawMessage) {
        final String parts[] = RawMessage.toString().split(
                "(?<=\\G.{" + 500 + "})");
        for (final String msg : parts) {
            messageQueue.add(msg);// append to queue
        }
    }

    /**
     * Sends a message to the specified channel.
     * 
     * @param Message
     *            The message to send.
     * @param channel
     *            The channel to send the message to.
     */
    public void sendNotice(final String to, final String message) {
        final String prefix = "NOTICE " + to + " :";
        final int length = 500 - prefix.length();
        final String parts[] = message.toString().split(
                "(?<=\\G.{" + length + "})");
        for (final String part : parts) {
            final String msg = prefix + part;
            messageQueue.add(msg);
        }
    }

    /**
     * Changes the nickname of the IRC bot.
     * 
     * @param server
     *            The server we are connected to
     * @param Nick
     *            The name to change to.
     */
    public void changeNick(final IRCServer server, final String Nick) {
        if (isConnected(server)) {
            try {
                writer.write("NICK " + Nick + "\r\n");
                writer.flush();
            } catch (final IOException e) {
                Methods.debug(e);
            }
        }
    }

    /**
     * Bans a user from the IRC channel if the bot is OP.
     * 
     * @param Nick
     *            The user to kick.
     * @param channel
     *            The channel to ban in.
     */
    public void kick(final IRCServer server, final String Nick,
            final String channel, final String reason) {
        if (isConnected(server)) {
            try {
                writer.write("KICK " + channel + " " + Nick + " " + reason
                        + "\r\n");
                writer.flush();
            } catch (final IOException e) {
                Methods.debug(e);
            }
        }
    }

    /**
     * Bans a user from the IRC channel if the bot is OP.
     * 
     * @param Nick
     *            The user to kick.
     * @param channel
     *            The channel to ban in.
     */
    public void mode(final IRCServer server, final String nick,
            final String channel, final String mode) {
        if (isConnected(server)) {
            try {
                writer.write("MODE " + channel + " " + mode + " " + nick
                        + "\r\n");
                writer.flush();
            } catch (final IOException e) {
                Methods.debug(e);
            }
        }
    }

    /**
     * Bans a user from the IRC channel if the bot is OP.
     * 
     * @param Nick
     *            The user to ban.
     * @param channel
     *            The channel to ban in.
     * @param server
     *            The server to kick the client from
     * @param reason
     *            Their ban reason.
     */
    public void ban(final IRCServer server, final String nick,
            final String channel, final String reason) {
        if (isConnected(server)) {
            try {
                kick(server, nick, channel, reason);
                writer.write("MODE " + channel + " +b " + nick + "\r\n");
                writer.flush();
            } catch (final IOException e) {
                Methods.debug(e);
            }
        }
    }
}