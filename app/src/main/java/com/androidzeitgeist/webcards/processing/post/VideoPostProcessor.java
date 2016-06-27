/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.processing.post;

import com.androidzeitgeist.featurizer.features.WebsiteFeatures;
import com.androidzeitgeist.webcards.model.WebCard;
import com.androidzeitgeist.webcards.processing.ContentProcessor;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Post processor for creating video cards based on open graph data.
 */
public class VideoPostProcessor implements PostProcessor {
    @Override
    public void process(Document document, WebsiteFeatures features, ContentProcessor.ProcessorCallback callback) {
        String videoURL;
        Elements urlElements = document.select("meta[property='og:video:url']");
        if (urlElements.isEmpty()) {
            return;
        }
        videoURL = urlElements.get(0).absUrl("content");

        String videoType;
        Elements typeElements = document.select("meta[property='og:video:type']");
        if (typeElements.isEmpty()) {
            return;
        }
        videoType = typeElements.get(0).attr("content");

        if (!"text/html".equals(videoType)) {
            return;
        }

        WebCard card = WebCard.createVideoCard(features, videoURL);
        callback.onWebCardCreated(card);
    }
}
