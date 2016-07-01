/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;

import com.androidzeitgeist.webcards.model.CardType;
import com.androidzeitgeist.webcards.model.WebCard;
import com.androidzeitgeist.webcards.processing.ContentProcessor;
import com.androidzeitgeist.webcards.viewer.PhotoActivity;
import com.androidzeitgeist.webcards.viewer.VideoActivity;

/* package-private */ class OverlayController implements ContentProcessor.ProcessorCallback {
    private static OverlayController instance;

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
}
