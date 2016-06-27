/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.processing;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.androidzeitgeist.featurizer.Featurizer;
import com.androidzeitgeist.featurizer.features.WebsiteFeatures;
import com.androidzeitgeist.webcards.model.WebCard;
import com.androidzeitgeist.webcards.processing.post.DefaultPostProcessor;
import com.androidzeitgeist.webcards.processing.post.FlickrPhotoPostProcessor;
import com.androidzeitgeist.webcards.processing.post.InstagramPhotoPostProcessor;
import com.androidzeitgeist.webcards.processing.post.PostProcessor;
import com.androidzeitgeist.webcards.processing.post.TwitterPostProcessor;
import com.androidzeitgeist.webcards.processing.post.VideoPostProcessor;
import com.androidzeitgeist.webcards.processing.pre.PreProcessor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
public class ContentProcessor {
    private static final String TAG = "WebCards/Processor";

    public interface ProcessorCallback {
        void onWebCardCreated(WebCard card);
        void onWebCardFailed(String url);
    }

    public static class MainThreadProcessorCallback implements ProcessorCallback {
        private ProcessorCallback callback;
        private Handler handler;

        public MainThreadProcessorCallback(ProcessorCallback callback) {
            this.handler = new Handler(Looper.getMainLooper());
            this.callback = callback;
        }

        @Override
        public void onWebCardCreated(final WebCard card) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onWebCardCreated(card);
                }
            });
        }

        @Override
        public void onWebCardFailed(final String url) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onWebCardFailed(url);
                }
            });
        }
    }

    private OkHttpClient client;
    private ExecutorService service;
    private Featurizer featurizer;

    private List<PreProcessor> preProcessors = Collections.emptyList();

    private List<PostProcessor> postProcessors = Arrays.asList(
            new DefaultPostProcessor(),
            new VideoPostProcessor(),
            new InstagramPhotoPostProcessor(),
            new FlickrPhotoPostProcessor(),
            new TwitterPostProcessor()
    );

    public ContentProcessor() {
        this.service = Executors.newSingleThreadExecutor();
        this.client = new OkHttpClient();
        this.featurizer = new Featurizer(client);
    }

    public void process(final String url, final ProcessorCallback callback) {
        service.submit(new Runnable() {
            @Override
            public void run() {
                processInternal(url, callback);
            }
        });
    }

    private void processInternal(String url, final ProcessorCallback callback) {
        final Request request = buildRequest(url);

        for (final PreProcessor processor : preProcessors) {
            processor.process(request);
        }

        final Document document = executeAndParse(request);
        if (document == null) {
            callback.onWebCardFailed(url);
            return;
        }

        final WebsiteFeatures features = featurizer.featurize(document);
        if (features == null) {
            callback.onWebCardFailed(url);
            return;
        }

        Log.d(TAG, "Received document and features (type=" + features.getType() + ", url=" + url + ")");

        for (final PostProcessor processor : postProcessors) {
            processor.process(document, features, callback);
        }
    }

    private Request buildRequest(String url) {
        return new Request.Builder()
                .url(url)
                .build();
    }

    private Document executeAndParse(Request request) {
        try {
            final Response response = client.newCall(request).execute();
            final ResponseBody body = response.body();

            return Jsoup.parse(
                    body.byteStream(),
                    body.contentType().charset(StandardCharsets.UTF_8).name(),
                    response.request().url().toString());
        } catch (IOException e) {
            Log.d(TAG, "IOException while performing request and parsing document", e);
            return null;
        }
    }
}
