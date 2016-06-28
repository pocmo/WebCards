/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.processing.post;

import android.util.Log;

import com.androidzeitgeist.featurizer.features.WebsiteFeatures;
import com.androidzeitgeist.webcards.model.WebCard;
import com.androidzeitgeist.webcards.processing.ContentProcessor;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Very simple post processor to create a product card from an Amazon URL.
 */
public class AmazonPostProcessor implements PostProcessor {
    private Pattern asinPattern = Pattern.compile(".*/(dp|gp/product)/([^/]+).*");

    @Override
    public void process(Document document, WebsiteFeatures features, ContentProcessor.ProcessorCallback callback) {
        final String url = features.getUrl();

        if (!url.contains("www.amazon.")) {
            return;
        }

        final Matcher matcher = asinPattern.matcher(url);
        if (!matcher.matches()) {
            Log.w("SKDBG", "NO MATCH: " + url);
            return;
        }

        final String asin = matcher.group(2);
        final String productPhotoUrl = "http://images.amazon.com/images/P/" + asin + ".01.MZZZZZZ.jpg";

        Elements priceElements = document.select(".a-color-price");
        if (priceElements.isEmpty()) {
            return;
        }

        // We assume that the first price we find is the one we are interested in..
        final String price = priceElements.get(0).text();

        String title = features.getTitle();

        // Cleanup title
        title = title.replaceFirst("^Amazon\\.[^:]+: ", "");

        final WebCard card = WebCard.createProductCard(title, url, productPhotoUrl, price);
        callback.onWebCardCreated(card);
    }
}
