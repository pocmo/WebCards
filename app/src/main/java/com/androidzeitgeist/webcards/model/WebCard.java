/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.model;

import android.text.TextUtils;

import com.androidzeitgeist.featurizer.features.WebsiteFeatures;

public class WebCard {
    public static WebCard createPlaceholder(String url) {
        WebCard card = new WebCard();

        card.url = url;
        card.type = CardType.PLACEHOLDER;

        return card;
    }

    public static WebCard createError(String url) {
        WebCard card = new WebCard();

        card.url = url;
        card.type = CardType.ERROR;

        return card;
    }

    public static WebCard createVideoCard(WebsiteFeatures features, String videoURL) {
        WebCard card = createDefaultCardFromFeatures(features);

        card.type = CardType.VIDEO;
        card.url = videoURL;

        return card;
    }

    public static WebCard createArticleCard(WebsiteFeatures features) {
        WebCard card = createDefaultCardFromFeatures(features);

        card.type = CardType.ARTICLE;

        return card;
    }

    public static WebCard createTwitterCard(String handle) {
        WebCard card = new WebCard();

        if (handle.startsWith("@")) {
            handle = handle.substring(1);
        }

        card.type = CardType.TWITTER;
        card.title = "@" + handle;
        card.url = "https://twitter.com/" + handle;

        return card;
    }

    public static WebCard createPhotoCard(String photoUrl) {
        WebCard card = new WebCard();

        card.type = CardType.PHOTO;
        card.url = photoUrl;
        card.imageUrl = photoUrl;

        return card;
    }

    public static WebCard createProductCard(String productTitle, String productUrl, String productPhotoUrl, String price) {
        ProductWebCard card = new ProductWebCard();

        card.title = productTitle;
        card.imageUrl = productPhotoUrl;
        card.type = CardType.PRODUCT;
        card.price = price;
        card.url = productUrl;


        return card;
    }

    public static WebCard createDefaultCardFromFeatures(WebsiteFeatures features) {
        if (TextUtils.isEmpty(features.getTitle())
                || TextUtils.isEmpty(features.getUrl())
                || TextUtils.isEmpty(features.getIconUrl())) {
            // There are requires values missing.
            return null;
        }

        WebCard card = new WebCard();

        card.title = features.getTitle();
        card.url = features.getUrl();
        card.iconUrl = features.getIconUrl();
        card.imageUrl = features.getImageUrl();
        card.description = features.getDescription();

        card.type = CardType.DEFAULT;

        return card;
    }

    protected String title;
    protected String url;
    protected String iconUrl;
    protected String imageUrl;
    protected CardType type;
    protected String description;

    public String getTitle() {
        return title;
    }

    public CardType getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getDescription() {
        return description;
    }
}
