/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.processing.pre;

import android.net.Uri;

import okhttp3.Request;

/**
 * Google+ uses a JavaScript based redirector. This pre processor will extract the actual URL from
 * the redirector URL and create a new request.
 */
public class GooglePlusPreProcessor implements PreProcessor {
    private static final String URL_PREFIX = "://plus.url.google.com/url?q=";

    @Override
    public Request process(Request request) {
        final String url = request.url().toString();

        if (url.contains(URL_PREFIX)) {
            final String actualUrl = Uri.parse(url).getQueryParameter("q");

            if (actualUrl != null) {
                return request.newBuilder()
                        .url(actualUrl)
                        .build();
            }
        }

        return request;
    }
}
