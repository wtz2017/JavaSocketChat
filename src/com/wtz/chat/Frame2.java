package com.wtz.chat;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Frame2 extends JFrame {
    private static JLabel label;
    private JButton b1;
    private JButton b2;
    private JTextField txt;
    private JTextArea txa;

    public Frame2() {
        super("Hello Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 100);
        setVisible(true);

        // 设置窗体布局
        setLayout(new FlowLayout());

        // 添加标签
        label = new JLabel("A Label");
        add(label);

        // 添加文本输入框
        txt = new JTextField(10);
        add(txt);

        // 添加文本区域
        txa = new JTextArea(5, 10);
        add(new JScrollPane(txa));

        // 添加按钮
        b1 = new JButton("add data");
        b2 = new JButton("clear data");
        add(b1);
        add(b2);

        // 添加事件
        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // TODO Auto-generated method stub
                txa.append("this is JText Area\n");
            }
        });
        b2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // TODO Auto-generated method stub
                txa.setText("");
            }
        });

    }

    class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            // TODO Auto-generated method stub
            // 将按钮的名称显示在TextField中
            txt.setText(((JButton) event.getSource()).getText());
        }

    }
}
