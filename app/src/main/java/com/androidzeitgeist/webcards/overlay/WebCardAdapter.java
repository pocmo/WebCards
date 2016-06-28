/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.overlay;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import com.androidzeitgeist.webcards.R;
import com.androidzeitgeist.webcards.model.CardType;
import com.androidzeitgeist.webcards.model.WebCard;
import com.androidzeitgeist.webcards.overlay.viewholder.ArticleViewHolder;
import com.androidzeitgeist.webcards.overlay.viewholder.DefaultViewHolder;
import com.androidzeitgeist.webcards.overlay.viewholder.ErrorViewHolder;
import com.androidzeitgeist.webcards.overlay.viewholder.PhotoViewHolder;
import com.androidzeitgeist.webcards.overlay.viewholder.PlaceholderViewHolder;
import com.androidzeitgeist.webcards.overlay.viewholder.TwitterViewHolder;
import com.androidzeitgeist.webcards.overlay.viewholder.VideoViewHolder;
import com.androidzeitgeist.webcards.overlay.viewholder.WebCardViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * WebCard adapter for the overlay.
 */
/* package-private */ class WebCardAdapter extends RecyclerView.Adapter<WebCardViewHolder> {
    private final RecyclerView recyclerView;
    private final List<WebCard> cards;

    private int lastAnimatedPosition = -1;

    /* package-private */ WebCardAdapter(RecyclerView recyclerView) {
        this.cards = new ArrayList<>();
        this.recyclerView = recyclerView;
    }

    /* package-private */ void addCard(WebCard card) {
        if (cards.size() == 1) {
            if (cards.get(0).getType() == CardType.PLACEHOLDER) {
                cards.set(0, card);
                notifyItemChanged(0);
                return;
            }
        }

        cards.add(card);
        notifyItemInserted(cards.size() - 1);
    }

    /* package-private */ WebCard getCard(int position) {
        return cards.get(position);
    }

    /* package-private */ void removeCard(WebCard card) {
        int position = cards.indexOf(card);
        if (position != -1) {
            removeCard(position);
        }
    }

    /* package-private */ void removeCard(int position) {
        cards.remove(position);
        notifyItemRemoved(position);

        if (cards.size() == 0) {
            OverlayController.get().onOverlayEmpty();
        }
    }

    /* package-private */ void removeCards() {
        cards.clear();
        notifyDataSetChanged();

        lastAnimatedPosition = -1;
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

            case ARTICLE:
                return R.layout.card_article;

            case TWITTER:
                return R.layout.card_twitter;

            case PHOTO:
                return R.layout.card_photo;

            default:
                return R.layout.card_default;
        }
    }

    @Override
    public WebCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        CardView view = (CardView) inflater.inflate(R.layout.wrapper_card, parent, false);
        inflater.inflate(viewType, view, true);

        switch (viewType) {
            case R.layout.card_placeholder:
                return new PlaceholderViewHolder(view);

            case R.layout.card_error:
                return new ErrorViewHolder(view);

            case R.layout.card_video:
                return new VideoViewHolder(view);

            case R.layout.card_article:
                return new ArticleViewHolder(view);

            case R.layout.card_twitter:
                return new TwitterViewHolder(view);

            case R.layout.card_photo:
                return new PhotoViewHolder(view);

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

        view.setTranslationX(recyclerView.getWidth());

        view.animate()
                .translationX(0)
                .setDuration(500)
                .setInterpolator(new AccelerateInterpolator())
                .start();

        holder.setAnimateOnAttach(false);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }
}
