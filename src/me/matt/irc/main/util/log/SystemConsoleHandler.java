package me.matt.irc.main.util.log;

import java.util.logging.ConsoleHandler;

/**
 * Log to system.
 *
 * @author matthewlanglois
 *
 */
public class SystemConsoleHandler extends ConsoleHandler {

    /**
     * Set the handler to log to the system pritnstream.
     */
    public SystemConsoleHandler() {
        super();
        this.setOutputStream(System.out);
    }
}
