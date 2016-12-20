package pub.xgan.rxdemo.rxJava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import io.reactivex.MaybeObserver;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import pub.xgan.rxdemo.R;
import pub.xgan.rxdemo.utils.LogUtil;

/**
 * Created by Derrick on 2016/12/17.
 * 基本操作符的使用
 */

public class Rx7Activity extends AppCompatActivity {
    public static final String TAG = Rx7Activity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx7);
        setTitle("筛选操作符");
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
     * ofType   filter
     */
    public void base1Demo() {
        //ofType
        Observable.just(1, 2, 3, "A")
                .doOnNext(new Consumer<Serializable>() {
                    @Override
                    public void accept(Serializable serializable) throws Exception {
                        LogUtil.e(TAG, "发送了:   " + serializable);
                    }
                })
                .ofType(Integer.class)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        LogUtil.e(TAG, "经过ofType()筛选:   " + integer);
                        return integer > 1;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        LogUtil.e(TAG, "筛选 >1 之后：  " + integer);
                    }
                });
    }

    /**
     * first(传递默认值)  firstOrError   区别是default和error
     */
    private void base2Demo() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(4);
                int b = 1 / 0;
            }
        })
                //如果出错  这里需要处理
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Integer>>() {
                    @Override
                    public ObservableSource<? extends Integer> apply(Throwable throwable) throws Exception {
                        LogUtil.e(TAG, "+apply:    " + throwable.getMessage());
                        return Observable.just(5);
                    }
                })
                .filter(new Predicate<Serializable>() {
                    @Override
                    public boolean test(Serializable serializable) throws Exception {
                        //这儿添加筛选
                        return false;
                    }
                })
                .first(10086)   //
                .subscribe(new Consumer<Serializable>() {
                    @Override
                    public void accept(Serializable serializable) throws Exception {
                        LogUtil.e(TAG, "accept:   " + serializable);
                    }
                });

        //出错处理同上
        Observable.just(1, 2, 3, 4)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        //这儿添加筛选
                        return true;
                    }
                })
                .firstOrError()
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer value) {
                        LogUtil.e(TAG, "onSuccess:   " + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e(TAG, "onError:   " + e.getMessage());
                    }
                });

        //last最后一个或默认值   出错处理同上
        Observable.just(1, 2, 3, 4)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return false;
                    }
                })
                .last(10085)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        LogUtil.e(TAG, "lastOrDefault:   " + integer);
                    }
                });

        //如果出错  直接在onError中处理   single如果只有一个值  或返回默认值
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(4);
                int b = 1 / 0;
            }
        })
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return false;
                    }
                })
                .single(10086)
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer value) {
                        LogUtil.e(TAG, "----- " + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e(TAG, "onError:   " + e.getMessage());
                    }
                });


    }

    /**
     * skip   skipLast   take  takeLast
     * 下例中：   跳过第一个   跳过最后一个   取前面7个    取后面6个
     */
    private void base3Demo() {
        Observable.range(1, 10)
                .skip(1)//11  不会报错  执行onComplete
                .skipLast(1)
                .take(7)
                .takeLast(6)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        LogUtil.e(TAG, "onNext:    " + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e(TAG, "onError:   " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.e(TAG, "onComplete");
                    }
                });
    }

    /**
     * debounce
     * elementAt
     * ignoreElements
     */
    private void base4Demo() {
        Observable.range(1, 11)
                .flatMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Integer integer) throws Exception {
                        try {
                            Thread.sleep(integer * 200);
                        } catch (Exception e) {
                            LogUtil.e(TAG, e.getMessage());
                        }
                        return Observable.just(integer);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                //1s内  传递最多一个  没有就算了
                .debounce(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        LogUtil.e(TAG, "accept:   " + integer);
                    }
                });


        //elementAt  后面的数据忽略了
        Observable.range(1, 10)
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        LogUtil.e(TAG, "所有数据:    " + integer);
                    }
                })
                .elementAt(4)
                .subscribe(new MaybeObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer value) {
                        LogUtil.e(TAG, "elementAt~onSuccess:   " + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e(TAG, "elementAt~onError:  " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.e(TAG, "elementAt~onComplete");
                    }
                });

        //ignoreElements
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
//                e.onError(new RuntimeException("呵呵"));
            }
        })
                .ignoreElements()
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        LogUtil.e(TAG, "ignoreElements");
                    }
                });
    }
}
