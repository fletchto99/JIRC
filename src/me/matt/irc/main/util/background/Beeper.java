package me.matt.irc.main.util.background;

/**
 * A class which provides a beeper system.
 *
 * @author matthewlanglois
 */
public class Beeper {

    /**
     * A method that causes the beeper to beep once.
     */
    public static void beep() {
        try {
            java.awt.Toolkit.getDefaultToolkit().beep();
        } catch (final Exception e) {
        }
    }
}