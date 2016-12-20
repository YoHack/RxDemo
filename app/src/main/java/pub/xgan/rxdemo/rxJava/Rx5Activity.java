package pub.xgan.rxdemo.rxJava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import pub.xgan.rxdemo.R;
import pub.xgan.rxdemo.utils.LogUtil;

/**
 * Created by Derrick on 2016/12/17.
 * 观察者比较慢
 */

public class Rx5Activity extends AppCompatActivity {
    public static final String TAG = Rx5Activity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx1);
        setTitle("OOM测试");
    }

    public void click(View view) {
        switch (view.getId()) {
            case R.id.btn_1: {
                base1Demo();
                break;
            }
            case R.id.btn_2: {
                base2Demo();
                break;
            }
        }
    }


    public void base1Demo() {
        //在一个线程中   无限发送和接受
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; ; i++) {
                    LogUtil.e(TAG, "onNext:   " + i);
                    e.onNext(i);
                }
            }
        }).subscribeOn(Schedulers.io())     //都放在子线程之中
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        LogUtil.e(TAG, "accept:    " + integer);
                        Thread.sleep(2000);
                    }
                });
    }

    private void base2Demo() {
        //不在一个线程    产生的被观察者将会放在一个队列中   但是队列是有限的...这样会OOM的
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                for (int i = 0; ; i++) {
                    e.onNext(String.valueOf(i));
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String str) throws Exception {
                        LogUtil.e(TAG, "accept:    " + str);
                        Thread.sleep(5000);
                    }
                });

    }

}
