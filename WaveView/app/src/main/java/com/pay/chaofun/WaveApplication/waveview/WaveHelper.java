package com.pay.chaofun.WaveApplication.waveview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;


public class WaveHelper {
    private WaveView mWaveView;

    private AnimatorSet mAnimatorSet;

    /**
     * default: 1f ---  no shift
     * 0f ---  waveview can shift
     */
    private float mWaveShiftRatio = 1f;
    /**
     * default: 0.5f --- 居中位置
     */
    private float mWaterLevelRatio = 0.5f;
    private float mWaterMAXLevelRatio = 0.5f;


    private List<Animator> animators;
    private int REPET_COUT = 0;

    /**
     * 默认的waveview，静止状态
     * 可以在创建waveHelper时，设置对应的参数值，启动动画
     *
     * @param waveView
     */
    public WaveHelper(WaveView waveView) {
        mWaveView = waveView;
    }

    public void start() {
        initAnimation();
        mWaveView.setShowWave(true);
        if (mAnimatorSet != null) {
            mAnimatorSet.start();
        }
    }

    /**
     * 动一小会：测试设置的动画时间为3秒，不重复
     */
    public void setShiftAFewTime() {
        setmWaveShiftRatio(0f);
        setmWaterLevelRatio(0.5f);
    }

    public void defaultAlawysShift() {
        setmWaveShiftRatio(0f);
        setmWaterLevelRatio(0.5f);
        setRepet();

    }

    public void fromBottomShift() {
        setmWaveShiftRatio(0f);
        setmWaterLevelRatio(0f);
        setRepet();
    }
    public void fromBottom2Top() {
        setmWaterMAXLevelRatio(1f);
        setmWaveShiftRatio(0f);
        setmWaterLevelRatio(0f);
        setRepet();
    }

    public void setRepet() {
        REPET_COUT = ValueAnimator.INFINITE;
    }

    /**
     * 0f~1f
     */
    public void setShiftAnim() {
        // horizontal animation.
        // wave waves infinitely
        ObjectAnimator waveShiftAnim = ObjectAnimator.ofFloat(mWaveView,
                "waveShiftRatio", mWaveShiftRatio, 1f);
        if (REPET_COUT != 0)
            waveShiftAnim.setRepeatCount(REPET_COUT);
        waveShiftAnim.setDuration(3000);
        waveShiftAnim.setInterpolator(new LinearInterpolator());
        animators.add(waveShiftAnim);

    }


    /**
     * 0f~0.5f
     */
    public void setWaterLevelAnim() {
        // vertical animation.
        // water level increases from 0 to center of WaveView
        ObjectAnimator waterLevelAnim = ObjectAnimator.ofFloat(mWaveView,
                "waterLevelRatio", mWaterLevelRatio, mWaterMAXLevelRatio);
        waterLevelAnim.setDuration(3000);
        waterLevelAnim.setInterpolator(new DecelerateInterpolator());
        animators.add(waterLevelAnim);

    }


    public void setmWaterMAXLevelRatio(float ratio){
        mWaterMAXLevelRatio=ratio;
    }

    public void setmWaveShiftRatio(float ratio) {
        mWaveShiftRatio = ratio;
    }

    public void setmWaterLevelRatio(float ratio) {
        mWaterLevelRatio = ratio;
    }

    private void initAnimation() {
        animators = new ArrayList<Animator>();
        mAnimatorSet = new AnimatorSet();
        setShiftAnim();
        setWaterLevelAnim();
        mAnimatorSet.playTogether(animators);
    }

    public void cancel() {
        if (mAnimatorSet != null) {
            mAnimatorSet.end();
        }
    }
}
