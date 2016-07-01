/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;

import com.androidzeitgeist.webcards.R;

/**
 * Helper class for dragging the handle and coordinating the drag/animation state between handle
 * and overlay.
 */
/* package-private */ class DragCoordinator implements View.OnTouchListener {
    private final WindowManager windowManager;
    private final OverlayView overlayView;
    private final HandleView handleView;
    private final DismissAreaView dismissAreaView;

    private final int handleVertialDragOffset;
    private final int windowHeight;
    private final int windowWidth;

    private boolean isHoveringOverDismissArea;
    private boolean isOpen;

    /* package-private */ DragCoordinator(Context context, OverlayView overlayView, HandleView handleView, DismissAreaView dismissAreaView) {
        this.overlayView = overlayView;
        this.handleView = handleView;
        this.dismissAreaView = dismissAreaView;

        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        handleView.setOnTouchListener(this);

        final Resources resources = context.getResources();
        final DisplayMetrics displayMetrics = resources.getDisplayMetrics();

        windowWidth = displayMetrics.widthPixels;
        windowHeight = displayMetrics.heightPixels;

        handleVertialDragOffset = (int) resources.getDimension(R.dimen.handle_vertical_drag_offset);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            dismissAreaView.setVisibility(View.VISIBLE);
        }

        if (isHoveringOverDismissArea != calculateIfHoveringOverDismissArea(event)) {
            isHoveringOverDismissArea = !isHoveringOverDismissArea;

            handleView.setVisibility(isHoveringOverDismissArea ? View.INVISIBLE : View.VISIBLE);
            dismissAreaView.setHighlight(isHoveringOverDismissArea);
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE | event.getAction() == MotionEvent.ACTION_DOWN) {
            overlayView.setVisibility(View.VISIBLE);

            int xOffset = handleView.getWidth() / 2;
            int yOffset = handleView.getHeight() / 2;
            int x = (int) event.getRawX() - xOffset;
            int y = (int) event.getRawY() - yOffset;

            int positionX = Math.min(windowWidth - handleView.getWidth() - x, handleView.getMinOffsetX());

            updateHandlePosition(positionX, y);
            updateOverlayViewPosition(positionX);

            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            dismissAreaView.setVisibility(View.GONE);
            handleView.setVisibility(View.VISIBLE);

            if (isHoveringOverDismissArea) {
                OverlayController.get().removeOverlay();
            } else {
                openOrCloseIfNeeded();
            }

            return true;
        }

        return false;
    }

    private boolean calculateIfHoveringOverDismissArea(MotionEvent event) {
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) handleView.getLayoutParams();
        final int y = layoutParams.y;

        final int center = windowHeight / 2;
        final int position = y + center - (handleView.getHeight() / 2);

        return position >= windowHeight - (handleView.getHeight() * 2);
    }

    private void openOrCloseIfNeeded() {
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) handleView.getLayoutParams();

        if (layoutParams.x < handleView.getMinOffsetX() / 2) {
            animateClose();
        } else {
            animateOpen();
        }
    }

    /* package-private */ Animator animateClose() {
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) handleView.getLayoutParams();

        ObjectAnimator animator = ObjectAnimator.ofInt(this, "animationOffset", layoutParams.x, 0);
        animator.setDuration(250);
        animator.setInterpolator(new AccelerateInterpolator());

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                overlayView.setVisibility(View.GONE);

                isOpen = false;
            }
        });

        animator.start();

        return animator;
    }

    /* package-private */ void animateOpen() {
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) handleView.getLayoutParams();

        ObjectAnimator animator = ObjectAnimator.ofInt(this, "animationOffset", layoutParams.x, handleView.getMinOffsetX());
        animator.setDuration(250);
        animator.setInterpolator(new AccelerateInterpolator());

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isOpen = true;
            }
        });

        animator.start();
    }

    public boolean isOpen() {
        return isOpen;
    }

    @SuppressWarnings("unused") // Used by animator
    public void setAnimationOffset(int x) {
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) handleView.getLayoutParams();
        layoutParams.x = x;
        windowManager.updateViewLayout(handleView, layoutParams);

        updateOverlayViewPosition(x);
    }

    private void updateHandlePosition(int x, int y) {
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) handleView.getLayoutParams();

        layoutParams.x = x;

        if (x <= handleVertialDragOffset) {
            layoutParams.y = y - (windowHeight / 2) + (handleView.getHeight() / 2);
        }

        windowManager.updateViewLayout(handleView, layoutParams);
    }

    private void updateOverlayViewPosition(int x) {
        overlayView.setTranslationX(overlayView.getWidth() - x);
    }
}
