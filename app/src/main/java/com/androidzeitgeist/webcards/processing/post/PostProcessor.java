/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.processing.post;

import com.androidzeitgeist.featurizer.features.WebsiteFeatures;
import com.androidzeitgeist.webcards.processing.ContentProcessor;

import org.jsoup.nodes.Document;

/**
 * Processor that runs after a request has been executed and a parsed document is available.
 */
public interface PostProcessor {
    void process(Document document, WebsiteFeatures features, ContentProcessor.ProcessorCallback callback);
}
