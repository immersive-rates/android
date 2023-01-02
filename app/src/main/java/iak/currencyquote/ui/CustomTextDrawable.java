package iak.currencyquote.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.Currency;

public class CustomTextDrawable extends Drawable {

    private final String text;
    private final Paint textPaint;
    private final Rect canvasBounds;

    public CustomTextDrawable(String text) {
        this.canvasBounds = getBounds();
        this.text = text;
        this.textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);
//        textPaint.setShadowLayer(6f, 0, 0, Color.BLACK);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);

    }

    @Override
    public void draw(Canvas canvas) {
        final Rect canvasBounds = getBounds();

        int centerX = canvasBounds.width() >> 1;
        float radius = centerX;

        textPaint.setTextSize(radius);

        canvas.drawText(text, canvasBounds.width() / 2f, canvasBounds.height() / 2f - (textPaint.descent() + textPaint.ascent()) / 2, textPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        textPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        textPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void log(String message) {
        Log.d("DRAWABLE_TAG", message);
    }
}