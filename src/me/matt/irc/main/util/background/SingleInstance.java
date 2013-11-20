package me.matt.irc.main.util.background;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

/**
 * Check to see if another instance of the program is running using LOCK files.
 * 
 * @author matthewlanglois
 * 
 */
public class SingleInstance {

    private final String appName;
    private File file;
    private FileChannel channel;
    private FileLock lock;

    /**
     * Create an instance of the class.
     * 
     * @param appName
     *            The application the check for
     */
    public SingleInstance(final String appName) {
        this.appName = appName;
    }

    /**
     * Check if the application is running with an instance of this class.
     * 
     * @return True if the application is running; otherwise false.
     */
    public boolean isAppActive() {
        try {
            file = new File(System.getProperty("user.home"), appName + ".tmp");
            channel = new RandomAccessFile(file, "rw").getChannel();

            try {
                lock = channel.tryLock();// check for the lock
            } catch (final OverlappingFileLockException e) {
                // already locked
                closeLock();
                return true;
            }

            if (lock == null) {
                closeLock();
                return true;
            }

            Runtime.getRuntime().addShutdownHook(new Thread() {
                // destroy the lock when the JVM is closing
                @Override
                public void run() {
                    closeLock();
                    deleteFile();
                }
            });
            return false;
        } catch (final Exception e) {
            closeLock();// error setting up the lock
            return true;
        }
    }

    /**
     * Closes the lock on the file and removes the file.
     */
    private void closeLock() {
        try {
            lock.release();
        } catch (final Exception e) {
        }
        try {
            channel.close();
        } catch (final Exception e) {
        }
    }

    /**
     * Deletes the lock file.
     */
    private void deleteFile() {
        try {
            file.delete();
        } catch (final Exception e) {
        }
    }

}
