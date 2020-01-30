package com.ad4th.devote.network;

public interface RetroCallbackBase<T> {
    void onError(Throwable t);

    void onSuccess(int code, T receivedData);

    void onFailure(int code);
}