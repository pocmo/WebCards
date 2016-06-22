/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.model;

import android.text.TextUtils;

import com.androidzeitgeist.featurizer.features.WebsiteFeatures;

public class WebCard {
    public static WebCard createFromFeatures(WebsiteFeatures features) {
        if (TextUtils.isEmpty(features.getTitle())
                || TextUtils.isEmpty(features.getUrl())
                || TextUtils.isEmpty(features.getIconUrl())
                || TextUtils.isEmpty(features.getImageUrl())) {
            // There are requires values missing.
            return null;
        }

        WebCard card = new WebCard();

        card.title = features.getTitle();
        card.url = features.getUrl();
        card.iconUrl = features.getIconUrl();
        card.imageUrl = features.getImageUrl();

        card.type = CardType.fromString(features.getType());

        return card;
    }

    private String title;
    private String url;
    private String iconUrl;
    private String imageUrl;
    private CardType type;

    public String getTitle() {
        return title;
    }
}
