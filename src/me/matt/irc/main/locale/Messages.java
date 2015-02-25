package me.matt.irc.main.locale;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

import me.matt.irc.main.Configuration;
import me.matt.irc.main.util.io.IniParser;

/**
 * A class containing all of the messages used within the program.
 *
 * @author matthewlanglois
 *
 */
public class Messages {
    // Reset all of the variables
    /**
     * Set the currently running language on the program.
     *
     * @param lang
     *            The language to set.
     */
    public static void setLanguage(final Language lang) {
        try {
            final URL src = Configuration
                    .getResourceURL(Configuration.Paths.Resources.MESSAGES
                            + lang.getName() + ".txt");
            Messages.map.clear();
            Messages.map = IniParser.deserialise(src.openStream()).get(
                    IniParser.emptySection);
            final HashMap<String, String> mapNative = IniParser.deserialise(
                    src.openStream()).get(IniParser.emptySection);
            for (final Entry<String, String> entry : mapNative.entrySet()) {
                Messages.map.put(entry.getKey(), entry.getValue());
            }
        } catch (final IOException e) {
        }
        Messages.LANGUAGE = Messages.map.get("LANGUAGE");
        Messages.CURR = Messages.map.get("CURR");
        Messages.ENGLISH = Messages.map.get("ENGLISH");
        Messages.FRENCH = Messages.map.get("FRENCH");
        Messages.SPANISH = Messages.map.get("SPANISH");

        Messages.IP = Messages.map.get("IP");
        Messages.NAME = Messages.map.get("NAME");
        Messages.IDENTIFY = Messages.map.get("IDENTIFY");
        Messages.PASSWORD = Messages.map.get("PASSWORD");
        Messages.CANCEL = Messages.map.get("CANCEL");
        Messages.CONNECT = Messages.map.get("CONNECT");
        Messages.CHANNEL = Messages.map.get("CHANNEL");
        Messages.JOINCHANNEL = Messages.map.get("JOINCHAN");
        Messages.PARTCHANNEL = Messages.map.get("PARTCHAN");
        Messages.RELOAD = Messages.map.get("RELOAD");
        Messages.DEBUG = Messages.map.get("DEBUG");
        Messages.ABOUT = Messages.map.get("ABOUT");
        Messages.ABOUT_MESSAGE = Messages.map.get("ABOUTMSG");
        Messages.EXIT = Messages.map.get("EXIT");
    }

    private static HashMap<String, String> map;

    /**
     * Set up and load the language.
     */
    static {
        final String defaultLang = "en";
        final String lang = Locale.getDefault().getLanguage();// Fetch their
                                                              // computers
                                                              // home language
        try {
            URL src = Configuration
                    .getResourceURL(Configuration.Paths.Resources.MESSAGES
                            + defaultLang + ".txt");
            Messages.map = IniParser.deserialise(src.openStream()).get(
                    IniParser.emptySection);
            if (!lang.startsWith(defaultLang)) {
                for (final String avail : new String[] { "fr" }) {
                    if (lang.startsWith(avail)) {
                        src = Configuration
                                .getResourceURL(Configuration.Paths.Resources.MESSAGES
                                        + avail + ".txt");
                        final HashMap<String, String> mapNative = IniParser
                                .deserialise(src.openStream()).get(
                                        IniParser.emptySection);
                        for (final Entry<String, String> entry : mapNative
                                .entrySet()) {
                            Messages.map.put(entry.getKey(), entry.getValue());
                        }
                        break;
                    }
                }
            }
        } catch (final IOException ignored) {
            // ignored
        }
    }

    public static String LANGUAGE = Messages.map.get("LANGUAGE");
    public static String CURR = Messages.map.get("CURR");
    public static String ENGLISH = Messages.map.get("ENGLISH");
    public static String FRENCH = Messages.map.get("FRENCH");
    public static String SPANISH = Messages.map.get("SPANISH");

    public static String IP = Messages.map.get("IP");
    public static String NAME = Messages.map.get("NAME");
    public static String IDENTIFY = Messages.map.get("IDENTIFY");
    public static String PASSWORD = Messages.map.get("PASSWORD");
    public static String CANCEL = Messages.map.get("CANCEL");
    public static String CONNECT = Messages.map.get("CONNECT");
    public static String CHANNEL = Messages.map.get("CHANNEL");
    public static String JOINCHANNEL = Messages.map.get("JOINCHAN");
    public static String PARTCHANNEL = Messages.map.get("PARTCHAN");
    public static String RELOAD = Messages.map.get("RELOAD");
    public static String DEBUG = Messages.map.get("DEBUG");
    public static String ABOUT = Messages.map.get("ABOUT");
    public static String ABOUT_MESSAGE = Messages.map.get("ABOUTMSG");
    public static String EXIT = Messages.map.get("EXIT");

}
