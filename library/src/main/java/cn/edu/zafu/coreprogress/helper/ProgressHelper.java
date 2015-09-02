package cn.edu.zafu.coreprogress.helper;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

import cn.edu.zafu.coreprogress.listener.ProgressRequestListener;
import cn.edu.zafu.coreprogress.listener.ProgressResponseListener;
import cn.edu.zafu.coreprogress.progress.ProgressRequestBody;
import cn.edu.zafu.coreprogress.progress.ProgressResponseBody;

/**
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-09-02
 * Time: 17:33
 */
public class ProgressHelper {
    public static OkHttpClient addProgressResponseListener(OkHttpClient client,final ProgressResponseListener progressListener){
        OkHttpClient clone = client.clone();
        clone.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();
            }
        });
        return clone;
    }

    public static ProgressRequestBody addProgressRequestListener(RequestBody requestBody,ProgressRequestListener progressRequestListener){
        return new ProgressRequestBody(requestBody,progressRequestListener);
    }
}
