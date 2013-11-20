package me.matt.irc.main.managers;

import me.matt.irc.main.managers.handlers.IRCHandler;

/**
 * This class contains all of the applciations handles.
 * 
 * @author matthewlanglois
 * 
 */
public class HandleManager {

    private IRCHandler irc = null;

    /**
     * Creates an instance of the Handle class.
     */
    public HandleManager() {
        irc = new IRCHandler();
    }

    /**
     * Fetches the IRCHandler.
     * 
     * @return The IRCHandler.
     */
    public IRCHandler getIRCHandler() {
        return irc;
    }

}
