package com.vivo.vivofanclub.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.vivo.vivofanclub.R;
import com.vivo.vivofanclub.RegisterActivity;
import com.vivo.vivofanclub.model.GiftModel;

import java.util.ArrayList;


public class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.ViewHolder> {

    Context context;
    ArrayList<GiftModel> giftList;

    public GiftAdapter(Context context, ArrayList<GiftModel> giftList) {
        this.context = context;
        this.giftList = giftList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gift_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final GiftModel giftModel = giftList.get(position);

        holder.giftTv.setText(giftModel.getGiftName());
        Picasso.get().load(giftModel.getGiftImage()).placeholder(R.drawable.giftbox).into(holder.giftImg);

        holder.itemView.setOnClickListener(view -> context.startActivity(new Intent(context, RegisterActivity.class)));
    }

    @Override
    public int getItemCount() {
        return giftList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView giftImg;
        private final TextView giftTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            giftImg = itemView.findViewById(R.id.giftImg);
            giftTv = itemView.findViewById(R.id.giftTv);
        }
    }
}