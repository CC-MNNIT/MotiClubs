package com.example.notificationapp.data.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notificationapp.Constants
import com.example.notificationapp.R
import com.example.notificationapp.data.network.AdminResponse
import com.example.notificationapp.data.network.PostResponse
import com.example.notificationapp.data.network.api.RetrofitAccessObject
import com.example.notificationapp.view.activities.ClubActivity
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class PostListAdapter(private val mPosts: List<PostResponse>, private val mContext: Context) :
    RecyclerView.Adapter<PostListAdapter.CustomVH>() {

    companion object {
        private const val TAG = "PostListAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CustomVH(View.inflate(parent.context, R.layout.post_list_item, null))

    override fun onBindViewHolder(holder: CustomVH, position: Int) {
        holder.bindView(mPosts[position])
    }

    override fun getItemCount(): Int = mPosts.size

    inner class CustomVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val name: AppCompatTextView = itemView.findViewById(R.id.admin_name)
        private val description: AppCompatTextView = itemView.findViewById(R.id.textDescription)
        private val background: MaterialCardView = itemView.findViewById(R.id.item_background)
        private val profilePic: ImageView? = itemView.findViewById(R.id.profile_pic)
        private val dateTime: AppCompatTextView = itemView.findViewById(R.id.post_time)

        fun bindView(postResponse: PostResponse) {
            Log.d(TAG, "bindView: $postResponse")
            description.text = postResponse.message
            dateTime.text = convertLongToTime(postResponse.time)

            var adminResponse: AdminResponse? = null
            val preferences = mContext.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)

            RetrofitAccessObject.getRetrofitAccessObject()
                .getAdminDetails(preferences.getString(Constants.TOKEN, ""), postResponse.adminEmail)
                .enqueue(object : Callback<AdminResponse?> {
                    override fun onResponse(
                        call: Call<AdminResponse?>,
                        response: Response<AdminResponse?>
                    ) {
                        adminResponse = response.body()!!
                        name.text = adminResponse!!.name
//                        if (adminResponse!!.avatar != null) {
//                            Picasso.get().load(adminResponse!!.avatar).placeholder(R.drawable.ic_person).into(profilePic)
//                        }
                    }

                    override fun onFailure(call: Call<AdminResponse?>, t: Throwable) {
                    }
                })
            background.setOnClickListener {
                val intent = Intent(mContext, ClubActivity::class.java)
//                intent.putExtra(Constants.CLUB_NAME, mPosts[adapterPosition].name)
//                intent.putExtra(Constants.CLUB_ID, mPosts[adapterPosition].id)
//                intent.putExtra(Constants.CLUB_DESC, mClubs[adapterPosition].description)
                mContext.startActivity(intent)
            }
        }
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return format.format(date)
    }
}