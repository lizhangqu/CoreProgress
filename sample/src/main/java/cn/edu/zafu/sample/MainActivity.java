package cn.edu.zafu.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.edu.zafu.coreprogress.helper.ProgressHelper;
import cn.edu.zafu.coreprogress.listener.ProgressListener;
import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener;

public class MainActivity extends AppCompatActivity {
    private static final OkHttpClient client = new OkHttpClient.Builder()
            //设置超时，不设置可能会报异常
            .connectTimeout(1000, TimeUnit.MINUTES)
            .readTimeout(1000, TimeUnit.MINUTES)
            .writeTimeout(1000, TimeUnit.MINUTES)
            .build();
    private Button upload,download;
    private ProgressBar uploadProgress,downloadProgeress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        uploadProgress= (ProgressBar) findViewById(R.id.upload_progress);
        downloadProgeress= (ProgressBar) findViewById(R.id.download_progress);
        upload= (Button) findViewById(R.id.upload);
        download= (Button) findViewById(R.id.download);
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

    private void download() {
        //这个是非ui线程回调，不可直接操作UI
        final ProgressListener progressResponseListener = new ProgressListener() {
            @Override
            public void onProgress(long bytesRead, long contentLength, boolean done) {
                Log.e("TAG", "bytesRead:" + bytesRead);
                Log.e("TAG", "contentLength:" + contentLength);
                Log.e("TAG", "done:" + done);
                if (contentLength != -1) {
                    //长度未知的情况下回返回-1
                    Log.e("TAG", (100 * bytesRead) / contentLength + "% done");
                }
                Log.e("TAG", "================================");
            }
        };


        //这个是ui线程回调，可直接操作UI
        final UIProgressListener uiProgressResponseListener = new UIProgressListener() {
            @Override
            public void onUIProgress(long bytesRead, long contentLength, boolean done) {
                Log.e("TAG", "bytesRead:" + bytesRead);
                Log.e("TAG", "contentLength:" + contentLength);
                Log.e("TAG", "done:" + done);
                if (contentLength != -1) {
                    //长度未知的情况下回返回-1
                    Log.e("TAG", (100 * bytesRead) / contentLength + "% done");
                }
                Log.e("TAG", "================================");
                //ui层回调
                downloadProgeress.setProgress((int) ((100 * bytesRead) / contentLength));
                //Toast.makeText(getApplicationContext(), bytesRead + " " + contentLength + " " + done, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onUIStart(long bytesRead, long contentLength, boolean done) {
                super.onUIStart(bytesRead, contentLength, done);
                Toast.makeText(getApplicationContext(),"start",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUIFinish(long bytesRead, long contentLength, boolean done) {
                super.onUIFinish(bytesRead, contentLength, done);
                Toast.makeText(getApplicationContext(),"end",Toast.LENGTH_SHORT).show();
            }
        };

        //构造请求
        final Request request1 = new Request.Builder()
                .url("http://121.41.119.107:81/test/1.doc")
                .build();

        //包装Response使其支持进度回调
        ProgressHelper.addProgressResponseListener(client, uiProgressResponseListener).newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("TAG", "error ", e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Log.e("TAG", response.body().string());
            }
        });
    }

    private void upload() {
        File file = new File("/sdcard/1.doc");
        //此文件必须在手机上存在，实际情况下请自行修改，这个目录下的文件只是在我手机中存在。


        //这个是非ui线程回调，不可直接操作UI
        final ProgressListener progressListener = new ProgressListener() {
            @Override
            public void onProgress(long bytesWrite, long contentLength, boolean done) {
                Log.e("TAG", "bytesWrite:" + bytesWrite);
                Log.e("TAG", "contentLength" + contentLength);
                Log.e("TAG", (100 * bytesWrite) / contentLength + " % done ");
                Log.e("TAG", "done:" + done);
                Log.e("TAG", "================================");
            }
        };


        //这个是ui线程回调，可直接操作UI
        final UIProgressListener uiProgressRequestListener = new UIProgressListener() {
            @Override
            public void onUIProgress(long bytesWrite, long contentLength, boolean done) {
                Log.e("TAG", "bytesWrite:" + bytesWrite);
                Log.e("TAG", "contentLength" + contentLength);
                Log.e("TAG", (100 * bytesWrite) / contentLength + " % done ");
                Log.e("TAG", "done:" + done);
                Log.e("TAG", "================================");
                //ui层回调
                uploadProgress.setProgress((int) ((100 * bytesWrite) / contentLength));
                //Toast.makeText(getApplicationContext(), bytesWrite + " " + contentLength + " " + done, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onUIStart(long bytesWrite, long contentLength, boolean done) {
                super.onUIStart(bytesWrite, contentLength, done);
                Toast.makeText(getApplicationContext(),"start",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUIFinish(long bytesWrite, long contentLength, boolean done) {
                super.onUIFinish(bytesWrite, contentLength, done);
                Toast.makeText(getApplicationContext(),"end",Toast.LENGTH_SHORT).show();
            }
        };

        //构造上传请求，类似web表单
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("hello", "android")
                .addFormDataPart("photo", file.getName(), RequestBody.create(null, file))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"another\";filename=\"another.dex\""), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();

        //进行包装，使其支持进度回调
        final Request request = new Request.Builder().url("http://121.41.119.107:81/test/result.php").post(ProgressHelper.addProgressRequestListener(requestBody, uiProgressRequestListener)).build();
        //开始请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("TAG", "error ", e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Log.e("TAG", response.body().string());
            }
        });

    }
}
