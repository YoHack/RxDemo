package pub.xgan.rxdemo.rxJava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import pub.xgan.rxdemo.R;
import pub.xgan.rxdemo.utils.LogUtil;

/**
 * Created by Derrick on 2016/12/17.
 * 包含了基本的使用  基本的订阅以及逻辑
 */

public class Rx1Activity extends AppCompatActivity {
    public static final String TAG = Rx1Activity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx1);
        setTitle("简单的订阅");
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
            case R.id.btn_3: {
                base3Demo();
                break;
            }
            case R.id.btn_4: {
                base4Demo();
                break;
            }
        }
    }


    public void base1Demo() {
        //创建被观察者
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        });

        //创建观察者
        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                LogUtil.e(TAG, "onSubscribe:  " + d.toString());
            }

            @Override
            public void onNext(Integer value) {
                LogUtil.e(TAG, "onNext:  " + value);
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(TAG, "onError: ");
            }

            @Override
            public void onComplete() {
                LogUtil.e(TAG, "onComplete");
            }
        };

        //订阅
        observable.subscribe(observer);
    }

    private void base2Demo() {
        //流式调用
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
//                throw new RuntimeException("he");  有这一步就boom了   onComplete和onError只会调用一次
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                LogUtil.e(TAG, "onSubscribe:  " + d.toString());
            }

            @Override
            public void onNext(Integer value) {
                LogUtil.e(TAG, "onNext:  " + value);
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                LogUtil.e(TAG, "onComplete");
            }
        });

    }

    private void base3Demo() {
        //查看先后顺序
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                LogUtil.e(TAG, "emmit:  " + 1);
                e.onNext(1);
                LogUtil.e(TAG, "emmit:  " + 2);
                e.onNext(2);
                LogUtil.e(TAG, "emmit:  " + 3);
                e.onNext(3);
                LogUtil.e(TAG, "Complete ");
                e.onComplete();
                //这儿之后就不会观察到
                LogUtil.e(TAG, "emmit:  " + 4);
                e.onNext(4);
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                LogUtil.e(TAG, "onSubscribe:  " + d.toString());
            }

            @Override
            public void onNext(Integer value) {
                LogUtil.e(TAG, "onNext:  " + value);
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                LogUtil.e(TAG, "onComplete");
            }
        });
    }

    private void base4Demo() {
        //只关注onNext   有异常就boom   onComplete假装没看到  还是可以收到onNext
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                LogUtil.e(TAG, "emmit:  " + 1);
                e.onNext(1);
                LogUtil.e(TAG, "emmit:  " + 2);
                e.onNext(2);
                LogUtil.e(TAG, "emmit:  " + 3);
                e.onNext(3);
                LogUtil.e(TAG, "Send  Complete ");
                e.onComplete();
                //
                LogUtil.e(TAG, "emmit:  " + 4);
                e.onNext(4);
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                LogUtil.e(TAG, "accept:    " + integer);
            }
        });

    }
}
