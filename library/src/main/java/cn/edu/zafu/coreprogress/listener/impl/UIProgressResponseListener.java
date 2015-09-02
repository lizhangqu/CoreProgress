package cn.edu.zafu.coreprogress.listener.impl;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

import cn.edu.zafu.coreprogress.listener.ProgressModel;
import cn.edu.zafu.coreprogress.listener.ProgressResponseListener;

/**
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-09-02
 * Time: 22:34
 */
public abstract class UIProgressResponseListener implements ProgressResponseListener {
    private static final int RESPONSE_UPDATE = 0x01;

    private static class UIHandler extends Handler {
        private final WeakReference<UIProgressResponseListener> mUIProgressResponseListenerWeakReference;

        public UIHandler(Looper looper, UIProgressResponseListener uiProgressResponseListener) {
            super(looper);
            mUIProgressResponseListenerWeakReference = new WeakReference<UIProgressResponseListener>(uiProgressResponseListener);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RESPONSE_UPDATE:
                    UIProgressResponseListener uiProgressResponseListener = mUIProgressResponseListenerWeakReference.get();
                    if (uiProgressResponseListener != null) {
                        ProgressModel progressModel = (ProgressModel) msg.obj;
                        uiProgressResponseListener.onUIResponseProgress(progressModel.getCurrentBytes(), progressModel.getContentLength(), progressModel.isDone());
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private final Handler mHandler = new UIHandler(Looper.getMainLooper(), this);

    @Override
    public void onResponseProgress(long bytesRead, long contentLength, boolean done) {
        Message message = Message.obtain();
        message.obj = new ProgressModel(bytesRead, contentLength, done);
        message.what = RESPONSE_UPDATE;
        mHandler.sendMessage(message);
    }

    public abstract void onUIResponseProgress(long bytesRead, long contentLength, boolean done);
}
