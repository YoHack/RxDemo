package pub.xgan.rxdemo.rxJava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import pub.xgan.rxdemo.R;
import pub.xgan.rxdemo.utils.LogUtil;

/**
 * Created by Derrick on 2016/12/17.
 * 基本数据流转换
 */

public class Rx3Activity extends AppCompatActivity {
    public static final String TAG = Rx3Activity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx2);
        setTitle("变换");
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
        //   map
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                LogUtil.e(TAG, "onNext   " + 1);
                e.onNext(1);
                LogUtil.e(TAG, "onNext   " + 2);
                e.onNext(2);
                LogUtil.e(TAG, "onNext   " + 3);
                e.onNext(3);
                LogUtil.e(TAG, "onNext   " + 4);
                e.onNext(4);
                e.onComplete();
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                LogUtil.e(TAG, "map~~apply:   " + integer);
                return String.valueOf(integer);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                LogUtil.e(TAG, "accept: " + s);
            }
        });
    }

    private void base2Demo() {
        //  flatMap  并不能保证顺序   如果需要保证顺序使用concatMap
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                LogUtil.e(TAG, "onNext   " + 1);
                e.onNext(1);
                LogUtil.e(TAG, "onNext   " + 2);
                e.onNext(2);
                LogUtil.e(TAG, "onNext   " + 3);
                e.onNext(3);
                LogUtil.e(TAG, "onNext   " + 4);
                e.onNext(4);
                e.onComplete();
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                List<String> temp = new ArrayList<String>(integer);
                for (int i = 0; i < integer; i++) {
                    temp.add(integer + "    " + i);
                }
                LogUtil.e(TAG, "flatMap:   " + integer);
                return Observable.fromIterable(temp).delay(10, TimeUnit.MILLISECONDS);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                LogUtil.e(TAG, "accept:    " + s);
            }
        });

    }

    private void base3Demo() {
        //切换线程  灵活变化  我相信你
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                //模拟现在是登陆
                LogUtil.eM(TAG, "现在注册中....1s");
                Thread.sleep(1200);
                e.onNext(1);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        LogUtil.eM(TAG, "主线程中刚一波");
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer integer) throws Exception {
                        LogUtil.eM(TAG, "现在登陆中");
                        Thread.sleep(1500);
                        return Observable.just(String.valueOf(integer));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        LogUtil.eM(TAG, "好了  搞完了");
                    }
                });
    }
}
