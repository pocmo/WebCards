/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay.viewholder;

import android.view.View;
import android.widget.TextView;

import com.androidzeitgeist.webcards.R;
import com.androidzeitgeist.webcards.model.WebCard;

/**
 * View holder implementation for twitter cards.
 */
public class TwitterViewHolder extends WebCardViewHolder {
    private TextView titleView;

    public TwitterViewHolder(View itemView) {
        super(itemView);

        this.titleView = (TextView) itemView.findViewById(R.id.title);
    }

    @Override
    public void bind(WebCard webCard) {
        titleView.setText(webCard.getTitle());
    }
}
