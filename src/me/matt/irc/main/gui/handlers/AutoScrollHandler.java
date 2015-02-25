package me.matt.irc.main.gui.handlers;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Creates a handler that automatically scrolls down.
 *
 * @author matthewlanglois
 *
 */
public class AutoScrollHandler implements DocumentListener, WindowListener {

    private final JScrollBar parent;

    public AutoScrollHandler(final JScrollBar parent) {
        this.parent = parent;
    }

    @Override
    public void changedUpdate(final DocumentEvent e) {
        this.maybeScrollToBottom();
    }

    @Override
    public void insertUpdate(final DocumentEvent e) {
        this.maybeScrollToBottom();
    }

    /**
     * Check if the scrollbar is at the bottom.
     *
     * @return True if fully extended; otherwise false.
     */
    private boolean isScrollBarFullyExtended() {
        final int minimumValue = parent.getValue() + parent.getVisibleAmount();
        final int maximumValue = parent.getMaximum();
        return maximumValue == minimumValue;
    }

    /**
     * Check if we should scroll.
     */
    private void maybeScrollToBottom() {
        final boolean scrollBarAtBottom = this.isScrollBarFullyExtended();
        final boolean scrollLock = Toolkit.getDefaultToolkit()
                .getLockingKeyState(KeyEvent.VK_SCROLL_LOCK);
        if (scrollBarAtBottom && !scrollLock) {
            SwingUtilities.invokeLater(() -> AutoScrollHandler.this
                    .scrollToBottom());
        }
    }

    @Override
    public void removeUpdate(final DocumentEvent e) {
        this.maybeScrollToBottom();
    }

    /**
     * Scroll to the bottom of the component.
     */
    private void scrollToBottom() {
        parent.setValue(parent.getMaximum());
    }

    @Override
    public void windowActivated(final WindowEvent arg0) {
    }

    @Override
    public void windowClosed(final WindowEvent arg0) {
    }

    @Override
    public void windowClosing(final WindowEvent arg0) {
    }

    @Override
    public void windowDeactivated(final WindowEvent arg0) {
    }

    @Override
    public void windowDeiconified(final WindowEvent arg0) {
    }

    @Override
    public void windowIconified(final WindowEvent arg0) {
    }

    @Override
    public void windowOpened(final WindowEvent arg0) {
    }
}
