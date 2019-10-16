package com.app.stripepayment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.stripepayment.R;

import java.util.List;


public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.CardListHolder> {
    private final Context context;
    private List<String> items;

    public CardListAdapter(Context context, List<String> items) {

        this.context = context;
        this.items = items;

    }

    @Override
    public CardListHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_card_layout, parent, false);
        return new CardListHolder(v);
    }

    @Override
    public void onBindViewHolder(CardListHolder holder, int position) {
        String item = items.get(position);

        holder.tv_card_number.setText("XXXX-XXXX-XXXX-"+item);

    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    class CardListHolder extends RecyclerView.ViewHolder {

        private TextView tv_card_number;

        public CardListHolder(@NonNull View itemView) {
            super(itemView);

            tv_card_number = itemView.findViewById(R.id.tv_card_number);


        }

    }
}