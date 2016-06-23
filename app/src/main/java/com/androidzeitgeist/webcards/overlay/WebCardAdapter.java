/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay;

import android.animation.Animator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import com.androidzeitgeist.webcards.R;
import com.androidzeitgeist.webcards.model.WebCard;
import com.androidzeitgeist.webcards.overlay.viewholder.DefaultViewHolder;
import com.androidzeitgeist.webcards.overlay.viewholder.ErrorViewHolder;
import com.androidzeitgeist.webcards.overlay.viewholder.PlaceholderViewHolder;
import com.androidzeitgeist.webcards.overlay.viewholder.VideoViewHolder;
import com.androidzeitgeist.webcards.overlay.viewholder.WebCardViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * WebCard adapter for the overlay.
 */
public class WebCardAdapter extends RecyclerView.Adapter<WebCardViewHolder> {
    private final RecyclerView recyclerView;
    private final List<WebCard> cards;

    private int lastAnimatedPosition = -1;

    public WebCardAdapter(RecyclerView recyclerView) {
        this.cards = new ArrayList<>();
        this.recyclerView = recyclerView;
    }

    public void addCard(WebCard card) {
        // Currently we just deal with one card for simplicity.
        if (cards.isEmpty()) {
            cards.add(card);
            notifyItemInserted(0);
        } else {
            cards.set(0, card);
            notifyItemChanged(0);
        }
    }

    public void removeCards() {
        cards.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        WebCard card = cards.get(position);

        switch (card.getType()) {
            case PLACEHOLDER:
                return R.layout.card_placeholder;

            case ERROR:
                return R.layout.card_error;

            case VIDEO:
                return R.layout.card_video;

            default:
                return R.layout.card_default;
        }
    }

    @Override
    public WebCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        switch (viewType) {
            case R.layout.card_placeholder:
                return new PlaceholderViewHolder(view);

            case R.layout.card_error:
                return new ErrorViewHolder(view);

            case R.layout.card_video:
                return new VideoViewHolder(view);

            default:
                return new DefaultViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(WebCardViewHolder holder, int position) {
        holder.bind(cards.get(position));

        if (position > lastAnimatedPosition) {
            // If this is an item at a completely new position then animate it.
            holder.setAnimateOnAttach(true);

            lastAnimatedPosition = position;
        }
    }

    @Override
    public void onViewAttachedToWindow(WebCardViewHolder holder) {
        if (!holder.shouldAnimateOnAttach()) {
            return;
        }

        View view = holder.itemView;

        final int x = recyclerView.getWidth() / 2;
        final int y = recyclerView.getHeight();
        final int radius = Math.max(recyclerView.getWidth(), recyclerView.getHeight()) / 2;

        Animator animator = ViewAnimationUtils.createCircularReveal(view, x, y, 0, radius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();

        holder.setAnimateOnAttach(false);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }
}
