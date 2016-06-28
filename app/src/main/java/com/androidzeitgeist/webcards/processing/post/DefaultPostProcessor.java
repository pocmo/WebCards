/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.processing.post;

import com.androidzeitgeist.featurizer.features.WebsiteFeatures;
import com.androidzeitgeist.webcards.model.WebCard;
import com.androidzeitgeist.webcards.processing.ContentProcessor;

import org.jsoup.nodes.Document;

/**
 * Default post processor that will create a more or less generic card for the URL.
 */
public class DefaultPostProcessor implements PostProcessor {
    private static final String OPEN_GRAPH_TYPE_ARTICLE = "article";

    @Override
    public void process(Document document, WebsiteFeatures features, ContentProcessor.ProcessorCallback callback) {
        final WebCard card;

        if (OPEN_GRAPH_TYPE_ARTICLE.equals(features.getType())) {
            // This website actual has an article open graph type: Use the article card.
            card = WebCard.createArticleCard(features);
        } else {
            card = WebCard.createDefaultCardFromFeatures(features);
        }

        if (card != null) {
            callback.onWebCardCreated(card);
        } else {
            callback.onWebCardFailed(document.baseUri());
        }
    }
}
