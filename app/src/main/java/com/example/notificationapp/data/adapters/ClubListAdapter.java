package com.example.notificationapp.data.adapters;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notificationapp.R;
import com.example.notificationapp.data.network.model.ClubModel;
import com.example.notificationapp.utils.Constants;
import com.example.notificationapp.view.activities.ClubActivity;

import java.util.List;

public class ClubListAdapter extends RecyclerView.Adapter<ClubListAdapter.CustomVH> {

    private final Context mContext;
    private final List<ClubModel> mClubs;

    public ClubListAdapter(List<ClubModel> clubs, Context context) {
        this.mClubs = clubs;
        this.mContext = context;
    }

    @NonNull
    @Override
    public CustomVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomVH(View.inflate(parent.getContext(), R.layout.club_list_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomVH holder, int position) {
        holder.setView(mClubs.get(position));
    }

    @Override
    public int getItemCount() {
        return mClubs.size();
    }

    public class CustomVH extends RecyclerView.ViewHolder {

        private final AppCompatTextView name, description;

        public CustomVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.club_name);
            description = itemView.findViewById(R.id.textDescription);
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, ClubActivity.class);
                intent.putExtra(Constants.CLUB_NAME, mClubs.get(getAdapterPosition()).getName());
                intent.putExtra(Constants.CLUB_ID, mClubs.get(getAdapterPosition()).getId());
                intent.putExtra(Constants.CLUB_DESC, mClubs.get(getAdapterPosition()).getDescription());
                mContext.startActivity(intent);
            });
        }

        public void setView(ClubModel clubModel) {
            name.setText(clubModel.getName());
            description.setText(clubModel.getDescription());
        }
    }
}
