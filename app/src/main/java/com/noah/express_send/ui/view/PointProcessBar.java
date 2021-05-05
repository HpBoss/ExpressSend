package com.noah.express_send.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @description: 节点进度条
 * @author: DMingO
 * @date: 2020/4/15
 */
public class PointProcessBar extends View {
 
    /**
     * 未选中时的连线画笔
     */
    private Paint mLinePaint;
    /**
     * 选中时的连线画笔
     */
    private Paint mLineSelectedPaint;
    /**
     * 未选中时的文字画笔
     */
    private Paint mTextPaint;
    /**
     * 选中时的文字画笔
     */
    private Paint mTextSelPaint;
 
    /**
     * 未选中时的实心圆画笔
     */
    private Paint mCirclePaint;
    /**
     * 选中时的内部实心圆画笔
     */
    private Paint mCircleSelPaint;
    /**
     * 选中时的边框圆画笔
     */
    private Paint mCircleStrokeSelPaint;
 
    /**
     * 未选中时的线，节点圆的颜色
     */
    private int mColorUnselected  = Color.parseColor("#BEBEBE");
    /**
     * 选中时的颜色
     */
    private int mColorSelected = Color.parseColor("#61A4E4");
    /**
     * 未选中的文字颜色
     */
    private int mColorTextUnselected  = Color.parseColor("#5c030f09");
 
    /**
     * 绘制的节点个数，由底部节点标题数量控制
     */
    int circleCount ;
 
    /**
     * 连线的高度
     */
    float mLineHeight = 7f;
 
    //圆的直径
    float mCircleHeight = 30f;
    float mCircleSelStroke = 6f;
    float mCircleFillRadius = 8f;
 
    //文字大小
    float mTextSize  = 20f;
 
    //文字离顶部的距离
    float mMarginTop = 30f;
    /**
     * 首个圆向中心偏移的距离
     */
    float marginLeft = 30f;
 
    /**
     * 最后一个圆向中心偏移的距离
     */
    float marginRight = marginLeft;
 
    /**
     * 每个节点相隔的距离
     */
    float divideWidth;
 
    int defaultHeight;
 
    /**
     * 节点底部的文字列表
     */
    List<String> textList = new ArrayList<>();
 
    /**
     * 文字同宽高的矩形，用来测量文字
     */
    List<Rect> mBounds;
    /**
     * 存储每个圆心在同一直线上的节点圆的 x 坐标值
     */
    List<Float> circleLineJunctions = new ArrayList<>();
    /**
     * 选中项集合
     */
    Set<Integer> selectedIndexSet = new HashSet<>();
 
    public PointProcessBar(Context context) {
        super(context);
    }
 
