/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay.viewholder;

import android.view.View;
import android.widget.TextView;

import com.androidzeitgeist.webcards.R;
import com.androidzeitgeist.webcards.model.WebCard;

/**
 * View holder for showing errors.
 */
public class ErrorViewHolder extends WebCardViewHolder {
    private TextView urlView;

    public ErrorViewHolder(View itemView) {
        super(itemView);

        urlView = (TextView) itemView.findViewById(R.id.url);
    }

    @Override
    public void bind(WebCard card) {
        urlView.setText("\uD83D\uDE1E\n" + card.getUrl());
    }
}
