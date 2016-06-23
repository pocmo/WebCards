/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
        animateCircular(oldHolder, false).start();

        dispatchAddFinished(newHolder);

        return false;
    }

    @Override
    public boolean animateAdd(final RecyclerView.ViewHolder holder) {
        animateCircular(holder, true).start();

        return false;
    }

    private Animator animateCircular(final RecyclerView.ViewHolder item, boolean show) {
        final View view = item.itemView;

        final int x = view.getMeasuredWidth() / 2;
        final int y = view.getMeasuredHeight();
        final int radius = Math.max(view.getMeasuredWidth(), view.getMeasuredHeight()) / 2;

        Animator animator = ViewAnimationUtils.createCircularReveal(view, x, y, show ? 0 : radius, show ? radius : 0);

        animator.setInterpolator(new AccelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                dispatchAnimationFinished(item);
            }
        });

        return animator;
    }
}