    public PointProcessBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }
 
    public PointProcessBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
 
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PointProcessBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
 
    /**
     * 初始化画笔属性
     */
    private void initPaint(){
 
        mLinePaint = new Paint();
        mLineSelectedPaint = new Paint();
        mCirclePaint = new Paint();
        mTextPaint = new Paint();
        mCircleStrokeSelPaint = new Paint();
        mTextSelPaint=new Paint();
        mCircleSelPaint = new Paint();
 
        mLinePaint.setColor(mColorUnselected);
        //设置填充
        mLinePaint.setStyle(Paint.Style.FILL);
        //笔宽像素
        mLinePaint.setStrokeWidth(mLineHeight);
        //锯齿不显示
        mLinePaint.setAntiAlias(true);
 
        mLineSelectedPaint.setColor(mColorSelected);
        mLineSelectedPaint.setStyle(Paint.Style.FILL);
        mLineSelectedPaint.setStrokeWidth(mLineHeight);
        mLineSelectedPaint.setAntiAlias(true);
 
        mCirclePaint.setColor(mColorUnselected);
        //设置填充
        mCirclePaint.setStyle(Paint.Style.FILL);
        //笔宽像素
        mCirclePaint.setStrokeWidth(1);
        //锯齿不显示
        mCirclePaint.setAntiAlias(true);
 
        //选中时外框空心圆圈画笔
        mCircleStrokeSelPaint.setColor(mColorSelected);
        mCircleStrokeSelPaint.setStyle(Paint.Style.STROKE);
        mCircleStrokeSelPaint.setStrokeWidth(mCircleSelStroke);
        mCircleStrokeSelPaint.setAntiAlias(true);
        //选中时的内部填充圆画笔
        mCircleSelPaint.setStyle(Paint.Style.FILL);
        mCircleSelPaint.setStrokeWidth(1);
        mCircleSelPaint.setAntiAlias(true);
        mCircleSelPaint.setColor(mColorSelected);
 
        //普通状态的文本 画笔
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mColorUnselected);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        //选中后的文本画笔
        mTextSelPaint.setTextSize(mTextSize);
        mTextSelPaint.setColor(mColorSelected);
        mTextSelPaint.setAntiAlias(true);
        mTextSelPaint.setTextAlign(Paint.Align.CENTER);
    }
 
    /**
     * 测量文字的长宽，将文字视为rect矩形
     */
    private void measureText(){
        mBounds = new ArrayList<>();
        for(String name : textList){
            Rect mBound = new Rect();
            mTextPaint.getTextBounds(name, 0, name.length(), mBound);
            mBounds.add(mBound);
        }
    }
 
 
 
 
    /**
     * 测量view的高度
     */
    private void measureHeight(){
        if (mBounds!=null && mBounds.size()!=0) {
            defaultHeight = (int) (mCircleHeight + mMarginTop + mCircleSelStroke + mBounds.get(0).height()/2);
        } else {
            defaultHeight  = (int) (mCircleHeight + mMarginTop+mCircleSelStroke);
        }
    }
 
 
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        //宽高都设置为wrap_content
       if(widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST){
            //宽设置为wrap_content
            setMeasuredDimension(widthSpecSize,defaultHeight);
        }else if(widthSpecMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(widthSpecSize,heightSpecSize);
        }else if(heightSpecMode == MeasureSpec.AT_MOST){
            //高设置为wrap_content
            setMeasuredDimension(widthSpecSize, defaultHeight);
        }else{
            //宽高都设置为match_parent或具体的dp值
            setMeasuredDimension(widthSpecSize, heightSpecSize);
        }
    }
 
    @Override
    protected void onDraw(Canvas canvas) {
        //若未设置节点标题或者选中项的列表，则取消绘制
        if (textList == null || textList.isEmpty() ||
                selectedIndexSet == null || selectedIndexSet.isEmpty() ||
                mBounds == null || mBounds.isEmpty()) {
            return;
        }
        //画灰色圆圈的个数
        circleCount=textList.size();
        //每个圆相隔的距离（重要），可以通过这个调节节点间距
        divideWidth = (getWidth() - mCircleHeight ) / (circleCount - 1);
        //绘制文字和圆形
        for (int i=0; i < circleCount ;i++){
            float cx;
            float cy;
            float textX;
            if (i==0){
                //第一个节点，圆心需要向右偏移
                cx = mCircleHeight / 2 + i * divideWidth + marginLeft;
                cy = mCircleHeight / 2 + mCircleSelStroke;
                textX = cx;
                circleLineJunctions.add(cx + mCircleHeight / 2);
            }else if (i==textList.size()-1){
                //最后一个节点，圆心需要向左偏移
                cx = mCircleHeight / 2 + i * divideWidth - marginRight;
                cy = mCircleHeight / 2 + mCircleSelStroke;
                textX = cx;
                circleLineJunctions.add(cx - mCircleHeight / 2);
            }else {
                //中间部分的节点
                cx = mCircleHeight / 2 + i * divideWidth;
                cy = mCircleHeight / 2+mCircleSelStroke;
                textX = cx;
                circleLineJunctions.add(cx - mCircleHeight / 2);
                circleLineJunctions.add(cx + mCircleHeight / 2);
            }
            if (getSelectedIndexSet().contains(i)){
                //若当前位置节点被包含在选中项Set中，判定此节点被选中
                canvas.drawCircle(cx , cy, mCircleHeight / 2, mCircleStrokeSelPaint);
                canvas.drawCircle(cx, cy, mCircleFillRadius, mCircleSelPaint);
                canvas.drawText(textList.get(i), textX, (float) (mCircleHeight + mMarginTop +mCircleSelStroke+mBounds.get(i).height()/2.0), mTextSelPaint);
            }else {
                //若当前位置节点没有被包含在选中项Set中，判定此节点没有被选中
                canvas.drawCircle(cx , cy, mCircleHeight / 2, mCirclePaint);
                canvas.drawText(textList.get(i), textX, (float) (mCircleHeight + mMarginTop +mCircleSelStroke+mBounds.get(i).height()/2.0), mTextPaint);
            }
        }
        for(int i = 1 , j = 1 ; j <= circleLineJunctions.size() && ! circleLineJunctions.isEmpty()  ; ++i , j=j+2){
            if(getSelectedIndexSet().contains(i)){
                canvas.drawLine(circleLineJunctions.get(j-1),mCircleHeight/2+mCircleSelStroke,
                        circleLineJunctions.get(j) ,mCircleHeight/2+mCircleSelStroke,mLineSelectedPaint);
            }else {
                canvas.drawLine(circleLineJunctions.get(j-1) + 2,mCircleHeight/2+mCircleSelStroke,
                        circleLineJunctions.get(j) ,mCircleHeight/2+mCircleSelStroke,mLinePaint);
            }
        }
    }
 
    /**
     * 供外部调用，显示控件
     * @param titles 底部标题内容列表
     * @param indexSet 选中项Set
     */
    public void show(List<String> titles , Set<Integer> indexSet){
        if(titles != null && ! titles.isEmpty()){
            this.textList = titles;
        }
        if(indexSet != null  && ! indexSet.isEmpty()){
            this.selectedIndexSet = indexSet;
        }
        measureText();
        measureHeight();
        //绘制
        invalidate();
    }
 
    /**
     * 更新底部节点标题内容
     * @param textList 节点标题内容列表
     */
    public void refreshTextList(List<String> textList) {
        this.textList = textList;
        measureText();
        measureHeight();
        invalidate();
    }
 
    /**
     * 获取节点选中状态
     * @return 节点选中状态列表
     */
    public Set<Integer> getSelectedIndexSet() {
        return selectedIndexSet;
    }
 
    /**
     * 更新选中项
     * @param set 选中项Set
     */
    public void refreshSelectedIndexSet(Set<Integer> set) {
        this.selectedIndexSet = set;
        invalidate();
    }
 
 
}
