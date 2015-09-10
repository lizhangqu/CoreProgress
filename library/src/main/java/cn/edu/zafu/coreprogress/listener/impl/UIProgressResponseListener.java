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

import cn.edu.zafu.coreprogress.listener.ProgressResponseListener;

/**
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-09-02
 * Time: 22:34
 */
public abstract class UIProgressResponseListener implements ProgressResponseListener {
    private static final int RESPONSE_UPDATE = 0x02;
    private static final int RESPONSE_START = 0x04;
    private static final int RESPONSE_FINISH = 0x06;
    private boolean isFirst = false;

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
                case RESPONSE_UPDATE: {
                    UIProgressResponseListener uiProgressResponseListener = mUIProgressResponseListenerWeakReference.get();
                    if (uiProgressResponseListener != null) {
                        //获得进度实体类
                        ProgressModel progressModel = (ProgressModel) msg.obj;
                        //回调抽象方法
                        uiProgressResponseListener.onUIResponseProgress(progressModel.getCurrentBytes(), progressModel.getContentLength(), progressModel.isDone());
                    }
                    break;
                }
                case RESPONSE_START: {
                    UIProgressResponseListener uiProgressResponseListener = mUIProgressResponseListenerWeakReference.get();
                    if (uiProgressResponseListener != null) {
                        //获得进度实体类
                        ProgressModel progressModel = (ProgressModel) msg.obj;
                        //回调抽象方法
                        uiProgressResponseListener.onUIResponseStart(progressModel.getCurrentBytes(), progressModel.getContentLength(), progressModel.isDone());
                    }
                    break;
                }
                case RESPONSE_FINISH: {
                    UIProgressResponseListener uiProgressResponseListener = mUIProgressResponseListenerWeakReference.get();
                    if (uiProgressResponseListener != null) {
                        //获得进度实体类
                        ProgressModel progressModel = (ProgressModel) msg.obj;
                        //回调抽象方法
                        uiProgressResponseListener.onUIResponseFinish(progressModel.getCurrentBytes(), progressModel.getContentLength(), progressModel.isDone());
                    }
                    break;
                }
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

        //如果是第一次，发送消息
        if (!isFirst) {
            isFirst = true;
            Message start = Message.obtain();
            start.obj = new ProgressModel(bytesRead, contentLength, done);
            start.what = RESPONSE_START;
            mHandler.sendMessage(start);
        }
        //通过Handler发送进度消息
        Message message = Message.obtain();
        message.obj = new ProgressModel(bytesRead, contentLength, done);
        message.what = RESPONSE_UPDATE;
        mHandler.sendMessage(message);

        //如果已完成，发送消息
        if(done) {
            Message finish = Message.obtain();
            finish.obj = new ProgressModel(bytesRead, contentLength, done);
            finish.what = RESPONSE_FINISH;
            mHandler.sendMessage(finish);
        }
    }

    /**
     * UI层回调抽象方法
     *
     * @param bytesRead     当前读取响应体字节长度
     * @param contentLength 总字节长度
     * @param done          是否读取完成
     */
    public abstract void onUIResponseProgress(long bytesRead, long contentLength, boolean done);

    /**
     * UI层开始下载回调方法
     * @param bytesRead 当前读取响应体字节长度
     * @param contentLength 总字节长度
     * @param done 是否读取完成
     */
    public void onUIResponseStart(long bytesRead, long contentLength, boolean done) {

    }

    /**
     * UI层结束下载回调方法
     * @param bytesRead 当前读取响应体字节长度
     * @param contentLength 总字节长度
     * @param done 是否读取完成
     */
    public void onUIResponseFinish(long bytesRead, long contentLength, boolean done) {

    }
}
