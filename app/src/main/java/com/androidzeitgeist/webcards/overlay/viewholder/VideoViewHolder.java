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
 * View holder for video cards.
 */
public class VideoViewHolder extends WebCardViewHolder {
    private TextView titleView;
    private ImageView imageView;

    public VideoViewHolder(View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.image);
        titleView = (TextView) itemView.findViewById(R.id.title);
    }

    @Override
    public void bind(WebCard webCard) {
        titleView.setText(webCard.getTitle());

        Picasso.with(itemView.getContext())
                .load(webCard.getImageUrl())
                .into(imageView);
    }
}
