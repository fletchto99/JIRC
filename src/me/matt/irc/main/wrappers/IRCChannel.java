package me.matt.irc.main.wrappers;

import java.util.ArrayList;
import java.util.List;

/**
 * This class creates an IRC channel to join.
 *
 * @author fletch_to_99 <fletchto99@hotmail.com>
 *
 */
public class IRCChannel {

    private final String channel;
    private final List<String> ops;
    private final List<String> admins;
    private final List<String> hops;
    private final List<String> voices;
    private final String password;

    /**
     * Creates an IRCChannel.
     *
     * @param channel
     *            The channel to join.
     * @param password
     *            The password of the channel.
     */
    public IRCChannel(final String channel, final String password) {
        this.password = password;
        this.channel = channel;
        ops = new ArrayList<String>();
        hops = new ArrayList<String>();
        admins = new ArrayList<String>();
        voices = new ArrayList<String>();
    }

    /**
     * Fetches the OPS in this channel.
     *
     * @return The OPS in this channel.
     */
    public List<String> getAdminList() {
        return admins;
    }

    /**
     * Fetches the IRC channel name.
     *
     * @return The IRC channel's name.
     */
    public String getChannel() {
        return channel;
    }

    /**
     * Fetches the OPS in this channel.
     *
     * @return The OPS in this channel.
     */
    public List<String> getHOpList() {
        return hops;
    }

    /**
     * Fetches the OPS in this channel.
     *
     * @return The OPS in this channel.
     */
    public List<String> getOpList() {
        return ops;
    }

    /**
     * Fetches the channel's password.
     *
     * @return the channel's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Fetches the voices in this channel.
     *
     * @return The voices in this channel.
     */
    public List<String> getVoiceList() {
        return voices;
    }

}
