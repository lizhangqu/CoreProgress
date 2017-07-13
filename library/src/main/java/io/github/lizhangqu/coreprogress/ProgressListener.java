package io.github.lizhangqu.coreprogress;

/**
 * 进度回调
 *
 * @author lizhangqu
 * @version V1.0
 * @since 2017-07-12 16:19
 */
public abstract class ProgressListener implements ProgressCallback {
    boolean started;
    long lastRefreshTime = 0L;
    long lastBytesWritten = 0L;
    int minTime = 100;//最小回调时间100ms，避免频繁回调

    /**
     * 进度发生了改变，如果numBytes，totalBytes，percent都为-1，则表示总大小获取不到
     *
     * @param numBytes   已读/写大小
     * @param totalBytes 总大小
     * @param percent    百分比
     */
    public final void onProgressChanged(long numBytes, long totalBytes, float percent) {
        if (!started) {
            onProgressStart(totalBytes);
            started = true;
        }
        if (numBytes == -1 && totalBytes == -1 && percent == -1) {
            onProgressChanged(-1, -1, -1, -1);
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRefreshTime >= minTime || numBytes == totalBytes || percent >= 1F) {
            long intervalTime = (currentTime - lastRefreshTime);
            if (intervalTime == 0) {
                intervalTime += 1;
            }
            long updateBytes = numBytes - lastBytesWritten;
            final long networkSpeed = updateBytes / intervalTime;
            onProgressChanged(numBytes, totalBytes, percent, networkSpeed);
            lastRefreshTime = System.currentTimeMillis();
            lastBytesWritten = numBytes;
        }
        if (numBytes == totalBytes || percent >= 1F) {
            onProgressFinish();
        }
    }

    /**
     * 进度发生了改变，如果numBytes，totalBytes，percent，speed都为-1，则表示总大小获取不到
     *
     * @param numBytes   已读/写大小
     * @param totalBytes 总大小
     * @param percent    百分比
     * @param speed      速度 bytes/ms
     */
    public abstract void onProgressChanged(long numBytes, long totalBytes, float percent, float speed);

    /**
     * 进度开始
     *
     * @param totalBytes 总大小
     */
    public void onProgressStart(long totalBytes) {

    }

    /**
     * 进度结束
     */
    public void onProgressFinish() {

    }
}
