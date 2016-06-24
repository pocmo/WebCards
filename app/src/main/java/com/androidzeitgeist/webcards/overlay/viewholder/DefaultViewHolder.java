/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay.viewholder;

import android.text.TextUtils;
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
    private ImageView iconView;
    private ImageView backgroundView;
    private TextView descriptionView;

    public DefaultViewHolder(View itemView) {
        super(itemView);

        titleView = (TextView) itemView.findViewById(R.id.title);
        urlView = (TextView) itemView.findViewById(R.id.url);
        iconView = (ImageView) itemView.findViewById(R.id.icon);
        backgroundView = (ImageView) itemView.findViewById(R.id.background);
        descriptionView = (TextView) itemView.findViewById(R.id.description);
    }

    @Override
    public void bind(WebCard webCard) {
        titleView.setText(webCard.getTitle());
        urlView.setText(webCard.getUrl());

        Picasso.with(itemView.getContext())
                .load(webCard.getIconUrl())
                .into(iconView);

        final String imageUrl = webCard.getImageUrl();
        if (imageUrl != null) {
            backgroundView.setVisibility(View.VISIBLE);

            Picasso.with(itemView.getContext())
                    .load(imageUrl)
                    .into(backgroundView);
        } else {
            backgroundView.setVisibility(View.GONE);
        }

        final String description = webCard.getDescription();
        if (TextUtils.isEmpty(description)) {
            descriptionView.setText(description);
            descriptionView.setVisibility(View.VISIBLE);
        } else {
            descriptionView.setVisibility(View.GONE);
        }
    }
}
