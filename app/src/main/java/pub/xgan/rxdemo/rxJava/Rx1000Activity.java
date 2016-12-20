package pub.xgan.rxdemo.rxJava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import pub.xgan.rxdemo.R;
import pub.xgan.rxdemo.utils.LogUtil;

/**
 * Created by Derrick on 2016/12/17.
 * 包含了基本的使用  基本的订阅以及逻辑
 */

public class Rx1000Activity extends AppCompatActivity {
    public static final String TAG = Rx1000Activity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx1000);
        setTitle("Some Etc");
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
     * compose etc
     */
    public void base1Demo() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                LogUtil.e(TAG, "-----" + 1);
                e.onNext(1);
                LogUtil.e(TAG, "-----" + 2);
                e.onNext(2);
                LogUtil.e(TAG, "-----" + 3);
                e.onNext(3);
                LogUtil.e(TAG, "-----" + 4);
                e.onNext(4);
            }
        }).subscribeOn(Schedulers.io())
                .compose(new ObservableTransformer<Integer, Integer>() {    //这儿可以单独抽出来
                    @Override
                    public ObservableSource<Integer> apply(Observable<Integer> upstream) {
                        //这里面可以多次变换  可用于使用相同的变化情况
                        return upstream.map(new Function<Integer, String>() {
                            @Override
                            public String apply(Integer integer) throws Exception {
                                LogUtil.e(TAG, "----A----" + integer);
                                return "" + integer;
                            }
                        }).map(new Function<String, Integer>() {
                            @Override
                            public Integer apply(String s) throws Exception {
                                LogUtil.e(TAG, "-----B-----" + s);
                                return Integer.valueOf(s);
                            }
                        });
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        LogUtil.e(TAG, "----C---- " + integer);
                    }
                });
    }

    /**
     * doOnSubscribe调用  注意两个subscribeOn
     */
    private void base2Demo() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Thread.sleep(3000);
                LogUtil.eM(TAG, "emmit:  " + 1);
                e.onNext(1);
                LogUtil.eM(TAG, "emmit:  " + 2);
                e.onNext(2);
                LogUtil.eM(TAG, "emmit:  " + 3);
                e.onNext(3);
                LogUtil.eM(TAG, "emmit:  " + 4);
                e.onNext(4);
            }
        }).subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        LogUtil.eM(TAG, "初始化");
                        Toast.makeText(Rx1000Activity.this, "等还是不等，这是一个问题", Toast.LENGTH_SHORT).show();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        LogUtil.eM(TAG, "accept:   " + integer);
                    }
                });
    }


    /**
     * 模拟登陆  然后放入数据库  然后放入数据库是要时间的 登陆数据回显示界面
     */
    private void base3Demo() {
        //查看先后顺序
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                LogUtil.eM(TAG, "开始请求网络登陆....");
                e.onNext("1111");
            }
        }).subscribeOn(Schedulers.io())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        LogUtil.eM(TAG, "写入数据库中");
                        Thread.sleep(1000);
                        LogUtil.eM(TAG, "写完了");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        LogUtil.eM(TAG, "更新user");
                    }
                });
    }

    /**
     * 出错   然后  分别使用   onErrorResumeNext 和  doOnError
     */
    private void base4Demo() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                LogUtil.e(TAG, "emmit:  " + 1);
                e.onNext(1);
                LogUtil.e(TAG, "emmit:  " + 2);
                e.onNext(2);
                LogUtil.e(TAG, "emmit:  " + 3);
                e.onNext(3);
                //这儿报错试一下
                int b = 4 / 0;
                LogUtil.e(TAG, "emmit:  " + 4);
                e.onNext(4);
            }
        }).subscribeOn(Schedulers.io())
                //doOnError   和  onErrorResumeNext
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Integer>>() {
                    @Override
                    public ObservableSource<? extends Integer> apply(Throwable throwable) throws Exception {
                        LogUtil.e(TAG, "apply:     " + throwable.getMessage());
//                        return Observable.just(10086);
//                        return Observable.empty();  //发送empty
                        return Observable.never();
                    }
                })
//                .doOnError(new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        LogUtil.e(TAG, "accept:   " + throwable.getMessage());
//                    }
//                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        LogUtil.e(TAG, "consume:  " + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e(TAG, "onError:   " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
