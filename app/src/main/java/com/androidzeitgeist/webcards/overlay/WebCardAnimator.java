/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay;

import android.animation.Animator;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;

/**
 * Animator implementation for the overlay recyclerview.
 */
public class WebCardAnimator extends DefaultItemAnimator {
    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
        animateCircularHide(oldHolder).start();

        dispatchAddFinished(newHolder);

        return false;
    }

    private Animator animateCircularHide(final RecyclerView.ViewHolder item) {
        final View view = item.itemView;

        final int x = view.getMeasuredWidth() / 2;
        final int y = view.getMeasuredHeight();
        final int radius = Math.max(view.getWidth(), view.getHeight()) / 2;

        Animator animator = ViewAnimationUtils.createCircularReveal(view, x, y, radius, 0);

        animator.setInterpolator(new AccelerateInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dispatchAnimationFinished(item);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                dispatchAnimationFinished(item);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        return animator;
    }
}
