package me.matt.irc.main.wrappers;

/**
 * This class creates an IRCServer to connect to.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class IRCServer {

    private final String server;
    private final int port;
    private final String nick;
    private final String nserv_password;
    private final boolean identify;

    /**
     * The IRCServer wrapper.
     * 
     * @param server
     *            The servers ip.
     * @param port
     *            The port.
     * @param nick
     *            The nickname.
     * @param nserv_password
     *            The nickserv password.
     * @param identify
     *            The option to identify.
     */
    public IRCServer(final String server, final int port, final String nick,
            final String nserv_password, final boolean identify) {
        this.server = server;
        this.port = port;
        this.nick = nick;
        this.nserv_password = nserv_password;
        this.identify = identify;
    }

    /**
     * Fetches the servers address.
     * 
     * @return The servers address.
     */
    public String getServer() {
        return server;
    }

    /**
     * Fetches the servers port.
     * 
     * @return The servers port.
     */
    public int getPort() {
        return port;
    }

    /**
     * Fetches the users nick name.
     * 
     * @return The users nick name.
     */
    public String getNick() {
        return nick;
    }

    /**
     * Fetches the users password.
     * 
     * @return The users password.
     */
    public String getNickservPassword() {
        return nserv_password;
    }

    /**
     * Checks if the user is identifying with nickserv.
     * 
     * @return True if the user is identifying with nickserv; otherwise false.
     */
    public boolean isIdentifing() {
        return identify;
    }
}
