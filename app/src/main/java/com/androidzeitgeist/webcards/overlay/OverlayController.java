/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

import com.androidzeitgeist.webcards.model.WebCard;
import com.androidzeitgeist.webcards.processing.ContentProcessor;

/* package-private */ class OverlayController implements ContentProcessor.ProcessorCallback {
    private static OverlayController instance;

    private OverlayService overlayService;
    private OverlayView overlayView;
    private HandleView handleView;
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

    /* package-private */ void setViews(OverlayView overlayView, HandleView handleView) {
        this.overlayView = overlayView;
        this.handleView = handleView;
        this.dragCoordinator = new DragCoordinator(overlayService, overlayView, handleView);
    }

    /* package-private */ void onOverlayEmpty() {
        dragCoordinator.animateClose().addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                overlayView.removeFromRoot();
                handleView.removeFromRoot();
                overlayService.stopSelf();
            }
        });
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
}
