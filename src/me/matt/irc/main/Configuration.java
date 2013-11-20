package me.matt.irc.main;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.matt.irc.main.util.io.IOHelper;

/**
 * This class holds all of the configuration data used within the program.
 * 
 * @author matthewlanglois
 * 
 */
public class Configuration {

    /**
     * An enum containing all of the avalible operating systems.
     * 
     * @author matthewlanglois
     * 
     */
    public enum OperatingSystem {
        MAC, WINDOWS, LINUX, UNKNOWN
    }

    /**
     * Fetches the current OS.
     * 
     * @return The current operating system.
     */
    public static OperatingSystem getCurrentOperatingSystem() {
        return Configuration.CURRENT_OS;
    }

    private static final OperatingSystem CURRENT_OS;

    public static final String NAME = "JIRC";

    // The location of the LookAndFeel Class that is used.
    public static String SKIN = "org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel";

    private static boolean RUNNING_FROM_JAR = false;

    /**
     * Set up all of our static variables (e.x. the current OS)
     */
    static {
        // The jar location.
        final URL resource = Configuration.class.getClassLoader().getResource(
                Paths.Resources.VERSION);
        if (resource != null) { // The location will be null when running from a
                                // jarfile.
            RUNNING_FROM_JAR = true;
        }
        final String os = System.getProperty("os.name"); // fetch the OS.
        if (os.contains("Mac")) {
            CURRENT_OS = OperatingSystem.MAC;
        } else if (os.contains("Windows")) {
            CURRENT_OS = OperatingSystem.WINDOWS;
        } else if (os.contains("Linux")) {
            CURRENT_OS = OperatingSystem.LINUX;
        } else {
            CURRENT_OS = OperatingSystem.UNKNOWN;
        }
    }

    /**
     * Make required directories.
     */
    public static void makeDirs() {
        final ArrayList<String> dirs = new ArrayList<String>();
        dirs.add(Paths.getHomeDirectory());
        dirs.add(Paths.getCacheDirectory());
        for (final String name : dirs) {
            final File dir = new File(name);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }

    /**
     * A static class containing all paths used within the program.
     * 
     * @author matthewlanglois
     * 
     */
    public static class Paths {

        /**
         * The path to all of the resources within the program.
         * 
         * @author matthewlanglois
         * 
         */
        public static class Resources {

            public static final String ROOT = "resources";

            public static final String ROOT_IMG = ROOT + "/images";

            public static final String ICON = ROOT_IMG + "/icon.png";

            public static final String ICON_ADD = ROOT_IMG + "/add.png";

            public static final String ICON_CHANNEL = ROOT_IMG + "/channel.png";

            public static final String ICON_CLOSE = ROOT_IMG + "/close.png";

            public static final String ICON_WRENCH = ROOT_IMG + "/wrench.png";

            public static final String ICON_HOME = ROOT_IMG + "/home.png";

            public static final String ICON_BUG = ROOT_IMG + "/debug.png";

            public static final String ICON_LANGUAGE = ROOT_IMG
                    + "/keyboard.png";

            public static final String ICON_REFRESH = ROOT_IMG
                    + "/arrow_refresh.png";

            public static final String ICON_GITHUB = ROOT_IMG + "/github.png";
            public static final String ICON_INFO = ROOT_IMG
                    + "/information.png";

            public static final String ICON_EXIT = ROOT_IMG
                    + "/application_delete.png";

            public static final String VERSION = ROOT + "/version.txt";

            public static final String MESSAGES = ROOT + "/messages/";

            public static final String JARS = ROOT + "/lib/";

            public static final String TRIDENT = "trident.jar";

            public static final String SUBSTANCE = "substance.jar";

            public static final String LOGO = getCacheDirectory()
                    + File.separator + "logo.png";

            public static final String COLORS = getCacheDirectory()
                    + File.separator + "colors.png";

        }

        /**
         * A class containing all of the URL's used within the program.
         * 
         * @author matthewlanglois
         * 
         */
        public static class URLs {

            public static final String BASE = "http://www.fletchto99.x10.mx/";

            public static final String GITHUB = BASE + "github.php";

            public static final String WEBSITE = BASE + "jirc/index.html";

            public static final String DOWNLOAD = BASE + "jirc/JIRC.jar";

            public static final String LATEST = BASE + "jirc/version.txt";
        }

        /**
         * Windows AppData directory.
         * 
         * @return The app data directory.
         */
        public static String getAppDir() {
            return System.getenv("APPDATA") + File.separator;
        }

        /**
         * The home directory for all of the programs files.
         * 
         * @return The home directory.
         */
        public static String getHomeDirectory() {
            return getAppDir() + "JIRC";
        }

        /**
         * A directory containing all files that will be cached by the program.
         * 
         * @return The cache directory for the program.
         */
        public static String getCacheDirectory() {
            return Paths.getHomeDirectory() + File.separator + "Cache"
                    + File.separator;
        }

        // A map containing all of the resources used <name, path of storage>
        private static Map<String, String> cachableResources;

        /**
         * Fetch all of the publicly available cached resources.
         * 
         * @return The cached resources.
         */
        public static Map<String, String> getCachableResources() {
            if (cachableResources == null) {
                cachableResources = new HashMap<String, String>(4);
                cachableResources.put("trident.jar", getCacheDirectory());
                cachableResources.put("substance.jar", getCacheDirectory());
                cachableResources.put("logo.png", getCacheDirectory());
                cachableResources.put("colors.png", getCacheDirectory());
            }
            return cachableResources;
        }
    }

    /**
     * Fetch a path as a URL
     * 
     * @param path
     *            The path of the initial location.
     * @return The path as a valid URL.
     * @throws MalformedURLException
     *             Invalid location.
     */
    public static URL getResourceURL(final String path)
            throws MalformedURLException {
        return RUNNING_FROM_JAR ? Configuration.class.getResource("/" + path)
                : new File(path).toURI().toURL();
    }

    /**
     * Check if we are able to skin the program.
     * 
     * @return True if we can skin; otherwise false.
     */
    public static boolean isSkinAvailable() {
        Class<?> substance = null;
        try {
            substance = Class.forName(SKIN); // find the class
        } catch (final ClassNotFoundException ignored) {
        }
        return substance != null;
    }

    /**
     * Grab the running jar path.
     * 
     * @return The path of the running jar.
     */
    public static String getRunningJarPath() {
        if (!RUNNING_FROM_JAR) {
            return null; // null if not running from jar
        }
        String path = new File(Configuration.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath()).getParent();
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (final UnsupportedEncodingException ignored) {
        }
        return path;
    }

    /**
     * Grab the running jar path.
     * 
     * @return The path of the running jar.
     */
    public static String getRunningJar() {
        if (!RUNNING_FROM_JAR) {
            return null; // null if not running from jar
        }
        String path = new File(Configuration.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath()).getAbsolutePath();
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (final UnsupportedEncodingException ignored) {
        }
        return path;
    }

    /**
     * Fetch the currently running version.
     * 
     * @return The current version.
     */
    public static int getVersion() {
        final URL src;
        try {
            src = getResourceURL(Paths.Resources.VERSION);
        } catch (final MalformedURLException ignored) {
            return -1;
        }
        return Integer.parseInt(IOHelper.readString(src).trim());
    }

}
