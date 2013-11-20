package me.matt.irc;

import java.io.IOException;
import java.net.URLDecoder;

import javax.swing.SwingUtilities;

import me.matt.irc.main.IRC;
import me.matt.irc.main.gui.LoadScreen;

/**
 * The main application. Executed via the Boot class.
 * 
 * @author matthewlanglois
 * 
 */
public class Application {

    private static IRC instance; // The currently running instance.

    /**
     * The main void that will be executed when the program is ran.
     * 
     * @param args
     *            The arguments to pass to the program.
     */
    public static void main(final String[] args) {
        final StringBuilder param = new StringBuilder(64);
        final char s = ' ';
        for (final String arg : args) {
            param.append(s);
            param.append(arg);
        }
        // check for the nosingleinstance argument
        LoadScreen
                .showDialog(param.toString().contains("-nosingleinstance") ? true
                        : false);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // execute the program in its own thread for stability.
                    // Passing the required parameters.
                    instance = new IRC(param.toString());
                } catch (final Exception e) {
                }
            }
        });
    }

    /**
     * Reboot the program once the dependancies are successfully extracted.
     * 
     * @throws IOException
     *             Invalid file location.
     */
    public static void reboot() throws IOException {
        reboot(Boot.class.getProtectionDomain().getCodeSource().getLocation()
                .getPath());
    }

    /**
     * Reboot the program once the dependancies are successfully extracted.
     * 
     * @throws IOException
     *             Invalid file location.
     */
    public static void reboot(String location) throws IOException {
        location = URLDecoder.decode(location, "UTF-8").replaceAll("\\\\", "/");
        final String main = Boot.class.getCanonicalName();
        final char q = '"', s = ' ';
        final StringBuilder param = new StringBuilder(64);

        param.append("javaw");
        param.append(s);

        param.append(s);
        param.append("-classpath");
        param.append(s);
        param.append(q);
        param.append(location);
        param.append(q);
        param.append(s);
        param.append(main);

        Runtime.getRuntime().exec(param.toString());
    }

    /**
     * Fetches the instance of IRC.
     * 
     * @return The current instance of IRC.
     */
    public static IRC getInstance() {
        return instance;
    }

}
