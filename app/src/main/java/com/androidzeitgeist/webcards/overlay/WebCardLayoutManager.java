/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Layout manager implementation for the overlay recycler view.
 */
/* package-private */ class WebCardLayoutManager extends LinearLayoutManager {
    /* package-private */ WebCardLayoutManager(Context context) {
        super(context);

        setReverseLayout(true);
    }
}
