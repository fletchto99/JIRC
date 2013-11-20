package me.matt.irc.main.util.log;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import me.matt.irc.main.gui.components.ColoredTextPane;
import me.matt.irc.main.util.IRCModifier;

/**
 * This class is used to append logrecords to a TextPane.
 * 
 * @author matthewlanglois
 * 
 */
public class TextPaneLogHandler extends Handler {

    private final ColoredTextPane parent;

    /**
     * Initilize the loghandler.
     * 
     * @param parent
     *            The text pane parent.
     */
    public TextPaneLogHandler(final ColoredTextPane parent) {
        this.parent = parent;
    }

    @Override
    public void close() throws SecurityException {
        // unused
    }

    @Override
    public void flush() {
        // unused
    }

    /**
     * Publish the records to the text parent.
     * 
     * @param record
     *            The record to log.
     */
    @Override
    public void publish(final LogRecord record) {
        final String msg = record.getMessage();
        if (record.getLevel().intValue() < Level.WARNING.intValue()) {
            parent.append(IRCModifier.RED.getModifier() + msg + "\n");
        } else if (record.getLevel().intValue() == Level.WARNING.intValue()) {
            parent.append(IRCModifier.YELLOW.getModifier() + msg + "\n");
        } else {
            parent.append(IRCModifier.BLACK.getModifier() + msg + "\n");
        }
    }

}
