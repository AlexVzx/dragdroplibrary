/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.h6ah4i.android.example.advrecyclerview.demo_d_basic;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.h6ah4i.android.example.advrecyclerview.common.data.AbstractDataProvider;
import com.h6ah4i.android.example.advrecyclerview.common.utils.DrawableUtils;
import com.h6ah4i.android.example.advrecyclerview.common.utils.ViewUtils;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemState;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class DraggableExampleItemAdapter
        extends RecyclerView.Adapter<DraggableExampleItemAdapter.MyViewHolder>
        implements DraggableItemAdapter<DraggableExampleItemAdapter.MyViewHolder> {
    private static final String TAG = "MyDraggableItemAdapter";

    private AbstractDataProvider mProvider;

    public static class MyViewHolder extends AbstractDraggableItemViewHolder {
        public FrameLayout mContainer;
        public View mDragHandle;
        public TextView mTextView;

        public MyViewHolder(View v) {
            super(v);
            mContainer = v.findViewById(R.id.container);
            mDragHandle = v.findViewById(R.id.drag_handle);
            mTextView = v.findViewById(android.R.id.text1);
        }
    }

    public DraggableExampleItemAdapter(AbstractDataProvider dataProvider) {
        mProvider = dataProvider;

        // DraggableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return mProvider.getItem(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return mProvider.getItem(position).getViewType();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate((viewType == 0) ? R.layout.list_item_draggable : R.layout.list_item2_draggable, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final AbstractDataProvider.Data item = mProvider.getItem(position);

        // set text
        holder.mTextView.setText(item.getText());

        // set background resource (target view ID: container)
        final DraggableItemState dragState = holder.getDragState();

        if (dragState.isUpdated()) {
            int bgResId;

            if (dragState.isActive()) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if (dragState.isDragging()) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }
    }

    @Override
    public int getItemCount() {
        return mProvider.getCount();
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        Log.d(TAG, "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");

        mProvider.moveItem(fromPosition, toPosition);
    }

    @Override
    public boolean onCheckCanStartDrag(@NonNull MyViewHolder holder, int position, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (containerView.getTranslationX() + 0.5f);
        final int offsetY = containerView.getTop() + (int) (containerView.getTranslationY() + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(@NonNull MyViewHolder holder, int position) {
        // no drag-sortable range specified
        return null;
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }

    @Override
    public void onItemDragStarted(@NonNull MyViewHolder holder,int position) {
        notifyDataSetChanged();
    }

    @Override
    public void onItemDragFinished(@NonNull MyViewHolder holder,int fromPosition, int toPosition, boolean result) {
        notifyDataSetChanged();
    }
}
