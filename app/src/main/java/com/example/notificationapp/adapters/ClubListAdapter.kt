package com.example.notificationapp.adapters

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notificationapp.R
import com.example.notificationapp.adapters.ClubListAdapter.CustomVH
import com.example.notificationapp.api.ClubModel
import com.example.notificationapp.app.Constants
import com.example.notificationapp.view.activities.ClubActivity
import com.google.android.material.card.MaterialCardView

class ClubListAdapter(private val mClubs: List<ClubModel>, private val mContext: Context) : RecyclerView.Adapter<CustomVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CustomVH(View.inflate(parent.context, R.layout.club_list_item, null))

    override fun onBindViewHolder(holder: CustomVH, position: Int) {
        holder.bindView(mClubs[position])
    }

    override fun getItemCount(): Int = mClubs.size

    inner class CustomVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val name: AppCompatTextView = itemView.findViewById(R.id.club_name)
        private val description: AppCompatTextView = itemView.findViewById(R.id.textDescription)
        private val background: MaterialCardView = itemView.findViewById(R.id.item_background)

        fun bindView(clubModel: ClubModel) {
            name.text = clubModel.name
            description.text = clubModel.description
            background.setOnClickListener {
                val intent = Intent(mContext, ClubActivity::class.java)
                intent.putExtra(Constants.CLUB_NAME, clubModel.name)
                intent.putExtra(Constants.CLUB_ID, clubModel.id)
                intent.putExtra(Constants.CLUB_DESC, clubModel.description)
                mContext.startActivity(intent)
            }
        }
    }
}