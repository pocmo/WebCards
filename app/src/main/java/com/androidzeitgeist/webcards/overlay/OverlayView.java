/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay;

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
import android.widget.FrameLayout;
import android.widget.Toast;

import com.androidzeitgeist.webcards.R;
import com.androidzeitgeist.webcards.model.WebCard;
import com.androidzeitgeist.webcards.util.ItemClickSupport;

/**
 * Root layout for the overlay.
 */
public class OverlayView extends FrameLayout implements ItemClickSupport.OnItemClickListener {
    private WindowManager windowManager;
    private WebCardAdapter adapter;

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

    /* package-private */ synchronized void addToRoot() {
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
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                PixelFormat.TRANSLUCENT);

        layoutParams.gravity = Gravity.BOTTOM | Gravity.END;

        try {
            windowManager.addView(this, layoutParams);
        } catch (final SecurityException | WindowManager.BadTokenException e) {
            // We do not have the permission to add a view to the window ("draw over other apps")
            Toast.makeText(getContext(), R.string.toast_missing_permission, Toast.LENGTH_SHORT).show();
        }
    }

    /* package-private */ synchronized void removeFromRoot() {
        if (!isShown()) {
            return;
        }

        windowManager.removeView(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new WebCardLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new WebCardAnimator(recyclerView));

        adapter = new WebCardAdapter(recyclerView);
        recyclerView.setAdapter(adapter);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(this);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new WebCardTouchHelper(adapter));
        touchHelper.attachToRecyclerView(recyclerView);
    }

    /* package-private */ synchronized void addCard(WebCard card) {
        if (!isShown()) {
            addToRoot();
        }

        adapter.addCard(card);
    }

    /* package-private */ synchronized void removeCards() {
        adapter.removeCards();
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

        OverlayController.get().closeOverlay();
    }
}
