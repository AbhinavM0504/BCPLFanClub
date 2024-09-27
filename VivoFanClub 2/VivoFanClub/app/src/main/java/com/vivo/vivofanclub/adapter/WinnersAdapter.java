package com.vivo.vivofanclub.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vivo.vivofanclub.R;
import com.vivo.vivofanclub.model.WinnersModel;

import java.util.ArrayList;


public class WinnersAdapter extends RecyclerView.Adapter<WinnersAdapter.ViewHolder> {

    Context context;
    ArrayList<WinnersModel> winnersList;

    public WinnersAdapter(Context context, ArrayList<WinnersModel> winnersList) {
        this.context = context;
        this.winnersList = winnersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_winners_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final WinnersModel winnersModel = winnersList.get(position);

        holder.tvWinners.setText(winnersModel.getWinnerName());

        holder.itemView.setOnClickListener(view -> {
            //context.startActivity(new Intent(context, RegisterActivity.class));
        });

    }

    @Override
    public int getItemCount() {
        return winnersList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvWinners;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWinners = itemView.findViewById(R.id.tvWinners);
        }
    }
}