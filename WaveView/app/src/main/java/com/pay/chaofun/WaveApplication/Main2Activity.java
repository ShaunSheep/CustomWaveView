package com.pay.chaofun.WaveApplication;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;

import com.pay.chaofun.WaveApplication.waveview.WaveHelper;
import com.pay.chaofun.WaveApplication.waveview.WaveView;

public class Main2Activity extends AppCompatActivity {

    private WaveView mWaveView;
    private WaveHelper waveHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        RelativeLayout view = (RelativeLayout) findViewById(R.id.main2);


        mWaveView = new WaveView(getApplicationContext());
        mWaveView.setBackground(Color.RED);
        mWaveView.initWaveColor(0xff4671e5, 0xff3e96e6);
        mWaveView.setWaterLevelRatio(0.5f);
        mWaveView.setWaveShiftRatio(0.5f);
        final WaveHelper mHelper = new WaveHelper(mWaveView);
        mHelper.start();


        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(200, 200);
        view.addView(mWaveView, layoutParams);

        mWaveView.setClickable(true);
        mWaveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("click", "click");
                mHelper.cancel();
                waveHelper = new WaveHelper(mWaveView);
                waveHelper.setmWaveShiftRatio(0f);
                waveHelper.setmWaterLevelRatio(0.5f);
                waveHelper.start();

            }
        });

    }

    /**
     * 指定动画的时间和结束为止
     */
    public void shiftAppointTime(View view) {
        if (waveHelper != null)
            waveHelper.cancel();
        waveHelper = new WaveHelper(mWaveView);
        waveHelper.setShiftAFewTime();
        waveHelper.start();
    }

    /**
     * 在指定位置一直动。0.5f表示居中
     * @param view
     */
    public void shiftAlways(View view) {
        if (waveHelper != null)
            waveHelper.cancel();
        waveHelper = new WaveHelper(mWaveView);
        waveHelper.defaultAlawysShift();
        waveHelper.start();
    }

    /**
     * 从底部变换到中间位置，0f~0.5f
     * @param view
     */
    public void shiftAlwaysFromBottom(View view) {
        if (waveHelper != null)
            waveHelper.cancel();
        waveHelper = new WaveHelper(mWaveView);
        waveHelper.fromBottomShift();
        waveHelper.start();
    }

    /**
     * 溢满效果
     * @param view
     */
    public void shiftBottom2Top(View view){
        if(waveHelper!=null)
            waveHelper.cancel();
        waveHelper = new WaveHelper(mWaveView);
        waveHelper.fromBottom2Top();
        waveHelper.start();
    }

}
