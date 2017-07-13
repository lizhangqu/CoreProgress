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
    private final Handler mHandler;

    private static final int WHAT = 10000;
    private static final String CURRENT_BYTES = "numBytes";
    private static final String TOTAL_BYTES = "totalBytes";
    private static final String PERCENT = "percent";
    private static final String SPEED = "speed";

    public ProgressUIListener() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg == null) {
                    return;
                }
                if (msg.what != WHAT) {
                    return;
                }
                Bundle data = msg.getData();
                if (data == null) {
                    return;
                }
                long numBytes = data.getLong(CURRENT_BYTES);
                long totalBytes = data.getLong(TOTAL_BYTES);
                float percent = data.getFloat(PERCENT);
                float speed = data.getFloat(SPEED);
                onUIProgressChanged(numBytes, totalBytes, percent, speed);
            }
        };
    }

    public final void onProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            onUIProgressChanged(numBytes, totalBytes, percent, speed);
            return;
        }
        Message message = mHandler.obtainMessage();
        message.what = WHAT;
        Bundle data = new Bundle();
        data.putLong(CURRENT_BYTES, numBytes);
        data.putLong(TOTAL_BYTES, totalBytes);
        data.putFloat(PERCENT, percent);
        data.putFloat(SPEED, speed);
        message.setData(data);
        mHandler.sendMessage(message);
    }

    public abstract void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed);
}
