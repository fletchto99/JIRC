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
            map = IniParser.deserialise(src.openStream()).get(
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
                            map.put(entry.getKey(), entry.getValue());
                        }
                        break;
                    }
                }
            }
        } catch (final IOException ignored) {
            // ignored
        }
    }

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
            map.clear();
            map = IniParser.deserialise(src.openStream()).get(
                    IniParser.emptySection);
            final HashMap<String, String> mapNative = IniParser.deserialise(
                    src.openStream()).get(IniParser.emptySection);
            for (final Entry<String, String> entry : mapNative.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
        } catch (final IOException e) {
        }
        LANGUAGE = map.get("LANGUAGE");
        CURR = map.get("CURR");
        ENGLISH = map.get("ENGLISH");
        FRENCH = map.get("FRENCH");
        SPANISH = map.get("SPANISH");

        IP = map.get("IP");
        NAME = map.get("NAME");
        IDENTIFY = map.get("IDENTIFY");
        PASSWORD = map.get("PASSWORD");
        CANCEL = map.get("CANCEL");
        CONNECT = map.get("CONNECT");
        CHANNEL = map.get("CHANNEL");
        JOINCHANNEL = map.get("JOINCHAN");
        PARTCHANNEL = map.get("PARTCHAN");
        RELOAD = map.get("RELOAD");
        DEBUG = map.get("DEBUG");
        ABOUT = map.get("ABOUT");
        ABOUT_MESSAGE = map.get("ABOUTMSG");
        EXIT = map.get("EXIT");
    }

    public static String LANGUAGE = map.get("LANGUAGE");
    public static String CURR = map.get("CURR");
    public static String ENGLISH = map.get("ENGLISH");
    public static String FRENCH = map.get("FRENCH");
    public static String SPANISH = map.get("SPANISH");

    public static String IP = map.get("IP");
    public static String NAME = map.get("NAME");
    public static String IDENTIFY = map.get("IDENTIFY");
    public static String PASSWORD = map.get("PASSWORD");
    public static String CANCEL = map.get("CANCEL");
    public static String CONNECT = map.get("CONNECT");
    public static String CHANNEL = map.get("CHANNEL");
    public static String JOINCHANNEL = map.get("JOINCHAN");
    public static String PARTCHANNEL = map.get("PARTCHAN");
    public static String RELOAD = map.get("RELOAD");
    public static String DEBUG = map.get("DEBUG");
    public static String ABOUT = map.get("ABOUT");
    public static String ABOUT_MESSAGE = map.get("ABOUTMSG");
    public static String EXIT = map.get("EXIT");

}
