package me.matt.irc.main.util.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import me.matt.irc.main.Configuration;
import me.matt.irc.main.util.Methods;
import me.matt.irc.main.util.StringUtil;

public class IOHelper {

    /**
     * Copys and input stream to a buffer then saves it to an output stream.
     *
     * @param in
     *            The input stream to read from
     * @param out
     *            The outputsream to write to.
     * @throws IOException
     *             Exception in the input stream.
     */
    private final static void copyInputStream(final InputStream in,
            final OutputStream out) throws IOException {
        try {
            final byte[] buff = new byte[4096];
            int n;
            while ((n = in.read(buff)) > 0) {
                out.write(buff, 0, n);
            }
        } finally {
            out.flush();
            out.close();
            in.close();
        }
    }

    /**
     * Extract files from within the running jar.
     *
     * @param fileName
     *            The file to extract.
     * @param dest
     *            The final destination of the file.
     * @return True if successfully extracted; otherwise false.
     */
    public static boolean extractFromJar(final String fileName,
            final String dest) {
        if (Configuration.getRunningJarPath() == null) {// check if running from
                                                        // jar
            return false;
        }
        final File file = new File(dest + fileName);// init the file
        if (file.exists()) {// check if the file exists
            return false;
        }

        if (file.isDirectory()) {// check if the final file is to be a dir
            file.mkdir();
            return false;
        }
        try {
            final JarFile jar = new JarFile(Configuration.getRunningJar());// load
                                                                           // the
                                                                           // currently
                                                                           // running
                                                                           // jar
            final Enumeration<JarEntry> e = jar.entries();// create an enumeration of
            // all of the files packed
            // in the jar
            while (e.hasMoreElements()) {// iterate through each file
                final JarEntry je = e.nextElement();
                if (!je.getName().contains(fileName)) {
                    continue;// skip if the file is incorrect
                }
                final InputStream in = new BufferedInputStream(
                        jar.getInputStream(je));
                final OutputStream out = new BufferedOutputStream(
                        new FileOutputStream(file));
                IOHelper.copyInputStream(in, out);// copy the buffer and save the file
                jar.close();
                return true;
            }
            jar.close();
        } catch (final Exception e) {
            Methods.debug(e);
            return false;
        }
        return false;
    }

    /**
     * Read the bytes from an input stream.
     *
     * @param is
     *            The inputstream to read from.
     * @return The bytes of the open stream.
     */
    public static byte[] read(final InputStream is) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            final byte[] temp = new byte[4096];
            int read;
            while ((read = is.read(temp)) != -1) {
                buffer.write(temp, 0, read);
            }
        } catch (final IOException ignored) {
            try {
                buffer.close();
            } catch (final IOException ignored2) {
            }
            buffer = null;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (final IOException ignored) {
            }
        }
        return buffer == null ? null : buffer.toByteArray();
    }

    /**
     * Read the bytes from a url's stream.
     *
     * @param in
     *            The url to open the stream on.
     * @return The bytes read.
     */
    public static byte[] read(final URL in) {
        try {
            return IOHelper.read(in.openStream());
        } catch (final IOException ignored) {
            return null;
        }
    }

    /**
     * Reads a string's bytes.
     *
     * @param in
     *            The url to read from.
     * @return The text on te page.
     */
    public static String readString(final URL in) {
        return StringUtil.newStringUtf8(IOHelper.read(in));
    }
}
