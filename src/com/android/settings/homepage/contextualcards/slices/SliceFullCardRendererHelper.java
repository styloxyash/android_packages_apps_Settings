/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.homepage.contextualcards.slices;

import android.animation.LayoutTransition;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.slice.Slice;
import androidx.slice.SliceItem;
import androidx.slice.widget.EventInfo;
import androidx.slice.widget.SliceView;

import com.android.settings.R;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.ContextualCardFeatureProvider;
import com.android.settings.overlay.FeatureFactory;

import java.util.Set;

/**
 * Card renderer helper for {@link ContextualCard} built as slice full card.
 */
class SliceFullCardRendererHelper implements SliceView.OnSliceActionListener {
    private static final String TAG = "SliceFCRendererHelper";

    private final Context mContext;

    private Set<ContextualCard> mCardSet;

    SliceFullCardRendererHelper(Context context) {
        mContext = context;
    }

    RecyclerView.ViewHolder createViewHolder(View view) {
        return new SliceViewHolder(view);
    }

    void bindView(RecyclerView.ViewHolder holder, ContextualCard card, Slice slice,
            Set<ContextualCard> cardSet) {
        final SliceViewHolder cardHolder = (SliceViewHolder) holder;
        cardHolder.sliceView.setScrollable(false);
        cardHolder.sliceView.setTag(card.getSliceUri());
        //TODO(b/114009676): We will soon have a field to decide what slice mode we should set.
        cardHolder.sliceView.setMode(SliceView.MODE_LARGE);
        cardHolder.sliceView.setSlice(slice);
        mCardSet = cardSet;
        // Set this listener so we can log the interaction users make on the slice
        cardHolder.sliceView.setOnSliceActionListener(this);

        // Customize slice view for Settings
        cardHolder.sliceView.showTitleItems(true);
        if (card.isLargeCard()) {
            cardHolder.sliceView.showHeaderDivider(true);
            cardHolder.sliceView.showActionDividers(true);
        }
    }

    @Override
    public void onSliceAction(@NonNull EventInfo eventInfo, @NonNull SliceItem sliceItem) {
        // sliceItem.getSlice().getUri() is like
        // content://android.settings.slices/action/wifi/_gen/0/_gen/0
        // contextualCard.getSliceUri() is prefix of sliceItem.getSlice().getUri()
        final ContextualCardFeatureProvider contextualCardFeatureProvider =
                FeatureFactory.getFactory(mContext).getContextualCardFeatureProvider(mContext);
        for (ContextualCard card : mCardSet) {
            if (sliceItem.getSlice().getUri().toString().startsWith(
                    card.getSliceUri().toString())) {
                contextualCardFeatureProvider.logContextualCardClick(card, eventInfo.rowIndex,
                        eventInfo.actionType);
                break;
            }
        }
    }

    static class SliceViewHolder extends RecyclerView.ViewHolder {
        public final SliceView sliceView;

        public SliceViewHolder(View view) {
            super(view);
            sliceView = view.findViewById(R.id.slice_view);
            sliceView.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        }
    }
}