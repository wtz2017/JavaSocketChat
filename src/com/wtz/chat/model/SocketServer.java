package com.wtz.chat.model;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SocketServer {
    private final String TAG = SocketServer.class.getSimpleName();

    private ServerSocket mSever;
    private Thread mServerStarter;

    private int QUEUE_CAPACITY = 10;
//    private BlockingQueue<String> mMessageQueue;
    private ArrayList<BlockingQueue<String>> mMsgQueueList;

    private IMessageReceive mUpdateHandler;
    private IMessageReceive mReceivingHandler;

    public SocketServer(int port, IMessageReceive updateHandler) throws IOException {
        this.mUpdateHandler = updateHandler;
        mReceivingHandler = new IMessageReceive() {

            @Override
            public void onReceive(boolean isLocal, String address, String message) {
                String strMsg = (message != null ? message : "");
                updateMessages(isLocal, address, message);
            }
        };

        mSever = new ServerSocket(port);
        mMsgQueueList = new ArrayList<BlockingQueue<String>>();
        
        start();
    }

    private void start() {
        boolean hadStarted = hadStarted();
        System.out.println("start...mSever = " + mSever + ", hadStarted = " + hadStarted);
        if (mSever != null && !mSever.isClosed() && !hadStarted) {
            // TODO------------------区分处理上边的判断！！！
            mServerStarter = new Thread(new Runnable() {

                @Override
                public void run() {
                    System.out.println("start...run");
                    while (true) {
                        beginListen();
                    }
                }

            });
            mServerStarter.start();
        }
    }

    private boolean hadStarted() {
        if (mServerStarter == null) {
            return false;
        }
        if (!mServerStarter.isAlive()) {
            return false;
        }
        if (mServerStarter.isInterrupted()) {
            return false;
        }

        return true;
    }

    private void beginListen() {
        if (mSever == null) {
            return;
        }
        try {
            System.out.println("beginListen...mSever.accept");
            final Socket clientSocket = mSever.accept();
            System.out.println("accept new client ok! socket = " + clientSocket);

            new Thread(new Runnable() {
                public void run() {
                    try {
                        BlockingQueue<String> messageQueue = new ArrayBlockingQueue<String>(QUEUE_CAPACITY);
                        mMsgQueueList.add(messageQueue);
                        new Thread(new SendingRunnable(clientSocket, messageQueue, TAG)).start();
                        new Thread(new ReceivingRunnable(clientSocket, mReceivingHandler, TAG))
                                .start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket() {
        if (mSever != null) {
            try {
                mSever.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSever = null;
        }
    }

    public void sendMsg(String msg) {
        if (mMsgQueueList == null || mMsgQueueList.size() == 0) {
            return;
        }

        for (BlockingQueue<String> queue : mMsgQueueList) {
            queue.add(msg);
        }
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
