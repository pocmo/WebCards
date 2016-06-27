/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.WindowManager;

import com.androidzeitgeist.webcards.R;
import com.androidzeitgeist.webcards.model.WebCard;
import com.androidzeitgeist.webcards.processing.ContentProcessor;

public class OverlayService extends Service implements ContentProcessor.ProcessorCallback {
    private static final String TAG = "WebCards/OverlayService";

    private static final String EXTRA_URL = "url";

    private OverlayView overlayView;

    public static void processUrl(Context context, String url) {
        Intent intent = new Intent(context, OverlayService.class);
        intent.putExtra(EXTRA_URL, url);
        context.startService(intent);
    }

    private WindowManager windowManager;
    private ContentProcessor contentProcessor;

    @Override
    public void onCreate() {
        super.onCreate();

        this.windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        this.contentProcessor = new ContentProcessor();

        initializeOverlay();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initializeOverlay() {
        if (overlayView == null) {
            overlayView = (OverlayView) LayoutInflater.from(this).inflate(R.layout.overlay, null);
        }

        overlayView.show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_NOT_STICKY;
        }

        final String url = intent.getStringExtra(EXTRA_URL);
        if (url == null) {
            return START_NOT_STICKY;
        }

        process(url);

        // For now we just start as not sticky. If the process dies later then the intent is not
        // redelivered. Eventually we might want to return START_REDELIVER_INTENT and resume
        // processing an URL if the service got killed. But this will require that we track the
        // progress of the Intent / URL and that we can stop the service for this "startId" later.
        return START_NOT_STICKY;
    }

    private void process(String url) {
        overlayView.addCard(WebCard.createPlaceholder(url));

        contentProcessor.process(url, new ContentProcessor.MainThreadProcessorCallback(this));
    }

    @Override
    public void onWebCardCreated(WebCard card) {
        overlayView.addCard(card);
    }

    @Override
    public void onWebCardFailed(String url) {
        overlayView.addCard(WebCard.createError(url));
    }
}
