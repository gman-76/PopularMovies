package com.projects.gerhardschoeman.popularmovies;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Gerhard on 21/10/2015.
 */
public class ScaledImage extends ImageView {
    private final String LOGTAG = "ScaledImage";

    public ScaledImage(Context context) {
        super(context);
    }

    public ScaledImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaledImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try{
            Drawable drawable = getDrawable();
            if (drawable == null) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            } else {
                int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
                int viewHeight = MeasureSpec.getSize(heightMeasureSpec);
                //int intrinsicWidth = drawable.getIntrinsicWidth();
                //int intrinsicHeight = drawable.getIntrinsicHeight();
                //if(intrinsicWidth==0){
                //    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                //}else {
                    if (viewWidth != 0 && viewHeight==0 && viewHeight<viewWidth) {
                        //Log.d(LOGTAG,"Original image width:" + Integer.toString(intrinsicWidth) + " height:" + Integer.toString(intrinsicHeight));
                        //double density = getResources().getDisplayMetrics().density;
                        viewHeight = (int) (1.5 * (float) viewWidth);
                        //Log.d(LOGTAG, "Image display density = " + Double.toString(density));
                        //Log.d(LOGTAG,"Scaled image to width:" + Integer.toString(viewWidth) + " height:" + Integer.toString(viewHeight));
                        setMeasuredDimension(viewWidth, viewHeight);
                    } else if(viewHeight!=0 && viewHeight<viewWidth){
                        viewWidth = (int) ((float)viewHeight/1.5);
                        setMeasuredDimension(viewWidth, viewHeight);
                    }else {
                        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    }
                //}
            }
        }catch (Exception e) {
            Log.e(LOGTAG,"Exception in onMeasure(): " + e.getMessage());
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
