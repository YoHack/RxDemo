package pub.xgan.rxdemo.rxJava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observables.GroupedObservable;
import pub.xgan.rxdemo.R;
import pub.xgan.rxdemo.utils.LogUtil;

/**
 * Created by Derrick on 2016/12/17.
 * 基本操作符的使用
 */

public class Rx6Activity extends AppCompatActivity {
    public static final String TAG = Rx6Activity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx6);
        setTitle("操作符");
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

    /**
     * just from create 就不举例了吧
     * Empty:  不发射任何数据  但是正常终止的Observable
     * Never:   既不发送也不终止
     * Error:   错误结尾
     */
    public void base1Demo() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                LogUtil.e(TAG, "-----" + 1);
                e.onNext(1);
                int b = 4 / 0;
                e.onNext(2);
            }
        })
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Integer>>() {
                    @Override
                    public ObservableSource<? extends Integer> apply(Throwable throwable) throws Exception {
                        LogUtil.e(TAG, throwable.getClass().getName());
                        //分别测试不同结尾
                        return Observable.never();
//                        return Observable.empty();
//                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Integer value) {
                        LogUtil.e(TAG, "onNext" + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e(TAG, "onError:  " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.e(TAG, "onComplete:   ");
                    }
                });
    }

    /**
     * doOnSubscribe调用  注意两个subscribeOn
     * range()  timer() internal()  repeat()  defer()
     */
    private void base2Demo() {
        Observable.range(0, 1).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                LogUtil.e(TAG, "range(0, 1):    " + integer);
            }
        });

        //interval  无限发
        final Disposable s = Observable.interval(0, 1, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                LogUtil.e(TAG, "interval:   " + aLong);
            }
        });

        //timer计时   然后终止无限发
        Observable.timer(10, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                LogUtil.e(TAG, "use timer to stop interval");
                s.dispose();
            }
        });

        //repeat重复咯
        Observable.just("我是谁， 山东大李逵")
                .repeat(14)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        LogUtil.e(TAG, "repeat:  " + s);
                    }
                });

        //defer在订阅的时候才创建observable
        Observable<Integer> observable = Observable.defer(new Callable<ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> call() throws Exception {
                return Observable.just(temp);
            }
        });
        //记住中间改了一次值
        temp = 2;
        observable.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                LogUtil.e(TAG, "defer:    " + integer);
            }
        });


    }

    private int temp = 1;


    /**
     * cast()强转   map()  flatMap()  concatMap()  不想写
     * groupBy scan
     */
    private void base3Demo() {
        //groupBy   可以分组的哟  亲
        Observable.range(1, 10).groupBy(new Function<Integer, Long>() {
            @Override
            public Long apply(Integer integer) throws Exception {
                return (long) integer % 3;
            }
        }).subscribe(new Observer<GroupedObservable<Long, Integer>>() {
            @Override
            public void onSubscribe(Disposable d) {
                LogUtil.e(TAG, "onSubscribe:   " + d);
            }

            @Override
            public void onNext(GroupedObservable<Long, Integer> value) {
                final String flag = "GroupBy~~~~" + value.getKey();
                LogUtil.e(TAG, flag);
                value.toList().subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        LogUtil.e(TAG, flag + ":   " + integers.toString());
                    }
                });

//                value.subscribe(new Observer<Integer>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(Integer value) {
//                        LogUtil.e(TAG, flag + "B:   " + value);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


        //scan   就是加加加
        Observable.range(1, 6).scan(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                LogUtil.e(TAG, "integer:   " + integer + "   integer2:   " + integer2);
                return integer + integer2;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                LogUtil.e(TAG, "accept:   " + integer);
            }
        });

    }

    /**
     * buffer  window
     */
    private void base4Demo() {
        //buffer  有bug立马发bug   buffer中数据丢弃  subscribe订阅处理
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                LogUtil.e(TAG, "1");
                e.onNext(1);
                LogUtil.e(TAG, "2");
                e.onNext(2);
                LogUtil.e(TAG, "3");
                e.onNext(3);
                LogUtil.e(TAG, "4");
                e.onNext(4);
                LogUtil.e(TAG, "5");
                e.onNext(5);
                LogUtil.e(TAG, "即将有bug");
                int b = 1 / 0;
                LogUtil.e(TAG, "6");
                e.onNext(6);
            }
        }).buffer(2)
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Integer> value) {
                        LogUtil.e(TAG, value.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        LogUtil.e(TAG, "---------------------------------------");

        //window  子集形式   error会传递
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                LogUtil.e(TAG, "1");
                e.onNext(1);
                LogUtil.e(TAG, "2");
                e.onNext(2);
                LogUtil.e(TAG, "3");
                e.onNext(3);
                LogUtil.e(TAG, "4");
                e.onNext(4);
                LogUtil.e(TAG, "5");
                e.onNext(5);
                LogUtil.e(TAG, "即将有bug000");
                int b = 1 / 0;
                LogUtil.e(TAG, "6");
                e.onNext(6);
            }
        }).window(2)
                .subscribe(new Observer<Observable<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtil.e(TAG, "onSubscribe:  " + d);
                    }

                    @Override
                    public void onNext(Observable<Integer> value) {
                        LogUtil.e(TAG, "onNext:   " + value);
                        value.subscribe(new Observer<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                LogUtil.e(TAG, "--OnSubscribe: " + d);
                            }

                            @Override
                            public void onNext(Integer value) {
                                LogUtil.e(TAG, "--onNext:   " + value);
                            }

                            @Override
                            public void onError(Throwable e) {
                                LogUtil.e(TAG, "--onError:   " + e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                                LogUtil.e(TAG, "--onComplete");
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e(TAG, "onError" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.e(TAG, "onComplete");
                    }
                });
    }
}
