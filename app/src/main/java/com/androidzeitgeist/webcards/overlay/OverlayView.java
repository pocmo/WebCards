/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import com.androidzeitgeist.webcards.R;
import com.androidzeitgeist.webcards.model.WebCard;
import com.androidzeitgeist.webcards.util.ItemClickSupport;

/**
 * Root layout for the overlay.
 */
public class OverlayView extends FrameLayout implements ItemClickSupport.OnItemClickListener {
    private WindowManager windowManager;
    private WebCardAdapter adapter;

    private boolean isAnimating;

    public OverlayView(Context context) {
        super(context);
        init(context);
    }

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public synchronized void show() {
        setVisibility(View.VISIBLE);

        if (isShown()) {
            return;
        }

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;

        try {
            windowManager.addView(this, layoutParams);
        } catch (final SecurityException | WindowManager.BadTokenException e) {
            // We do not have the permission to add a view to the window ("draw over other apps")
            return;
        }
    }

    private synchronized void hide() {
        if (!isShown()) {
            return;
        }

        windowManager.removeView(this);
    }

    public synchronized void animateHide() {
        if (isAnimating) {
            return;
        }

        animate()
                .translationX(getWidth())
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        setVisibility(View.INVISIBLE);
                        setTranslationX(0);

                        adapter.removeCards();

                        hide();

                        isAnimating = false;
                    }
                })
                .setInterpolator(new AccelerateInterpolator())
                .start();

        isAnimating = true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new WebCardLayoutManager(getContext(), this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new WebCardAnimator(recyclerView));

        adapter = new WebCardAdapter(recyclerView);
        recyclerView.setAdapter(adapter);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(this);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new WebCardTouchHelper(adapter));
        touchHelper.attachToRecyclerView(recyclerView);
    }

    public synchronized void addCard(WebCard card) {
        if (!isShown()) {
            show();
        }

        adapter.addCard(card);
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, View view, int position) {
        WebCard card = adapter.getCard(position);

        Intent intent = new CustomTabsIntent.Builder().build().intent;
        intent.setData(Uri.parse(card.getUrl()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome");
        getContext().startActivity(intent);

        adapter.removeCard(card);

        if (adapter.getItemCount() == 0) {
            animateHide();
        }
    }
}
