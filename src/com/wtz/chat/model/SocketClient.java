package com.wtz.chat.model;

import java.io.*;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SocketClient {
    private final String TAG = SocketClient.class.getSimpleName();

    private Socket mClient;

    private int QUEUE_CAPACITY = 10;
    private BlockingQueue<String> mMessageQueue;
    private Thread mSendThread;
    private Thread mRecThread;

    private IMessageReceive mUpdateHandler;
    private IMessageReceive mReceivingHandler;

    public SocketClient(String site, int port, IMessageReceive updateHandler)
            throws UnknownHostException, IOException {
        this.mUpdateHandler = updateHandler;
        mReceivingHandler = new IMessageReceive() {

            @Override
            public void onReceive(boolean isLocal, String address, String message) {
                String strMsg = (message != null ? message : "");
                updateMessages(isLocal, address, strMsg);
            }
        };

        mClient = new Socket(site, port);
        System.out.println("Client is created! site:" + site + " port:" + port);

        if (mClient != null && !mClient.isClosed()) {
            mMessageQueue = new ArrayBlockingQueue<String>(QUEUE_CAPACITY);
            mSendThread = new Thread(new SendingRunnable(mClient, mMessageQueue, TAG));
            mSendThread.start();

            mRecThread = new Thread(new ReceivingRunnable(mClient, mReceivingHandler, TAG));
            mRecThread.start();

            updateMessages(true, null, "Socket 连接成功！");
        }
    }

    public void closeSocket() {
        if (mClient != null) {
            try {
                mClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMsg(String msg) {
        if (mClient == null || mMessageQueue == null) {
            return;
        }

        mMessageQueue.add(msg);
        updateMessages(true, null, msg);
    }

    private synchronized void updateMessages(boolean isLocal, String address, String message) {
        System.out.println("Updating message...isLocal: " + isLocal + ", from: " + address
                + ", msg: " + message);
        if (mUpdateHandler != null) {
            mUpdateHandler.onReceive(isLocal, address, message);
        }
    }
}
