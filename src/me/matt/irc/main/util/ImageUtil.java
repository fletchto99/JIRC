package me.matt.irc.main.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import me.matt.irc.main.Configuration;

/**
 * A class pertaining to images.
 * 
 * @author Matt
 * 
 */
public class ImageUtil {

    /**
     * Fetches a buffered image.
     * 
     * @param resource
     *            The resource to load as a buffered image.
     * @return The BufferedImage of the select resource.
     * @throws IOException
     *             Unknown file.
     */
    public static BufferedImage getBufferedImage(final String resource)
            throws IOException {
        final File imageFile = new File(resource);
        return ImageIO.read(imageFile);
    }

    /**
     * Fetch a resourced image.
     * 
     * @param resource
     *            The image to fetch.
     * @return The resource as an Image.
     */
    public static Image getImage(final String resource) {
        try {
            return Toolkit.getDefaultToolkit().getImage(
                    Configuration.getResourceURL(resource));
        } catch (final Exception e) {
        }
        return null;
    }

}
