package com.wtz.chat.control;

import com.wtz.chat.model.IMessageReceive;
import com.wtz.chat.model.SocketClient;

public class ClientHandler extends BaseHandler {

    private SocketClient mSocketClient;
    private boolean isConnecting;
    private Thread mConnectThread;
    private IMessageReceive mUpdateHandler;

    public boolean isConnecting() {
        return isConnecting;
    }

    public void connectToServer(final String ipString, final String portString,
            final IMessageReceive updateHandler) {
        if (isConnecting) {
            System.out.println("正在连接中...");
            if (updateHandler != null) {
                updateHandler.onReceive(true, null, "正在连接中...");
            }
            return;
        }
        
        if (mSocketClient != null) {
            System.out.println("已经连接成功");
            if (updateHandler != null) {
                updateHandler.onReceive(true, null, "已经连接成功");
            }
            return;
        }

        isConnecting = true;
        mUpdateHandler = updateHandler;
        mConnectThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    int port = Integer.parseInt(portString);
                    if (mSocketClient != null) {
                        mSocketClient.closeSocket();
                    }
                    mSocketClient = new SocketClient(ipString, port, updateHandler);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (mSocketClient != null) {
                        mSocketClient.closeSocket();
                        mSocketClient = null;
                    }
                    if (updateHandler != null) {
                        updateHandler.onReceive(true, null, "连接错误！");
                    }
                } finally {
                    isConnecting = false;
                }
            }
        });
        mConnectThread.start();
    }

    @Override
    public void send(String msg) {
        try {
            mSocketClient.sendMsg(msg);
        } catch (Exception e) {
            e.printStackTrace();
            if (mUpdateHandler != null) {
                mUpdateHandler.onReceive(true, null, "发送失败！");
            }
        }
    }

    public void disconnect() {
        if (mSocketClient != null) {
            mSocketClient.closeSocket();
            mSocketClient = null;
        }
    }
}
