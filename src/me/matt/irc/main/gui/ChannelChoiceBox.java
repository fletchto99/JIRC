package me.matt.irc.main.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import me.matt.irc.Application;
import me.matt.irc.main.Configuration;
import me.matt.irc.main.gui.components.ChannelPanel;
import me.matt.irc.main.gui.components.ChannelToolBar;
import me.matt.irc.main.locale.Messages;
import me.matt.irc.main.util.ImageUtil;
import me.matt.irc.main.wrappers.IRCChannel;

/**
 * Creates a box where the user can enter their channel to join.
 * 
 * @author matthewlanglois
 * 
 */
public class ChannelChoiceBox extends JFrame {

    private static final long serialVersionUID = -9216906137953223811L;
    private final ChannelToolBar parent;

    /**
     * Create an instance of the box.
     * 
     * @param parent
     *            The parent tool bar to connect he choice box to.
     */
    public ChannelChoiceBox(final ChannelToolBar parent) {
        this.parent = parent;
        init();
    }

    /**
     * What to do when the action is performed.
     * 
     * @param e
     *            The event.
     */
    private void channelButtonActionPerformed(final ActionEvent e) {
        for (final ChannelPanel p : Application.getInstance().getChrome()
                .getPanels()) {
            if (p.getChannel().equalsIgnoreCase(channelName.getText())) {
                JOptionPane.showMessageDialog(null,
                        "You are already connected to that channel!");
                dispose();
                return;
            }
        }
        final IRCChannel c = new IRCChannel(channelName.getText(), passwordBox
                .getPassword().toString());
        Application.getInstance().getChannels().add(c);
        if (parent.getButtonCount() == 9 && parent.isAddVisible()) {
            parent.setAdd(false);
        } else if (!parent.isAddVisible()) {
            parent.setAdd(true);
        }
        parent.addTab(c.getChannel());
        Application.getInstance().getHandleManager().getIRCHandler().join(c);
        dispose();
    }

    /**
     * Initlize the box.
     */
    private void init() {
        channelLabel = new JLabel();
        passCheck = new JCheckBox();
        channelName = new JTextField();
        passwordLabel = new JLabel();
        passwordBox = new JPasswordField();
        joinButton = new JButton();

        channelName.setText("#test");

        setTitle(Messages.JOINCHANNEL);
        setResizable(false);
        setIconImage(ImageUtil.getImage(Configuration.Paths.Resources.ICON_ADD));
        final Container contentPane = getContentPane();
        contentPane.setLayout(null);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                dispose();
            }
        });

        channelLabel.setText(Messages.CHANNEL);
        contentPane.add(channelLabel);
        channelLabel.setBounds(20, 25, 55, 20);

        passCheck.setText(Messages.PASSWORD);
        contentPane.add(passCheck);
        passCheck.setBounds(new Rectangle(new Point(55, 55), passCheck
                .getPreferredSize()));
        passCheck.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                if (passCheck.isSelected()) {
                    passwordBox.setEnabled(true);
                } else {
                    passwordBox.setEnabled(false);
                }
            }

        });
        contentPane.add(channelName);
        channelName.setBounds(75, 25, 120,
                channelName.getPreferredSize().height);

        passwordLabel.setText(Messages.PASSWORD);
        contentPane.add(passwordLabel);
        passwordLabel.setBounds(15, 95, passwordLabel.getPreferredSize().width,
                19);
        contentPane.add(passwordBox);
        passwordBox.setBounds(75, 95, 120,
                passwordBox.getPreferredSize().height);
        passwordBox.setEnabled(false);

        joinButton.setText(Messages.JOINCHANNEL);
        joinButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                channelButtonActionPerformed(e);
            }
        });
        contentPane.add(joinButton);
        joinButton.setBounds(10, 120, 200, 30);

        contentPane.setPreferredSize(new Dimension(225, 185));
        setSize(225, 185);
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }

    // Variables.
    private JLabel channelLabel;
    private JCheckBox passCheck;
    private JTextField channelName;
    private JLabel passwordLabel;
    private JPasswordField passwordBox;
    private JButton joinButton;
}
