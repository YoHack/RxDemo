package pub.xgan.rxdemo.rxJava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;
import pub.xgan.rxdemo.R;
import pub.xgan.rxdemo.utils.LogUtil;

/**
 * Created by Derrick on 2016/12/17.
 * 包含了基本的使用  基本的订阅以及逻辑
 */

public class Rx4Activity extends AppCompatActivity {
    public static final String TAG = Rx4Activity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx4);
        setTitle("Zip目前说");
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


    /**
     * 由于是在一个线程中  可以看到zip是等待前面一个执行完后执行   如果前面exception   后面就跪了  直接调用最后的onError，   按最少那个
     */
    public void base1Demo() {

        // zip  专业产生integer三十年
        Observable<Integer> observableInt = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                LogUtil.e(TAG, "int~emit:   " + 1);
                e.onNext(1);
                Thread.sleep(111);
                LogUtil.e(TAG, "int~emit:   " + 2);
                e.onNext(2);
                Thread.sleep(111);
                LogUtil.e(TAG, "int~emit:   " + 3);
                e.onNext(3);
                Thread.sleep(111);
                LogUtil.e(TAG, "int~emit:   " + 4);
                e.onNext(4);
//                throw new RuntimeException("hehe");   这儿放exception后面就跪了
            }
        });

        Observable<String> observableStr = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                LogUtil.e(TAG, "String~emit:   " + "A");
                e.onNext("A");
                Thread.sleep(111);
                LogUtil.e(TAG, "String~emit:   " + "B");
                e.onNext("B");
                Thread.sleep(111);
                LogUtil.e(TAG, "String~emit:   " + "C");
                e.onNext("C");
                Thread.sleep(111);
                e.onComplete();
            }
        });

        Observable.zip(observableInt, observableStr, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                return s + "   " + integer;
            }
        })
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtil.e(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(String value) {
                        LogUtil.e(TAG, value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.e(TAG, "onComplete");
                    }
                });


    }

    /**
     * 和1不同的是线程
     */
    private void base2Demo() {

        // zip  专业产生integer三十年
        Observable<Integer> observableInt = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                LogUtil.eM(TAG, "int~emit:   " + 1);
                e.onNext(1);
                Thread.sleep(111);
                LogUtil.eM(TAG, "int~emit:   " + 2);
                e.onNext(2);
                Thread.sleep(111);
                LogUtil.eM(TAG, "int~emit:   " + 3);
                e.onNext(3);
//                try {
//                    Thread.sleep(10);
//                } catch (Exception o) {
//                    o.printStackTrace();
//                }
                Thread.sleep(10);//这儿会被报错  我都不知道为什么  下面的str调用了onComplete 这儿会bug
                LogUtil.eM(TAG, "int~emit:   " + 4);
                e.onNext(4);
            }
        }).subscribeOn(Schedulers.io());

        Observable<String> observableStr = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                LogUtil.eM(TAG, "String~emit:   " + "A");
                e.onNext("A");
                Thread.sleep(111);
                LogUtil.eM(TAG, "String~emit:   " + "B");
                e.onNext("B");
                Thread.sleep(111);
                LogUtil.eM(TAG, "String~emit:   " + "C");
                e.onNext("C");
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());


        Observable.zip(observableInt, observableStr, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                return s + "   " + integer;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                LogUtil.e(TAG, "onSubscribe");
            }

            @Override
            public void onNext(String value) {
                LogUtil.e(TAG, value);
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(TAG, e.getMessage());
            }

            @Override
            public void onComplete() {
                LogUtil.e(TAG, "onComplete");
            }
        });

    }

    private void base3Demo() {
        // zip  专业产生integer三十年
        Observable<Integer> observableInt = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                LogUtil.e(TAG, "int~emit:   " + 1);
                e.onNext(1);
                Thread.sleep(111);
                LogUtil.e(TAG, "int~emit:   " + 2);
                e.onNext(2);
                Thread.sleep(111);
                LogUtil.e(TAG, "int~emit:   " + 3);
                e.onNext(3);
                Thread.sleep(111);
                LogUtil.e(TAG, "int~emit:   " + 4);
                e.onNext(4);
//                throw new RuntimeException("hehe");   这儿放exception后面就跪了
            }
        });

        Observable<String> observableStr = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                LogUtil.e(TAG, "String~emit:   " + "A");
                e.onNext("A");
                Thread.sleep(111);
                LogUtil.e(TAG, "String~emit:   " + "B");
                e.onNext("B");
                Thread.sleep(111);
                LogUtil.e(TAG, "String~emit:   " + "C");
                e.onNext("C");
                Thread.sleep(111);
//                e.onComplete();
            }
        });
        Observable.zip(observableInt, Observable.error(new RuntimeException("呵呵")).onErrorResumeNext(new Function<Throwable, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                LogUtil.e(TAG, "apply   " + throwable.getMessage());
                return Observable.just(throwable.getMessage());
            }
        }), observableStr, new Function3<Integer, Object, String, Object>() {
            @Override
            public Object apply(Integer integer, Object o, String s) throws Exception {
                return integer + s + o.toString();
            }
        }).subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
                LogUtil.e(TAG, "onSubscribe");
            }

            @Override
            public void onNext(Object value) {
                LogUtil.e(TAG, "onNext:   " + value.toString());
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(TAG, "onError：   " + e.getMessage());
            }

            @Override
            public void onComplete() {
                LogUtil.e(TAG, "onComplete");
            }
        });


    }
}
