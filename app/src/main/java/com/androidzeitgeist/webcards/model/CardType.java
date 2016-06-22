/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.model;

public enum CardType {
    DEFAULT,

    ARTICLE,
    PRODUCT,
    VIDEO;

    public static CardType fromString(String value) {
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
