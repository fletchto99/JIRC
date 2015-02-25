package me.matt.irc.main.gui.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTextPane;

import me.matt.irc.main.util.Methods;

/**
 * Creates a text pane that holds text in order at the new line charcter.
 *
 * @author matthewlanglois
 *
 */
public class OrderedTextPane extends JTextPane {

    private static final long serialVersionUID = -5171292233885873672L;

    List<String> text = new ArrayList<String>();

    /**
     * Adds a message to the document.
     *
     * @param message
     *            The message to append.
     */
    public void append(final String message) {
        try {
            text.add(message);
            Collections.sort(text);
            this.refresh();
        } catch (final Exception e) {
            Methods.debug(e);
        }
    }

    /**
     * Removes a string from the document.
     *
     * @param message
     *            The text to remove.
     */
    private void refresh() {
        try {
            this.setText("");
            for (final String s : text) {
                if (!this.getText().equals("")) {
                    this.setText(this.getText() + "\n");
                }
                this.setText(this.getText() + s);
            }
        } catch (final Exception e) {
            Methods.debug(e);
        }
    }

    /**
     * Removes a string from the document.
     *
     * @param message
     *            The text to remove.
     */
    public void remove(final String message) {
        try {
            if (text.contains(message)) {
                text.remove(message);
            }
            Collections.sort(text);
            this.refresh();
        } catch (final Exception e) {
            Methods.debug(e);
        }
    }
}
