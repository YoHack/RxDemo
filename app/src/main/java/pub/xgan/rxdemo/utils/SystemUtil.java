package pub.xgan.rxdemo.utils;

import android.os.Looper;

/**
 * Created by Derrick on 2016/12/17.
 */

public class SystemUtil {

    /**
     * 是否是主线程
     * @return
     */
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
