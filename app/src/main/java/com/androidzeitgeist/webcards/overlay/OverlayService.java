/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.androidzeitgeist.webcards.R;
import com.androidzeitgeist.webcards.model.WebCard;
import com.androidzeitgeist.webcards.processing.ContentProcessor;

public class OverlayService extends Service {
    private static final String TAG = "WebCards/OverlayService";

    private static final String ACTION_OPEN_LINK = "open_link";
    private static final String ACTION_SHUTDOWN = "shutdown";

    private static final String EXTRA_URL = "url";

    private OverlayView overlayView;
    private HandleView handleView;
    private DismissAreaView dismissAreaView;

    public static void processUrl(Context context, String url) {
        Intent intent = new Intent(context, OverlayService.class);
        intent.setAction(ACTION_OPEN_LINK);
        intent.putExtra(EXTRA_URL, url);
        context.startService(intent);
    }

    private ContentProcessor contentProcessor;

    @Override
    public void onCreate() {
        super.onCreate();

        OverlayController.get().setOverlayService(this);

        this.contentProcessor = new ContentProcessor();

        initializeOverlay();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        OverlayController.get().removeOverlay();
    }

    private void initializeOverlay() {
        if (overlayView == null) {
            overlayView = (OverlayView) LayoutInflater.from(this).inflate(R.layout.overlay, null);
            handleView = new HandleView(this);
            dismissAreaView = new DismissAreaView(this);

            OverlayController.get().setViews(overlayView, handleView, dismissAreaView);
        }

        overlayView.addToRoot();
        handleView.addToRoot();
        dismissAreaView.addToRoot();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!canDrawOverlays()) {
            Toast.makeText(this, R.string.toast_missing_permission, Toast.LENGTH_SHORT).show();
            stopSelf();
            return START_NOT_STICKY;
        }

        if (intent == null) {
            return START_NOT_STICKY;
        }

        if (ACTION_SHUTDOWN.equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }

        final String url = intent.getStringExtra(EXTRA_URL);
        if (url == null) {
            return START_NOT_STICKY;
        }

        overlayView.removeCards();

        OverlayController.get().openOverlay();
        OverlayController.get().startTimeout();

        process(url);

        // Show permanent notification so that the service does not get killed while displaying the
        // overlay.
        startForeground(1, createPermanentNotification());

        // For now we just start as not sticky. If the process dies later then the intent is not
        // redelivered. Eventually we might want to return START_REDELIVER_INTENT and resume
        // processing an URL if the service got killed. But this will require that we track the
        // progress of the Intent / URL and that we can stop the service for this "startId" later.
        return START_NOT_STICKY;
    }

    private boolean canDrawOverlays() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        }

        return true;
    }

    private void process(String url) {
        overlayView.addCard(WebCard.createPlaceholder(url));

        contentProcessor.process(url, new ContentProcessor.MainThreadProcessorCallback(OverlayController.get()));
    }

    private Notification createPermanentNotification() {
        Intent intent = new Intent(this, OverlayService.class);
        intent.setAction(ACTION_SHUTDOWN);

        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);

        return new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setShowWhen(false)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentIntent(pendingIntent)
                .build();
    }
}
