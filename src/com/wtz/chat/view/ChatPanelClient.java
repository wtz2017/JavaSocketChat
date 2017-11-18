package com.wtz.chat.view;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.wtz.chat.control.ClientHandler;
import com.wtz.chat.model.IMessageReceive;

public class ChatPanelClient extends JPanel {

    private static final long serialVersionUID = 1223683985757043648L;

    private ClientHandler mClientHandler;

    private JTextArea msgShowArea;
    private JTextArea msgSendArea;

    public ChatPanelClient() {
        super(false);

        setLayout(new GridBagLayout());

        mClientHandler = new ClientHandler();

        JLabel label1 = new JLabel("对方IP和端口号：", JLabel.CENTER);
        add(label1, new GBC(0, 0, 1, 1).setFill(GBC.BOTH).setIpad(10, 10).setWeight(1, 1));

        final JTextField textField2 = new JTextField(4);
        add(textField2, new GBC(1, 0, 3, 1).setFill(GBC.BOTH).setIpad(10, 10).setWeight(1, 1));

        final JTextField textField3 = new JTextField(1);
        add(textField3, new GBC(4, 0, 1, 1).setFill(GBC.BOTH).setIpad(10, 10).setWeight(1, 1));

        JButton button4 = new JButton("连接");
        add(button4, new GBC(5, 0, 1, 1).setFill(GBC.BOTH).setIpad(10, 10).setWeight(1, 1));
        button4.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {

                mClientHandler.connectToServer(textField2.getText(), textField3.getText(),
                        new IMessageReceive() {

                            @Override
                            public void onReceive(boolean isLocal, String address, String message) {
                                showMsg(isLocal, address, message);
                            }
                        });

            }
        });

        JButton button5 = new JButton("断开");
        add(button5, new GBC(6, 0, 1, 1).setFill(GBC.BOTH).setIpad(10, 10).setWeight(1, 1));
        button5.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                mClientHandler.disconnect();
                showMsg(true, null, "已经断开");
            }
        });

        // 第2行--------------------------
        msgShowArea = new MyJTextArea(15, 10);
        JScrollPane scrollPane5 = new JScrollPane(msgShowArea);
        add(scrollPane5, new GBC(0, 1, 7, 3).setFill(GBC.BOTH).setIpad(10, 10).setWeight(1, 1));

        // 第3行--------------------------
        msgSendArea = new MyJTextArea(3, 8);
        msgSendArea.setToolTipText("ctrl + enter 组合键可以直接发送消息，enter换行");
        msgSendArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // 监听ctrl+enter组合键
                if ((e.getKeyCode() == KeyEvent.VK_ENTER) && (e.isControlDown())) {
                    sendMsg();
                }
            }
        });
        JScrollPane scrollPane6 = new JScrollPane(msgSendArea);
        add(scrollPane6, new GBC(0, 4, 6, 2).setFill(GBC.BOTH).setIpad(10, 10).setWeight(1, 1));

        JButton button7 = new JButton("发送");
        add(button7, new GBC(6, 4, 1, 1).setFill(GBC.BOTH).setIpad(10, 10).setWeight(1, 1));
        button7.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                sendMsg();
            }
        });
    }

    private void showMsg(boolean isLocal, String address, String message) {
        msgShowArea.append("\n");
        if (isLocal) {
            msgShowArea.append("我：");
        } else {
            msgShowArea.append(address);
        }
        msgShowArea.append("\n");
        msgShowArea.append("    ");
        msgShowArea.append(message + "\n");
        // 将光标移到最后，实现滚动条的自动滚动
        msgShowArea.setCaretPosition(msgShowArea.getText().length());
    }

    private void sendMsg() {
        mClientHandler.send(msgSendArea.getText());
        msgSendArea.setText("");
    }

}
