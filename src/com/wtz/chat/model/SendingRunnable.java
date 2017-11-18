package com.wtz.chat.model;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

public class SendingRunnable implements Runnable {
    private String TAG = SendingRunnable.class.getSimpleName();
    
    private BlockingQueue<String> mMessageQueue;
    private Socket mSocket;
    
    public SendingRunnable(Socket socket, BlockingQueue<String> messageQueue, String tag) {
        this.mMessageQueue = messageQueue;
        this.mSocket = socket;
        TAG = tag + "-" + TAG;
    }

    @Override
    public void run() {
        while (mSocket != null && !mSocket.isClosed()) {
            try {
                String msg = mMessageQueue.take();
                sendMessage(msg);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
    
    public void sendMessage(String msg) {
        try {
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(mSocket.getOutputStream())), true);
            out.println(msg);
            out.flush();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Client sent message: " + msg);
    }
}
