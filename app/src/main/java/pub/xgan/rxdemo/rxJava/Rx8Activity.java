package pub.xgan.rxdemo.rxJava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import pub.xgan.rxdemo.R;
import pub.xgan.rxdemo.utils.LogUtil;

/**
 * Created by Derrick on 2016/12/17.
 * 基本操作符的使用
 */

public class Rx8Activity extends AppCompatActivity {
    public static final String TAG = Rx8Activity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx8);
        setTitle("合并操作符");
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
     * 没有顺序呢 you can you up
     * merge:   这个有错误  立马终止
     * mergeDelayError：  有错误 最后终止  不会终止另外一个
     */
    public void base1Demo() {
        Observable<Integer> ob1 = Observable.range(1, 9)
                .subscribeOn(Schedulers.newThread())
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        Thread.sleep(10);
                        return integer;
                    }
                });
        Observable<Integer> ob2 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Thread.sleep(15);
                e.onNext(11);
                Thread.sleep(15);
                e.onNext(12);
                Thread.sleep(15);
                e.onNext(13);
                int b = 1 / 0;
                Thread.sleep(15);
                e.onNext(14);

            }
        })
                .subscribeOn(Schedulers.newThread());


        Observable.mergeDelayError(ob1, ob2)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        LogUtil.e(TAG, "onNext:  " + value);
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
     * contact   一个弄完了  再弄第二个  顺序不能乱
     */
    private void base2Demo() {
        Observable o1 = Observable.range(1, 9)
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        Thread.sleep(15);
                        LogUtil.e(TAG, "map~apply:  " + integer);
                        return integer;
                    }
                })
                .subscribeOn(Schedulers.newThread());

        Observable o2 = Observable.range(10, 9)
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        Thread.sleep(10);
                        LogUtil.e(TAG, "map~apply:  " + integer);
                        return integer;
                    }
                })
                .subscribeOn(Schedulers.newThread());

        Observable.concat(o1, o2)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        LogUtil.e(TAG, "contact: onNext:  " + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e(TAG, "contact: onError:   " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.e(TAG, "contact:onComplete");
                    }
                });


    }

    /**
     * startWith
     */
    private void base3Demo() {
        Observable.range(3, 4)
                .startWith(Arrays.asList(1, 2))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        LogUtil.e(TAG, "startWith~accept:  " + integer);
                    }
                });
    }

}
