/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.processing;

import android.os.Handler;
import android.os.Looper;

import com.androidzeitgeist.featurizer.Featurizer;
import com.androidzeitgeist.featurizer.features.WebsiteFeatures;
import com.androidzeitgeist.webcards.model.WebCard;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This URL processor will download a document on a background thread, extract some features and
 * then provide this to a callback.
 */
public class URLProcessor {
    public interface Callback {
        void onWebCardCreated(WebCard card);
        void onWebCardFailed(String url);
    }

    private ExecutorService service;
    private Featurizer featurizer;
    private Handler handler;

    public URLProcessor() {
        this.service = Executors.newCachedThreadPool();
        this.featurizer = new Featurizer();
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void process(final String url, final Callback callback) {
        service.submit(new Runnable() {
            @Override
            public void run() {
                processInternal(url, callback);
            }
        });
    }

    private void processInternal(String url, Callback callback) {
        try {
            final WebsiteFeatures features = featurizer.featurize(url);
            final WebCard card = WebCard.createFromFeatures(features);

            if (card != null) {
                onWebCardCreated(card, callback);
            } else {
                onWebCardFailed(callback, url);
            }
        } catch (IOException e) {
            onWebCardFailed(callback, url);
        }
    }

    private void onWebCardCreated(final WebCard card, final Callback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onWebCardCreated(card);
            }
        });
    }

    private void onWebCardFailed(final Callback callback, final String url) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onWebCardFailed(url);
            }
        });
    }
}
