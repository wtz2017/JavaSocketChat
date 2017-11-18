package com.wtz.chat.view;

import java.awt.FlowLayout;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.wtz.chat.utils.Utils;

public class Frame extends JFrame {

    private static final long serialVersionUID = -8386839067613179528L;

    public Frame() {
        super("Socket简易聊天器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 650);
        setLocation(300, 50);
        setLayout(new VerticalFlowLayout());
        setVisible(true);

        JPanel titlePanel = new JPanel(new FlowLayout());
        titlePanel.add(new JLabel("本机IP:"));
        titlePanel.add(new JLabel("" + Utils.getLocalIPAddress()));
        add(titlePanel);

        JTabbedPane tablePanel = new JTabbedPane();

        JPanel panel0 = new ChatPanelClient();
        tablePanel.addTab("客户端", null, panel0, "作为客户端");
        tablePanel.setMnemonicAt(0, KeyEvent.VK_0);

        JPanel panel1 = new ChatPanelServer();
        tablePanel.addTab("服务端", null, panel1, "作为服务端");
        tablePanel.setMnemonicAt(1, KeyEvent.VK_1);

        add(tablePanel);
    }

}
