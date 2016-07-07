/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.customtabs.CustomTabsIntent;
import android.widget.Toast;

import com.androidzeitgeist.webcards.MainActivity;
import com.androidzeitgeist.webcards.R;
import com.androidzeitgeist.webcards.model.CardType;
import com.androidzeitgeist.webcards.model.WebCard;
import com.androidzeitgeist.webcards.processing.ContentProcessor;
import com.androidzeitgeist.webcards.viewer.PhotoActivity;
import com.androidzeitgeist.webcards.viewer.VideoActivity;

/* package-private */ class OverlayController implements ContentProcessor.ProcessorCallback {
    private static final int OVERLAY_TIMEOUT = 4000;

    private static OverlayController instance;

    private final Handler handler;
    private Runnable timerRunnable;

    private OverlayService overlayService;
    private OverlayView overlayView;
    private HandleView handleView;
    private DismissAreaView dismissAreaView;

    private DragCoordinator dragCoordinator;

    /* package-private */ static synchronized OverlayController get() {
        if (instance == null) {
            instance = new OverlayController();
        }
        return instance;
    }

    public OverlayController() {
        handler = new Handler(Looper.getMainLooper());
    }

    /* package-private */ void setOverlayService(OverlayService overlayService) {
        this.overlayService = overlayService;
    }

    /* package-private */ void setViews(OverlayView overlayView, HandleView handleView, DismissAreaView dismissAreaView) {
        this.overlayView = overlayView;
        this.handleView = handleView;
        this.dismissAreaView = dismissAreaView;

        this.dragCoordinator = new DragCoordinator(overlayService, overlayView, handleView, dismissAreaView);
    }

    /* package-private */ void onOverlayEmpty() {
        dragCoordinator.animateClose().addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                removeOverlay();
            }
        });
    }

    /* package-private */ void removeOverlay() {
        overlayView.removeFromRoot();
        handleView.removeFromRoot();
        dismissAreaView.removeFromRoot();

        overlayService.stopSelf();
    }

    /* package-private */ void closeOverlay() {
        if (dragCoordinator.isOpen()) {
            dragCoordinator.animateClose();
        }
    }

    /* package-private */ void openOverlay() {
        if (!dragCoordinator.isOpen()) {
            dragCoordinator.animateOpen();
        }
    }

    /* package-private */ void startTimeout() {
        synchronized (handler) {
            stopTimeout();

            timerRunnable = new Runnable() {
                @Override
                public void run() {
                    closeOverlay();
                    timerRunnable = null;
                }
            };

            handler.postDelayed(timerRunnable, OVERLAY_TIMEOUT);
        }
    }

    /* package-private */ void stopTimeout() {
        if (timerRunnable == null) {
            return;
        }

        synchronized (handler) {
            if (timerRunnable != null) {
                handler.removeCallbacks(timerRunnable);
                timerRunnable = null;
            }
        }
    }

    @Override
    public void onWebCardCreated(WebCard card) {
        overlayView.addCard(card);
    }

    @Override
    public void onWebCardFailed(String url) {
        overlayView.addCard(WebCard.createError(url));
    }

    /* package-private */  void onCardClicked(WebCard card) {
        if (card.getType() == CardType.PHOTO) {
            PhotoActivity.show(overlayService, card.getUrl());
        } else if (card.getType() == CardType.VIDEO) {
            VideoActivity.show(overlayService, card.getUrl());
        } else {
            Intent intent = new CustomTabsIntent.Builder().build().intent;
            intent.setData(Uri.parse(card.getUrl()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.android.chrome");
            overlayService.startActivity(intent);
        }

        OverlayController.get().closeOverlay();
    }

    /* package-private */ void switchToApp() {
        dragCoordinator.animateClose().addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent(overlayService, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                overlayService.startActivity(intent);

                removeOverlay();
            }
        });
    }

    /* package-private */ void share(WebCard card) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, card.getUrl());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        overlayService.startActivity(intent);

        closeOverlay();
    }

    /* package-private */ void copyToClipboard(WebCard card) {
        ClipboardManager clipboardManager = (ClipboardManager) overlayService.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(new ClipData("URL", new String[] { "text/plain" }, new ClipData.Item(card.getUrl())));

        Toast.makeText(overlayService, R.string.toast_copy_clipboard, Toast.LENGTH_SHORT).show();

        closeOverlay();
    }
}
