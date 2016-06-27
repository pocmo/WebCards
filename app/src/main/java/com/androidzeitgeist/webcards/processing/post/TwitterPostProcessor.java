/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.processing.post;

import android.text.TextUtils;

import com.androidzeitgeist.featurizer.features.WebsiteFeatures;
import com.androidzeitgeist.webcards.model.WebCard;
import com.androidzeitgeist.webcards.processing.ContentProcessor;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Searches for twitter meta tags and creates twitter cards.
 */
public class TwitterPostProcessor implements PostProcessor {
    @Override
    public void process(Document document, WebsiteFeatures features, ContentProcessor.ProcessorCallback callback) {
        Elements twitterElements = document.select("meta[name='twitter:site']");
        if (twitterElements.size() > 0) {
            String handle = twitterElements.get(0).attr("content");

            if (!TextUtils.isEmpty(handle)) {
                WebCard card = WebCard.createTwitterCard(handle);
                callback.onWebCardCreated(card);
            }
        }
    }
}
