package com.wtz.chat.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReceivingRunnable implements Runnable {
    private String TAG = ReceivingRunnable.class.getSimpleName();

    private Socket mSocket;
    private IMessageReceive mReceivingHandler;

    public ReceivingRunnable(Socket socket, IMessageReceive h, String tag) {
        this.mReceivingHandler = h;
        this.mSocket = socket;
        TAG = tag + "-" + TAG;
    }

    @Override
    public void run() {
        System.out.println("ReceivingRunnable...mSocket = " + mSocket + ", mReceivingHandler = " + mReceivingHandler);
        if (mSocket == null || mReceivingHandler == null) {
            return;
        }

        BufferedReader input;
        try {
            input = new BufferedReader(new InputStreamReader(mSocket.getInputStream(), "UTF-8"));
            while (!Thread.currentThread().isInterrupted() && !mSocket.isClosed()) {

                String messageStr = null;
                messageStr = input.readLine();
                if (messageStr != null) {
                    System.out.println("Read from the stream: " + messageStr);
                    mReceivingHandler.onReceive(false, mSocket.getInetAddress().getHostAddress(), messageStr);
                } else {
                    System.out.println("The nulls! The nulls!");
                    break;
                }
            }
            input.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
