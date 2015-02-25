package me.matt.irc.main.gui.components;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import me.matt.irc.Application;
import me.matt.irc.main.util.IRCModifier;
import me.matt.irc.main.util.Methods;
import me.matt.irc.main.util.background.Beeper;
import me.matt.irc.main.wrappers.IRCChannel;

/**
 * This contains the information of each channel joined and disaplays it on the screen.
 *
 * @author matthewlanglois
 *
 */
public class ChannelPanel extends JComponent {

    private static final long serialVersionUID = 6793961848332824725L;

    private JTextField messageField;
    private AutoScrollPane chatScrollPane;
    private ColoredTextPane chatTextArea;
    private JScrollPane userScrollPane;
    private OrderedTextPane userTextArea;
    private final String channel;
    private int idx;

    /**
     * Create a new channel panel.
     *
     * @param idx
     *            The index of the panel.
     * @param channel
     *            The IRCChannel to join.
     */
    public ChannelPanel(final int idx, final String channel) {
        this.channel = channel;
        this.idx = idx;
        this.init();
    }

    /**
     * Adds a user to the users text pane.
     *
     * @param user
     *            The user to add.
     */
    public void addUser(final String user) {
        userTextArea.append(user);
    }

    /**
     * Fetch the IRCChannel.
     *
     * @return The IRCChannel.
     */
    public String getChannel() {
        return channel;
    }

    /**
     * Fetch the index.
     *
     * @return The index of the panel.
     */
    public int getIndex() {
        return idx;
    }

    /**
     * Fetch the message area.
     *
     * @return The message area.
     */
    public ColoredTextPane getMessageArea() {
        return chatTextArea;
    }

    /**
     * Initlize the panel.
     */
    public void init() {
        messageField = new JTextField();
        chatTextArea = new ColoredTextPane();
        userScrollPane = new JScrollPane();
        userTextArea = new OrderedTextPane();
        chatScrollPane = new AutoScrollPane(chatTextArea);

        userTextArea.setText("");
        chatTextArea.setText("");
        messageField.setText("");

        messageField.setBounds(5, 480, 585, 30);

        {
            chatScrollPane
                    .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            chatScrollPane.setViewportView(chatTextArea);
        }

        this.add(chatScrollPane);
        chatScrollPane.setBounds(5, 0, 585, 475);
        chatTextArea.setEditable(false);

        {
            userScrollPane.setViewportView(userTextArea);
        }
        userScrollPane.setBounds(595, 0, 120, 510);
        userTextArea.setEditable(false);

        messageField.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(final KeyEvent e) {
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.isControlDown()) {
                    if (e.getKeyCode() == KeyEvent.VK_K) {
                        SwingUtilities.invokeLater(() -> {
                            JDialog.setDefaultLookAndFeelDecorated(false);
                            new SimpleColorChooser(new Point(messageField
                                    .getLocationOnScreen().x, messageField
                                    .getLocationOnScreen().y), messageField);
                        });
                    } else if (e.getKeyCode() == 66) {
                        messageField.setText(messageField.getText()
                                + IRCModifier.BOLD.getModifier());
                    } else if (e.getKeyCode() == 85) {
                        messageField.setText(messageField.getText()
                                + IRCModifier.UNDERLINE.getModifier());
                    } else if (e.getKeyCode() == 73) {
                        messageField.setText(messageField.getText()
                                + IRCModifier.ITALIC.getModifier());
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (messageField.getText().equalsIgnoreCase("")) {
                        return;
                    }
                    IRCChannel chan = null;
                    for (final IRCChannel c : Application.getInstance()
                            .getChannels()) {
                        if (c.getChannel().equalsIgnoreCase(channel)) {
                            chan = c;
                        }
                    }
                    if (chan != null) {
                        Methods.sendMessage(ChannelPanel.this, chan,
                                messageField.getText());
                        messageField.setText("");
                    }
                }
            }

            @Override
            public void keyTyped(final KeyEvent e) {
            }
        });
        messageField.setDocument(new PlainDocument() {
            private static final long serialVersionUID = 1L;

            @Override
            public void insertString(final int offs, final String str,
                    final AttributeSet a) throws BadLocationException {
                if ((this.getLength() + str.length()) <= 512) {
                    super.insertString(offs, str, a);
                } else {
                    Beeper.beep();
                }
            }
        });
        this.add(messageField, BorderLayout.SOUTH);
        this.add(chatScrollPane, BorderLayout.NORTH);
        this.add(userScrollPane, BorderLayout.WEST);
    }

    /**
     * Removes a user from the ordered text pane.
     *
     * @param user
     *            The user to remove.
     */
    public void removeUser(final String user) {
        userTextArea.remove(user);
    }

    /**
     * Set the index of the panel.
     *
     * @param idx
     *            The index to set the panel to.
     */
    public void setIndex(final int idx) {
        this.idx = idx;
    }

}
