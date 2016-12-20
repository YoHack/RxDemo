package pub.xgan.rxdemo.rxJava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import pub.xgan.rxdemo.R;
import pub.xgan.rxdemo.utils.LogUtil;

import static io.reactivex.Observable.create;

/**
 * Created by Derrick on 2016/12/17.
 * 线程的基本切换
 */

public class Rx2Activity extends AppCompatActivity {
    public static final String TAG = Rx2Activity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx2);
        setTitle("线程简单切换");
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
        }
    }


    public void base1Demo() {
        //   如果没有指定   观察者和被观察者在同一个线程中
        create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                LogUtil.eM(TAG, "emmit:   " + 1);
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(4);
                e.onComplete();
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                LogUtil.eM(TAG, "onSubscribe");
            }

            @Override
            public void onNext(Integer value) {
                LogUtil.eM(TAG, "onNext:   " + value);
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.eM(TAG, "onError:   " + e.getMessage());
            }

            @Override
            public void onComplete() {
                LogUtil.eM(TAG, "onComplete");
            }
        });
    }

    private void base2Demo() {
        //切换线程
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                LogUtil.eM(TAG, "ommit:  " + 1);
                e.onNext(1);
                LogUtil.eM(TAG, "ommit:  " + 2);
                e.onNext(2);
            }
        });

        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer o) throws Exception {
                LogUtil.eM(TAG, "accept: " + o);
            }
        };

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);

    }

    private void base3Demo() {
        //切换线程  灵活变化  我相信你
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                LogUtil.eM(TAG, "ommit:  " + 1);
                e.onNext(1);
                LogUtil.eM(TAG, "ommit:  " + 2);
                e.onNext(2);
            }
        });

        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer o) throws Exception {
                LogUtil.eM(TAG, "End accept: " + o);
            }
        };

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        LogUtil.eM(TAG, "1  accept: " + integer);
                    }
                })
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        LogUtil.eM(TAG, "2  accept: " + integer);
                    }
                })
                .subscribe(consumer);
    }
}
