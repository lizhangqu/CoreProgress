package cn.edu.zafu.coreprogress;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

public class MainActivity extends AppCompatActivity {
    private static final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File file = new File("/sdcard/111");
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
        Request request = new Request.Builder().url("http://10.0.0.24/test/result.php").post(ProgressHelper.addProgressRequestListener(requestBody, progressListener)).build();

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
        final ProgressResponseListener progressListener1 = new ProgressResponseListener() {
            @Override
            public void onResponseProgress(long bytesRead, long contentLength, boolean done) {
                System.out.println(bytesRead);
                System.out.println(contentLength);
                System.out.println(done);
                System.out.format("%d%% done\n", (100 * bytesRead) / contentLength);

                System.out.println();
            }
        };

        final Request request1 = new Request.Builder()
                .url("http://127.0.0.1/test/1.iso")
                .build();


        new Thread(new Runnable() {
            @Override
            public void run() {
                Response execute = null;
                try {
                    execute = ProgressHelper.addProgressResponseListener(client, progressListener1).newCall(request1).execute();
                    System.out.println(execute.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }


}
