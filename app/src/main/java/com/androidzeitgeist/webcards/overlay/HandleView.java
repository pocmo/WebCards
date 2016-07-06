/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.Toast;

import com.androidzeitgeist.webcards.R;

/**
 * View representing the handle to drag the overlay.
 */
/* package-private */ class HandleView extends View {
    private final WindowManager windowManager;

    private final Paint paint;

    private int centerX;
    private int centerY;

    private final int margin;
    private final int openOffsetX;

    private final Bitmap streamBitmap;
    private final Bitmap openAppBitmap;

    private Bitmap currentBitmap;

    public HandleView(Context context) {
        super(context);

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        final Resources resources = getResources();

        margin = resources.getDimensionPixelSize(R.dimen.overlay_button_margin);
        openOffsetX = resources.getDimensionPixelSize(R.dimen.overlay_width);

        paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.overlayAccent));
        paint.setAntiAlias(true);

        streamBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_action_stream);
        openAppBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_action_open_app);
        currentBitmap = openAppBitmap;

        setElevation(resources.getDimensionPixelSize(R.dimen.overlay_button_elevation));

        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, getWidth(), getHeight());
            }
        });
    }

    /* package-private */ int getMargin() {
        return margin;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        centerX = w / 2;
        centerY = h / 2;
    }

    /* package-private */ int getOpenOffsetX() {
        return openOffsetX;
    }

    @Override
    public ViewOutlineProvider getOutlineProvider() {
        return super.getOutlineProvider();
    }

    /* package-private */ void updateState(boolean isOpen) {
        currentBitmap = isOpen ? openAppBitmap : streamBitmap;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, centerX, paint);

        canvas.drawBitmap(currentBitmap, centerX - currentBitmap.getWidth() / 2, centerY - currentBitmap.getHeight() / 2, paint);
    }

    /* package-private */ void addToRoot() {
        setVisibility(View.VISIBLE);

        if (isShown()) {
            return;
        }

        int size = getResources().getDimensionPixelSize(R.dimen.overlay_button_size);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                size,
                size,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
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
