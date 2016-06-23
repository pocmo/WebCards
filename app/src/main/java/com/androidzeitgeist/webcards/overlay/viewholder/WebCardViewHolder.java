/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.androidzeitgeist.webcards.model.WebCard;

/**
 * Base class for view holders for the overlay recyclerview.
 */
public abstract class WebCardViewHolder extends RecyclerView.ViewHolder {
    private boolean animateOnAttach;

    public WebCardViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(WebCard webCard);

    public void setAnimateOnAttach(boolean animateOnAttach) {
        this.animateOnAttach = animateOnAttach;
    }

    public boolean shouldAnimateOnAttach() {
        return animateOnAttach;
    }
}
