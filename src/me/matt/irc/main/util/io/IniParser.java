package me.matt.irc.main.util.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * This class is used to parse INI configuration files with ease.
 * 
 * @author matthewlanglois
 * 
 */
public class IniParser {

    // specific chars used within the INI file
    private static final char sectionOpen = '[';
    private static final char sectionClose = ']';
    private static final char keyBound = '=';
    private static final char[] comments = { '#', ';' };
    public static final String emptySection = "";

    /**
     * Properly load an INI file.
     * 
     * @param in
     *            The input stream to read from.
     * @return The loaded INI file.
     * @throws IOException
     *             Error reading the stream.
     */
    public static HashMap<String, HashMap<String, String>> deserialise(
            final InputStream in) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                in));
        final HashMap<String, HashMap<String, String>> data = deserialise(reader);
        reader.close();
        return data;
    }

    /**
     * Properly load an INI file.
     * 
     * @param input
     *            The reader to read from.
     * @return The loaded INI file.
     * @throws IOException
     *             Error reading the stream.
     */
    public static HashMap<String, HashMap<String, String>> deserialise(
            final BufferedReader input) throws IOException {
        final HashMap<String, HashMap<String, String>> data = new HashMap<String, HashMap<String, String>>();
        String line, section = emptySection;

        while ((line = input.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {// skip this line
                continue;
            }
            int z;
            final int l = line.length();
            final char t = line.charAt(0);
            if (t == sectionOpen) {
                z = line.indexOf(sectionClose, 1);
                z = z == -1 ? l : z;
                section = z == 1 ? "" : line.substring(1, z).trim();
            } else {
                boolean skip = false;
                for (final char c : comments) { // skip on lines that are
                                                // comments
                    if (t == c) {
                        skip = true;
                        break;
                    }
                }
                if (skip) {
                    continue;
                }
                z = line.indexOf(keyBound);
                z = z == -1 ? l : z;
                String key, value = "";
                key = line.substring(0, z).trim();
                if (++z < l) {
                    value = line.substring(z).trim();
                }
                if (!data.containsKey(section)) {// append the data
                    data.put(section, new HashMap<String, String>());
                }
                data.get(section).put(key, value);
            }
        }
        return data;
    }
}
