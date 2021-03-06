package me.matt.irc.main.util.background;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import me.matt.irc.Application;
import me.matt.irc.main.Configuration;
import me.matt.irc.main.util.Methods;
import me.matt.irc.main.util.io.HttpClient;
import me.matt.irc.main.util.io.IOHelper;

/**
 * A class pertaining to updating the Client.
 *
 * @author Matt
 *
 */
public class Updater {

    /**
     * Fetches the latest version from the website.
     *
     * @return The latest version.
     * @throws MalformedURLException
     *             Error in retriving the latest version.
     */
    public static int getLatestVersion() throws MalformedURLException {
        if (Updater.latest != -1) {
            return Updater.latest;
        }
        try {
            Updater.latest = Integer.parseInt(IOHelper.readString(
                    new URL(Configuration.Paths.URLs.LATEST)).trim());
        } catch (final NumberFormatException ignored) {
            Updater.latest = Configuration.getVersion();
        } catch (final NullPointerException ignored) {
            Updater.latest = Configuration.getVersion();
        }
        return Updater.latest;
    }

    /**
     * Check if the program needs upating.
     *
     * @return True if there needs to be an update; otherwise false.
     * @throws MalformedURLException
     *             Error checking for the latest version.
     */
    public static boolean needsUpdate() throws MalformedURLException {
        return Updater.getLatestVersion() > Configuration.getVersion();
    }

    /**
     * Updates the client.
     *
     * @throws IOException
     *             Error retriving the latest client.
     * @throws InterruptedException
     *             Error retriving the latest client.
     */
    public static void update() throws IOException, InterruptedException {
        Methods.log("An update is being applied (" + Configuration.getVersion()
                + " -> " + Updater.latest + ").");
        final File newJar = new File(Configuration.getRunningJarPath(),
                Configuration.NAME + "-" + Updater.latest + ".jar");
        HttpClient.download(new URL(Configuration.Paths.URLs.DOWNLOAD), newJar);
        Methods.log("Executing new jar.");
        Application.reboot(newJar.getAbsolutePath());
    }

    private static int latest = -1;

}
