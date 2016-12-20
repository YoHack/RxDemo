package pub.xgan.rxdemo.utils;

import android.util.Log;

/**
 * Created by Derrick on 2016/12/17.
 */

public class LogUtil {
    public static void e(String TAG, String msg) {
        Log.e(TAG, msg);
    }

    public static void eM(String TAG, String msg) {
        Log.e(TAG, "isMain:   " + SystemUtil.isMainThread() + "   Name:   " + Thread.currentThread().getName() + "   msg:  " + msg);
    }
}
