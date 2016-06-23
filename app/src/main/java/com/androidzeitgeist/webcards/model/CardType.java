/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.model;

import android.text.TextUtils;

public enum CardType {
    PLACEHOLDER,
    ERROR,

    DEFAULT,

    ARTICLE,
    PRODUCT,
    VIDEO;

    public static CardType fromString(String value) {
        if (TextUtils.isEmpty(value)) {
            return DEFAULT;
        }

        switch (value) {
            case "article":
                return ARTICLE;

            case "product":
                return PRODUCT;

            case "video":
            case "video.episode":
            case "video.movie":
            case "video.other":
                return VIDEO;

            default:
                return DEFAULT;
        }
    }
}
