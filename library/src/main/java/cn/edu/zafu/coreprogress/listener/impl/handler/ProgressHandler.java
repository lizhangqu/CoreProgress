package cn.edu.zafu.coreprogress.listener.impl.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener;
import cn.edu.zafu.coreprogress.listener.impl.model.ProgressModel;

/**
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-10-02
 * Time: 15:25
 */
public abstract class ProgressHandler extends Handler {
    public static final int UPDATE = 0x01;
    public static final int START = 0x02;
    public static final int FINISH = 0x03;
    //弱引用
    private final WeakReference<UIProgressListener> mUIProgressListenerWeakReference;

    public ProgressHandler(UIProgressListener uiProgressListener) {
        super(Looper.getMainLooper());
        mUIProgressListenerWeakReference = new WeakReference<UIProgressListener>(uiProgressListener);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case UPDATE: {
                UIProgressListener uiProgessListener = mUIProgressListenerWeakReference.get();
                if (uiProgessListener != null) {
                    //获得进度实体类
                    ProgressModel progressModel = (ProgressModel) msg.obj;
                    //回调抽象方法
                    progress(uiProgessListener, progressModel.getCurrentBytes(), progressModel.getContentLength(), progressModel.isDone());
                }
                break;
            }
            case START: {
                UIProgressListener uiProgressListener = mUIProgressListenerWeakReference.get();
                if (uiProgressListener != null) {
                    //获得进度实体类
                    ProgressModel progressModel = (ProgressModel) msg.obj;
                    //回调抽象方法
                    start(uiProgressListener, progressModel.getCurrentBytes(), progressModel.getContentLength(), progressModel.isDone());

                }
                break;
            }
            case FINISH: {
                UIProgressListener uiProgressListener = mUIProgressListenerWeakReference.get();
                if (uiProgressListener != null) {
                    //获得进度实体类
                    ProgressModel progressModel = (ProgressModel) msg.obj;
                    //回调抽象方法
                    finish(uiProgressListener, progressModel.getCurrentBytes(), progressModel.getContentLength(), progressModel.isDone());
                }
                break;
            }
            default:
                super.handleMessage(msg);
                break;
        }
    }

    public abstract void start(UIProgressListener uiProgressListener,long currentBytes, long contentLength, boolean done);
    public abstract void progress(UIProgressListener uiProgressListener,long currentBytes, long contentLength, boolean done);
    public abstract void finish(UIProgressListener uiProgressListener,long currentBytes, long contentLength, boolean done);
}
