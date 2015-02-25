package me.matt.irc;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import me.matt.irc.main.Configuration;

/**
 * The main Boot class generated with the mainfest.
 *
 * @author matthewlanglois
 *
 */
public class Boot {

    /**
     *
     * @param args
     *            The arguments to run the program with.
     * @throws ClassNotFoundException
     *             Throw this exception if the class was not found.
     * @throws InstantiationException
     *             Could not set up the program, corrupt program.
     * @throws IllegalAccessException
     *             Could not access the method.
     * @throws UnsupportedLookAndFeelException
     *             Not a valid look and feel (will never throw this unless java messes up).
     * @throws IOException
     *             Could not successfully decode the proper location for the Boot class (incorrectly packed jar).
     */
    public static void main(final String[] args) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException,
            UnsupportedLookAndFeelException, IOException {
        // The location of the current running file, diiferent when in a jar
        String location = Boot.class.getProtectionDomain().getCodeSource()
                .getLocation().getPath();
        // Detect the jar location
        location = URLDecoder.decode(location, "UTF-8").replaceAll("\\\\", "/");
        // The main class that boot will load
        final String main = Application.class.getCanonicalName();
        final char q = '"', s = ' ';
        final StringBuilder param = new StringBuilder(64);

        // Check to see if the user is using the supported operating system
        switch (Configuration.getCurrentOperatingSystem()) {
            case WINDOWS: // Correct os
                param.append("javaw"); // Set up to run a new java process
                param.append(s);
                break;
            default:
                UIManager.setLookAndFeel(UIManager
                        .getSystemLookAndFeelClassName()); // set
                                                           // a
                                                           // nice
                                                           // LAF
                JOptionPane.showMessageDialog(null,
                        "Matt's IRC client is incompatible with your OS!"); // Notify
                                                                            // the
                                                                            // user
                                                                            // that
                                                                            // they
                                                                            // are
                                                                            // incompatible
                return;
        }

        // Set up the program with the required dependancies if avalible
        for (final String name : new String[] {
                Configuration.Paths.Resources.TRIDENT,
                Configuration.Paths.Resources.SUBSTANCE }) {
            // find the file
            final File jar = new File(Configuration.Paths
                    .getCachableResources().get(name) + name);
            if (jar.exists() && jar.canRead()) {
                // append the arguments
                location += File.pathSeparatorChar + jar.getAbsolutePath();
            } else {
                break;
            }
        }

        param.append(s);
        param.append("-classpath"); // tell the program external dependancies
                                    // are required
        param.append(s);
        param.append(q);
        param.append(location); // add the dependaincies
        param.append(q);
        param.append(s);
        param.append(main);

        // append any arguments that were executed with this program
        /*
         * Valid arguments contain but are not limited to -nosingleinstance ; Allow the program to run multiple instances
         */
        for (final String arg : args) {
            param.append(s);
            param.append(arg);
        }
        // execute the program
        Runtime.getRuntime().exec(param.toString());
    }
}
