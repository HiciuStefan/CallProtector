package com.transapps.callprotector.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.transapps.callprotector.Models.Records;
import com.transapps.callprotector.R;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Hiciu on 2/23/2016.
 */
public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.MyViewHolder> {
    private LayoutInflater layoutInflater;
    List<Records> data = Collections.emptyList();
    String TAG = "Adapter  ";

    public SearchListAdapter(Context context, List<Records> data) {
        layoutInflater = LayoutInflater.from(context);
        this.data = data;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.custom_row_search, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        Log.d(TAG, "onCreateViewHolder ");
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Records current = data.get(position);
        holder.date.setText(current.date.toString());
        holder.details.setText(current.details);
        holder.userId.setText(current.userId);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void deleteItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(Records object) {
        data.add(object);
        notifyItemInserted(data.size());
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView id;
        TextView phone_number;
        TextView details;
        TextView markId;
        TextView userId;
        TextView date;

        public MyViewHolder(View itemView) {
            super(itemView);
            details = (TextView) itemView.findViewById(R.id.search_details);
            userId = (TextView) itemView.findViewById(R.id.search_user_id);
            date =(TextView) itemView.findViewById(R.id.search_date);
        }

        @Override
        public void onClick(View view) {
            deleteItem(getAdapterPosition());
        }
    }
}
