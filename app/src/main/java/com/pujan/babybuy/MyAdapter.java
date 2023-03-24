package com.pujan.babybuy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pujan.babybuy.DataClass;
import com.pujan.babybuy.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<DataClass> dataList;

    public MyAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (!dataList.get(position).getPrdImage().equals("")) {
            Glide.with(context).load(dataList.get(position).getPrdImage()).into(holder.recImage);
        }
        holder.recTitle.setText(dataList.get(position).getPrdTitle());
        holder.recDesc.setText(dataList.get(position).getPrdDesc());
        holder.recLang.setText(dataList.get(position).getPrdPrice());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ProductDetail.class);
                intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getPrdImage());
                intent.putExtra("Description", dataList.get(holder.getAdapterPosition()).getPrdDesc());
                intent.putExtra("Title", dataList.get(holder.getAdapterPosition()).getPrdTitle());
                intent.putExtra("Key", dataList.get(holder.getAdapterPosition()).getKey());
                intent.putExtra("Price", dataList.get(holder.getAdapterPosition()).getPrdPrice());
                intent.putExtra("lat", dataList.get(holder.getAdapterPosition()).getLat());
                intent.putExtra("lang", dataList.get(holder.getAdapterPosition()).getLang());
                intent.putExtra("address", dataList.get(holder.getAdapterPosition()).getAddress());
                intent.putExtra("productId", dataList.get(holder.getAdapterPosition()).getProductId());
                intent.putExtra("ispurchased", dataList.get(holder.getAdapterPosition()).isIspurchased());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<DataClass> searchList) {
        dataList = searchList;
        notifyDataSetChanged();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView recImage;
    TextView recTitle, recDesc, recLang;
    CardView recCard;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        recImage = itemView.findViewById(R.id.prdImage);
        recCard = itemView.findViewById(R.id.recCard);
        recDesc = itemView.findViewById(R.id.prdDesc);
        recLang = itemView.findViewById(R.id.prdPrice);
        recTitle = itemView.findViewById(R.id.prdTitle);
    }
}