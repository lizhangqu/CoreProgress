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

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * 功能介绍
 *
 * @author lizhangqu
 * @version V1.0
 * @since 2017-07-12 15:17
 */
public class ProgressTest {

    @Test
    public void testResponseProgress() throws IOException {
        String url = "http://assets.geilicdn.com/channelapk/1000n_shurufa_1.9.6.apk";
        File outFile = new File("library/files/out.apk");
        outFile.delete();
        outFile.getParentFile().mkdirs();
        outFile.createNewFile();

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.get();
        Call call = okHttpClient.newCall(builder.build());

        Response response = call.execute();
        ResponseBody body = response.body();
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        ResponseBody responseBody = ProgressHelper.withProgress(body, new ProgressListener() {
            @Override
            public void onProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                System.out.println("=============start===============");
                System.out.println("numBytes:" + numBytes);
                System.out.println("totalBytes:" + totalBytes);
                System.out.println("percent:" + percent);
                System.out.println("speed:" + speed);
                System.out.println("============= end ===============");
                if (percent == 1.0) {
                    atomicBoolean.set(true);
                }
            }
        });


        BufferedSource source = responseBody.source();
        BufferedSink sink = Okio.buffer(Okio.sink(outFile));
        source.readAll(sink);
        sink.flush();
        source.close();

        Assert.assertNotNull(source);
        Assert.assertTrue(atomicBoolean.get());
    }


    @Test
    public void testRequestProgress() throws IOException {
        String url = "http://www.baidu.com";
        File apkFile = new File("library/files/test.apk");

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        builder.url(url);

        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.addFormDataPart("test", apkFile.getName(), RequestBody.create(null, apkFile));
        MultipartBody build = bodyBuilder.build();

        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        RequestBody requestBody = ProgressHelper.withProgress(build, new ProgressListener() {
            @Override
            public void onProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                System.out.println("=============start===============");
                System.out.println("numBytes:" + numBytes);
                System.out.println("totalBytes:" + totalBytes);
                System.out.println("percent:" + percent);
                System.out.println("speed:" + speed);
                System.out.println("============= end ===============");
                if (percent == 1.0) {
                    atomicBoolean.set(true);
                }
            }
        });
        builder.post(requestBody);

        Call call = okHttpClient.newCall(builder.build());

        Response response = call.execute();

        Assert.assertNotNull(response);
        Assert.assertTrue(atomicBoolean.get());

        System.out.println(response.headers());
        System.out.println(response.request().headers());

    }

}
