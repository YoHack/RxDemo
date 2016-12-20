package pub.xgan.rxdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import pub.xgan.rxdemo.rxJava.Rx1000Activity;
import pub.xgan.rxdemo.rxJava.Rx1Activity;
import pub.xgan.rxdemo.rxJava.Rx2Activity;
import pub.xgan.rxdemo.rxJava.Rx3Activity;
import pub.xgan.rxdemo.rxJava.Rx4Activity;
import pub.xgan.rxdemo.rxJava.Rx5Activity;
import pub.xgan.rxdemo.rxJava.Rx6Activity;
import pub.xgan.rxdemo.rxJava.Rx7Activity;
import pub.xgan.rxdemo.rxJava.Rx8Activity;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("各种demo");
    }


    public void click(View view) {
        switch (view.getId()) {
            case R.id.btn_1: {
                toActivity(Rx1Activity.class);
                break;
            }

            case R.id.btn_2: {
                toActivity(Rx2Activity.class);
                break;
            }

            case R.id.btn_3: {
                toActivity(Rx3Activity.class);
                break;
            }
            case R.id.btn_4: {
                toActivity(Rx4Activity.class);
                break;
            }
            case R.id.btn_5: {
                toActivity(Rx5Activity.class);
                break;
            }
            case R.id.btn_6: {
                toActivity(Rx6Activity.class);
                break;
            }
            case R.id.btn_7: {
                toActivity(Rx7Activity.class);
                break;
            }
            case R.id.btn_8: {
                toActivity(Rx8Activity.class);
                break;
            }
            case R.id.btn_1000: {
                toActivity(Rx1000Activity.class);
                break;
            }
        }
    }

    private void toActivity(Class clazz) {
        startActivity(new Intent(this, clazz));
    }

}
