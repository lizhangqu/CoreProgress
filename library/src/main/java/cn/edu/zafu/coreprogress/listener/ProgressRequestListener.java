package cn.edu.zafu.coreprogress.listener;

/**
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-09-02
 * Time: 17:16
 */
public interface ProgressRequestListener {
    void onRequestProgress(long bytesWritten, long contentLength, boolean done);
}
