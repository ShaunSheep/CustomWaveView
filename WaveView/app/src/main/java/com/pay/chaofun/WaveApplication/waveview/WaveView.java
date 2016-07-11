package com.pay.chaofun.WaveApplication.waveview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;


/**
 * 自定义的收益波浪动画
 */
public class WaveView extends View {
    /**
     * +------------------------+
     * |<--wave length-> (长度)  |______
     * |   /\          |   /\   |  |
     * |  /  \         |  /  \  | amplitude(振幅)
     * | /    \        | /    \ |  |
     * |/      \       |/      \|__|____
     * |        \      /        |  |
     * |         \    /         |  |
     * |          \  /          |  |
     * |           \/           | water level(高度)
     * |                        |  |
     * |                        |  |
     * +------------------------+__|____
     */
    private static final float DEFAULT_AMPLITUDE_RATIO = 0.05f;
    private static final float DEFAULT_WATER_LEVEL_RATIO = 0.5f;
    private static final float DEFAULT_WAVE_LENGTH_RATIO = 1.0f;
    private static final float DEFAULT_WAVE_SHIFT_RATIO = 0.0f;

    public static final int DEFAULT_BEHIND_WAVE_COLOR = 0x28FFFFFF;
    public static final int DEFAULT_FRONT_WAVE_COLOR = 0x3CFFFFFF;
    public static final ShapeType DEFAULT_WAVE_SHAPE = ShapeType.CIRCLE;

    public enum ShapeType {
        CIRCLE,
        SQUARE
    }

    // 是否展示波浪
    private boolean mShowWave;

    // shader containing repeated waves
    //渲染包含重复的波浪
    private BitmapShader mWaveShader;
    //渲染矩阵
    private Matrix mShaderMatrix;
    //绘制波浪的画笔
    private Paint mViewPaint;
    //绘制边界的画笔
    private Paint mBorderPaint;
    //绘制背景颜色的画笔
    private Paint mBackgroundPaint;

    //显示“立即购买”字样
    private String text;

    private float mDefaultAmplitude;
    private float mDefaultWaterLevel;
    private float mDefaultWaveLength;
    private double mDefaultAngularFrequency;

    private float mAmplitudeRatio = DEFAULT_AMPLITUDE_RATIO;
    private float mWaveLengthRatio = DEFAULT_WAVE_LENGTH_RATIO;
    private float mWaterLevelRatio = DEFAULT_WATER_LEVEL_RATIO;
    private float mWaveShiftRatio = DEFAULT_WAVE_SHIFT_RATIO;

