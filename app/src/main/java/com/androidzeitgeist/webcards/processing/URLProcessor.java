/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.processing;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.androidzeitgeist.featurizer.Featurizer;
import com.androidzeitgeist.featurizer.features.WebsiteFeatures;
import com.androidzeitgeist.webcards.model.WebCard;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * This URL processor will download a document on a background thread, extract some features and
 * then provide this to a callback.
 */
public class URLProcessor {
    private static final String TAG = "WebCards/URLProcessor";

    public interface Callback {
        void onWebCardCreated(WebCard card);
        void onWebCardFailed(String url);
    }

    private OkHttpClient client;
    private ExecutorService service;
    private Featurizer featurizer;
    private Handler handler;

    public URLProcessor() {
        this.service = Executors.newSingleThreadExecutor();
        this.client = new OkHttpClient();
        this.featurizer = new Featurizer(client);
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void process(final String url, final Callback callback) {
        service.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    processInternal(url, callback);
                } catch (IOException e) {
                    onWebCardFailed(callback, url);
                } catch (Throwable t) {
                    Log.e(TAG, "Error in background thread", t);
                }
            }
        });
    }

    private void processInternal(String url, final Callback callback) throws IOException {
        final Request request = new Request.Builder()
                .url(url)
                .build();

        final Response response = client.newCall(request)
                .execute();

        ResponseBody body = response.body();

        final Document document = Jsoup.parse(
                body.byteStream(),
                body.contentType().charset(StandardCharsets.UTF_8).name(),
                url);

        final WebsiteFeatures features = featurizer.featurize(document);


        final WebCard card;

        if (TextUtils.isEmpty(features.getType()) && !TextUtils.isEmpty(features.getImageUrl())) {
            card = WebCard.createArticleCard(features);
        } else if ("article".equals(features.getType())) {
            card = WebCard.createArticleCard(features);
        } else {
            card = WebCard.createDefaultCardFromFeatures(features);
        }

        if (card != null) {
            onWebCardCreated(card, callback);
        } else {
            onWebCardFailed(callback, url);
        }

        if ("video".equals(features.getType())) {
            processVideo(document, features, callback);
        }

        if ("instapp:photo".equals(features.getType()) || "flickr_photos:photo".equals(features.getType())) {
            onWebCardCreated(WebCard.createPhotoCard(features.getImageUrl()), callback);
        }

        processTwitter(document, callback);
    }

    private void processVideo(Document document, WebsiteFeatures features, Callback callback) {
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
        onWebCardCreated(card, callback);
    }

    private void processTwitter(Document document, Callback callback) {
        Elements twitterElements = document.select("meta[name='twitter:site']");
        if (twitterElements.size() > 0) {
            String handle = twitterElements.get(0).attr("content");

            if (!TextUtils.isEmpty(handle)) {
                WebCard card = WebCard.createTwitterCard(handle);
                onWebCardCreated(card, callback);
            }
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
