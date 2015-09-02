package cn.edu.zafu.coreprogress.progress;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import cn.edu.zafu.coreprogress.listener.ProgressResponseListener;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-09-02
 * Time: 17:18
 */
public class ProgressResponseBody extends ResponseBody {

    private final ResponseBody responseBody;
    private final ProgressResponseListener progressListener;
    private BufferedSource bufferedSource;


    public ProgressResponseBody(ResponseBody responseBody, ProgressResponseListener progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
    }


    @Override public MediaType contentType() {
        return responseBody.contentType();
    }


    @Override public long contentLength() throws IOException {
        return responseBody.contentLength();
    }


    @Override public BufferedSource source() throws IOException {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }


    private Source source(Source source) {

        return new ForwardingSource(source) {
            long totalBytesRead = 0L;
            @Override public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                //if not know the contentLength,the contentLength() will return -1,but the bytesRead only return true when the data has been read
                progressListener.onResponseProgress(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                return bytesRead;
            }
        };
    }
}