    private int mBehindWaveColor = DEFAULT_BEHIND_WAVE_COLOR;
    private int mFrontWaveColor = DEFAULT_FRONT_WAVE_COLOR;
    private ShapeType mShapeType = DEFAULT_WAVE_SHAPE;

    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mShaderMatrix = new Matrix();
        mViewPaint = new Paint();
        mViewPaint.setAntiAlias(true);
    }

    public float getWaveShiftRatio() {
        return mWaveShiftRatio;
    }

    /**
     * 变换波浪
     *
     * @param waveShiftRatio Should be 0 ~ 1. Default to be 0.
     */
    public void setWaveShiftRatio(float waveShiftRatio) {
        if (mWaveShiftRatio != waveShiftRatio) {
            mWaveShiftRatio = waveShiftRatio;
            invalidate();
        }
    }

    public float getWaterLevelRatio() {
        return mWaterLevelRatio;
    }

    /**
     * 设置波浪的高度
     *
     * @param waterLevelRatio Should be 0 ~ 1. Default to be 0.5.
     */
    public void setWaterLevelRatio(float waterLevelRatio) {
        if (mWaterLevelRatio != waterLevelRatio) {
            mWaterLevelRatio = waterLevelRatio;
            invalidate();
        }
    }

    public float getAmplitudeRatio() {
        return mAmplitudeRatio;
    }

    /**
     * 设置波浪的振幅
     *
     * @param amplitudeRatio Default to be 0.05. Result of amplitudeRatio + waterLevelRatio should be less than 1.
     */
    public void setAmplitudeRatio(float amplitudeRatio) {
        if (mAmplitudeRatio != amplitudeRatio) {
            mAmplitudeRatio = amplitudeRatio;
            invalidate();
        }
    }

    public float getWaveLengthRatio() {
        return mWaveLengthRatio;
    }

    /**
     * 设置波浪的长度
     *
     * @param waveLengthRatio Default to be 1.
     */
    public void setWaveLengthRatio(float waveLengthRatio) {
        mWaveLengthRatio = waveLengthRatio;
    }

    public boolean isShowWave() {
        return mShowWave;
    }

    public void setShowWave(boolean showWave) {
        mShowWave = showWave;
    }

    public void setBorder(int width, int color) {
        if (mBorderPaint == null) {
            mBorderPaint = new Paint();
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setStyle(Style.STROKE);
//            mBorderPaint.setStyle(Style.FILL);
        }
        mBorderPaint.setColor(color);
        mBorderPaint.setStrokeWidth(width);
        invalidate();
    }

    public void setBackground(int color) {
        if (mBackgroundPaint == null) {
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setAntiAlias(true);
            mBackgroundPaint.setStyle(Style.FILL);
        }
        mBackgroundPaint.setColor(color);
        invalidate();
    }

    public void setWaveColor(int behindWaveColor, int frontWaveColor) {
        mBehindWaveColor = behindWaveColor;
        mFrontWaveColor = frontWaveColor;

        // need to recreate shader when color changed
        mWaveShader = null;
        createShader();
        invalidate();
    }

    /**
     * 修正崩溃bug  初始化时调用
     *
     * @param behindWaveColor
     * @param frontWaveColor
     */
    public void initWaveColor(int behindWaveColor, int frontWaveColor) {
        mBehindWaveColor = behindWaveColor;
        mFrontWaveColor = frontWaveColor;
    }

    public void setShapeType(ShapeType shapeType) {
        mShapeType = shapeType;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        createShader();
    }

    /**
     * 渲染波浪：重复水平移动，竖直方向为钳形
     */
    private void createShader() {
        mDefaultAngularFrequency = 2.0f * Math.PI / DEFAULT_WAVE_LENGTH_RATIO / getWidth();
        mDefaultAmplitude = getHeight() * DEFAULT_AMPLITUDE_RATIO;
        mDefaultWaterLevel = getHeight() * DEFAULT_WATER_LEVEL_RATIO;
        mDefaultWaveLength = getWidth();

        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);

        Paint wavePaint = new Paint();
        wavePaint.setStrokeWidth(2);
        wavePaint.setAntiAlias(true);
        //在bitmap上绘制波浪
        // y=Asin(ωx+φ)+h
        final int endX = getWidth() + 1;
        final int endY = getHeight() + 1;

        float[] waveY = new float[endX];

        wavePaint.setColor(mBehindWaveColor);
        for (int beginX = 0; beginX < endX; beginX++) {
            double wx = beginX * mDefaultAngularFrequency;
            float beginY = (float) (mDefaultWaterLevel + mDefaultAmplitude * Math.sin(wx));
            canvas.drawLine(beginX, beginY, beginX, endY, wavePaint);

            waveY[beginX] = beginY;
        }

        wavePaint.setColor(mFrontWaveColor);
        final int wave2Shift = (int) (mDefaultWaveLength / 4);
        for (int beginX = 0; beginX < endX; beginX++) {
            canvas.drawLine(beginX, waveY[(beginX + wave2Shift) % endX], beginX, endY, wavePaint);
        }

        mWaveShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
        mViewPaint.setShader(mWaveShader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //根据波浪状态，定义画笔的shader属性
        if (mShowWave && mWaveShader != null) {
            //第一次回调的时候，为paint设置shader属性
            if (mViewPaint.getShader() == null) {
                mViewPaint.setShader(mWaveShader);
            }

            //根据波浪的长度和振幅，缩放shader属性
            //缩放值决定于 波浪的长度，振幅的高度
            mShaderMatrix.setScale(
                    mWaveLengthRatio / DEFAULT_WAVE_LENGTH_RATIO,
                    mAmplitudeRatio / DEFAULT_AMPLITUDE_RATIO,
                    0,
                    mDefaultWaterLevel);

            //根据波浪的变化频率和波浪的高度 来平移shader
            //平移的数值 是由  mWaveShiftRatio的x值和mWaterLevelRatio的y值决定
            mShaderMatrix.postTranslate(
                    mWaveShiftRatio * getWidth(),
                    (DEFAULT_WATER_LEVEL_RATIO - mWaterLevelRatio) * getHeight());

            //将矩阵变换 添加到shader中
            mWaveShader.setLocalMatrix(mShaderMatrix);

            float borderWidth = mBorderPaint == null ? 0f : mBorderPaint.getStrokeWidth();
            float radius = getWidth() / 2f - borderWidth;
            switch (mShapeType) {
                case CIRCLE:
                    if (borderWidth > 0) {

                        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f,
                                (getWidth() - borderWidth) / 2f - 1f, mBorderPaint);
//                        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f,
//                            0, mBorderPaint);
                    }
                    if (mBackgroundPaint != null) {
//                        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f,(getWidth() - borderWidth) / 2f - 1f,mBackgroundPaint);
                        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, radius, mBackgroundPaint);
                    }
                    canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, radius, mViewPaint);
                    break;
                case SQUARE:
                    if (borderWidth > 0) {
                        canvas.drawRect(
                                borderWidth / 2f,
                                borderWidth / 2f,
                                getWidth() - borderWidth / 2f - 0.5f,
                                getHeight() - borderWidth / 2f - 0.5f,
                                mBorderPaint);
                    }
                    canvas.drawRect(borderWidth, borderWidth, getWidth() - borderWidth,
                            getHeight() - borderWidth, mViewPaint);
                    break;
            }
            // 在波浪上可以显示的文字包括:立即购买和其它2个子的文字
            String writeWord = "立即";
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.CENTER);
            //这里是写死的值，可以根据XML文件配置
            paint.setTextSize(55);
            Rect bounds = new Rect(0, 0, (int) radius, (int) radius);
            paint.getTextBounds(writeWord, 0, writeWord.length(), bounds);
            float fontHeight = paint.getFontMetrics().bottom * (getContext().getResources().getDisplayMetrics().density + (float) 0.5);

            if (!isEmpty(text)) {
                writeWord = text;
                canvas.drawText(writeWord, radius, radius + fontHeight / 2 - 5, paint);
            } else {
                //canvas的drawText，大家都不陌生吧！
                canvas.drawText(writeWord, radius, radius - 15, paint);
                canvas.drawText("购买", radius, radius + fontHeight - 5, paint);
            }
        } else {
            mViewPaint.setShader(null);
        }
    }

    public static boolean isEmpty(String s) {
        if (null == s || s.length() < 1) return true;
        return false;
    }

    /**
     * 根据dp获取px
     */
    public float getPxByDp(float dp) {
        Context c = getContext();
        Resources r;
        if (null == c) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }
        // param:1:返回的单位,输入的待转换的值
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, dp, r.getDisplayMetrics());
    }

    public void setText(String text) {
        this.text = text;
    }
}
