/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Layout manager implementation for the overlay recyclerview.
 */
public class WebCardLayoutManager extends LinearLayoutManager {
    private OverlayView overlayView;

    public WebCardLayoutManager(Context context, OverlayView overlayView) {
        super(context);

        this.overlayView = overlayView;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        final int scrollRange = super.scrollVerticallyBy(dy, recycler, state);
        final int overscroll = dy - scrollRange;

        if (overscroll < -20) {
            overlayView.animateHide();
        }

        return scrollRange;
    }
}
