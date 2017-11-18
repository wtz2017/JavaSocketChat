package com.wtz.chat.view;

import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.wtz.chat.MyJTextArea;
import com.wtz.chat.control.ServerHandler;
import com.wtz.chat.model.IMessageReceive;
import com.wtz.chat.model.IResultListener;
import com.wtz.chat.utils.NumberValidationUtils;
import com.wtz.chat.utils.QRCodeUtil;
import com.wtz.chat.utils.Utils;

public class ChatPanelServer extends JPanel {

    private static final long serialVersionUID = 3864141210347880107L;

    private ServerHandler mServerHandler;

    private JLabel mLabelCreateTips;
    private JTextField mTextFieldPort;
    private JLabel mQrcodeLabel;
    private JTextArea msgShowArea;
    private JTextArea msgSendArea;
    private ImageIcon mDefaultIcon = null;

    private final static int QRCODE_SIZE = 100;
    private final static String DEFAULT_QRCODE_PATH = "/resources/default_qrcode.png";

    public ChatPanelServer() {
        super(false);

        setLayout(new GridBagLayout());

        mServerHandler = new ServerHandler();

        JLabel label1 = new JLabel("设定端口号：", JLabel.CENTER);
        add(label1, new GBC(0, 0, 1, 1).setFill(GBC.BOTH).setIpad(10, 10).setWeight(1, 1));

        mTextFieldPort = new JTextField(1);
        add(mTextFieldPort, new GBC(1, 0, 2, 1).setFill(GBC.BOTH).setIpad(10, 10).setWeight(1, 1));

        JButton button3 = new JButton("创建");
        add(button3, new GBC(3, 0, 1, 1).setFill(GBC.BOTH).setIpad(10, 10).setWeight(1, 1));
        button3.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                String portString = mTextFieldPort.getText();
                if (!NumberValidationUtils.isIntString(portString)) {
                    showMsg(true, null, "请输入正确的整数端口号");
                    return;
                }
                mServerHandler.create(portString, mCreateServerListener, mMessageReceiver);
            }
        });

        JButton button4 = new JButton("销毁");
        add(button4, new GBC(4, 0, 1, 1).setFill(GBC.BOTH).setIpad(10, 10).setWeight(1, 1));
        button4.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                mServerHandler.destroy();
                showMsg(true, null, "已经销毁");
                mQrcodeLabel.setIcon(mDefaultIcon);
                mLabelCreateTips.setText("创建生成地址二维码：");
            }
        });

        // 第2行--------------------------
        mLabelCreateTips = new JLabel("创建生成地址二维码：", JLabel.CENTER);
        add(mLabelCreateTips,
                new GBC(0, 1, 3, 3).setFill(GBC.BOTH).setIpad(10, 10).setWeight(1, 1));

        try {
            mDefaultIcon = new ImageIcon(ImageIO.read(getClass().getResource(DEFAULT_QRCODE_PATH)));
            mDefaultIcon.setImage(mDefaultIcon.getImage().getScaledInstance(QRCODE_SIZE,
                    QRCODE_SIZE, Image.SCALE_DEFAULT));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        mQrcodeLabel = (mDefaultIcon == null) ? new JLabel() : new JLabel(mDefaultIcon);
        add(mQrcodeLabel, new GBC(3, 1, 3, 3).setFill(GBC.BOTH).setIpad(10, 10).setWeight(1, 1));

        // 第3行--------------------------
        msgShowArea = new MyJTextArea(15, 10);
        JScrollPane scrollPane5 = new JScrollPane(msgShowArea);
        add(scrollPane5, new GBC(0, 4, 5, 3).setFill(GBC.BOTH).setIpad(10, 10).setWeight(1, 1));

        // 第4行--------------------------
        msgSendArea = new MyJTextArea(3, 8);
        msgSendArea.setToolTipText("ctrl + enter 组合键可以直接发送消息，enter换行");
        JScrollPane scrollPane6 = new JScrollPane(msgSendArea);
        add(scrollPane6, new GBC(0, 7, 4, 2).setFill(GBC.BOTH).setIpad(10, 10).setWeight(1, 1));
        msgSendArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // 监听ctrl+enter组合键
                if ((e.getKeyCode() == KeyEvent.VK_ENTER) && (e.isControlDown())) {
                    sendMsg();
                }
            }
        });

        JButton button7 = new JButton("发送");
        add(button7, new GBC(4, 7, 1, 1).setFill(GBC.BOTH).setIpad(10, 10).setWeight(1, 1));
        button7.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                sendMsg();
            }
        });
    }

    private IResultListener mCreateServerListener = new IResultListener() {

        @Override
        public void onResult(boolean success, String error) {
            showMsg(true, null, error);
            if (success) {
                String text = Utils.getLocalIPAddress() + ":" + mTextFieldPort.getText();
                mQrcodeLabel.setIcon(QRCodeUtil.createQrCode(text, QRCODE_SIZE, QRCODE_SIZE));
                mLabelCreateTips.setText("扫描二维码来绑定吧：");
            }
        }
    };

    private IMessageReceive mMessageReceiver = new IMessageReceive() {

        @Override
        public void onReceive(boolean isLocal, String address, String message) {
            showMsg(isLocal, address, message);
        }
    };

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
        mServerHandler.send(msgSendArea.getText());
        msgSendArea.setText("");
    }

}
