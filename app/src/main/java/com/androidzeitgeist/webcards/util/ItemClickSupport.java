/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.androidzeitgeist.webcards.R;

import static android.support.v7.widget.RecyclerView.ViewHolder;
import static android.view.View.OnClickListener;
import static android.view.View.OnLongClickListener;

public final class ItemClickSupport {
    private final RecyclerView mRecyclerView;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    private final OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                mOnItemClickListener.onItemClicked(mRecyclerView, v, holder.getAdapterPosition());
            }
        }
    };

    private final OnLongClickListener mOnLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (mOnItemLongClickListener != null) {
                ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                return mOnItemLongClickListener.onItemLongClicked(
                        mRecyclerView, v, holder.getAdapterPosition());
            }
            return false;
        }
    };

    private final RecyclerView.OnChildAttachStateChangeListener mAttachListener =
            new RecyclerView.OnChildAttachStateChangeListener() {
                @Override
                public void onChildViewAttachedToWindow(View view) {
                    if (mOnItemClickListener != null) {
                        view.setOnClickListener(mOnClickListener);
                    }
                    if (mOnItemLongClickListener != null) {
                        view.setOnLongClickListener(mOnLongClickListener);
                    }
                }

                @Override
                public void onChildViewDetachedFromWindow(View view) {}
            };

    private ItemClickSupport(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mRecyclerView.setTag(R.id.item_click_support, this);
        mRecyclerView.addOnChildAttachStateChangeListener(mAttachListener);
    }

    @NonNull
    public static ItemClickSupport addTo(@NonNull RecyclerView view) {
        ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
        if (support == null) {
            support = new ItemClickSupport(view);
        }
        return support;
    }

    private void detach(@NonNull RecyclerView view) {
        view.removeOnChildAttachStateChangeListener(mAttachListener);
        view.setTag(R.id.item_click_support, null);
    }

    /**
     * Register a callback to be invoked when an item in the
     * RecyclerView has been clicked.
     *
     * @param listener The callback that will be invoked.
     */
    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    /**
     * Register a callback to be invoked when an item in the
     * RecyclerView has been clicked and held.
     *
     * @param listener The callback that will be invoked.
     */
    public void setOnItemLongClickListener(@Nullable OnItemLongClickListener listener) {
        if (!mRecyclerView.isLongClickable()) {
            mRecyclerView.setLongClickable(true);
        }
        mOnItemLongClickListener = listener;
    }

    /**
     * Interface definition for a callback to be invoked when an item in the
     * RecyclerView has been clicked.
     */
    public interface OnItemClickListener {

        /**
         * Callback method to be invoked when an item in the RecyclerView
         * has been clicked.
         *
         * @param recyclerView The RecyclerView where the click happened.
         * @param view         The view within the RecyclerView that was clicked
         *                     (this will be a view provided by the adapter).
         * @param position     The position of the view in the adapter.
         */
        void onItemClicked(RecyclerView recyclerView, View view, int position);
    }

    /**
     * Interface definition for a callback to be invoked when an item in the
     * RecyclerView has been clicked and held.
     */
    public interface OnItemLongClickListener {

        /**
         * Callback method to be invoked when an item in the RecyclerView
         * has been clicked and held.
         *
         * @param recyclerView The RecyclerView where the click happened.
         * @param view         The view within the RecyclerView that was clicked
         * @param position     The position of the view in the adapter.
         * @return true if the callback consumed the long click, false otherwise
         */
        boolean onItemLongClicked(RecyclerView recyclerView, View view, int position);
    }
}

