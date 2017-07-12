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

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;

/**
 * 带进度响应体
 */
class ProgressResponseBody extends ResponseBody {
    private final ResponseBody responseBody;
    private final ProgressCallback progressListener;
    private BufferedSource progressSource;


    ProgressResponseBody(ResponseBody responseBody, ProgressCallback progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
    }


    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }


    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }


    @Override
    public BufferedSource source() {
        if (progressListener == null) {
            return responseBody.source();
        }
        ProgressInputStream progressInputStream = new ProgressInputStream(responseBody.source().inputStream(), progressListener, contentLength());
        progressSource = Okio.buffer(Okio.source(progressInputStream));
        return progressSource;
    }

    @Override
    public void close() {
        if (progressSource != null) {
            try {
                progressSource.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}