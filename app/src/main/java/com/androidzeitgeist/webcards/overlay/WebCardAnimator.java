/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Animator implementation for the overlay recyclerview.
 */
public class WebCardAnimator extends DefaultItemAnimator {
    @Override
    public boolean animateAdd(final RecyclerView.ViewHolder holder) {
        final View view = holder.itemView;

        view.animate()
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        dispatchAnimationFinished(holder);
                    }
                })
                .setInterpolator(new AccelerateInterpolator())
                .start();

        return false;
    }
}
