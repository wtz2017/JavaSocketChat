package com.wtz.chat.control;

import com.wtz.chat.model.IMessageReceive;
import com.wtz.chat.model.IResultListener;
import com.wtz.chat.model.SocketServer;

public class ServerHandler extends BaseHandler {

    private SocketServer mSocketServer;
    private boolean isCreating;
    private Thread mCreateThread;
    private IMessageReceive mUpdateHandler;

    public boolean isCreating() {
        return isCreating;
    }

    public void create(final String portString, final IResultListener l, final IMessageReceive updateHandler) {
        if (isCreating) {
            System.out.println("正在创建服务端...");
            if (l != null) {
                l.onResult(false, "正在创建服务端...");
            }
            return;
        }

        if (mSocketServer != null) {
            System.out.println("已经存在服务端");
            if (l != null) {
                l.onResult(false, "已经存在服务端");
            }
            return;
        }

        isCreating = true;
        mUpdateHandler = updateHandler;
        mCreateThread = new Thread(new Runnable() {

            @Override
            public void run() {
                boolean ret = false;
                try {
                    int port = Integer.parseInt(portString);
                    mSocketServer = new SocketServer(port, mUpdateHandler);
                    ret = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    if (mSocketServer != null) {
                        mSocketServer.closeSocket();
                        mSocketServer = null;
                    }
                    ret = false;
                } finally {
                    isCreating = false;
                    if (ret) {
                        if (l != null) {
                            l.onResult(true, "创建成功！");
                        }
                    } else {
                        if (l != null) {
                            l.onResult(false, "创建错误！");
                        } 
                    }
                }
            }
        });
        mCreateThread.start();
    }

    @Override
    public void send(String msg) {
        try {
            mSocketServer.sendMsg(msg);
        } catch (Exception e) {
            e.printStackTrace();
            if (mUpdateHandler != null) {
                mUpdateHandler.onReceive(true, null, "发送失败！");
            }
        }
    }
    
    public void destroy() {
        if (mSocketServer != null) {
            mSocketServer.closeSocket();
            mSocketServer = null;
        }
    }
}
