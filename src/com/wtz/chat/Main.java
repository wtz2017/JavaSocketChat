package com.wtz.chat;

import javax.swing.SwingUtilities;

import com.wtz.chat.view.Frame;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new Frame();
            }
        });
    }

}
