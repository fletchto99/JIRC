package me.matt.irc.main.util.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

import me.matt.irc.main.Configuration;
import me.matt.irc.main.util.Methods;

/**
 * A class pertaining to HTTP stuff.
 * 
 * @author matthewlanglois
 */
public class HttpClient {
    static String httpUserAgent = null;

    /**
     * Fetch the native useragent.
     * 
     * @return The computers native useragent.
     */
    public static String getHttpUserAgent() {
        if (httpUserAgent == null) {
            httpUserAgent = getDefaultHttpUserAgent();
        }
        return httpUserAgent;
    }

    /**
     * Fetch the system useragent.
     * 
     * @return The systems default useragent.
     */
    private static String getDefaultHttpUserAgent() {
        final boolean x64 = System.getProperty("sun.arch.data.model").equals(
                "64");
        final String os;
        switch (Configuration.getCurrentOperatingSystem()) {
            case MAC:
                os = "Macintosh; Intel Mac OS X 10_6_6";
                break;
            case LINUX:
                os = "X11; Linux " + (x64 ? "x86_64" : "i686");
                break;
            default:
                os = "Windows NT 6.1" + (x64 ? "; WOW64" : "");
                break;
        }
        final StringBuilder buf = new StringBuilder(125);
        buf.append("Mozilla/5.0 (").append(os).append(")");
        buf.append(" AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
        return buf.toString();
    }

    /**
     * Open a HttpURLConnection
     * 
     * @return The open connection.
     * @throws IOException
     *             Invalid url.
     */
    public static HttpURLConnection getHttpConnection(final URL url)
            throws IOException {
        final HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.addRequestProperty("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        con.addRequestProperty("Accept-Charset",
                "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        con.addRequestProperty("Accept-Encoding", "gzip");
        con.addRequestProperty("Accept-Language", "en-us,en;q=0.5");
        con.addRequestProperty("Host", url.getHost());
        con.addRequestProperty("User-Agent", getHttpUserAgent());
        con.setConnectTimeout(10000);
        return con;
    }

    /**
     * Open a connection on the url.
     * 
     * @param url
     *            The url to open the connection on.
     * @return The open conenction.
     * @throws IOException
     *             Invalid url.
     */
    private static HttpURLConnection getConnection(final URL url)
            throws IOException {
        final HttpURLConnection con = getHttpConnection(url);
        con.setUseCaches(true);
        return con;
    }

    /**
     * Fetch the final url to be used.
     * 
     * @param url
     *            The url to finalize.
     * @return The final URL.
     * @throws IOException
     *             Invalid url.
     */
    public static URL getFinalURL(final URL url) throws IOException {
        return getFinalURL(url, true);
    }

    /**
     * Fetch the final url.
     * 
     * @param url
     *            The url to finalize.
     * @param httpHead
     *            To add the request method HEAD.
     * @return The final URC.
     * @throws IOException
     *             Invalid URL.
     */
    private static URL getFinalURL(final URL url, final boolean httpHead)
            throws IOException {
        final HttpURLConnection con = getConnection(url);
        con.setInstanceFollowRedirects(false);
        if (httpHead) {
            con.setRequestMethod("HEAD");
        }
        con.connect();
        switch (con.getResponseCode()) {
            case HttpURLConnection.HTTP_MOVED_PERM:
            case HttpURLConnection.HTTP_MOVED_TEMP:
            case HttpURLConnection.HTTP_SEE_OTHER:
                return getFinalURL(new URL(con.getHeaderField("Location")),
                        true);
            case HttpURLConnection.HTTP_BAD_METHOD:
                return getFinalURL(url, false);
            default:
                return url;
        }
    }

    /**
     * Clones the open connection.
     * 
     * @param con
     *            The open connection.
     * @return The cloned connection.
     * @throws IOException
     *             Invalid connection.
     */
    private static HttpURLConnection cloneConnection(final HttpURLConnection con)
            throws IOException {
        final HttpURLConnection cloned = (HttpURLConnection) con.getURL()
                .openConnection();
        for (final Entry<String, List<String>> prop : con
                .getRequestProperties().entrySet()) {
            final String key = prop.getKey();
            for (final String value : prop.getValue()) {
                cloned.addRequestProperty(key, value);
            }
        }
        return cloned;
    }

    /**
     * Check to see when the URL was last modified.
     * 
     * @param url
     *            The url to check.
     * @param date
     *            The date to check.
     * @return True if modified since the date; otherwise false.
     */
    public static boolean isModifiedSince(URL url, long date) {
        try {
            url = getFinalURL(url);
            date -= TimeZone.getDefault().getOffset(date);
            final HttpURLConnection con = getConnection(url);
            con.setRequestMethod("HEAD");
            con.connect();
            final int resp = con.getResponseCode();
            con.disconnect();
            return resp != HttpURLConnection.HTTP_NOT_MODIFIED;
        } catch (final IOException ignored) {
            return true;
        }
    }

    /**
     * Downloads a file from a specified URL.
     * 
     * @param url
     *            The file to download.
     * @param file
     *            The file to write the bytes to.
     * @return The HTTP url connection.
     * @throws IOException
     *             Invalid file/url.
     */
    public static HttpURLConnection download(final URL url, final File file)
            throws IOException {
        return download(getConnection(getFinalURL(url)), file);
    }

    /**
     * Downloads a file from a specified URL.
     * 
     * @param con
     *            The open connection to the URL.
     * @param file
     *            The file to write the bytes to.
     * @return The HTTP url connection.
     * @throws IOException
     *             Invalid file/url.
     */
    public static HttpURLConnection download(final HttpURLConnection con,
            final File file) throws IOException {
        if (file.exists()) {
            final HttpURLConnection head = cloneConnection(con);
            final int offset = TimeZone.getDefault().getOffset(
                    file.lastModified());
            head.setIfModifiedSince(file.lastModified() - offset);
            head.setRequestMethod("HEAD");
            head.connect();
            if (head.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
                Methods.log("Using " + file.getName() + " from cache");
                con.disconnect();
                head.disconnect();
                return head;
            }
        }

        Methods.log("Downloading " + file.getName());

        final byte[] buffer = downloadBinary(con);

        if (!file.exists()) {
            file.createNewFile();
        }
        if (file.exists() && (!file.canRead() || !file.canWrite())) {
            file.setReadable(true);
            file.setWritable(true);
        }
        if (file.exists() && file.canRead() && file.canWrite()) {
            final FileOutputStream fos = new FileOutputStream(file);
            fos.write(buffer);
            fos.flush();
            fos.close();
        }

        if (con.getLastModified() != 0L) {
            final int offset = TimeZone.getDefault().getOffset(
                    con.getLastModified());
            file.setLastModified(con.getLastModified() + offset);
        }

        con.disconnect();
        return con;
    }

    /**
     * Download the bytes of the file.
     * 
     * @param con
     *            The conenction to read the bytes from.
     * @return The bytes for the file as an array.
     * @throws IOException
     *             Invalid file conenction.
     */
    private static byte[] downloadBinary(final URLConnection con)
            throws IOException {
        final DataInputStream di = new DataInputStream(con.getInputStream());
        byte[] buffer;
        final int len = con.getContentLength();
        if (len == -1) {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            int b;
            while ((b = di.read()) != -1) {
                out.write(b);
            }
            buffer = out.toByteArray();
        } else {
            buffer = new byte[con.getContentLength()];
            di.readFully(buffer);
        }
        di.close();
        if (buffer != null) {
            buffer = ungzip(buffer);
        }
        return buffer;
    }

    /**
     * Ungzip the data.
     * 
     * @param data
     *            The data to open.
     * @return The buffered data to save.
     */
    private static byte[] ungzip(final byte[] data) {
        if (data.length < 2) {
            return data;
        }

        final int header = (data[0] | data[1] << 8) ^ 0xffff0000;
        if (header != GZIPInputStream.GZIP_MAGIC) {
            return data;
        }

        try {
            final ByteArrayInputStream b = new ByteArrayInputStream(data);
            final GZIPInputStream gzin = new GZIPInputStream(b);
            final ByteArrayOutputStream out = new ByteArrayOutputStream(
                    data.length);
            for (int c = gzin.read(); c != -1; c = gzin.read()) {
                out.write(c);
            }
            return out.toByteArray();
        } catch (final IOException e) {
            e.printStackTrace();
            return data;
        }
    }

    /**
     * Opens a url with the users prefered browser.
     * 
     * @param url
     *            The url to open.
     */
    public static void openURL(final String url) {
        final Configuration.OperatingSystem os = Configuration
                .getCurrentOperatingSystem();
        try {
            if (os == Configuration.OperatingSystem.MAC) {
                final Class<?> fileMgr = Class
                        .forName("com.apple.eio.FileManager");
                final Method openURL = fileMgr.getDeclaredMethod("openURL",
                        new Class[] { String.class });
                openURL.invoke(null, url);
            } else if (os == Configuration.OperatingSystem.WINDOWS) {
                Runtime.getRuntime().exec(
                        "rundll32 url.dll,FileProtocolHandler " + url);
            } else {
                final String[] browsers = { "firefox", "opera", "konqueror",
                        "epiphany", "mozilla", "netscape", "google-chrome",
                        "chromium-browser" };
                String browser = null;
                for (int count = 0; count < browsers.length && browser == null; count++) {
                    if (Runtime.getRuntime()
                            .exec(new String[] { "which", browsers[count] })
                            .waitFor() == 0) {
                        browser = browsers[count];
                    }
                }
                if (browser == null) {
                    throw new Exception("Could not find web browser");
                } else {
                    Runtime.getRuntime().exec(new String[] { browser, url });
                }
            }
        } catch (final Exception e) {
            Methods.log("Unable to open " + url);
        }
    }
}