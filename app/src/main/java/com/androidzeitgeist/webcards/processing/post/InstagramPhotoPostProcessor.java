/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.processing.post;

import com.androidzeitgeist.featurizer.features.WebsiteFeatures;
import com.androidzeitgeist.webcards.model.WebCard;
import com.androidzeitgeist.webcards.processing.ContentProcessor;

import org.jsoup.nodes.Document;

/**
 * Post processor for showing a photo card for instagram photo pages.
 */
public class InstagramPhotoPostProcessor implements PostProcessor {
    private static final String OPEN_GRAPH_TYPE_INSTAGRAM_PHOTO = "instapp:photo";

    @Override
    public void process(Document document, WebsiteFeatures features, ContentProcessor.ProcessorCallback callback) {
        if (OPEN_GRAPH_TYPE_INSTAGRAM_PHOTO.equals(features.getType())) {
            callback.onWebCardCreated(WebCard.createPhotoCard(features.getImageUrl()));
        }

    }
}
