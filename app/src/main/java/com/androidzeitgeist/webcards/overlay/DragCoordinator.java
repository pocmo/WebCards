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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;

/**
 * Helper class for dragging the handle and coordinating the drag/animation state between handle
 * and overlay.
 */
/* package-private */ class DragCoordinator implements View.OnTouchListener {
    private static final int TAP_TIMEOUT = 2 * ViewConfiguration.getTapTimeout();

    private final WindowManager windowManager;
    private final OverlayView overlayView;
    private final HandleView handleView;
    private final DismissAreaView dismissAreaView;

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
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

        }

        if (isHoveringOverDismissArea != isHoveringOverDismissArea()) {
            isHoveringOverDismissArea = !isHoveringOverDismissArea;

            handleView.setVisibility(isHoveringOverDismissArea ? View.INVISIBLE : View.VISIBLE);
            dismissAreaView.setHighlight(isHoveringOverDismissArea);
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE | event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!isOpen && !couldBeATap(event)) {
                dismissAreaView.setVisibility(View.VISIBLE);
            }

            overlayView.setVisibility(View.VISIBLE);

            int xOffset = handleView.getWidth() / 2;
            int yOffset = handleView.getHeight() / 2;
            int x = (int) event.getRawX() - xOffset;
            int y = (int) event.getRawY() - yOffset;

            int translatedX = Math.min(windowWidth - handleView.getWidth() - x, handleView.getOpenOffsetX());
            int translatedY = y - (windowHeight / 2) + (handleView.getHeight() / 2);

            if (isOpen) {
                updateHandlePosition(translatedX + handleView.getMargin(), getHandleY());
                updateOverlayViewPosition(translatedX);
            } else {
                updateHandlePosition(getHandleX(), translatedY);
            }

            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            dismissAreaView.setVisibility(View.GONE);
            handleView.setVisibility(View.VISIBLE);

            if (couldBeATap(event)) {
                if (isOpen) {
                    OverlayController.get().switchToApp();
                } else {
                    animateOpen();
                }
            } else if (isHoveringOverDismissArea) {
                OverlayController.get().removeOverlay();
            } else {
                openOrCloseIfNeeded();
            }

            return true;
        }

        return false;
    }

    private boolean couldBeATap(MotionEvent event) {
        return event.getEventTime() <= event.getDownTime() + TAP_TIMEOUT;
    }

    private int getHandleX() {
        final WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) handleView.getLayoutParams();
        return layoutParams.x;
    }

    private int getHandleY() {
        final WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) handleView.getLayoutParams();
        return layoutParams.y;
    }

    private boolean isHoveringOverDismissArea() {
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) handleView.getLayoutParams();
        final int y = layoutParams.y;

        final int center = windowHeight / 2;
        final int position = y + center - (handleView.getHeight() / 2);

        return position >= windowHeight - (handleView.getHeight() * 2);
    }

    private void openOrCloseIfNeeded() {
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) handleView.getLayoutParams();

        if (layoutParams.x < handleView.getOpenOffsetX() / 2) {
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

        ObjectAnimator animator = ObjectAnimator.ofInt(this, "animationOffset", layoutParams.x, handleView.getOpenOffsetX() + handleView.getMargin());
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

        updateOverlayViewPosition(x - handleView.getMargin());
    }

    private void updateHandlePosition(int x, int y) {
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) handleView.getLayoutParams();

        layoutParams.x = x;
        layoutParams.y = y;

        windowManager.updateViewLayout(handleView, layoutParams);
    }

    private void updateOverlayViewPosition(int x) {
        overlayView.setTranslationX(overlayView.getWidth() - x);
    }
}
