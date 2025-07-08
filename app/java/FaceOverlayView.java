package com.example.vibeifyfer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FaceOverlayView extends View{
    private final Paint paint;
    private final List<RectF> faces = new ArrayList<>();

    public FaceOverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);
    }

    public void setFaces(List<RectF> faceBounds) {
        faces.clear();
        faces.addAll(faceBounds);
        invalidate(); // trigger redraw
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (RectF face : faces) {
            canvas.drawRect(face, paint);
        }
    }
}
