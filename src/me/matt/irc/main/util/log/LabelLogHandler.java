package me.matt.irc.main.util.log;

import java.awt.Color;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.JLabel;

/**
 * This class is used to log to a JLabel.
 *
 * @author matthewlanglois
 *
 */
public class LabelLogHandler extends Handler {

    public final JLabel label = new JLabel(); // allow access to the label in
                                              // order to set its properties.
    private final Color defaultColor;

    /**
     * Set up the handler.
     */
    public LabelLogHandler() {
        super();
        defaultColor = label.getForeground();
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
     * Publish the log record
     *
     * @param record
     *            The record to log.
     */
    @Override
    public void publish(final LogRecord record) {
        String msg = record.getMessage();
        if (record.getLevel().intValue() > Level.WARNING.intValue()) {
            label.setForeground(new Color(0xcc0000)); // red warning
        } else {
            label.setForeground(defaultColor);
            msg += " ..."; // apend ... to make the record look nice <- users
                           // like to think the program is working correctly
        }
        label.setText(msg);
    }

}
