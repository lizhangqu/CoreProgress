/**
 * Copyright 2017 区长
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
package io.github.lizhangqu.coreprogress;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * 流读写进度ui回调
 */
public abstract class ProgressUIListener extends ProgressListener {
    private Handler mHandler;

    private static final int WHAT_START = 0x01;
    private static final int WHAT_UPDATE = 0x02;
    private static final int WHAT_FINISH = 0x03;
    private static final String CURRENT_BYTES = "numBytes";
    private static final String TOTAL_BYTES = "totalBytes";
    private static final String PERCENT = "percent";
    private static final String SPEED = "speed";

    public ProgressUIListener() {

    }

    private void ensureHandler() {
        if (mHandler != null) {
            return;
        }
        synchronized (ProgressUIListener.class) {
            if (mHandler == null) {
                mHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg == null) {
                            return;
                        }
                        switch (msg.what) {
                            case WHAT_START:
                                Bundle startData = msg.getData();
                                if (startData == null) {
                                    return;
                                }
                                onUIProgressStart(startData.getLong(TOTAL_BYTES));
                                break;
                            case WHAT_UPDATE:
                                Bundle updateData = msg.getData();
                                if (updateData == null) {
                                    return;
                                }
                                long numBytes = updateData.getLong(CURRENT_BYTES);
                                long totalBytes = updateData.getLong(TOTAL_BYTES);
                                float percent = updateData.getFloat(PERCENT);
                                float speed = updateData.getFloat(SPEED);
                                onUIProgressChanged(numBytes, totalBytes, percent, speed);
                                break;
                            case WHAT_FINISH:
                                onUIProgressFinish();
                                break;
                            default:
                                break;

                        }
                    }
                };
            }
        }
    }

    /**
     * 进度发生了改变，如果numBytes，totalBytes，percent，speed都为-1，则表示总大小获取不到
     *
     * @param numBytes   已读/写大小
     * @param totalBytes 总大小
     * @param percent    百分比
     * @param speed      速度 bytes/ms
     */
    public final void onProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            onUIProgressChanged(numBytes, totalBytes, percent, speed);
            return;
        }
        ensureHandler();
        Message message = mHandler.obtainMessage();
        message.what = WHAT_UPDATE;
        Bundle data = new Bundle();
        data.putLong(CURRENT_BYTES, numBytes);
        data.putLong(TOTAL_BYTES, totalBytes);
        data.putFloat(PERCENT, percent);
        data.putFloat(SPEED, speed);
        message.setData(data);
        mHandler.sendMessage(message);
    }


    /**
     * 进度开始
     *
     * @param totalBytes 总大小
     */
    public final void onProgressStart(long totalBytes) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            onUIProgressStart(totalBytes);
            return;
        }
        ensureHandler();
        Message message = mHandler.obtainMessage();
        message.what = WHAT_START;
        Bundle data = new Bundle();
        data.putLong(TOTAL_BYTES, totalBytes);
        message.setData(data);
        mHandler.sendMessage(message);
    }

    /**
     * 进度结束
     */
    public final void onProgressFinish() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            onUIProgressFinish();
            return;
        }
        ensureHandler();
        Message message = mHandler.obtainMessage();
        message.what = WHAT_FINISH;
        mHandler.sendMessage(message);
    }

    /**
     * 进度发生了改变，如果numBytes，totalBytes，percent，speed都为-1，则表示总大小获取不到
     *
     * @param numBytes   已读/写大小
     * @param totalBytes 总大小
     * @param percent    百分比
     * @param speed      速度 bytes/ms
     */
    public abstract void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed);


    /**
     * 进度开始
     *
     * @param totalBytes 总大小
     */
    public void onUIProgressStart(long totalBytes) {

    }

    /**
     * 进度结束
     */
    public void onUIProgressFinish() {

    }
}
