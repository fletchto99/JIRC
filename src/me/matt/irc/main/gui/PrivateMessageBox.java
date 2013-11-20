package me.matt.irc.main.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import me.matt.irc.Application;
import me.matt.irc.main.Configuration;
import me.matt.irc.main.IRC;
import me.matt.irc.main.gui.components.AutoScrollPane;
import me.matt.irc.main.gui.components.ColoredTextPane;
import me.matt.irc.main.gui.components.SimpleColorChooser;
import me.matt.irc.main.util.IRCModifier;
import me.matt.irc.main.util.ImageUtil;
import me.matt.irc.main.util.Methods;

/**
 * Creates a box where the user can see private messages.
 * 
 * @author matthewlanglois
 * 
 */
public class PrivateMessageBox extends JFrame {

    /**
	 * 
	 */
    private static final long serialVersionUID = 7400526973228702056L;

    /**
     * Creates an instance of the private message box.
     * 
     * @param sender
     *            The sender that sent the message to us.
     */
    private PrivateMessageBox(final String sender) {
        this.sender = sender;
        init();
    }

    private JTextField messageField;
    private AutoScrollPane chatScrollPane;
    private ColoredTextPane chatTextArea;
    private final String sender;
    private static HashSet<PrivateMessageBox> boxes = new HashSet<PrivateMessageBox>();

    /**
     * init the GUI.
     */
    public void init() {
        setTitle(sender);
        messageField = new JTextField();
        chatTextArea = new ColoredTextPane();
        chatScrollPane = new AutoScrollPane(chatTextArea);
        setResizable(false);
        chatTextArea.setText("");
        messageField.setText("");
        setIconImage(ImageUtil.getImage(Configuration.Paths.Resources.ICON));
        {
            chatScrollPane
                    .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            chatScrollPane.setViewportView(chatTextArea);
        }
        chatTextArea.setEditable(false);

        messageField.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(final KeyEvent arg0) {
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.isControlDown()) {
                    if (e.getKeyCode() == 75) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                JDialog.setDefaultLookAndFeelDecorated(false);
                                new SimpleColorChooser(new Point(messageField
                                        .getLocationOnScreen().x, messageField
                                        .getLocationOnScreen().y), messageField);
                            }
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
                    Methods.sendMessage(sender, messageField.getText());
                    appendMessage(Application.getInstance()
                            .getConnectedServer().getNick(),
                            messageField.getText());
                    messageField.setText("");
                }
            }

            @Override
            public void keyTyped(final KeyEvent arg0) {
            }
        });
        add(messageField, BorderLayout.SOUTH);
        add(chatScrollPane, BorderLayout.CENTER);
        setPreferredSize(new Dimension(260, 300));
        setSize(260, 300);
        setLocationRelativeTo(getOwner());
        pack();
        setVisible(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                dispose();
            }
        });
    }

    /**
     * Appends a message to the box.
     * 
     * @param name
     *            The users name to append the message.
     * @param message
     *            The message to append.
     */
    public void appendMessage(final String name, final String message) {
        if (message.startsWith("/")) {
            IRC.onCommand(message, null);
            return;
        }
        if (chatTextArea.getText().equalsIgnoreCase("")) {
            chatTextArea.append(name + ": " + message);
        } else {
            chatTextArea.append("\n" + name + ": " + message);
        }
    }

    /**
     * Creates an instance of the message box for the user if one doens't exist otherwise flashes the box.
     * 
     * @param user
     *            The user to show the message box for.
     */
    public static void showMessageBox(final String user, final String msg) {
        if (get(user) != null) {
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                final PrivateMessageBox box = new PrivateMessageBox(user);
                boxes.add(box);
                box.appendMessage(user, msg);
            }

        });
    }

    /**
     * Creates an instance of the message box for the user if one doens't exist otherwise flashes the box.
     * 
     * @param user
     *            The user to show the message box for.
     * @param mesg
     *            The initial message.
     */
    public static void showMessageBox(final boolean you, final String user,
            final String msg) {
        if (get(user) != null) {
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                final PrivateMessageBox box = new PrivateMessageBox(user);
                boxes.add(box);
                box.appendMessage(Application.getInstance()
                        .getConnectedServer().getNick(), msg);
            }

        });
    }

    /**
     * Fetches a private message box by the selected users name.
     * 
     * @param name
     *            The name of the user.
     * @return The box registered to the specified user.
     */
    public static PrivateMessageBox get(final String name) {
        for (final PrivateMessageBox b : boxes) {
            if (b.getUser().equalsIgnoreCase(name)) {
                return b;
            }
        }
        return null;
    }

    /**
     * Fetch the user of the current PM Box.
     * 
     * @return The user that messaged us.
     */
    private String getUser() {
        return sender;
    }

    @Override
    /**
     * Properly dispose the box
     * @see JFrame.dispose();
     */
    public void dispose() {
        if (get(this.getUser()) != null) {
            boxes.remove(get(this.getUser()));// remove the instance
        }
        super.dispose();// properly dispose of the instance
    }
}
