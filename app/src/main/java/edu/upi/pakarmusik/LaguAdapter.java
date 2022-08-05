package edu.upi.pakarmusik;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class LaguAdapter extends RecyclerView.Adapter<LaguAdapter.LaguViewHolder> {

    private ArrayList<Lagu> dataList;
    private final Context context;

    public void updateWith(ArrayList<Lagu> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    LaguAdapter(Context context) {
        this.context = context;
    }

    @NotNull
    @Override
    public LaguViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_lagu, parent, false);
        return new LaguViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LaguViewHolder holder, int position) {
        holder.txtGenre.setText(dataList.get(position).getGenre().toUpperCase());
        holder.txtLink.setText(dataList.get(position).getLink());
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class LaguViewHolder extends RecyclerView.ViewHolder{
        private final TextView txtGenre, txtLink;

        public LaguViewHolder(View itemView) {
            super(itemView);
            txtGenre = itemView.findViewById(R.id.tvGenre);
            txtLink = itemView.findViewById(R.id.tvLink);
            itemView.setOnClickListener(v -> {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(txtLink.getText().toString()));
                webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    context.startActivity(webIntent);
                } catch (ActivityNotFoundException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }
}
