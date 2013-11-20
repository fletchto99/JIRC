package me.matt.irc.main.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * A string utility.
 * 
 * @author matthewlanglois
 */
public class StringUtil {

    /**
     * Get the bytes of a UTF-8 encoded string.
     * 
     * @param string
     *            The string to read the bytes from
     * @return The bytes of the string.
     */
    public static byte[] getBytesUtf8(final String string) {
        try {
            return string.getBytes("UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Creates a valid UTF-8 String.
     * 
     * @param bytes
     *            The bytes to write to the string.
     * 
     * @return The UTF-8 encoded String.
     */
    public static String newStringUtf8(final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try {
            return new String(bytes, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Throws an exteption to a string.
     * 
     * @param t
     *            The exception thrown.
     * @return The exception stacktrace as a String.
     */
    public static String throwableToString(final Throwable t) {
        if (t != null) {
            final Writer exception = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(exception);
            t.printStackTrace(printWriter);
            return exception.toString();
        }
        return "";
    }

}