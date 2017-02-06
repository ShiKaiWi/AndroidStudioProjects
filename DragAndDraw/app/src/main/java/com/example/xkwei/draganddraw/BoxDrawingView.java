package com.example.xkwei.draganddraw;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xkwei on 26/12/2016.
 */

public class BoxDrawingView extends View {
    private static final String TAG = "BoxDrawingView";
    private static final String BOX_ARRAY = "AllTheBoxes";
    private static final String SUPER_STATE = "SuperState";

    private Box mCurrentBox;
    private List<Box> BoxN = new ArrayList<>();
    private Paint mBoxPaint;
    private Paint mBackgroundPaint;
    private int mCanvasColor;


    public BoxDrawingView(Context context){
        this(context,null);
    }

    public BoxDrawingView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);

        TypedArray a = context.obtainStyledAttributes(attributeSet,R.styleable.BoxDrawingView);
        mCanvasColor = a.getColor(R.styleable.BoxDrawingView_canvas_color,0xfff8efe0);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(mCanvasColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        setMeasuredDimension(measureDimension(widthMeasureSpec),measureDimension(heightMeasureSpec));
    }

    private int measureDimension(int dimMeasureSpec){
        int mode = MeasureSpec.getMode(dimMeasureSpec);
        int size = MeasureSpec.getSize(dimMeasureSpec);
        int result = size;
        switch(mode){
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.AT_MOST:
                break;
            default:
        }
        return result;
    }
    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawPaint(mBackgroundPaint);

        for(Box box:BoxN){
            float left = Math.min(box.getOrigin().x,box.getCurrent().x);
            float right = Math.max(box.getOrigin().x,box.getCurrent().x);
            float top = Math.min(box.getOrigin().y,box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y,box.getCurrent().y);
            canvas.drawRect(left,top,right,bottom,mBoxPaint);
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent me){

        PointF currentPoint = new PointF(me.getX(),me.getY());

        switch(me.getAction()){
            case MotionEvent.ACTION_DOWN:
                mCurrentBox = new Box(currentPoint);
                BoxN.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_MOVE:
                if(mCurrentBox!=null){
                    mCurrentBox.setCurrent(currentPoint);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                mCurrentBox = null;
                break;
        }
        return true;
    }

    @Override
    public Parcelable onSaveInstanceState(){
        Bundle args = new Bundle();
        args.putParcelableArrayList(BOX_ARRAY,(ArrayList<Box>)BoxN);
        args.putParcelable(SUPER_STATE,super.onSaveInstanceState());
        return args;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state){
        Bundle args = (Bundle) state;
        BoxN = args.getParcelableArrayList(BOX_ARRAY);
        super.onRestoreInstanceState(args.getParcelable(SUPER_STATE));
    }
}
