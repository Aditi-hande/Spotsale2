package com.example.ecommerce.spotsale2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CustomImageView extends androidx.appcompat.widget.AppCompatImageView {

    protected Paint paint;
    protected int progress;
    protected boolean inProgress;

    public CustomImageView(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAlpha(100);
        paint.setStrokeWidth(3);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAlpha(100);
        paint.setStrokeWidth(3);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAlpha(100);
        paint.setStrokeWidth(3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(inProgress) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
            //canvas.drawText(progress + "%", getX(), getY(), paint);
        }
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
        invalidate();
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

}
