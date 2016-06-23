/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidzeitgeist.webcards.R;
import com.androidzeitgeist.webcards.model.WebCard;
import com.squareup.picasso.Picasso;

/**
 * Default view holder for generic websites without a specific type.
 */
public class DefaultViewHolder extends WebCardViewHolder {
    private TextView titleView;
    private TextView urlView;
    private ImageView imageView;

    public DefaultViewHolder(View itemView) {
        super(itemView);

        titleView = (TextView) itemView.findViewById(R.id.title);
        urlView = (TextView) itemView.findViewById(R.id.url);
        imageView = (ImageView) itemView.findViewById(R.id.image);
    }

    @Override
    public void bind(WebCard webCard) {
        titleView.setText(webCard.getTitle());
        urlView.setText(webCard.getUrl());

        Picasso.with(itemView.getContext())
                .load(webCard.getImageUrl())
                .into(imageView);
    }
}
