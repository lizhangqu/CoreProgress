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

import cn.edu.zafu.coreprogress.listener.ProgressRequestListener;

/**
 * 请求体回调实现类，用于UI层回调
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-09-02
 * Time: 22:34
 */
public abstract class UIProgressRequestListener implements ProgressRequestListener {
    private static final int REQUEST_UPDATE = 0x01;
    private static final int REQUEST_START = 0x03;
    private static final int REQUEST_FINISH = 0x05;
    private boolean isFirst = false;

    //处理UI层的Handler子类
    private static class UIHandler extends Handler {
        //弱引用
        private final WeakReference<UIProgressRequestListener> mUIProgressRequestListenerWeakReference;

        public UIHandler(Looper looper, UIProgressRequestListener uiProgressRequestListener) {
            super(looper);
            mUIProgressRequestListenerWeakReference = new WeakReference<UIProgressRequestListener>(uiProgressRequestListener);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_UPDATE: {
                    UIProgressRequestListener uiProgressRequestListener = mUIProgressRequestListenerWeakReference.get();
                    if (uiProgressRequestListener != null) {
                        //获得进度实体类
                        ProgressModel progressModel = (ProgressModel) msg.obj;
                        //回调抽象方法
                        uiProgressRequestListener.onUIRequestProgress(progressModel.getCurrentBytes(), progressModel.getContentLength(), progressModel.isDone());
                    }
                    break;
                }
                case REQUEST_START: {
                    UIProgressRequestListener uiProgressRequestListener = mUIProgressRequestListenerWeakReference.get();
                    if (uiProgressRequestListener != null) {
                        //获得进度实体类
                        ProgressModel progressModel = (ProgressModel) msg.obj;
                        //回调抽象方法
                        uiProgressRequestListener.onUIRequestStart(progressModel.getCurrentBytes(), progressModel.getContentLength(), progressModel.isDone());
                    }
                    break;
                }
                case REQUEST_FINISH: {
                    UIProgressRequestListener uiProgressRequestListener = mUIProgressRequestListenerWeakReference.get();
                    if (uiProgressRequestListener != null) {
                        //获得进度实体类
                        ProgressModel progressModel = (ProgressModel) msg.obj;
                        //回调抽象方法
                        uiProgressRequestListener.onUIRequestFinish(progressModel.getCurrentBytes(), progressModel.getContentLength(), progressModel.isDone());
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
    public void onRequestProgress(long bytesWrite, long contentLength, boolean done) {
        //如果是第一次，发送消息
        if (!isFirst) {
            isFirst = true;
            Message start = Message.obtain();
            start.obj = new ProgressModel(bytesWrite, contentLength, done);
            start.what = REQUEST_START;
            mHandler.sendMessage(start);
        }

        //通过Handler发送进度消息
        Message message = Message.obtain();
        message.obj = new ProgressModel(bytesWrite, contentLength, done);
        message.what = REQUEST_UPDATE;
        mHandler.sendMessage(message);

        if(done) {
            Message finish = Message.obtain();
            finish.obj = new ProgressModel(bytesWrite, contentLength, done);
            finish.what = REQUEST_START;
            mHandler.sendMessage(finish);
        }
    }

    /**
     * UI层回调抽象方法
     *
     * @param bytesWrite    当前写入的字节长度
     * @param contentLength 总字节长度
     * @param done          是否写入完成
     */
    public abstract void onUIRequestProgress(long bytesWrite, long contentLength, boolean done);

    /**
     * UI层开始请求回调方法
     * @param bytesWrite 当前写入的字节长度
     * @param contentLength 总字节长度
     * @param done 是否写入完成
     */
    public void onUIRequestStart(long bytesWrite, long contentLength, boolean done) {

    }

    /**
     * UI层结束请求回调方法
     * @param bytesWrite 当前写入的字节长度
     * @param contentLength 总字节长度
     * @param done 是否写入完成
     */
    public void onUIRequestFinish(long bytesWrite, long contentLength, boolean done) {

    }
}
