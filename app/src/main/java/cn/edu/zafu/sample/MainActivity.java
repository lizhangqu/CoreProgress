package cn.edu.zafu.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;

import cn.edu.zafu.coreprogress.helper.ProgressHelper;
import cn.edu.zafu.coreprogress.listener.ProgressRequestListener;
import cn.edu.zafu.coreprogress.listener.ProgressResponseListener;
import cn.edu.zafu.coreprogress.listener.impl.UIProgressResponseListener;

public class MainActivity extends AppCompatActivity {
    private static final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File file = new File("/data/app/ApiDemos.apk");
        final ProgressRequestListener progressListener = new ProgressRequestListener() {
            @Override
            public void onRequestProgress(long bytesWrite, long contentLength, boolean done) {
                Log.e("TAG", bytesWrite + "");
                Log.e("TAG", contentLength + "");
                Log.e("TAG", "%d%% done " + (100 * bytesWrite) / contentLength);
                Log.e("TAG", done + "");
                Log.e("TAG", "\n");
            }
        };

        RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                .addFormDataPart("her", "区嫂")
                .addFormDataPart("photo", file.getName(), RequestBody.create(null, file))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"another\";filename=\"another.dex\""), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();
        final Request request = new Request.Builder().url("http://192.168.142.1/test/result.php").post(ProgressHelper.addProgressRequestListener(requestBody, progressListener)).build();

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        final ProgressResponseListener progressListener1 = new ProgressResponseListener() {
            @Override
            public void onResponseProgress(long bytesRead, long contentLength, boolean done) {
                Log.e("TAG", "bytesRead:" + bytesRead);
                Log.e("TAG", "contentLength:" + contentLength);
                Log.e("TAG", "done:" + done);
                if (contentLength != -1) {
                    //长度未知的情况下回返回-1
                    Log.e("TAG", (100 * bytesRead) / contentLength + "% done");
                }
                Log.e("TAG", "\n");
            }
        };

        final UIProgressResponseListener uiProgressResponseListener=new UIProgressResponseListener() {
            @Override
            public void onUIResponseProgress(long bytesRead, long contentLength, boolean done) {
                Toast.makeText(getApplicationContext(),bytesRead + " "+contentLength +" "+done,Toast.LENGTH_LONG).show();
            }
        };

        final Request request1 = new Request.Builder()
                .url("http://img.blog.csdn.net/20150826100351823")
                .build();

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


}
