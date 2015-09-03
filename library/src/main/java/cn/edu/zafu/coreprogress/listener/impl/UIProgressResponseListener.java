/**
 * Copyright 2015 ZhangQu Li
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    private static final int RESPONSE_UPDATE = 0x02;
    //处理UI层的Handler子类
    private static class UIHandler extends Handler {
        //弱引用
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
                        //获得进度实体类
                        ProgressModel progressModel = (ProgressModel) msg.obj;
                        //回调抽象方法
                        uiProgressResponseListener.onUIResponseProgress(progressModel.getCurrentBytes(), progressModel.getContentLength(), progressModel.isDone());
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }
    //主线程Handler
    private final Handler mHandler = new UIHandler(Looper.getMainLooper(), this);

    @Override
    public void onResponseProgress(long bytesRead, long contentLength, boolean done) {
        //通过Handler发送进度消息
        Message message = Message.obtain();
        message.obj = new ProgressModel(bytesRead, contentLength, done);
        message.what = RESPONSE_UPDATE;
        mHandler.sendMessage(message);
    }

    /**
     * UI层回调抽象方法
     * @param bytesRead 当前读取响应体字节长度
     * @param contentLength 总字节长度
     * @param done 是否读取完成
     */
    public abstract void onUIResponseProgress(long bytesRead, long contentLength, boolean done);
}
