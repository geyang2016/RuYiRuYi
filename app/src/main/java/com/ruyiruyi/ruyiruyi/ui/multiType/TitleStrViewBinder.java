package com.ruyiruyi.ruyiruyi.ui.multiType;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ruyiruyi.ruyiruyi.R;

import me.drakeet.multitype.ItemViewProvider;

public class TitleStrViewBinder extends ItemViewProvider<TitleStr, TitleStrViewBinder.ViewHolder> {

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_title_str, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull TitleStr titleStr) {
        holder.titleView.setText(titleStr.getTitle());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleView;

        ViewHolder(View itemView) {
            super(itemView);
            titleView = ((TextView) itemView.findViewById(R.id.title));
        }
    }
}