/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.androidzeitgeist.webcards.R;

/**
 * View showing a "dimiss area" indicator in the bottom right of the screen.
 */
/* package-private */ class DismissAreaView extends View {
    private WindowManager windowManager;

    private Paint strokePaint;
    private Paint backgroundPaint;
    private Paint highlightPaint;

    private int indicatorSize;
    private int indicatorMargin;
    private int crossSize;

    private Rect indicatorRect;
    private float[] crossLines;

    private boolean highlight;

    public DismissAreaView(Context context) {
        super(context);

        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        final Resources resources = getResources();
        this.indicatorSize = resources.getDimensionPixelSize(R.dimen.dismiss_rectangle_size);
        this.indicatorMargin = resources.getDimensionPixelSize(R.dimen.dismiss_rectangle_margin);
        this.crossSize = resources.getDimensionPixelSize(R.dimen.dismiss_cross_size);

        backgroundPaint = new Paint();

        strokePaint = new Paint();
        strokePaint.setColor(Color.WHITE);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(resources.getDimensionPixelSize(R.dimen.dismiss_indicator_stroke_width));

        highlightPaint = new Paint();
        highlightPaint.setColor(resources.getColor(R.color.overlayAccent));
        highlightPaint.setStyle(Paint.Style.FILL);
    }

    /* package-private */ void setHighlight(boolean highlight) {
        if (highlight != this.highlight) {
            this.highlight = highlight;
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        backgroundPaint.setShader(new RadialGradient(w, h, h, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.MIRROR));

        indicatorRect =  new Rect(
                w - indicatorSize - indicatorMargin,
                h - indicatorSize - indicatorMargin,
                w - indicatorMargin,
                h - indicatorMargin);

        final int centerX = w - indicatorMargin - (indicatorSize / 2);
        final int centerY = h - indicatorMargin - (indicatorSize / 2);

        final int left = centerX - (crossSize / 2);
        final int top = centerY - (crossSize / 2);
        final int right = centerX + (crossSize / 2);
        final int bottom = centerY + (crossSize / 2);

        crossLines = new float[] {
                left, top, right, bottom,
                right, top, left, bottom
        };
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        setVisibility(View.GONE);
    }

    /* package-private */ void addToRoot() {
        if (isShown()) {
            return;
        }

        int size = getResources().getDimensionPixelSize(R.dimen.dismiss_area_size);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                size,
                size,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                PixelFormat.TRANSLUCENT);

        layoutParams.gravity = Gravity.BOTTOM | Gravity.END;

        try {
            windowManager.addView(this, layoutParams);
        } catch (final SecurityException | WindowManager.BadTokenException e) {
            // We do not have the permission to add a view to the window ("draw over other apps")
            Toast.makeText(getContext(), R.string.toast_missing_permission, Toast.LENGTH_SHORT).show();
        }
    }

    /* package-private */ void removeFromRoot() {
        if (!isShown()) {
            return;
        }

        windowManager.removeView(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Gradient background
        canvas.drawCircle(getWidth(), getHeight(), getHeight(), backgroundPaint);

        if (highlight) {
            // Highlight: Rectangle filled
            canvas.drawRect(indicatorRect, highlightPaint);
        }

        // Rectangle outline
        canvas.drawRect(indicatorRect, strokePaint);

        // Cross
        canvas.drawLines(crossLines, strokePaint);
    }
}
