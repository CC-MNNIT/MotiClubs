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

    private Context context;
    private List<ClubModel> clubs;

    public ClubListAdapter(List<ClubModel> clubs, Context context) {
        this.clubs = clubs;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomVH(View.inflate(parent.getContext(), R.layout.club_list_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomVH holder, int position) {
        holder.setView(clubs.get(position));
    }

    @Override
    public int getItemCount() {
        return clubs.size();
    }

    public class CustomVH extends RecyclerView.ViewHolder {

        private AppCompatTextView name, description;

        public CustomVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.club_name);
            description = itemView.findViewById(R.id.textDescription);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ClubActivity.class);
                    intent.putExtra(Constants.CLUB_NAME, clubs.get(getAdapterPosition()).getName());
                    intent.putExtra(Constants.CLUB_ID, clubs.get(getAdapterPosition()).getId());
                    intent.putExtra(Constants.CLUB_DESC, clubs.get(getAdapterPosition()).getDescription());
                    context.startActivity(intent);
                }
            });
        }

        public void setView(ClubModel clubModel) {
            name.setText(clubModel.getName());
            description.setText(clubModel.getDescription());
        }
    }
}
