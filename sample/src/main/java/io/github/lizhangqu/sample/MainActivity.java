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
package io.github.lizhangqu.sample;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import io.github.lizhangqu.coreprogress.ProgressHelper;
import io.github.lizhangqu.coreprogress.ProgressUIListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class MainActivity extends AppCompatActivity {
    private Button upload, download;
    private TextView uploadInfo, downloadInfo;
    private ProgressBar uploadProgress, downloadProgeress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        uploadProgress = (ProgressBar) findViewById(R.id.upload_progress);
        downloadProgeress = (ProgressBar) findViewById(R.id.download_progress);

        uploadInfo = (TextView) findViewById(R.id.upload_info);
        downloadInfo = (TextView) findViewById(R.id.download_info);

        upload = (Button) findViewById(R.id.upload);
        download = (Button) findViewById(R.id.download);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download();
            }
        });
    }

    private void upload() {
        uploadInfo.setText("start upload");
        String currentApkPath = getApplicationContext().getPackageResourcePath();
        File apkFile = new File(currentApkPath);
        String url = "https://www.baidu.com";

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        builder.url(url);

        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.addFormDataPart("test", apkFile.getName(), RequestBody.create(null, apkFile));
        MultipartBody build = bodyBuilder.build();

        RequestBody requestBody = ProgressHelper.withProgress(build, new ProgressUIListener() {

            //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
            @Override
            public void onUIProgressStart(long totalBytes) {
                super.onUIProgressStart(totalBytes);
                Log.e("TAG", "onUIProgressStart:" + totalBytes);
                Toast.makeText(getApplicationContext(), "开始上传：" + totalBytes, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                Log.e("TAG", "=============start===============");
                Log.e("TAG", "numBytes:" + numBytes);
                Log.e("TAG", "totalBytes:" + totalBytes);
                Log.e("TAG", "percent:" + percent);
                Log.e("TAG", "speed:" + speed);
                Log.e("TAG", "============= end ===============");
                uploadProgress.setProgress((int) (100 * percent));
                uploadInfo.setText("numBytes:" + numBytes + " bytes" + "\ntotalBytes:" + totalBytes + " bytes" + "\npercent:" + percent * 100 + " %" + "\nspeed:" + speed * 1000 / 1024 / 1024 + "  MB/秒");

            }

            //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
            @Override
            public void onUIProgressFinish() {
                super.onUIProgressFinish();
                Log.e("TAG", "onUIProgressFinish:");
                Toast.makeText(getApplicationContext(), "结束上传", Toast.LENGTH_SHORT).show();
            }
        });
        builder.post(requestBody);

        Call call = okHttpClient.newCall(builder.build());

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", "=============onFailure===============");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("TAG", "=============onResponse===============");
                Log.e("TAG", "request headers:" + response.request().headers());
                Log.e("TAG", "response headers:" + response.headers());
            }
        });
    }

    private void download() {
        uploadInfo.setText("start download");
        String url = "https://speed.hetzner.de/100MB.bin";

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.get();
        Call call = okHttpClient.newCall(builder.build());

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", "=============onFailure===============");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("TAG", "=============onResponse===============");
                Log.e("TAG", "request headers:" + response.request().headers());
                Log.e("TAG", "response headers:" + response.headers());
                ResponseBody responseBody = ProgressHelper.withProgress(response.body(), new ProgressUIListener() {

                    //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                    @Override
                    public void onUIProgressStart(long totalBytes) {
                        super.onUIProgressStart(totalBytes);
                        Log.e("TAG", "onUIProgressStart:" + totalBytes);
                        Toast.makeText(getApplicationContext(), "开始下载：" + totalBytes, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                        Log.e("TAG", "=============start===============");
                        Log.e("TAG", "numBytes:" + numBytes);
                        Log.e("TAG", "totalBytes:" + totalBytes);
                        Log.e("TAG", "percent:" + percent);
                        Log.e("TAG", "speed:" + speed);
                        Log.e("TAG", "============= end ===============");
                        downloadProgeress.setProgress((int) (100 * percent));
                        downloadInfo.setText("numBytes:" + numBytes + " bytes" + "\ntotalBytes:" + totalBytes + " bytes" + "\npercent:" + percent * 100 + " %" + "\nspeed:" + speed * 1000 / 1024 / 1024 + " MB/秒");
                    }

                    //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                    @Override
                    public void onUIProgressFinish() {
                        super.onUIProgressFinish();
                        Log.e("TAG", "onUIProgressFinish:");
                        Toast.makeText(getApplicationContext(), "结束下载", Toast.LENGTH_SHORT).show();
                    }
                });

                BufferedSource source = responseBody.source();

                File outFile = new File(getFilesDir(), "temp.bin");
                outFile.delete();
                outFile.getParentFile().mkdirs();
                outFile.createNewFile();

                BufferedSink sink = Okio.buffer(Okio.sink(outFile));
                source.readAll(sink);
                sink.flush();
                source.close();
            }
        });

    }


}
