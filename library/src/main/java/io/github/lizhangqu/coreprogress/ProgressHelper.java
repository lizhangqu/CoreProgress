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

import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * 进度回调请求体/响应体包装类
 */
public class ProgressHelper {
    /**
     * 包装请求体为带进度的请求体
     *
     * @param requestBody      待包装的请求体
     * @param progressListener 进度回调监听
     * @return 带进度的请求体，使用此请求体进行请求
     */
    public static RequestBody withProgress(RequestBody requestBody, ProgressListener progressListener) {
        if (requestBody == null) {
            throw new IllegalArgumentException("requestBody == null");
        }
        if (progressListener == null) {
            throw new IllegalArgumentException("progressListener == null");
        }
        return new ProgressRequestBody(requestBody, progressListener);
    }

    /**
     * 包装请求体为带进度的响应体
     *
     * @param responseBody     待包装的响应体
     * @param progressListener 进度回调监听
     * @return 带进度的响应体，使用此响应体进行响应数据的读取
     */
    public static ResponseBody withProgress(ResponseBody responseBody, ProgressListener progressListener) {
        if (responseBody == null) {
            throw new IllegalArgumentException("responseBody == null");
        }
        if (progressListener == null) {
            throw new IllegalArgumentException("progressListener == null");
        }
        return new ProgressResponseBody(responseBody, progressListener);
    }
}
