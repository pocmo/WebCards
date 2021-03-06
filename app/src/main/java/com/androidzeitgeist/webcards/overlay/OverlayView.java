/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.androidzeitgeist.webcards.R;
import com.androidzeitgeist.webcards.model.WebCard;
import com.androidzeitgeist.webcards.util.ItemClickSupport;

/**
 * Root layout for the overlay.
 */
public class OverlayView extends FrameLayout implements ItemClickSupport.OnItemClickListener, ItemClickSupport.OnItemLongClickListener {
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

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            OverlayController.get().stopTimeout();
        }

        return super.onInterceptTouchEvent(event);
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

        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener(this)
                .setOnItemLongClickListener(this);

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

        OverlayController.get().onCardClicked(card);

        adapter.removeCard(card);
    }

    @Override
    public boolean onItemLongClicked(RecyclerView recyclerView, View view, int position) {
        final WebCard card = adapter.getCard(position);

        ContextThemeWrapper context = new ContextThemeWrapper(view.getContext(), android.R.style.Theme_Material_Light);

        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.menu_card);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final int id = item.getItemId();

                if (id == R.id.share) {
                    OverlayController.get().share(card);
                } else if (id == R.id.copy) {
                    OverlayController.get().copyToClipboard(card);
                }

                return false;
            }
        });

        popupMenu.show();

        return true;
    }
}
