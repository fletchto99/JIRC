package me.matt.irc.main.gui.components;

import javax.swing.JScrollPane;
import javax.swing.text.DefaultCaret;

import me.matt.irc.main.gui.handlers.AutoScrollHandler;

/**
 * Create a component that will automatically scroll down.
 * 
 * @author matthewlanglois
 * 
 */
public class AutoScrollPane extends JScrollPane {

    /**
     * serial
     */
    private static final long serialVersionUID = -5474890539587769921L;

    /**
     * Create a textpane that autoscrolls
     * 
     * @param parent
     *            The pane to scroll.
     */
    public AutoScrollPane(final ColoredTextPane parent) {
        ((DefaultCaret) parent.getCaret())
                .setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        parent.getDocument().addDocumentListener(
                new AutoScrollHandler(super.verticalScrollBar));
    }
}
