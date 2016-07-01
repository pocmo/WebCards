/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.androidzeitgeist.webcards.R;

/**
 * View representing the handle to drag the overlay.
 */
/* package-private */ class HandleView extends View {
    private WindowManager windowManager;

    private Paint fillPaint;
    private Paint strokePaint;

    private Path path;
    private float[] points;

    private int openOffsetX;

    public HandleView(Context context) {
        super(context);

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        openOffsetX = getResources ().getDimensionPixelSize(R.dimen.overlay_width);

        fillPaint = new Paint();
        fillPaint.setColor(0x66666666);

        strokePaint = new Paint();
        strokePaint.setColor(0xFFFFFFFF);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        path = new Path();

        path.moveTo(w, 0);
        path.lineTo(w, h);
        path.lineTo(0, h/2);
        path.close();

        points = new float[] {
            w, 0,
            0, h/2,
            0, h/2,
            w, h
        };
    }

    /* package-private */ int getOpenOffsetX() {
        return openOffsetX;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (path == null) {
            return;
        }

        canvas.drawPath(path, fillPaint);

        canvas.drawLines(points, strokePaint);
    }

    /* package-private */ void addToRoot() {
        setVisibility(View.VISIBLE);

        if (isShown()) {
            return;
        }

        int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getResources().getDisplayMetrics());

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                value,
                value,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                PixelFormat.TRANSLUCENT);

        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.END;
        layoutParams.x = openOffsetX;

        try {
            windowManager.addView(this, layoutParams);
        } catch (final SecurityException | WindowManager.BadTokenException e) {
            // We do not have the permission to add a view to the window ("draw over other apps")
            Toast.makeText(getContext(), R.string.toast_missing_permission, Toast.LENGTH_SHORT).show();
        }
    }

    /* package-private */ synchronized void removeFromRoot() {
        if (!isShown()) {
            return;
        }

        windowManager.removeView(this);
    }
}
