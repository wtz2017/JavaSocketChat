package com.wtz.chat.model;

public interface IMessageReceive {
    void onReceive(boolean isLocal, String address, String message);
}
 